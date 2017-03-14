package com.ipmph.v.setting.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipmph.v.R;
import com.ipmph.v.VideoPlayerActivity;
import com.ipmph.v.application.APPApplication;
import com.ipmph.v.multidownload.adapter.DownloadAdapter;
import com.ipmph.v.multidownload.entitis.FileInfo;
import com.ipmph.v.multidownload.notification.NotificationUtil;
import com.ipmph.v.multidownload.service.DownloadService;
import com.ipmph.v.tool.CommonUtil;

public class CacheActivity extends FragmentActivity {

	public ListView listView;

	public static DownloadAdapter mDownloadAdapter;
	public static NotificationUtil mNotificationUtil = null;
	public static FileInfo fileInfo;
	private static boolean firstEnter=true;
	private LinearLayout empty_prompt;
	private boolean showDelete = false;
	private TextView delete, delete_All;

	public void onCreate(Bundle savedInstanceStage) {
		super.onCreate(savedInstanceStage);
		setContentView(R.layout.cache_activity);
		initViews();
	}

	private void initViews() {
		// 鍒濆鍖栨帶浠�
		listView = (ListView) findViewById(R.id.list_view);
		delete = (TextView) findViewById(R.id.delete);
		mNotificationUtil = new NotificationUtil(this);
		empty_prompt = (LinearLayout) findViewById(R.id.empty_prompt);
		
		// List<ThreadInfo> list=new
		// ThreadDAOImple(this).getAllData("thread_info");
		// for(int i=0;i<list.size();i++)
		// Log.d("gaolei",
		// "list.get(i)------Cache--------"+list.get(i).toString());
		String localVideoCount = getSharedPreferences("LocalVideo", 0)
				.getString("localVideoCount", "");
		Log.d("gaolei", "CacheActivity-----localVideoCount-----------|"
				+ localVideoCount);
		File cacheFileDir = new File(APPApplication.downloadSdcardPath);
		
		if(firstEnter){
		if (cacheFileDir.isDirectory()) {
			File[] files = cacheFileDir.listFiles();
//			if(localVideoCount.equals(files.length+""))return;
			for (int i =0; i < files.length; i++) {
				String videoImgUrl = getSharedPreferences("DownloadVideoInfo", 0)
						.getString(files[i].getName(), "");
				Log.d("gaolei", "videoName-----localVideo-----------|"
						+ files[i].getName());
				Log.d("gaolei", "videoImgUrl-----localVideo-----------|"
						+ videoImgUrl);
				FileInfo fileInfo = new FileInfo(i, 
						"",files[i].getAbsolutePath(), files[i].getName(), 0, 0,true,videoImgUrl);
				APPApplication.mFileList.add(fileInfo);
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("localVideoCount", files.length+"");
			CommonUtil.createSP(this, "LocalVideo", map);
		}
		firstEnter=false;
		}
		String mVideoPath = getIntent().getStringExtra("mVideoPath");
		String videoName = getIntent().getStringExtra("video_name");
		String videoImgUrl = getIntent().getStringExtra("videoImgUrl");
		Log.d("gaolei", "CacheActivity----mVideoPath-----------"+ mVideoPath);
		if (mVideoPath!=null&&mVideoPath.length() > 1)
//			addCacheVideo(CommonUrl.mVideoPath_env);
		addCacheVideo(mVideoPath,videoName,videoImgUrl);
		else{
			if(APPApplication.mFileList.size()==0)
				empty_prompt.setVisibility(View.VISIBLE);
			mDownloadAdapter = new DownloadAdapter(this, APPApplication.mFileList);
			listView.setAdapter(mDownloadAdapter);
			jumpToVideoPlayerListener();
		}
			
	}

	public void onBack(View view) {
		finish();
	}


	public void addCacheVideo(String videoUrl,String videoName,String videoImgUrl) {
		empty_prompt.setVisibility(View.GONE);
		Log.d("gaolei", "videoUrl-----ACTION_ADD_DOWNLOAD-----------"
				+ videoUrl);
//		String videoName = FileUtil.getfileName(videoUrl);
		fileInfo = new FileInfo(APPApplication.mFileList.size(), videoUrl,
				APPApplication.downloadSdcardPath+"/"+videoName+".mp4", videoName+".mp4", 0, 0,false,videoImgUrl);
		APPApplication.mFileList.add(fileInfo);
		Log.d("gaolei", "mFileList.size()-------------"
				+ APPApplication.mFileList.size());
//		if(mAdapter==null){
		mDownloadAdapter = new DownloadAdapter(this, APPApplication.mFileList);
		listView.setAdapter(mDownloadAdapter);
		jumpToVideoPlayerListener();
//		}else{
//			mAdapter.changeList(AppApplication.mFileList);
//		}
		Intent serviceIntent = new Intent(CacheActivity.this,
				DownloadService.class);
		serviceIntent.setAction(DownloadService.ACTION_START);
		serviceIntent.putExtra("fileInfo", fileInfo);
		startService(serviceIntent);
		// mAdapter.notifyDataSetChanged();
		rememberVideoInfo(videoName+".mp4",videoImgUrl);
	}
	private void jumpToVideoPlayerListener(){
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent =new Intent(CacheActivity.this,VideoPlayerActivity.class);
				intent.putExtra("localFileUrl", APPApplication.mFileList.get(position).getFileUrl());
				intent.putExtra("localFileName", APPApplication.mFileList.get(position).getFileName());
				Log.d("gaolei", "fileUrl-------------"+APPApplication.mFileList.get(position).getFileUrl());
				startActivity(intent);

			}
		});
	}
	public void showItemDelete(View view) {
		if (mDownloadAdapter == null)
			return;
		if (!showDelete) {
			mDownloadAdapter.showDeleteIcon(true);
			delete.setText(getString(R.string.cancel));
			showDelete = true;
		} else {
			mDownloadAdapter.showDeleteIcon(false);
			delete.setText(getString(R.string.delete));
			showDelete = false;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		AppApplication.mFileList.clear();
		Log.d("gaolei", "CacheActivity----------------onDestroy---------");
	}
	public void rememberVideoInfo(String videoName,String videoImgUrl) {
		Map<String, String> map = new HashMap<String, String>();
		Log.d("gaolei", "videoName----------------remember---------"+videoName);
		Log.d("gaolei", "videoImgUrl----------------remember---------"+videoImgUrl);
		map.put(videoName, videoImgUrl);
		CommonUtil.createSP(this, "DownloadVideoInfo", map);
	}
	public static class NetWorkReceiver extends BroadcastReceiver {  
	    @Override  
	    public void onReceive(Context context, Intent intent) {  
	        // TODO Auto-generated method stub  
	        //Toast.makeText(context, intent.getAction(), 1).show();  
	        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	        NetworkInfo activeInfo = manager.getActiveNetworkInfo();  
	        if(activeInfo==null){
	        	Toast.makeText(context,"网络连接断开",Toast.LENGTH_LONG).show();
	        	if(mDownloadAdapter!=null)
	        	mDownloadAdapter.notifyAllStop();
	        	Log.d("gaolei", "CacheActivity---------------网络连接断开了---------");
	        }else{
	        	if(mDownloadAdapter!=null)
	        		Toast.makeText(context,"网络恢复连接",Toast.LENGTH_LONG).show();
	        	mDownloadAdapter.notifyAllStart();
//	        	Log.d("gaolei", "CacheActivity---------------网络连接连接了---------");
	        }
//	        Toast.makeText(context, "mobile:"+mobileInfo.isConnected()+"\n"+"wifi:"+wifiInfo.isConnected()  
//	                        +"\n"+"active:"+activeInfo.getTypeName(), 1).show();  
	    }  //如果无网络连接activeInfo为null  
	  
	}  
}
