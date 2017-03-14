package com.ipmph.v.setting.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipmph.v.LoginActivity;
import com.ipmph.v.R;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.fragment.MyFragment;
import com.ipmph.v.object.LoginResultObject;
import com.ipmph.v.object.UserInfoObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;
import com.ipmph.v.tool.HttpUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class UserInfoActivity extends FragmentActivity implements
		NetRequestIterface {

	private NetRequest netRequest;
	private ImageView user_photo;
	private RelativeLayout modify_pwd_layout;
	private boolean inModifyPwd = false;
	private EditText origin_pwd_edit, new_pwd_edit;
	private TextView account_name, register_time;

	private static final int PHOTO_REQUEST_CAMERA = 1;// ����
	private static final int PHOTO_REQUEST_GALLERY = 2;// �������ѡ��
	private static final int PHOTO_REQUEST_CUT = 3;// ���
	private RelativeLayout update_photo_layout, edit_photo_fullscreen_layout,
			edit_photo_outer_layout, uploading_photo_progress;
	private Animation get_photo_layout_out_from_up,
			get_photo_layout_in_from_down;
	private Bitmap bitmap;
	private Bitmap photoBitmap;
	private boolean isChangePhoto;

	/* ͷ������ */
	private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
	private File tempFile;

	private Handler myHandler = new Handler();
//	private Runnable mLoadingRunnable = new Runnable() {
//
//		@Override
//		public void run() {
////			Log.d("gaolei", "userImg-------UserInfoActivity---------"
////					+ UserInfoObject.getInstance().userImg);
////			CommonUtil.getUtilInstance().displayRoundCornerImage(
////					UserInfoObject.getInstance().userImg, user_photo, 120);
//		}
//	};

	public void onCreate(Bundle savedInstanceStage) {
		super.onCreate(savedInstanceStage);
		Log.d("gaolei", "UserInfoActivity-----------onCreate---------");
		setContentView(R.layout.user_info_activity);
		netRequest = new NetRequest(this, this);
		user_photo = (ImageView) findViewById(R.id.user_info_photo);
		origin_pwd_edit = (EditText) findViewById(R.id.origin_pwd_edit);
		new_pwd_edit = (EditText) findViewById(R.id.new_pwd_edit);
		account_name = (TextView) findViewById(R.id.account_name);
		register_time = (TextView) findViewById(R.id.register_time);
		modify_pwd_layout = (RelativeLayout) findViewById(R.id.modify_pwd_layout);

		update_photo_layout = (RelativeLayout) findViewById(R.id.update_photo_layout);
		edit_photo_fullscreen_layout = (RelativeLayout) findViewById(R.id.edit_photo_fullscreen_layout);
		edit_photo_outer_layout = (RelativeLayout) findViewById(R.id.edit_photo_outer_layout);
		uploading_photo_progress = (RelativeLayout) findViewById(R.id.uploading_photo_progress);
		String sessionId = LoginResultObject.getInstance().sessionId;
		getUserInfo(sessionId);
		account_name.setText(UserInfoObject.getInstance().username);
		if (UserInfoObject.getInstance().createDate != null)
			register_time.setText(UserInfoObject.getInstance().createDate
					.split(" ")[0]);
		

//		getWindow().getDecorView().post(new Runnable() {
//			@Override
//			public void run() {
//				myHandler.post(mLoadingRunnable);
//			}
//		});
	}

	public void onBack(View view) {
		if (inModifyPwd) {
			hideModifyPwd();
			return;
		}
		finish();
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		Log.d("gaolei", "result-------UserInfoActivity---------" + result);
		
		if (requestUrl.equals(CommonUrl.getUserInfo)) {
			try {
				JSONObject object = new JSONObject(result);
				String userImg = object.getString("userImg");
//				UserInfoObject.getInstance().userImg = userImg;
//				CommonUtil.getUtilInstance().displayRoundCornerImage(CommonUrl.baseUrl +userImg,
//						user_photo, 120);
				 new DownloadImageTask().execute(CommonUrl.baseUrl +userImg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (requestUrl.equals(CommonUrl.updatePassword)) {
			try {
				JSONObject object = new JSONObject(result);
				String message = object.getString("message");

				if (message.equals("成功")) {
					CommonUtil.showToast(this,
							getString(R.string.modify_password_sucess));
					hideModifyPwd();
					logout(user_photo);
					startActivity(new Intent(this, LoginActivity.class));
				}
				CommonUtil.showToast(this, message);
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

	public void logout(View view) {
		Map<String, Object> map = new HashMap<String, Object>();
		String sessionId = LoginResultObject.getInstance().sessionId;
		Log.d("gaolei", "sessionId-------UserInfoActivity" + "---------"
				+ sessionId);
		map.put("jeesite.session.id", sessionId);
		netRequest.httpRequest(map, CommonUrl.logout);
		LoginResultObject.getInstance().sessionId = null;
		SharedPreferences account = getSharedPreferences("account", 0);
		Editor editor = account.edit();
		editor.clear();
		editor.commit();
		Message msg = MyFragment.handler.obtainMessage();
		msg.arg1 = MyFragment.LOGOUT;
		MyFragment.handler.sendMessage(msg);
		finish();
	}

	public void confirmModifyPwd(View view) {
		String originPwd = origin_pwd_edit.getText().toString();
		String newPwd = new_pwd_edit.getText().toString();
		if (originPwd.length() == 0) {
			CommonUtil.showToast(this, getString(R.string.input_origin_pwd));
			return;
		}
		if (newPwd.length() == 0) {
			CommonUtil.showToast(this, getString(R.string.input_new_pwd));
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("oldPassword", originPwd);
		map.put("password", newPwd);
		netRequest.httpRequestWithID(map, CommonUrl.updatePassword);
	}
	private  void getUserInfo(String sessionId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jeesite.session.id", sessionId);
		if(netRequest!=null){
		netRequest.httpRequest(map, CommonUrl.getUserInfo);
		netRequest.httpRequest(map, CommonUrl.getHomeUser);
		
		}
	}
	public void modifyPassword(View view) {
		inModifyPwd = true;
		modify_pwd_layout.setVisibility(View.VISIBLE);
	}

	private void hideModifyPwd() {
		inModifyPwd = false;
		modify_pwd_layout.setVisibility(View.GONE);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (inModifyPwd) {
				hideModifyPwd();
				return true;
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

	/*
	 * �ϴ�ͼƬ
	 */
	public void upload() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			byte[] buffer = out.toByteArray();

			byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
			String photo = new String(encode);

			RequestParams params = new RequestParams();
			params.put("imgStr", photo);
			String sessionId = LoginResultObject.getInstance().sessionId;

			AsyncHttpClient client = new AsyncHttpClient();
			client.post(CommonUrl.updateUserPhoto+sessionId, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					try {
						if (statusCode == 200) {

							Toast.makeText(UserInfoActivity.this,
									getString(R.string.upload_photo_success), 0)
									.show();
							String sessionId = LoginResultObject.getInstance().sessionId;
							getUserInfo(sessionId);
							isChangePhoto=true;
						} else {
							Toast.makeText(
									UserInfoActivity.this,
									getString(R.string.upload_photo_failure)
											+ statusCode, 0).show();

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(
							UserInfoActivity.this,
							getString(R.string.upload_photo_failure)
									+ statusCode, 0).show();

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * ������ȡ
	 */
	public void gallery(View view) {
		// ����ϵͳͼ�⣬ѡ��һ��ͼƬ
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	/*
	 * �������ȡ
	 */
	public void camera(View view) {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// �жϴ洢���Ƿ�����ã����ý��д洢
		if (hasSdcard()) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
		}
		startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			if (data != null) {
				// �õ�ͼƬ��ȫ·��
				Uri uri = data.getData();
				crop(uri);
			}

		} else if (requestCode == PHOTO_REQUEST_CAMERA) {
			if (hasSdcard()) {
				tempFile = new File(Environment.getExternalStorageDirectory(),
						PHOTO_FILE_NAME);
				crop(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(UserInfoActivity.this, "δ�ҵ��洢�����޷��洢��Ƭ��", 0)
						.show();
			}

		} else if (requestCode == PHOTO_REQUEST_CUT) {
			try {
				bitmap = data.getParcelableExtra("data");
				upload();
				hideEditPhotoLayout(update_photo_layout);
				boolean delete = tempFile.delete();
				System.out.println("delete = " + delete);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void showEditPhotoLayout(View view) {
		edit_photo_fullscreen_layout.setVisibility(View.VISIBLE);
		get_photo_layout_in_from_down = AnimationUtils.loadAnimation(this,
				R.anim.search_layout_in_from_down);
		edit_photo_outer_layout.startAnimation(get_photo_layout_in_from_down);
	}

	public void hideEditPhotoLayout(View view) {
		get_photo_layout_out_from_up = AnimationUtils.loadAnimation(this,
				R.anim.search_layout_out_from_up);
		edit_photo_outer_layout.startAnimation(get_photo_layout_out_from_up);
		get_photo_layout_out_from_up
				.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
						edit_photo_fullscreen_layout.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub
					}
				});
	}

	private void crop(Uri uri) {
		// �ü�ͼƬ��ͼ
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// �ü���ı�����1��1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// �ü������ͼƬ�ĳߴ��С
		intent.putExtra("outputX", 250);
		intent.putExtra("outputY", 250);
		// ͼƬ��ʽ
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);// ȡ������ʶ��
		intent.putExtra("return-data", true);// true:������uri��false������uri
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
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
        protected void onPostExecute(String  result) {
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
	public void onStart() {
		super.onStart();
		Log.d("gaolei", "UserInfoActivity-----------onStart---------");
	}
	public void onResume() {
		super.onResume();
		Log.d("gaolei", "UserInfoActivity-----------onResume---------");
	}
	public void onPause() {
		super.onPause();
		Log.d("gaolei", "UserInfoActivity-----------onPause---------");
	}
	public void onStop(){
		super.onStop();
		Log.d("gaolei", "UserInfoActivity-----------onStop--");
		if(isChangePhoto){
			MyFragment.changePhoto=true;
		}else{
			MyFragment.changePhoto=false;
		}
	}
	public void onDestroy(){
		super.onDestroy();
		Log.d("gaolei", "UserInfoActivity-----------onDestroy--");
	}
}
