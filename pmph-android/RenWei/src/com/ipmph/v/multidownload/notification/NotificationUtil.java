package com.ipmph.v.multidownload.notification;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ipmph.v.R;
import com.ipmph.v.multidownload.entitis.FileInfo;
import com.ipmph.v.multidownload.service.DownloadService;
import com.ipmph.v.setting.activity.CacheActivity;
import com.ipmph.v.tool.CommonUtil;

public class NotificationUtil {

	private Context mContext;
	private NotificationManager mNotificationManager = null;
	private Map<Integer, Notification> mNotifications = null;

	public NotificationUtil(Context context) {
		this.mContext = context;
		// 锟斤拷锟较低惩ㄖ拷锟斤拷锟斤拷锟�
		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// 锟斤拷锟斤拷通知锟侥硷拷锟斤拷
		mNotifications = new HashMap<Integer, Notification>();
	}

	/**
	 * 锟斤拷示通知锟斤拷
	 * 
	 * @param fileInfo
	 */
	public void showNotification(FileInfo fileInfo) {
		// 锟叫讹拷通知锟角凤拷锟窖撅拷锟斤拷示
		if (!mNotifications.containsKey(fileInfo.getId())) {
			Notification notification = new Notification();
			notification.tickerText = fileInfo.getFileName()
					+ mContext.getString(R.string.start_download);
			notification.when = System.currentTimeMillis();
			notification.icon = R.drawable.app_icon;
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			// 锟斤拷锟酵ㄖ拷锟斤拷锟斤拷图
			Intent intent = new Intent(mContext, CacheActivity.class);
			PendingIntent pd = PendingIntent
					.getActivity(mContext, 0, intent, 0);
			notification.contentIntent = pd;
			// 锟斤拷锟斤拷远锟斤拷锟斤拷图RemoteViews锟斤拷锟斤拷
			RemoteViews remoteViews = new RemoteViews(
					mContext.getPackageName(), R.layout.notification_layout);
			// 锟斤拷锟斤拷远锟斤拷锟斤拷图锟斤拷锟斤拷锟矫匡拷始锟斤拷锟斤拷录锟�
			Intent intentStart = new Intent(mContext, DownloadService.class);
			intentStart.setAction(DownloadService.ACTION_START);
			intentStart.putExtra("fileInfo", fileInfo);
			PendingIntent piStart = PendingIntent.getService(mContext, 0,
					intentStart, 0);
			// remoteViews.setOnClickPendingIntent(R.id.start_button, piStart);

			// 锟斤拷锟斤拷远锟斤拷锟斤拷图锟斤拷锟斤拷锟矫斤拷锟斤拷锟斤拷锟斤拷录锟�
			Intent intentStop = new Intent(mContext, DownloadService.class);
			intentStop.setAction(DownloadService.ACTION_STOP);
			intentStop.putExtra("fileInfo", fileInfo);
			PendingIntent piStop = PendingIntent.getService(mContext, 0,
					intentStop, 0);
			// remoteViews.setOnClickPendingIntent(R.id.stop_button, piStop);
			// 锟斤拷锟斤拷TextView锟斤拷锟侥硷拷锟斤拷锟斤拷锟斤拷
			remoteViews.setTextViewText(R.id.download_name,
					fileInfo.getFileName());
			remoteViews.setTextViewText(R.id.download_status,
					mContext.getString(R.string.downloading));
			String time = CommonUtil.transformMillisToDate(
					System.currentTimeMillis()).split(" ")[1];
			remoteViews.setTextViewText(R.id.download_time, time);
			// 锟斤拷锟斤拷Notification锟斤拷锟斤拷图
			notification.contentView = remoteViews;
			// 锟斤拷锟斤拷Notification通知
			mNotificationManager.notify(fileInfo.getId(), notification);
			// 锟斤拷Notification锟斤拷拥锟斤拷锟斤拷锟斤拷锟�
			mNotifications.put(fileInfo.getId(), notification);
		}
	}

	/**
	 * 取锟斤拷通知锟斤拷通知
	 */
	public void cancelNotification(int id) {
		mNotificationManager.cancel(id);
		mNotifications.remove(id);
	}

	/**
	 * 锟斤拷锟斤拷通知锟斤拷锟斤拷锟斤拷锟斤拷
	 * 
	 * @param id
	 *            锟斤拷取Notification锟斤拷id
	 * @param progress
	 *            锟斤拷取锟侥斤拷锟斤拷
	 */
	public void updataNotification(int id, int progress) {
		Notification notification = mNotifications.get(id);
		if (notification != null) {
			// 锟睫改斤拷锟斤拷锟斤拷锟斤拷锟斤拷
			notification.contentView.setProgressBar(R.id.download_progress,
					100, progress, false);
			notification.contentView.setTextViewText(R.id.progress,progress+"%");
			mNotificationManager.notify(id, notification);
		}
	}
}
