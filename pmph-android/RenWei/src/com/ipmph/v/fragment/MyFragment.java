package com.ipmph.v.fragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipmph.v.LoginActivity;
import com.ipmph.v.R;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.object.LoginResultObject;
import com.ipmph.v.object.UserInfoObject;
import com.ipmph.v.setting.activity.AlbumPromptActivity;
import com.ipmph.v.setting.activity.CacheActivity;
import com.ipmph.v.setting.activity.MessageActivity;
import com.ipmph.v.setting.activity.SettingActivity;
import com.ipmph.v.setting.activity.UserInfoActivity;
import com.ipmph.v.setting.activity.VideoCollectActivity;
import com.ipmph.v.setting.activity.WatchRecordActivity;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;
import com.ipmph.v.tool.HttpUtils;

public class MyFragment extends Fragment implements NetRequestIterface,
		OnClickListener {
	public static int gaolei = 0, zhaoyue = 0;

	private RelativeLayout play_record_layout, setting_layout, person_layout,
			cache_layout, album_prompt_layout, video_collect_layout,
			message_layout;
	private boolean isLogin = false;
	private static NetRequest netRequest;
	private static ImageView user_photo;
	public static final int LOGIN = 1;
	public static final int LOGOUT = 2;
	private static TextView click_login;
	private Bitmap photoBitmap;
	public static boolean changePhoto = false;

	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case LOGIN:
				String sessionId = LoginResultObject.getInstance().sessionId;
				getUserInfo(sessionId);
				break;
			case LOGOUT:
				user_photo.setImageResource(R.drawable.default_photo);
				click_login.setText("点击登录\n登录后您可以享受更多特权");
				break;
			}
		}
	};

	public static void changeGaoLei(int arg) {
		gaolei = arg;
	}
	public void onResume() {
		super.onResume();
		Log.d("gaolei", "MyFragment-----------onResume---------");
	}
	public void onPause() {
		super.onPause();
		Log.d("gaolei", "MyFragment-----------onPause---------");
	}
	public void onStop(){
		super.onStop();
		Log.d("gaolei", "MyFragment-----------onStop--");
	}
	public void onDestroy(){
		super.onDestroy();
		Log.d("gaolei", "MyFragment-----------onDestroy--");
	}
	public void onStart() {
		super.onStart();
		Log.d("gaolei", "MyFragment-----------onStart---------");
//		if (changePhoto) {
			String sessionId = LoginResultObject.getInstance().sessionId;
			getUserInfo(sessionId);
//			changePhoto=false;
//		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.my_fragment, null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		netRequest = new NetRequest(this, getActivity());
		play_record_layout = (RelativeLayout) view
				.findViewById(R.id.play_record_layout);
		setting_layout = (RelativeLayout) view
				.findViewById(R.id.setting_layout);
		person_layout = (RelativeLayout) view.findViewById(R.id.person_layout);
		cache_layout = (RelativeLayout) view.findViewById(R.id.cache_layout);
		album_prompt_layout = (RelativeLayout) view
				.findViewById(R.id.album_prompt_layout);
		video_collect_layout = (RelativeLayout) view
				.findViewById(R.id.video_collect_layout);
		message_layout = (RelativeLayout) view
				.findViewById(R.id.message_layout);
		user_photo = (ImageView) view.findViewById(R.id.user_photo);
		click_login = (TextView) view.findViewById(R.id.click_login);
		play_record_layout.setOnClickListener(this);
		setting_layout.setOnClickListener(this);
		person_layout.setOnClickListener(this);
		cache_layout.setOnClickListener(this);
		album_prompt_layout.setOnClickListener(this);
		video_collect_layout.setOnClickListener(this);
		message_layout.setOnClickListener(this);
		String sessionId = LoginResultObject.getInstance().sessionId;
		Log.d("gaolei", "sessionId-------MyFragment---------" + sessionId);
		if (sessionId == null) {
			click_login.setText(getString(R.string.click_login));
			return;
		}
		getUserInfo(sessionId);
	}

	private static void getUserInfo(String sessionId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jeesite.session.id", sessionId);
		if (netRequest != null) {
			netRequest.httpRequest(map, CommonUrl.getUserInfo);
			netRequest.httpRequest(map, CommonUrl.getHomeUser);

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		String sessionId = LoginResultObject.getInstance().sessionId;
		switch (v.getId()) {
		case R.id.person_layout:
			Log.d("gaolei", "sessionId-------MyFragment---------"
					+ LoginResultObject.getInstance().sessionId);
			if (LoginResultObject.getInstance().sessionId == null) {
				intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), UserInfoActivity.class);
				// intent = new Intent(getActivity(),
				// UploadPhotoActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.play_record_layout:
			if (sessionId == null) {
				Toast.makeText(getActivity(), getString(R.string.please_login),
						Toast.LENGTH_LONG).show();
				intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				return;
			}
			intent = new Intent(getActivity(), WatchRecordActivity.class);
			startActivity(intent);
			break;
		case R.id.cache_layout:
			intent = new Intent(getActivity(), CacheActivity.class);
			intent.putExtra("mVideoPath", "");
			startActivity(intent);
			break;
		case R.id.album_prompt_layout:
			if (sessionId == null) {
				Toast.makeText(getActivity(), getString(R.string.please_login),
						Toast.LENGTH_LONG).show();
				intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				return;
			}
			intent = new Intent(getActivity(), AlbumPromptActivity.class);
			startActivity(intent);
			break;
		case R.id.video_collect_layout:
			if (sessionId == null) {
				Toast.makeText(getActivity(), getString(R.string.please_login),
						Toast.LENGTH_LONG).show();
				intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				return;
			}
			intent = new Intent(getActivity(), VideoCollectActivity.class);
			startActivity(intent);
			break;
		case R.id.message_layout:
			if (sessionId == null) {
				Toast.makeText(getActivity(), getString(R.string.please_login),
						Toast.LENGTH_LONG).show();
				intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				return;
			}
			intent = new Intent(getActivity(), MessageActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_layout:

			intent = new Intent(getActivity(), SettingActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		if (requestUrl.equals(CommonUrl.getUserInfo)) {
			try {
				JSONObject object = new JSONObject(result);
				String userImg = object.getString("userImg");
				// UserInfoObject.getInstance().userImg = userImg;
				// CommonUtil.getUtilInstance().displayRoundCornerImage(CommonUrl.baseUrl
				// +userImg,
				// user_photo, 120);
				new DownloadImageTask().execute(CommonUrl.baseUrl + userImg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(CommonUrl.getHomeUser)) {
			try {
				JSONObject objct = new JSONObject(result);
				// .getString("homeUser");
				JSONObject object2 = objct.getJSONObject("homeUser");
				String createDate = object2.getString("createDate");
				String username = object2.getString("loginName");
				UserInfoObject.getInstance().username = username;
				UserInfoObject.getInstance().createDate = createDate;
				click_login.setText(username);
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

	class DownloadImageTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			System.out.println("[downloadImageTask->]doInBackground "
					+ params[0]);
			photoBitmap = HttpUtils.getNetWorkBitmap(params[0]);
			return "";
		}

		// 下载完成回调
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			user_photo.setImageBitmap(photoBitmap);
			System.out.println("result = " + result);
			super.onPostExecute(result);
		}

		// 更新进度回调
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}
}
