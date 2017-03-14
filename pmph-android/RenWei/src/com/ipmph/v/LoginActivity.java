package com.ipmph.v;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.fragment.MyFragment;
import com.ipmph.v.object.LoginResultObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class LoginActivity extends FragmentActivity implements
		NetRequestIterface {

	private ImageView remember_password_icon;
	boolean isRememberPwd = true;
	private NetRequest netRequest;
	private EditText account_edit, password_edit, account_register_edit,
			password_register_edit, password_register_edit2, verify_code_edit;
	private RelativeLayout register_layout, modify_pwd_layout;
	private TextView title, title_right,verify_code1,verify_code2,verify_code3,verify_code4;
	private boolean inRegister = false, inProtocal = false,
			inModifyPwd = false;
	public static boolean autoLogin = false;
	private String username, password;
	private WebView webview_protocal;
    private Random random = new Random();  
	private static final char[] CHARS = {  
        '2', '3', '4', '5', '6', '7', '8', '9',  
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm',   
        'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'  
       };  
	StringBuilder codeBuilder = new StringBuilder();  
	public void onCreate(Bundle savedInstanceStage) {
		super.onCreate(savedInstanceStage);
		setContentView(R.layout.login_activity);
		remember_password_icon = (ImageView) findViewById(R.id.remember_password_icon);
		account_edit = (EditText) findViewById(R.id.account_edit);
		password_edit = (EditText) findViewById(R.id.password_edit);
		account_register_edit = (EditText) findViewById(R.id.account_register_edit);
		password_register_edit = (EditText) findViewById(R.id.password_register_edit);
		password_register_edit2 = (EditText) findViewById(R.id.password_register_edit2);
		verify_code_edit = (EditText) findViewById(R.id.verify_code_edit);
		verify_code1 = (TextView) findViewById(R.id.verify_code1);
		verify_code2 = (TextView) findViewById(R.id.verify_code2);
		verify_code3 = (TextView) findViewById(R.id.verify_code3);
		verify_code4 = (TextView) findViewById(R.id.verify_code4);
		title = (TextView) findViewById(R.id.title);
		title_right = (TextView) findViewById(R.id.title_right);
		webview_protocal = (WebView) findViewById(R.id.webview_protocal);
		register_layout = (RelativeLayout) findViewById(R.id.register_layout);
		modify_pwd_layout = (RelativeLayout) findViewById(R.id.modify_pwd_layout);
		netRequest = new NetRequest(this, this);
	}

	public void switchRememberPwd(View view) {

		if (isRememberPwd) {
			remember_password_icon.setImageResource(R.drawable.remember_pwd_no);
			isRememberPwd = false;
		} else {
			remember_password_icon.setImageResource(R.drawable.remember_pwd);
			isRememberPwd = true;
		}
	}

	public void modifyPwd(View view) {
		if (!inModifyPwd) {
			modify_pwd_layout.setVisibility(View.VISIBLE);
			title_right.setVisibility(View.GONE);
			title.setText(getString(R.string.modify_password));
			inModifyPwd = true;
		}
	}

	public void hideModifyPwd() {
		if (inModifyPwd) {
			modify_pwd_layout.setVisibility(View.GONE);
			title_right.setVisibility(View.VISIBLE);
			title.setText(getString(R.string.login));
			inModifyPwd = false;
		}
	}

	public void onBack(View view) {
		if (inModifyPwd) {
			hideModifyPwd();
			return;
		}
		if (inProtocal) {
			hideProtocal();
			return;
		}
		if (!inRegister)
			finish();
		else
			hideRegister(title_right);
	}

	public void login(View view) {

		username = account_edit.getText().toString();
		password = password_edit.getText().toString();
		if (username.length() < 1) {
			Toast.makeText(this, getString(R.string.input_account),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (password.length() < 1) {
			Toast.makeText(this, getString(R.string.input_password),
					Toast.LENGTH_LONG).show();
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", username);
		map.put("password", password);
		netRequest.httpRequest(map, CommonUrl.login);
	}

	public void register(View view) {
		String account_register = account_register_edit.getText().toString();
		String password_register = password_register_edit.getText().toString();
		String password_register2 = password_register_edit2.getText()
				.toString();
		if (account_register.length() < 1) {
			Toast.makeText(this, getString(R.string.input_account),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (password_register.length() < 1) {
			Toast.makeText(this, getString(R.string.input_password),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (!password_register.equals(password_register2)) {
			Toast.makeText(this, getString(R.string.input_password_dismatch),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (!verify_code_edit.getText().toString().equals(codeBuilder.toString())) {
			Toast.makeText(this, getString(R.string.verify_code_dismatch),
					Toast.LENGTH_LONG).show();
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", account_register);
		map.put("password", password_register2);
		map.put("fr", "android");
		netRequest.httpRequest(map, CommonUrl.homeRegister);
	}

	public void rememberAccount() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("password", password);
		CommonUtil.createSP(this, "account", map);
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		Log.d("gaolei", "result-----LoginActivity-----------" + result);
		if (requestUrl.equals(CommonUrl.login)) {
			try {
				JSONObject object = new JSONObject(result);
				String status = object.getString("status");
				String message = object.getString("message");
				String sessionId = null;
				if (object.has("jeesite.session.id")) {
					sessionId = object.getString("jeesite.session.id");
				}
				Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT)
						.show();
				Gson gson = new Gson();
				LoginResultObject loginObject = gson.fromJson(result,
						LoginResultObject.class);
				LoginResultObject.getInstance().status = status;
				LoginResultObject.getInstance().message = message;
				LoginResultObject.getInstance().sessionId = sessionId;
				if (message.equals("登录成功")) {
					if(isRememberPwd)
					rememberAccount();
					Message msg = MyFragment.handler.obtainMessage();
					msg.arg1 = MyFragment.LOGIN;
					MyFragment.handler.sendMessage(msg);
					finish();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(CommonUrl.homeRegister)) {
			try {
				JSONObject object = new JSONObject(result);
				String state = object.getString("state");
				String message = object.getString("message");
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
				hideRegister(register_layout);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub

	}

	public void goToRegister(View view) {
		if (!inRegister) {
			register_layout.setVisibility(View.VISIBLE);
			title.setText(getString(R.string.register));
			title_right.setText(getString(R.string.login));
			inRegister = true;
			createCode();
		} else {
			hideRegister(title_right);
		}
	}

	public void hideRegister(View view) {
		register_layout.setVisibility(View.GONE);
		title.setText(getString(R.string.login));
		title_right.setText(getString(R.string.register));
		inRegister = false;
	}

	public void goToProtocal(View view) {
		WebSettings  webSettings = webview_protocal.getSettings();
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
		webSettings.setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
		webSettings.setSupportZoom(true);//是否可以缩放，默认true
		webSettings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
		webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
		webSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
		webSettings.setAppCacheEnabled(true);//是否使用缓存
		webSettings.setDomStorageEnabled(true);//DOM Storage
		
		webview_protocal.loadUrl(CommonUrl.appagreement);
		webview_protocal.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
		webview_protocal.setVisibility(View.VISIBLE);
		title.setText(getString(R.string.protocal));
		title_right.setVisibility(View.GONE);
		inProtocal = true;
	}

	public void hideProtocal() {
		webview_protocal.setVisibility(View.GONE);
		title.setText(getString(R.string.register));
		title_right.setVisibility(View.VISIBLE);
		inProtocal = false;
	}
	 //验证码  
    private void createCode() {  
        
        for (int i = 0; i < 4; i++) {  
        	String code=CHARS[random.nextInt(CHARS.length)]+"";
        	codeBuilder.append(code);
        	if(i==0){
        		verify_code1.setText(code);
        	}
        	if(i==1){
        		verify_code2.setText(code);
        	}
        	if(i==2){
        		verify_code3.setText(code);
        	}
        	if(i==3){
        		verify_code4.setText(code);
        	}
        	
        }  
    } 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (inModifyPwd) {
				hideModifyPwd();
				return true;
			}
			if (inProtocal) {
				hideProtocal();
				return true;
			}
			if (inRegister) {
				hideRegister(register_layout);
			} else {
				finish();
			}
			return true;
		}
		// 拦截MENU按钮点击事件，让他无任何操作
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
