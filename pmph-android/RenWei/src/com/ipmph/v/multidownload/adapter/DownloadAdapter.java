package com.ipmph.v.multidownload.adapter;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.VideoPlayerActivity;
import com.ipmph.v.multidownload.entitis.FileInfo;
import com.ipmph.v.multidownload.service.DownloadService;
import com.ipmph.v.multidownload.service.DownloadTask;
import com.ipmph.v.setting.activity.CacheActivity;
import com.ipmph.v.tool.CommonUtil;

public class DownloadAdapter extends BaseAdapter {

	private Context mContext = null;
	private List<FileInfo> mFilelist = null;

	private LayoutInflater layoutInflater;
	private boolean showDeleteIcon = false;
	private int finishedPosition = -1;
	private SparseBooleanArray arr;
	private boolean allStop = false;

	public DownloadAdapter(Context mContext, List<FileInfo> mFilelist) {
		super();
		this.mContext = mContext;
		this.mFilelist = mFilelist;
		layoutInflater = LayoutInflater.from(mContext);
		arr = new SparseBooleanArray();
		for (int i = 0; i < mFilelist.size(); i++) {
			arr.put(i, true);
		}
	}

	@Override
	public int getCount() {
		return mFilelist.size();
	}

	@Override
	public Object getItem(int position) {
		return mFilelist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void notifyCacheFinished(int id) {
//		if(id==mFilelist.size())
//		finishedPosition = id-1;
//		notifyDataSetChanged();
	}

	public void changeList(List<FileInfo> mFilelist) {
		this.mFilelist = mFilelist;
		notifyDataSetChanged();
	}

	public void showDeleteIcon(boolean bool) {
		showDeleteIcon = bool;
		notifyDataSetChanged();
	}

	public void notifyAllStop() {
		for (int position = 0; position < mFilelist.size(); position++) {
			arr.put(position, !arr.get(position));
			FileInfo mFileInfo = mFilelist.get(position);
			if (!arr.get(position) && !mFileInfo.isComplete()) {
				Log.d("gaolei", "DownloadAdapter--------notifyAllStop------");
				Intent intent = new Intent(mContext, DownloadService.class);
				intent.setAction(DownloadService.ACTION_STOP);
				intent.putExtra("fileInfo", mFileInfo);
				mContext.startService(intent);
			}
		}
		allStop = true;
		notifyDataSetChanged();
	}

	public void notifyAllStart() {
		Log.d("gaolei", "DownloadAdapter--------notifyAllStart------");
//		for (int position = 0; position < mFilelist.size(); position++) {
//			arr.put(position, !arr.get(position));
//			FileInfo mFileInfo = mFilelist.get(position);
//			if (arr.get(position) && !mFileInfo.isComplete()) {
//				Intent intent = new Intent(mContext, DownloadService.class);
//				intent.setAction(DownloadService.ACTION_START);
//				intent.putExtra("fileInfo", mFileInfo);
//				mContext.startService(intent);
//			}
//		}
		allStop = false;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.download_item, null);
			holder.textview = (TextView) convertView
					.findViewById(R.id.file_textview);
			holder.download_status = (TextView) convertView
					.findViewById(R.id.download_status);
			holder.download_progress = (TextView) convertView
					.findViewById(R.id.download_progress);
			holder.progressBar = (ProgressBar) convertView
					.findViewById(R.id.progressBar2);
			holder.root_layout = (RelativeLayout) convertView
					.findViewById(R.id.root_layout);
			holder.delete_layout = (RelativeLayout) convertView
					.findViewById(R.id.delete_layout);
			holder.video_img = (ImageView) convertView
					.findViewById(R.id.video_img);
			holder.play_pause_button = (ImageView) convertView
					.findViewById(R.id.play_pause_button);
			holder.progressBar.setMax(100);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final FileInfo mFileInfo = mFilelist.get(position);
		holder.textview.setText(mFileInfo.getFileName());
		CommonUtil.getUtilInstance().displayLowQualityInImage(
				mFileInfo.getVideoImgUrl(), holder.video_img);
//		Log.d("gaolei", "DownloadAdapter--------position------"+position);
//		Log.d("gaolei", "DownloadAdapter--------allStop------"+allStop);
//		Log.d("gaolei", "DownloadAdapter--------mFileInfo.isComplete()------"+mFileInfo.isComplete());
		if (allStop) {

			if (!mFileInfo.isComplete()) {
				holder.play_pause_button
						.setImageResource(R.drawable.cache_pause);
				holder.download_status.setText("已暂停");
			}
		}
		// else{
		// if(!mFileInfo.isComplete()){
		// holder.play_pause_button.setImageResource(R.drawable.cache_play);
		// holder.download_status.setText("正在缓存");
		// }
		// }

		if (mFileInfo.getVideoImgUrl() == null
				|| mFileInfo.getVideoImgUrl().equals(""))
			holder.video_img
					.setImageResource(R.drawable.default_img_horizontal);
		if (showDeleteIcon)
			holder.delete_layout.setVisibility(View.VISIBLE);
		else
			holder.delete_layout.setVisibility(View.GONE);

		if (mFileInfo.isComplete()) {
			holder.progressBar.setVisibility(View.GONE);
			holder.download_progress.setVisibility(View.GONE);
			holder.play_pause_button.setVisibility(View.GONE);
			holder.download_status.setText(mContext
					.getString(R.string.downloaded));
			holder.download_status.setTextColor(Color.parseColor("#998d89"));
		} else {
			if(!allStop){
			holder.progressBar.setVisibility(View.VISIBLE);
			holder.download_progress.setVisibility(View.VISIBLE);
			holder.play_pause_button.setVisibility(View.VISIBLE);
			holder.download_status.setText(mContext
					.getString(R.string.downloading));
			holder.play_pause_button
			.setImageResource(R.drawable.cache_play);
			holder.download_status.setTextColor(Color.parseColor("#998d89"));
			}
		}
		if (finishedPosition == position) {
			holder.progressBar.setVisibility(View.GONE);
			holder.download_progress.setVisibility(View.GONE);
			holder.play_pause_button.setVisibility(View.GONE);
			holder.download_status.setText(mContext
					.getString(R.string.downloaded));
		}
		Log.d("gaolei", "DownloadAdapter--------progress------"+mFileInfo.getFinished());
		if (mFileInfo.getFinished() < 100) {
			if(mFileInfo.getFinished()>0){
			holder.download_progress.setText(mFileInfo.getFinished() + "%");
			holder.progressBar.setProgress(mFileInfo.getFinished());
			}
		} else {
			Log.d("gaolei", "DownloadAdapter--------position------"+position);
			Log.d("gaolei", "DownloadAdapter--------progress----end--"+100);

			holder.progressBar.setVisibility(View.GONE);
			holder.download_progress.setVisibility(View.GONE);
			holder.play_pause_button.setVisibility(View.GONE);
			holder.download_status.setText(mContext
					.getString(R.string.downloaded));
			// Intent intent = new Intent(mContext, DownloadService.class);
			// intent.setAction(DownloadService.ACTION_STOP);
			// intent.putExtra("fileInfo", mFileInfo);
			// mContext.startService(intent);
		}
		holder.delete_layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				CommonUtil.deleteKey(mContext, "DownloadVideoInfo",
						mFileInfo.getFileName());
				if (!mFileInfo.isComplete()) {
					Intent intent = new Intent(mContext, DownloadService.class);
					intent.setAction(DownloadService.ACTION_STOP);
					intent.putExtra("fileInfo", mFileInfo);
					mContext.startService(intent);
					DownloadTask.mDao.deleteThread(mFileInfo.getUrl());
					CacheActivity.mNotificationUtil
							.cancelNotification(mFileInfo.getId());
				}
				String fileUrl = mFilelist.get(position).getFileUrl();
				File file = new File(fileUrl);
				if (file.exists())
					file.delete();
				mFilelist.remove(position);
				notifyDataSetChanged();
			}
		});

		holder.play_pause_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// updatePausePlay(holder.play_pause_button,holder.play_pause_button,mFileInfo,
				arr.put(position, !arr.get(position));
				Log.d("gaolei",
						"arr.get(position)--------------" + arr.get(position));
				if (!arr.get(position)) {
					Log.d("gaolei", "download--------pause------");
					Intent intent = new Intent(mContext, DownloadService.class);
					intent.setAction(DownloadService.ACTION_STOP);
					intent.putExtra("fileInfo", mFileInfo);
					mContext.startService(intent);
					holder.play_pause_button
							.setImageResource(R.drawable.cache_pause);
					holder.download_status.setText("已暂停");
				} else {
					if (CommonUtil.isMobile(mContext)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext); // 先得到构造器
					builder.setTitle("提示"); // 设置标题
					builder.setMessage("您现在用的是手机流量，是否继续 缓存？"); // 设置内容
					builder.setIcon(R.drawable.prompt_icon);// 设置图标，图片id即可
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() { // 设置确定按钮
								@Override
								public void onClick(DialogInterface dialog, int which) {
									continueCache(mFileInfo,holder);
									dialog.dismiss(); // 关闭dialog
								}
							});
					builder.setNegativeButton("取消",
							new DialogInterface.OnClickListener() { // 设置取消按钮
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				} else {
					continueCache(mFileInfo,holder);
				}
				
					Log.d("gaolei", "download--------start------");
					
				}
			}
		});

		return convertView;
	}

	private void continueCache(FileInfo mFileInfo,ViewHolder holder){
		Intent intent = new Intent(mContext, DownloadService.class);
		intent.setAction(DownloadService.ACTION_START);
		intent.putExtra("fileInfo", mFileInfo);
		mContext.startService(intent);

		holder.play_pause_button
				.setImageResource(R.drawable.cache_play);
		holder.download_status.setText(mContext.getString(R.string.downloading));
	}
	public void updataProgress(int position, int progress) {
		if (mFilelist.size() > 0) {
			int tempPosition = position;
			if (position == mFilelist.size())
				tempPosition = position - 1;
			FileInfo info = mFilelist.get(tempPosition);
			info.setFinished(progress);
			if (progress == 100)
				info.setComplete(true);
			notifyDataSetChanged();
		}
	}

//	private void updatePausePlay(RelativeLayout root_layout,
//			TextView download_play_pause, FileInfo mFileInfo, int position,
//			Boolean bool) {
//
//		if (bool == true) {
//			Intent intent = new Intent(mContext, DownloadService.class);
//			intent.setAction(DownloadService.ACTION_START);
//			intent.putExtra("fileInfo", mFileInfo);
//			mContext.startService(intent);
//			root_layout.setTag("false");
//			download_play_pause.setText(mContext.getString(R.string.downloading));
//			download_play_pause.setTextColor(Color.parseColor("#ec6f36"));
//		} else {
//			Intent intent = new Intent(mContext, DownloadService.class);
//			intent.setAction(DownloadService.ACTION_STOP);
//			intent.putExtra("fileInfo", mFileInfo);
//			mContext.startService(intent);
//			root_layout.setTag("true");
//			download_play_pause.setText("已暂停");
//			download_play_pause.setTextColor(Color.parseColor("#998d89"));
//		}
//
//	}

	static class ViewHolder {
		TextView textview, download_status, download_progress;
		RelativeLayout root_layout;
		ImageView play_pause_button;
		Button stopButton;
		ProgressBar progressBar;
		RelativeLayout delete_layout;
		private ImageView video_img;
	}

}
