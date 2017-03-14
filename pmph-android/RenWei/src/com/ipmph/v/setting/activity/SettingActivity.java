package com.ipmph.v.setting.activity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipmph.v.MainActivity;
import com.ipmph.v.R;
import com.ipmph.v.application.APPApplication;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.sdcard.SDCardSelectDialog;
import com.ipmph.v.service.DownLoadService;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;
import com.ipmph.v.tool.FileUtil;

public class SettingActivity extends FragmentActivity implements
		NetRequestIterface {

	private LinearLayout switch_layout1, switch_layout2, switch_layout3;
	private TextView switch_off1, switch_off2, switch_off3, switch_on1,
			switch_on2, switch_on3, cache_capacity;
	private WebView webview_new_ver;
	private boolean flag1 = true, flag2 = false, flag3 = false,
			inDownload = false;
	private NetRequest netRequest;

	// private String download_url =
	// "http://shouji.360tpcdn.com/160329/a9037075b8d3aa98fbf6115c54a5b895/com.alensw.PicFolder_4722404.apk";
	// private String urlthree =
	// "http://s1.music.126.net/download/android/CloudMusic_3.4.1.133604_official.apk";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		netRequest = new NetRequest(this, this);
		switch_off1 = (TextView) findViewById(R.id.switch_off1);
		switch_off2 = (TextView) findViewById(R.id.switch_off2);
		switch_off3 = (TextView) findViewById(R.id.switch_off3);
		switch_on1 = (TextView) findViewById(R.id.switch_on1);
		switch_on2 = (TextView) findViewById(R.id.switch_on2);
		switch_on3 = (TextView) findViewById(R.id.switch_on3);
		cache_capacity = (TextView) findViewById(R.id.cache_capacity);
		try {
			cache_capacity.setText(" ("
					+ FileUtil.getFormatSize(FileUtil.getFolderSize(new File(
							APPApplication.cacheImgPath))) + ")");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		webview_new_ver = (WebView) findViewById(R.id.webview_new_ver);
		webview_new_ver.getSettings().setDomStorageEnabled(true);
		webview_new_ver.getSettings().setJavaScriptEnabled(true);

		String recommend = getSharedPreferences("recommend",
				Activity.MODE_PRIVATE).getString("recommend", "false");
		if (recommend.equals("false")) {
			switch_on1.setVisibility(View.INVISIBLE);
			switch_off1.setVisibility(View.VISIBLE);
		} else {
			switch_on1.setVisibility(View.VISIBLE);
			switch_off1.setVisibility(View.INVISIBLE);

		}
		String mobile_cache = getSharedPreferences("mobile_cache",
				Activity.MODE_PRIVATE).getString("mobile_cache", "false");
		if (mobile_cache.equals("false")) {
			switch_off2.setVisibility(View.VISIBLE);
			switch_on2.setVisibility(View.INVISIBLE);
		} else {
			switch_off2.setVisibility(View.INVISIBLE);
			switch_on2.setVisibility(View.VISIBLE);
		}
		String auto_cache = getSharedPreferences("auto_cache",
				Activity.MODE_PRIVATE).getString("auto_cache", "false");
		if (auto_cache.equals("false")) {
			switch_off3.setVisibility(View.VISIBLE);
			switch_on3.setVisibility(View.INVISIBLE);
		} else {
			switch_off3.setVisibility(View.INVISIBLE);
			switch_on3.setVisibility(View.VISIBLE);
		}
	}

	public void toggleSwitch1(View view) {
		Map<String, String> map = new HashMap<String, String>();
		if (!flag1) {
			switch_off1.setVisibility(View.VISIBLE);
			switch_on1.setVisibility(View.INVISIBLE);
			map.put("recommend", "true");
			CommonUtil.createSP(this, "recommend", map);
			flag1 = true;
		} else {
			switch_off1.setVisibility(View.INVISIBLE);
			switch_on1.setVisibility(View.VISIBLE);
			map.put("recommend", "false");
			CommonUtil.createSP(this, "recommend", map);
			flag1 = false;
		}

	}

	public void toggleSwitch2(View view) {
		Map<String, String> map = new HashMap<String, String>();
		if (!flag2) {
			switch_off2.setVisibility(View.VISIBLE);
			switch_on2.setVisibility(View.INVISIBLE);
			map.put("mobile_cache", "true");
			CommonUtil.createSP(this, "cache", map);
			flag2 = true;
		} else {
			switch_off2.setVisibility(View.INVISIBLE);
			switch_on2.setVisibility(View.VISIBLE);
			map.put("mobile_cache", "false");
			CommonUtil.createSP(this, "mobile_cache", map);
			flag2 = false;
		}

	}

	public void toggleSwitch3(View view) {
		Map<String, String> map = new HashMap<String, String>();
		if (!flag3) {
			switch_off3.setVisibility(View.VISIBLE);
			switch_on3.setVisibility(View.INVISIBLE);
			map.put("auto_cache", "true");
			CommonUtil.createSP(this, "auto_cache", map);
			flag3 = true;
		} else {
			switch_off3.setVisibility(View.INVISIBLE);
			switch_on3.setVisibility(View.VISIBLE);
			map.put("auto_cache", "false");
			CommonUtil.createSP(this, "auto_cache", map);
			flag3 = false;
		}

	}

	public void selectSdcard(View view) {
		SDCardSelectDialog dialog = new SDCardSelectDialog(this, R.style.dialog);
		dialog.show();
	}

	public void clearCache(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
		builder.setTitle("提示"); // 设置内容
		String message = "亲，缓存能节省流量，您确定要清除吗？";
		builder.setMessage(message); // 设置内容
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { // 设置确定按钮
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 关闭dialog
						MainActivity.loadingDialog.show();
						File cacheDir = new File(APPApplication.cacheImgPath);
						for (File file : cacheDir.listFiles()) {
							file.delete();
						}
						Toast.makeText(SettingActivity.this,
								getString(R.string.clear_cache_sucess),
								Toast.LENGTH_LONG).show();
						MainActivity.loadingDialog.cancel();
						CommonUtil.clearSP(SettingActivity.this,
								"DownloadVideoInfo");
						cache_capacity.setVisibility(View.GONE);
					}
				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { // 设置取消按钮
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	public void detectNewVersion(View view) {
		// String versionName= AndroidUtil.getVersionName(this);
		netRequest.httpRequestGet(null, CommonUrl.detectNewVersion);
		MainActivity.loadingDialog.show();
	}

	public void goToDownloadApk(String apkUrl) {
		// CommonUtil.showToast(this, getString(R.string.has_new_version));
		// Uri uri = Uri.parse(apkUrl);
		// Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		// startActivity(intent);
		Intent intent = new Intent(this, DownLoadService.class);
		intent.putExtra("download_url", CommonUrl.baseUrl + apkUrl);
		startService(intent);
	}

	public void onBack(View view) {
		if (inDownload) {
			webview_new_ver.setVisibility(View.GONE);
			inDownload = false;
			return;
		}
		finish();
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		Log.d("gaolei", "result--------SettingActivity--------" + result);
		try {
			JSONObject object = new JSONObject(result);
			JSONObject object2 = object.getJSONObject("data");
			int hasNew = object2.getInt("hasNew");
			final String url = object2.getString("url");
			final String version = object2.getString("ver");
			final String content = object2.getString("content");
			final String date = object2.getString("date");
			if (hasNew == 1) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
				builder.setTitle("更新提示"); // 设置内容
				String message = "新版本：" + version + "\n" + "更新内容：" + content;
				builder.setMessage(message); // 设置内容
				builder.setIcon(R.drawable.download);// 设置图标，图片id即可
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() { // 设置确定按钮
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss(); // 关闭dialog
								if(url==null||url.length()==0)return;
								Intent intent = new Intent(
										SettingActivity.this,
										DownLoadService.class);
								intent.putExtra("download_url",
										CommonUrl.baseUrl + url);
								startService(intent);
							}
						});
				builder.setNegativeButton("稍后",
						new DialogInterface.OnClickListener() { // 设置取消按钮
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			}

			else
				CommonUtil.showToast(this,
						getString(R.string.has_no_new_version));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MainActivity.loadingDialog.cancel();
		}
		MainActivity.loadingDialog.cancel();
	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (inDownload) {
				webview_new_ver.setVisibility(View.GONE);
				inDownload = false;
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
