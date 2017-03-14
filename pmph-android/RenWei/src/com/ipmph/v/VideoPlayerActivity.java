//=======================================================================================
//点量软件，致力于做最专业的通用软件库，节省您的开发时间
//
//	Copyright:	Copyright (c) Dolit.cn
//  版权所有：	点量软件有限公司
//              如果您是个人作为非商业目的使用，您可以自由、免费的使用点量软件库和演示程序，
//              也期待收到您反馈的意见和建议，共同改进点量产品
//              如果您是商业使用，那么您需要联系作者申请产品的商业授权。
//              点量软件库所有演示程序的代码对外公开，软件库的代码只限付费用户个人使用。
//  官方网站：  http://www.dolit.cn   http://blog.dolit.cn
//
//=======================================================================================

package com.ipmph.v;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.dolit.media.player.widget.VideoView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ipmph.v.adapter.AlbumCountAdapter;
import com.ipmph.v.adapter.AlbumRelativeAdapter;
import com.ipmph.v.adapter.AlbumSeriesAdapter;
import com.ipmph.v.adapter.LabelAdapter;
import com.ipmph.v.adapter.LabelAlbumAdapter;
import com.ipmph.v.adapter.ResolutionAdapter;
import com.ipmph.v.adapter.VideoRelativeAdapter;
import com.ipmph.v.application.APPApplication;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.multidownload.entitis.FileInfo;
import com.ipmph.v.object.AlbumDetailObject;
import com.ipmph.v.object.AlbumDetailObject.AlbumContentObject.AlbumVideoObject;
import com.ipmph.v.object.AlbumDetailObject.AlbumContentObject.VideoClassObject;
import com.ipmph.v.object.AlbumDetailObject.AlbumOtherVideoObject;
import com.ipmph.v.object.LoginResultObject;
import com.ipmph.v.object.VideoDetailObject;
import com.ipmph.v.object.VideoDetailObject.OtherVideoObject;
import com.ipmph.v.object.VideoDetailObject.VideoPlayObject.VideoPathObject;
import com.ipmph.v.setting.activity.CacheActivity;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class VideoPlayerActivity extends Activity implements OnClickListener,
		OnTouchListener, NetRequestIterface {
	private VideoView mPlayer;
	private View mBufferingIndicator;
	private RelativeLayout videoview_layout, mMediaController;
	private boolean isPortraint = true;
	private ImageView switch_screen, mediacontroller_play_pause;
	private TextView expand_detail, resolution_switch;
	private int screenWidth, statusBarHeight, videoViewHeight;
	private static int LockScreen = -1;// 用于记录是否关闭屏幕旋转，0为关闭1为开启
	private SeekBar progress_seekbar;
	private TextView mFileName, album_video_name, mEndTime, mCurrentTime,
			plays, collect, add_watch_list, download, hot_video, have_update,
			label, video_play_detail, relative_album;
	private boolean mShowing = true, mDragging;
	private static final int sDefaultTimeout = 3000;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;
	private static final int CHANGE_VIDEOVIEW_BG = 3;
	private AudioManager mAM;
	private long mDuration;
	private NetRequest netRequest;
	private GridView relative_gridview, hot_gridview, album_count_gridview,
			label_gridview;
	private ListView album_series_listview, resolution_listview;
	private boolean isPlaying = false, isBigScreen = false;
	private View divider2;
	private AlbumSeriesAdapter seriesAdapter;
	private ArrayList<AlbumVideoObject> albumVideoList;
	private LinearLayout album_layout;
	private RelativeLayout video_detail_layout;
	private boolean isDetailExpand = false, isResolution = false;
	private String albumVideoID, videoID, collectionID, seelistID, mVideoPath,
			video_detail, video_name, videoImgUrl, albumName;
	private long currentPosition = 0;
	private int albumCountPosition = 0;
	private Map<Integer, Integer> albumVideoMap = new HashMap<Integer, Integer>();
	private AlbumVideoObject videoObject;
	private String localFileUrl;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			long pos;
			switch (msg.what) {
			case FADE_OUT:
				showOrHideController();
				break;
			case SHOW_PROGRESS:
				pos = setControllerProgress();
				if (!mDragging && mShowing) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
				}
				break;
			case CHANGE_VIDEOVIEW_BG:
				mPlayer.setBackgroundColor(0x00000000);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_player_activity);
		Log.d("gaolei", "onCreate--------------");
		netRequest = new NetRequest(this, this);
		screenWidth = APPApplication.screenWidth;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = getResources().getDimensionPixelSize(resourceId);
		}
		videoViewHeight = screenWidth * 9 / 16;

		try {
			// 1代表开启自动旋转true，0代表未开启自动旋转false
			// Settings.System.getInt(mContext.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,0);
			LockScreen = Settings.System.getInt(getContentResolver(),
					Settings.System.ACCELEROMETER_ROTATION);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initViews();
		mHandler.sendEmptyMessageDelayed(CHANGE_VIDEOVIEW_BG, 400);
	}

	public void onRestart() {
		super.onRestart();
		mPlayer.start();
		// Log.d("gaolei", "VideoPlayerActivity-------onReStart--------------");
		Log.d("gaolei", "onRestart-------currentPosition----" + currentPosition);
		// Log.d("gaolei", "VideoPlayerActivity-------localFileUrl----"
		// + localFileUrl);
		Log.d("gaolei", "onRestart-------isPortraint----" + isPortraint);
		Log.d("gaolei", "onRestart-------isBigScreen----" + isBigScreen);
		if (isBigScreen)
			autoChangeToFullScreen();
		// else
		// handToSmallScreen();
		if (localFileUrl == null)
			mPlayer.setVideoPath(CommonUrl.baseUrl + mVideoPath);
		else
			mPlayer.setVideoPath(localFileUrl);
		mPlayer.seekTo(currentPosition);

	}

	public void onStop() {
		super.onStop();
		currentPosition = mPlayer.getCurrentPosition();
		Log.d("gaolei", "VideoPlayerActivity-------onStop--------------");
		Log.d("gaolei", "VideoPlayerActivity---onStop----currentPosition--"
				+ currentPosition);
		Log.d("gaolei", "VideoPlayerActivity---onStop----isPortraint--"
				+ isPortraint);
		if (!isPortraint)
			isBigScreen = true;
		mPlayer.pause();
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d("gaolei", "VideoPlayerActivity-------onDestroy--------------");
	}

	// @Override
	// protected void onNewIntent(Intent intent) {
	// Log.d("gaolei", "---------onNewIntent--------------");
	// super.onNewIntent(intent);
	// video_detail_layout.setVisibility(View.GONE);
	// enterPlayerActivity(intent);
	// }

	private void initViews() {
		mAM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mPlayer = (VideoView) findViewById(R.id.video_view);
		videoview_layout = (RelativeLayout) findViewById(R.id.videoview_layout);
		mMediaController = (RelativeLayout) findViewById(R.id.media_controller);
		mMediaController.setVisibility(View.GONE);
		mBufferingIndicator = findViewById(R.id.buffering_indicator);
		mEndTime = (TextView) findViewById(R.id.mediacontroller_time_total);
		mCurrentTime = (TextView) findViewById(R.id.mediacontroller_time_current);
		mFileName = (TextView) findViewById(R.id.mediacontroller_file_name);
		album_video_name = (TextView) findViewById(R.id.album_video_name);
		collect = (TextView) findViewById(R.id.collect);
		add_watch_list = (TextView) findViewById(R.id.add_watch_list);
		plays = (TextView) findViewById(R.id.plays);
		download = (TextView) findViewById(R.id.download);
		label = (TextView) findViewById(R.id.label);
		video_play_detail = (TextView) findViewById(R.id.video_play_detail);
		relative_album = (TextView) findViewById(R.id.relative_album);
		hot_video = (TextView) findViewById(R.id.hot_video);
		have_update = (TextView) findViewById(R.id.have_update);
		divider2 = (View) findViewById(R.id.divider2);
		switch_screen = (ImageView) findViewById(R.id.switch_screen);
		mediacontroller_play_pause = (ImageView) findViewById(R.id.mediacontroller_play_pause);
		expand_detail = (TextView) findViewById(R.id.expand_detail);
		resolution_switch = (TextView) findViewById(R.id.resolution_switch);
		expand_detail.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				expandDetail();
				return false;
			}
		});
		album_layout = (LinearLayout) findViewById(R.id.album_layout);
		video_detail_layout = (RelativeLayout) findViewById(R.id.video_detail_layout);
		progress_seekbar = (SeekBar) findViewById(R.id.mediacontroller_seekbar);
		relative_gridview = (GridView) findViewById(R.id.relative_gridview);
		hot_gridview = (GridView) findViewById(R.id.hot_gridview);
		album_count_gridview = (GridView) findViewById(R.id.album_count_gridview);
		label_gridview = (GridView) findViewById(R.id.label_gridview);
		album_series_listview = (ListView) findViewById(R.id.album_series_listview);
		resolution_listview = (ListView) findViewById(R.id.resolution_listview);

		mPlayer.setMediaBufferingIndicator(mBufferingIndicator);
		LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, videoViewHeight);
		videoview_layout.setLayoutParams(params);
		mPlayer.setOnTouchListener(this);
		// expand_detail.setOnTouchListener(this);
		switch_screen.setOnClickListener(this);
		mMediaController.setOnClickListener(this);
		mediacontroller_play_pause.setOnClickListener(this);
		progress_seekbar.setOnSeekBarChangeListener(mSeekListener);

		Intent intent = getIntent();
		localFileUrl = intent.getStringExtra("localFileUrl");
		videoImgUrl = intent.getStringExtra("videoImgUrl");

		if (localFileUrl != null) {
			if (localFileUrl.length() > 0) {
				String localFileName = intent.getStringExtra("localFileName");
				plays.setVisibility(View.GONE);
				download.setVisibility(View.GONE);
				video_detail_layout.setVisibility(View.GONE);
				resolution_switch.setVisibility(View.GONE);
				mFileName.setText(localFileName);
				mPlayer.setVideoPath(localFileUrl);
				mPlayer.start();
			}
		} else {
			videoID = intent.getStringExtra("videoID");
			String videoname = intent.getStringExtra("videoname");
			albumVideoID = intent.getStringExtra("albumVideoID");
			String albumVideoname = intent.getStringExtra("albumVideoname");
			enterPlayerActivity(videoID, videoname, albumVideoID,
					albumVideoname);
		}

	}

	private void enterPlayerActivity(String videoID, String videoname,
			String albumVideoID, String albumVideoname) {
		currentPosition = 0;
		mPlayer.pause();
		this.videoID = videoID;
		this.albumVideoID = albumVideoID;
		if (videoID != null) {
			if (videoname != null)
				album_video_name.setText(videoname);
			relative_album.setText(getString(R.string.relative_video));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("videoID", videoID);
			netRequest.httpRequestWithID(map, CommonUrl.getVideo);

		} else {
			if (albumVideoname != null) {
				album_video_name.setText(albumVideoname);
				albumName = albumVideoname;
			}
			relative_album.setText(getString(R.string.relative_album));
			add_watch_list.setVisibility(View.VISIBLE);
			Log.d("gaolei", "albumVideoID----------------" + albumVideoID);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("albumvideoid", albumVideoID);
			netRequest.httpRequestWithID(map, CommonUrl.getAlbumVideo);
		}
	}

	private void playVideo(final String videoPath) {
		mVideoPath = videoPath;
		Log.d("gaolei", "CommonUrl.baseUrl--videoPath-----------"
				+ CommonUrl.baseUrl + videoPath);
		if (CommonUtil.isMobile(VideoPlayerActivity.this)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
			builder.setTitle("提示"); // 设置标题
			builder.setMessage("您现在用的是手机流量，是否继续播放？"); // 设置内容
			builder.setIcon(R.drawable.prompt_icon);// 设置图标，图片id即可
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() { // 设置确定按钮
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (currentPosition == 0) {
								mPlayer.setVideoPath(CommonUrl.baseUrl
										+ videoPath);
								mPlayer.start();
							} else if (currentPosition > 0) {
								mPlayer.seekTo(currentPosition);
							}
							dialog.dismiss(); // 关闭dialog
						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() { // 设置取消按钮
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
							dialog.dismiss();
						}
					});
			builder.create().show();
		} else {
			Log.d("gaolei", "currentPosition---------2------" + currentPosition);
			if (currentPosition == 0) {
				mPlayer.setVideoPath(CommonUrl.baseUrl + videoPath);
				mPlayer.start();
			} else if (currentPosition > 0) {
				mPlayer.seekTo(currentPosition);
			}
		}
		Log.d("gaolei", "videoID---------playVideo------" + videoID);
		Log.d("gaolei", "albumVideoID-------playVideo--------" + albumVideoID);
		Map<String, Object> watchMap = new HashMap<String, Object>();
		watchMap.put("videoID", videoID);
		if (albumVideoID != null) {
			watchMap.put("watchrecordType", 0);
			watchMap.put("albumVideoID", albumVideoID);
		} else {
			watchMap.put("watchrecordType", 1);
		}
		netRequest.httpRequestWithID(watchMap, CommonUrl.addWatchRecord);
	}

	public void showOrHideController() {

		if (mShowing) {
			mHandler.removeMessages(SHOW_PROGRESS);
			mHandler.removeMessages(FADE_OUT);
			mMediaController.setVisibility(View.GONE);
			resolution_listview.setVisibility(View.GONE);
			isResolution = false;
			mShowing = false;
		} else {
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
			mMediaController.setVisibility(View.VISIBLE);
			hideMediaController(sDefaultTimeout);
			mShowing = true;
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			autoChangeToFullScreen();
			Log.d("gaolei", "ORIENTATION_LANDSCAPE-------------");
		}
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			autoChangeToSmallScreen();
			Log.d("gaolei", "ORIENTATION_PORTRAIT-------------");
		}
	}

	public void switchScreen(View view) {
		if (isPortraint) {
			handToFullScreen();
		} else {
			handToSmallScreen();
		}
	}

	public void handToSmallScreen() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		autoChangeToSmallScreen();
		/**
		 * 这里点击按钮转屏，用户2秒内不转屏幕，将自动识别当前屏幕方向
		 */
		autoSwitchScreenOrientation();

	}

	public void handToFullScreen() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		autoChangeToFullScreen();
		autoSwitchScreenOrientation();
	}

	private void autoChangeToFullScreen() {
		isPortraint = false;
		isBigScreen = false;
		LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, APPApplication.screenWidth);
		videoview_layout.setLayoutParams(params);
		mPlayer.setLayoutParams(params);
		Log.d("gaolei", "APPApplication.screenHeight----------------"
				+ APPApplication.screenHeight);
		Log.d("gaolei", " APPApplication.screenWidth----------------"
				+ APPApplication.screenWidth);
		Log.d("gaolei",
				"mPlayer.getHeight()----------------" + mPlayer.getHeight());
		Log.d("gaolei",
				"mPlayer.getWidth()----------------" + mPlayer.getWidth());
		WindowManager.LayoutParams windowparams = getWindow().getAttributes();
		windowparams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setAttributes(windowparams);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		switch_screen.setImageResource(R.drawable.player_switch_small);

	}

	private void autoChangeToSmallScreen() {
		isPortraint = true;
		LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, videoViewHeight);
		videoview_layout.setLayoutParams(params);
		mPlayer.setLayoutParams(params);
		WindowManager.LayoutParams windowparams = getWindow().getAttributes();
		windowparams.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setAttributes(windowparams);
		getWindow()
				.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		switch_screen.setImageResource(R.drawable.player_switch_big);

	}

	public void autoSwitchScreenOrientation() {
		if (LockScreen == 1) {
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
				}
			}, 1500);
		}
	}

	public void onBack(View view) {
		finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!isPortraint) {
				handToSmallScreen();
			} else {
				Log.d("gaolei", "onKeyDown---------------");
				mPlayer.setBackgroundColor(0x000000);
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.switch_screen:
			if (isPortraint)
				handToFullScreen();
			else
				handToSmallScreen();
			break;
		case R.id.mediacontroller_play_pause:
			updatePausePlay();
			break;
		case R.id.media_controller:
			showOrHideController();
			break;
		case R.id.expand_detail:
			expandDetail();
			break;
		}
	}

	private void updatePausePlay() {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
			mediacontroller_play_pause
					.setImageResource(R.drawable.player_pause);
		} else {
			mPlayer.start();
			mediacontroller_play_pause.setImageResource(R.drawable.player_play);
		}
	}

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			mDragging = true;
			mHandler.removeMessages(SHOW_PROGRESS);
			mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
		}

		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser)
				return;

			long newposition = (mDuration * progress) / 1000;

			String time = generateTime(newposition);
			mPlayer.seekTo(newposition);
			mCurrentTime.setText(time);
		}

		public void onStopTrackingTouch(SeekBar bar) {
			mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
			hideMediaController(sDefaultTimeout);
			mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
			mDragging = false;
			mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
		}
	};

	public void hideMediaController(int sDefaultTimeout) {
		mHandler.sendEmptyMessageDelayed(FADE_OUT, sDefaultTimeout);
	}

	private static String generateTime(long position) {
		int totalSeconds = (int) (position / 1000);

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;
		if (hours > 0) {
			return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
					seconds).toString();
		} else {
			return String.format(Locale.US, "%02d:%02d", minutes, seconds)
					.toString();
		}
	}

	private long setControllerProgress() {
		if (mPlayer == null || mDragging)
			return 0;

		int position = mPlayer.getCurrentPosition();
		int duration = mPlayer.getDuration();
		if (progress_seekbar != null) {
			if (duration > 0) {
				long pos = 1000L * position / duration;
				// Log.d("gaolei", "progress--------------" + pos);
				progress_seekbar.setProgress((int) pos);
				currentPosition = position;
			}
			int percent = mPlayer.getBufferPercentage();
			progress_seekbar.setSecondaryProgress(percent * 10);
		}

		mDuration = duration;
		if (mEndTime != null)
			mEndTime.setText("/" + generateTime(mDuration));
		if (mCurrentTime != null)
			mCurrentTime.setText(generateTime(position));

		return position;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			showOrHideController();
			break;
		}
		}
		return false;
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		Log.d("gaolei", "result---------VideoPlayerActivity-----------"
				+ result);

		if (requestUrl.equals(CommonUrl.getVideo)) {
			try {
				final VideoDetailObject object = new Gson().fromJson(result,
						VideoDetailObject.class);
				album_layout.setVisibility(View.GONE);
				video_name = object.video.videoname;
				collectionID = object.video.collectionID;
				mFileName.setText(object.video.videoname);
				videoImgUrl = CommonUrl.baseUrl + object.video.videoImgUrl
						+ ".small" + object.video.suffix;
				plays.setText(object.video.videopnumber + "");
				ArrayList<VideoPathObject> videoPathList = object.video.videopathList;
				playVideo(videoPathList.get(videoPathList.size() - 1).videoUrl);
				switchResolution(videoPathList);
				if (object.video.iscollection == 1) {
					CommonUtil.changeTextDrawable(this, collect, "top",
							R.drawable.video_collect, R.string.collected, null);
				} else {
					CommonUtil
							.changeTextDrawable(this, collect, "top",
									R.drawable.video_collect_no,
									R.string.collect, null);
				}
				final LabelAdapter labelAdapter = new LabelAdapter(
						object.video.videoLabelList, this);

				label_gridview.setAdapter(labelAdapter);
				label_gridview
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								// labelAdapter.changePosition(position);
								Intent intent = new Intent(
										VideoPlayerActivity.this,
										SearchActivity.class);
								intent.putExtra("label",
										object.video.videoLabelList
												.get(position).videoLabelName);
								startActivity(intent);
							}
						});
				// }
				if (object.otherVideoList.size() > 0)
					relative_album.setVisibility(View.VISIBLE);
				relative_gridview.setAdapter(new VideoRelativeAdapter(
						object.otherVideoList, VideoPlayerActivity.this));
				relative_gridview
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								// seriesAdapter.changePosition(-1);
								OtherVideoObject videoObject = object.otherVideoList
										.get(position);
								String videoname = videoObject.videoname;

								enterPlayerActivity(videoObject.videoID,
										videoname, null, null);
							}
						});
				video_detail = object.video.videodetails;
				video_play_detail.setText("视频详情：" + video_detail);
				Map<String, Object> hotVideoMap = new HashMap<String, Object>();
				hotVideoMap.put("videoclassID",
						object.video.videoclassList.get(videoPathList.size()-1).videoclassID);

				hotVideoMap.put("type", 1);
				hotVideoMap.put("pageSize", 20);
				netRequest.httpRequest(hotVideoMap, CommonUrl.getHotVideo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("gaolei",
						"e.getMessage()------getVideo----------"
								+ e.getMessage());
			}
		}
		if (requestUrl.equals(CommonUrl.getAlbumVideo)) {
			albumVideoMap.put(0, 0);
			try {
				final AlbumDetailObject object = new Gson().fromJson(result,
						AlbumDetailObject.class);

				if (object.albumvideo.isSeeList == 1)
					CommonUtil.changeTextDrawable(this, add_watch_list, "top",
							R.drawable.add_watch_list,
							R.string.cancel_watch_list, null);
				else
					CommonUtil.changeTextDrawable(this, add_watch_list, "top",
							R.drawable.add_watch_list_no,
							R.string.add_watch_list, null);

				isDetailExpand = true;
				Map<String, Object> hotVideoMap = new HashMap<String, Object>();
				List<VideoClassObject> classList = object.albumvideo.albumvideoclassList;
				String videoclassID = classList.get(classList.size() - 1).videoclassID;
				video_detail = object.albumvideo.albumvideodetails;
				final LabelAlbumAdapter labelAdapter = new LabelAlbumAdapter(
						object.albumvideo.videoLabelList, this);
				video_play_detail.setText("专辑详情：" + video_detail);
				label_gridview.setAdapter(labelAdapter);
				label_gridview
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								// labelAdapter.changePosition(position);
								Intent intent = new Intent(
										VideoPlayerActivity.this,
										SearchActivity.class);
								intent.putExtra("label",
										object.albumvideo.videoLabelList
												.get(position).videoLabelName);
								startActivity(intent);
							}
						});
				CommonUtil.changeTextDrawable(this, collect, "top",
						R.drawable.video_collect_no, R.string.collect, null);
				// videoclassID : 一级分类id
				hotVideoMap.put("videoclassID", videoclassID);
				hotVideoMap.put("type", 0);
				hotVideoMap.put("pageSize", 20);
				netRequest.httpRequest(hotVideoMap, CommonUrl.getAlbumHotVideo);
				if (object.otherVideoList.size() > 0)
					relative_album.setVisibility(View.VISIBLE);
				relative_gridview.setAdapter(new AlbumRelativeAdapter(
						object.otherVideoList, VideoPlayerActivity.this));
				relative_gridview
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								// seriesAdapter.changePosition(-1);
								AlbumOtherVideoObject videoObject = object.otherVideoList
										.get(position);
								String albumVideoname = videoObject.albumVideoname;
								enterPlayerActivity(null, null,
										videoObject.albumVideoID,
										albumVideoname);
							}
						});

				albumVideoList = object.albumvideo.albumvideolist;
				Log.d("gaolei",
						" albumVideoList.size()------getAlbumVideo--------"
								+ albumVideoList.size());
				have_update.setText(object.albumvideo.albumvideonewdetails);
				if (albumVideoList.size() > 0)
					album_layout.setVisibility(View.VISIBLE);
				final AlbumCountAdapter countAdapter = new AlbumCountAdapter(
						albumVideoList, this);
				album_count_gridview.setAdapter(countAdapter);

				seriesAdapter = new AlbumSeriesAdapter(albumVideoList, this);
				updateAlbumVideo(albumVideoList.get(0));
				album_series_listview.setAdapter(seriesAdapter);
				album_series_listview
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								currentPosition = 0;
								mPlayer.pause();
								seriesAdapter.changePosition(position);
								AlbumVideoObject videoObject = albumVideoList
										.get(countAdapter.selectedPosition * 5
												+ position);

								updateAlbumVideo(videoObject);
								albumVideoMap.clear();
								albumVideoMap.put(albumCountPosition, position);

							}
						});

				album_count_gridview
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub

								albumCountPosition = position;
								countAdapter.changePosition(position);
								for (Map.Entry<Integer, Integer> entry : albumVideoMap
										.entrySet()) {
									if (entry.getKey() == position) {
										seriesAdapter.changePosition(entry
												.getValue());
										seriesAdapter.changeList(position);
									} else {
										seriesAdapter.changePosition(-1);
										seriesAdapter.changeList(position);
									}
								}
							}
						});

			} catch (Exception e) {
				Log.e("gaolei", "e.getMessage()--------------" + e.getMessage());
			}
		}
		if (requestUrl.equals(CommonUrl.getHotVideo)) {
			try {
				JSONObject object = new JSONObject(result);
				final List<OtherVideoObject> hot_video_list = new Gson()
						.fromJson(object.getString("videoList"),
								new TypeToken<List<OtherVideoObject>>() {
								}.getType());
				if (hot_video_list.size() > 0) {
					hot_gridview.setAdapter(new VideoRelativeAdapter(
							hot_video_list, VideoPlayerActivity.this));
					hot_video.setVisibility(View.VISIBLE);
					divider2.setVisibility(View.VISIBLE);
					hot_gridview
							.setOnItemClickListener(new AdapterView.OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub
									// seriesAdapter.changePosition(-1);
									OtherVideoObject videoObject = hot_video_list
											.get(position);
									String videoname = videoObject.videoname;
									enterPlayerActivity(videoObject.videoID,
											videoname, null, null);
								}
							});
				} else {
					hot_video.setVisibility(View.GONE);
					divider2.setVisibility(View.GONE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(CommonUrl.getAlbumHotVideo)) {
			try {
				JSONObject object = new JSONObject(result);
				// Log.d("gaolei", "result-------getAlbumHotVideo-------" +
				// result);
				final List<AlbumOtherVideoObject> hot_album_video_list = new Gson()
						.fromJson(object.getString("albumVideoList"),
								new TypeToken<List<AlbumOtherVideoObject>>() {
								}.getType());
				if (hot_album_video_list.size() > 0) {
					hot_gridview.setAdapter(new AlbumRelativeAdapter(
							hot_album_video_list, VideoPlayerActivity.this));
					hot_video.setVisibility(View.VISIBLE);
					divider2.setVisibility(View.VISIBLE);
					hot_gridview
							.setOnItemClickListener(new AdapterView.OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub
									// seriesAdapter.changePosition(-1);
									AlbumOtherVideoObject videoObject = hot_album_video_list
											.get(position);
									String albumVideoname = videoObject.albumVideoname;
									enterPlayerActivity(null, null,
											videoObject.albumVideoID,
											albumVideoname);
								}
							});
				} else {
					hot_video.setVisibility(View.GONE);
					divider2.setVisibility(View.GONE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(CommonUrl.addSeeList)) {
			// Log.d("gaolei", "result-------addSeeList-------" + result);
			try {
				JSONObject object = new JSONObject(result);
				String message = object.getString("message");
				seelistID = object.getString("seelistID");
				if (message.equals("成功")) {
					CommonUtil.showToast(this, "加入看单成功");
					CommonUtil.changeTextDrawable(this, add_watch_list, "top",
							R.drawable.add_watch_list,
							R.string.cancel_watch_list, null);
				}

				if (message.equals("已加入看单"))
					CommonUtil.showToast(this, "已加入看单");
				CommonUtil.changeTextDrawable(this, add_watch_list, "top",
						R.drawable.add_watch_list, R.string.cancel_watch_list,
						null);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(CommonUrl.addCollection)) {
			// Log.d("gaolei", "result-------addCollection-------" + result);
			try {
				JSONObject object = new JSONObject(result);
				String message = object.getString("message");
				collectionID = object.getString("collectionID");
				if (message.equals("成功") || message.equals("已收藏")) {
					CommonUtil.showToast(this, "收藏成功");
					CommonUtil.changeTextDrawable(this, collect, "top",
							R.drawable.video_collect, R.string.collected, null);
					if (albumVideoID != null)
						videoObject.iscollection = 1;
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(CommonUrl.addWatchRecord)) {
			Log.d("gaolei", "result-------addWatchRecord-------" + result);

		}
		if (requestUrl.equals(CommonUrl.updateCollectionDelFlag)) {
			Log.d("gaolei", "result-------updateCollectionDelFlag-------"
					+ result);
			try {
				JSONObject object = new JSONObject(result);
				String message = object.getString("message");
				if (message.equals("成功")) {
					CommonUtil.showToast(this, "取消收藏成功");
					CommonUtil
							.changeTextDrawable(this, collect, "top",
									R.drawable.video_collect_no,
									R.string.collect, null);
					if (albumVideoID != null)
						videoObject.iscollection = 0;
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(CommonUrl.updateSeeListDelFlag)) {
			try {
				JSONObject object = new JSONObject(result);
				String message = object.getString("message");
				if (message.equals("成功")) {
					CommonUtil.showToast(this, "取消看单成功");
					CommonUtil.changeTextDrawable(this, add_watch_list, "top",
							R.drawable.add_watch_list_no,
							R.string.add_watch_list, null);
				}
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

	public void addCollection(View view) {
		String sessionId = LoginResultObject.getInstance().sessionId;
		if (sessionId == null) {
			Toast.makeText(this, getString(R.string.operate_after_login),
					Toast.LENGTH_LONG).show();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		if (videoID != null) {
			if (collect.getText().toString()
					.equals(getString(R.string.collected))) {
				updateCollectionDelFlag();
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("videoid", videoID);
				if (albumVideoID != null) {
					map.put("collectiontype", 0);
					map.put("albumVideoid", albumVideoID);
				} else
					map.put("collectiontype", 1);
				netRequest.httpRequestWithID(map, CommonUrl.addCollection);
			}
		}
	}

	public void addSeeList(View view) {
		String sessionId = LoginResultObject.getInstance().sessionId;
		if (sessionId == null) {
			Toast.makeText(this, getString(R.string.operate_after_login),
					Toast.LENGTH_LONG).show();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}

		if (albumVideoID != null) {
			if (add_watch_list.getText().toString()
					.equals(getString(R.string.cancel_watch_list))) {
				updateSeeListDelFlag();
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("albumVideoID", albumVideoID);
				netRequest.httpRequestWithID(map, CommonUrl.addSeeList);
			}
		}
	}

	public void updateAlbumVideo(AlbumVideoObject videoObject) {
		this.videoObject = videoObject;
		if (videoObject.iscollection == 0) {
			CommonUtil.changeTextDrawable(VideoPlayerActivity.this, collect,
					"top", R.drawable.video_collect_no, R.string.collect, null);
		} else {
			CommonUtil.changeTextDrawable(VideoPlayerActivity.this, collect,
					"top", R.drawable.video_collect, R.string.collected, null);
		}
		videoID = videoObject.videoid;
		video_name = videoObject.videoname;
		mFileName.setText(video_name);
		plays.setText(videoObject.videopnumber + "");
		List<VideoPathObject> videopathList = videoObject.videopathList;
		playVideo(videopathList.get(videopathList.size() - 1).videoUrl);
		switchResolution(videopathList);
		collectionID = videoObject.collectionID;
	}

	public void updateCollectionDelFlag() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("collectionID", collectionID);
		netRequest.httpRequestWithID(map, CommonUrl.updateCollectionDelFlag);
	}

	public void updateSeeListDelFlag() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seelistID", seelistID);
		netRequest.httpRequestWithID(map, CommonUrl.updateSeeListDelFlag);
	}

	public void cacheVideo(View view) {

		if (mVideoPath == null || mPlayer.mCurrentState == mPlayer.STATE_ERROR) {
			CommonUtil.showToast(this, getString(R.string.cannot_cache));
			return;
		}
		for (int i = 0; i < APPApplication.mFileList.size(); i++) {
			FileInfo mFileInfo = APPApplication.mFileList.get(i);
			if ((mFileInfo.getFileName()).equals(video_name + ".mp4")) {
				CommonUtil.showToast(this, getString(R.string.have_cache));
				return;
			}
		}
		if (CommonUtil.isMobile(VideoPlayerActivity.this)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
			builder.setTitle("提示"); // 设置标题
			builder.setMessage("您现在用的是手机流量，是否继续 缓存？"); // 设置内容
			builder.setIcon(R.drawable.prompt_icon);// 设置图标，图片id即可
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() { // 设置确定按钮
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startCacheVideo();
							dialog.dismiss(); // 关闭dialog
						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() { // 设置取消按钮
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
							dialog.dismiss();
						}
					});
			builder.create().show();
		} else {
			startCacheVideo();
		}

	}
	private void startCacheVideo(){
		Intent intent = new Intent(VideoPlayerActivity.this,
				CacheActivity.class);
		intent.putExtra("mVideoPath", CommonUrl.baseUrl + mVideoPath);
		intent.putExtra("album_name", albumName);
		intent.putExtra("video_name", video_name);
		intent.putExtra("videoImgUrl", videoImgUrl);
		// intent.putExtra("mVideoPath", CommonUrl.mVideoPath_env);
		startActivity(intent);
	}

	public void showResolution(View view) {
		if (!isResolution) {
			resolution_listview.setVisibility(View.VISIBLE);
			isResolution = true;
		} else {
			resolution_listview.setVisibility(View.GONE);
			isResolution = false;
		}
	}

	private void switchResolution(final List<VideoPathObject> videopathList) {
		resolution_switch
				.setText(videopathList.get(videopathList.size() - 1).videoStatus);
		mediacontroller_play_pause.setImageResource(R.drawable.player_play);
		final ResolutionAdapter adapter = new ResolutionAdapter(videopathList,
				VideoPlayerActivity.this);
		resolution_listview.setAdapter(adapter);
		resolution_listview
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						// TODO Auto-generated method stub
						// currentPosition = mPlayer.getCurrentPosition();
						currentPosition = mPlayer.getCurrentPosition();
						Log.d("gaolei", "currentPosition---------1------"
								+ currentPosition);
						VideoPathObject pathObject = videopathList
								.get(position);
						playVideo(pathObject.videoUrl);
						adapter.changePosition(position);
						resolution_switch.setText(pathObject.videoStatus);
						resolution_listview.setVisibility(View.GONE);
					}
				});

	}

	public void expandDetail() {
		if (!isDetailExpand) {
			if (video_detail != null)
				video_play_detail.setMaxLines(10);
			// video_play_detail.setText("视频详情：" + video_detail);
			CommonUtil.changeTextDrawable(this, expand_detail, "left",
					R.drawable.arrow_up, R.string.withdrawal, null);
			isDetailExpand = true;
		} else {
			video_play_detail.setMaxLines(1);
			// video_play_detail.setText(getString(R.string.video_detail));
			CommonUtil.changeTextDrawable(this, expand_detail, "left",
					R.drawable.arrow_down, R.string.expand_more, null);
			isDetailExpand = false;
		}

	}
}