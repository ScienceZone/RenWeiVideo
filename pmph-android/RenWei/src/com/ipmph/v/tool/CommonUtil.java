package com.ipmph.v.tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipmph.v.R;
import com.ipmph.v.VideoPlayerActivity;
import com.ipmph.v.custom.CToast;
import com.ipmph.v.object.LoginResultObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class CommonUtil {
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	public static CommonUtil util;
	public static InputMethodManager imm;
	public static int BASE_SCREEN_WIDTH;
	public static int BASE_SCREEN_HEIGHT;
	public static float DENSITY;

	public CommonUtil(Context context) {
		imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	public CommonUtil() {
	}

	public static CommonUtil getUtilInstance() {
		if (util == null) {
			util = new CommonUtil();
		}
		return util;
	}

	public void displayRoundCornerImage(String url, ImageView view, int angle) {
		if (view == null) {
			return;
		}
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				// 锟斤拷锟斤拷锟节达拷锟斤拷锟斤拷锟绞碉拷诓锟斤拷校锟斤拷锟斤拷园锟絚acheInMemory(false)
				.cacheOnDisc(true).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565)// 锟斤拷止锟节达拷锟斤拷锟斤拷模锟酵计拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�锟斤拷Bitmap.Config.ARGB_8888
				.showStubImage(R.drawable.default_photo) // 锟斤拷锟截匡拷始默锟较碉拷图片
				// .showImageForEmptyUri(R.drawable.kedou)
				// url锟斤拷锟秸曪拷锟斤拷示锟斤拷图片锟斤拷锟皆硷拷锟斤拷锟斤拷drawable锟斤拷锟斤拷锟�
				// .showImageOnFail(R.drawable.k2k2k2k)// 锟斤拷锟斤拷失锟斤拷锟斤拷示锟斤拷图片
				.displayer(new RoundedBitmapDisplayer(angle)) // 圆锟角ｏ拷锟斤拷锟斤拷要锟斤拷删锟斤拷
				.build();
		// mImageLoader.displayImage(CommenUrl.imageUrl + url, view);
		mImageLoader.displayImage(CommonUrl.baseUrl + url, view, options);
		Log.d("gaolei", "displayRoundCorner20Image---------------");
	}

	public void displayOriginalImage(String url, ImageView view) {
		if (view == null) {
			return;
		}
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				// 锟斤拷锟斤拷锟节达拷锟斤拷锟斤拷锟绞碉拷诓锟斤拷校锟斤拷锟斤拷园锟絚acheInMemory(false)
				.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.ARGB_8888)// 锟斤拷止锟节达拷锟斤拷锟斤拷模锟酵计拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�锟斤拷Bitmap.Config.ARGB_8888
				.showStubImage(R.drawable.image_show_default) // 锟斤拷锟截匡拷始默锟较碉拷图片
				.build();
		// mImageLoader.displayImage(CommenUrl.imageUrl + url, view);
		mImageLoader.displayImage(url, view, options);
	}

	public void displayLowQualityInImage(String url, ImageView view) {
		if (view == null) {
			return;
		}
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				// 锟斤拷锟斤拷锟节达拷锟斤拷锟斤拷锟绞碉拷诓锟斤拷校锟斤拷锟斤拷园锟絚acheInMemory(false)
				.cacheOnDisc(true).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565)// 锟斤拷止锟节达拷锟斤拷锟斤拷模锟酵计拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�锟斤拷Bitmap.Config.ARGB_8888
				.showStubImage(R.drawable.default_img_horizontal) // 锟斤拷锟截匡拷始默锟较碉拷图片
				.build();
		mImageLoader.displayImage(url, view, options);
	}

	public void displayLowQualityInImageVertical(String url, ImageView view) {
		if (view == null) {
			return;
		}
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				// 锟斤拷锟斤拷锟节达拷锟斤拷锟斤拷锟绞碉拷诓锟斤拷校锟斤拷锟斤拷园锟絚acheInMemory(false)
				.cacheOnDisc(true).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565)// 锟斤拷止锟节达拷锟斤拷锟斤拷模锟酵计拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�锟斤拷Bitmap.Config.ARGB_8888
				.showStubImage(R.drawable.default_img_vertical) // 锟斤拷锟截匡拷始默锟较碉拷图片
				.build();
		mImageLoader.displayImage(url, view, options);
	}

	public void displayCircleImage(String url, ImageView view, String str) {
		if (view == null) {
			return;
		}
		if (str.equals("photo")) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.showImageOnLoading(R.drawable.default_photo).build();
			mImageLoader.displayImage(CommonUrl.baseUrl + url, view, options);
		} else {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.showImageOnLoading(R.drawable.image_show_default).build();
			mImageLoader.displayImage(CommonUrl.baseUrl + url, view, options);
		}
	}

	public static boolean isConnectingToInternet(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager != null) {
			NetworkInfo[] info = manager.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++) {
					System.out.println(info[i].getState());
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
		}
		return false;
	}

	public static boolean isMobile(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}

	public static void showCustomToast(Context context, String string) {
		CToast toast = CToast.makeText(context, string, CToast.LENGTH_SHORT);
		toast.show();
	}

	public static void showToast(Context context, String string) {
		Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void showLongToast(Context context, String string) {
		Toast toast = Toast.makeText(context, string, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void showLongCustomToast(Context context, String string) {
		CToast toast = CToast.makeText(context, string, CToast.LENGTH_LONG);
		toast.show();
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public static int px2dp(Context context, int px) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	public static void createSP(Context context, String name,
			Map<String, String> map) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				name, Activity.MODE_PRIVATE);
		// 实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		for (Map.Entry<String, String> entry : map.entrySet()) {
			editor.putString(entry.getKey(), entry.getValue());
		}
		// 提交当前数据
		editor.commit();
	}

	public static String readSP(Context context, String name, String key) {
		return context.getSharedPreferences(name, 0).getString(key, "");
	}
	public static void clearSP(Context context, String name) {
		context.getSharedPreferences(name, 0).edit().clear().commit();
	}

	public static void deleteKey(Context context, String name, String key) {
		context.getSharedPreferences(name, 0).edit().remove(key).commit();
	}

	public static Dialog createLoadingDialog(Context context) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.layout_loading_dialog, null); // 得到加载view
		// LinearLayout layout = (LinearLayout)
		// v.findViewById(R.id.dialog_view); // 加载布局
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog); // 创建自定义样式dialog
		loadingDialog.setCancelable(false); // 不可以用"返回键"取消
		loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		return loadingDialog;
	}

	public static void changeTextDrawable(Context context, TextView textView,
			String location, int drawableId, int stringId, String color) {
		Drawable video_cancel = context.getResources().getDrawable(drawableId);
		video_cancel.setBounds(0, 0, video_cancel.getMinimumWidth(),
				video_cancel.getMinimumHeight());
		if (location.equals("left"))
			textView.setCompoundDrawables(video_cancel, null, null, null);
		if (location.equals("top"))
			textView.setCompoundDrawables(null, video_cancel, null, null);
		if (color != null)
			textView.setTextColor(Color.parseColor(color));
		if (stringId != -1)
			textView.setText(context.getString(stringId));
	}

	public static void jumpToPlayerActivity(Context context,
			Map<String, String> map) {
		Intent intent = new Intent(context, VideoPlayerActivity.class);
		for (Entry<String, String> entry : map.entrySet()) {
			intent.putExtra(entry.getKey(), entry.getValue());
		}
		context.startActivity(intent);
	}

	// public static void videoPlay(VideoView videoView,String videoUrl){
	// videoView.setVideoPath(videoUrl);
	// videoView.start();
	// }
	public static String transformMillisToDate(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm",
		// Locale.CHINA);
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm",
				Locale.CHINA);
		return format.format(calendar.getTime());
	}

	public final static void updateScreenDimension(Context context) {
		final DisplayMetrics m = context.getResources().getDisplayMetrics();

		Configuration configuration = context.getResources().getConfiguration(); // 获取设置的配置信息
		if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			BASE_SCREEN_WIDTH = m.heightPixels;
			BASE_SCREEN_HEIGHT = m.widthPixels;
		} else {
			BASE_SCREEN_WIDTH = m.widthPixels;
			BASE_SCREEN_HEIGHT = m.heightPixels;
		}
		DENSITY = m.density;
	}

	public static void judgeIfLogined(Context context) {
		String sessionId = LoginResultObject.getInstance().sessionId;
		if (sessionId == null) {
			Toast.makeText(context, context.getString(R.string.please_login),
					Toast.LENGTH_LONG).show();
			return;
		}
	}
}