package com.ipmph.v.multidownload.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ipmph.v.multidownload.entitis.FileInfo;
import com.ipmph.v.setting.activity.CacheActivity;

public class ProgressReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
		if (DownloadService.ACTION_START.equals(intent.getAction())) {
			// 涓嬭浇寮�濮嬬殑鏃跺�欏惎鍔ㄩ�氱煡鏍�
			// mNotificationUtil.showNotification((FileInfo) intent
			// .getSerializableExtra("fileInfo"));
			CacheActivity.mNotificationUtil
					.showNotification(CacheActivity.fileInfo);

		} else if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
			// 鏇存柊杩涘害鏉＄殑鏃跺��

			int finished = intent.getIntExtra("finished", 0);
			if (finished <= 100) {
				int id = intent.getIntExtra("id", 0);
				if(CacheActivity.mDownloadAdapter!=null){
				CacheActivity.mDownloadAdapter.updataProgress(id, finished);
				CacheActivity.mNotificationUtil
						.updataNotification(id, finished);
				}
			} 
//			else {
//				CacheActivity.mDownloadAdapter.notifyCacheFinished(fileInfo
//						.getId());
//				CacheActivity.mNotificationUtil.cancelNotification(fileInfo
//						.getId());
//			}
//			Log.d("gaolei", "finished---------ProgressReceiver--------"
//					+ finished);

		} else if (DownloadService.ACTION_FINISHED.equals(intent.getAction())) {
			// 涓嬭浇缁撴潫鐨勬椂鍊�

//			Log.d("gaolei",
//					"fileInfo.getId()------finished--------" + fileInfo.getId());
			if(CacheActivity.mDownloadAdapter!=null){
			CacheActivity.mDownloadAdapter.updataProgress(fileInfo.getId(), 0);
			CacheActivity.mDownloadAdapter
					.notifyCacheFinished(fileInfo.getId());
			}
			// Toast.makeText(
			// CacheActivity.this,
			// AppApplication.mFileList.get(fileInfo.getId()).getFileName()
			// + getString(R.string.downloaded),
			// Toast.LENGTH_SHORT).show();
			// 涓嬭浇缁撴潫鍚庡彇娑堥�氱煡
			CacheActivity.mNotificationUtil
					.cancelNotification(fileInfo.getId());
		}
	}

}
