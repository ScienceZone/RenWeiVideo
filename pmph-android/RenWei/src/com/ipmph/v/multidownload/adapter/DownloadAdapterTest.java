package com.ipmph.v.multidownload.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.multidownload.entitis.FileInfo;
import com.ipmph.v.multidownload.service.DownloadService;

public class DownloadAdapterTest extends BaseAdapter {
	
	private Context mContext = null;
	private List<FileInfo> mFilelist = null;
	
	private LayoutInflater layoutInflater;
	

	public DownloadAdapterTest(Context mContext, List<FileInfo> mFilelist) {
		super();
		this.mContext = mContext;
		this.mFilelist = mFilelist;
		layoutInflater = LayoutInflater.from(mContext);
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final FileInfo mFileInfo = mFilelist.get(position);
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.download_item2, null);
			viewHolder = new ViewHolder();
			viewHolder.textview = (TextView) convertView.findViewById(R.id.file_textview);
			viewHolder.startButton = (Button) convertView.findViewById(R.id.start_button);
			viewHolder.stopButton = (Button) convertView.findViewById(R.id.stop_button);
			viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar2);
			viewHolder.root_layout = (RelativeLayout) convertView
					.findViewById(R.id.root_layout);
			
			viewHolder.textview.setText(mFileInfo.getFileName());
			viewHolder.progressBar.setMax(100);
			
			
			viewHolder.root_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, DownloadService.class);
					intent.setAction(DownloadService.ACTION_START);
					intent.putExtra("fileInfo", mFileInfo);
					mContext.startService(intent);
								
				}
			});
			
			convertView.setTag(viewHolder);
		} else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, DownloadService.class);
				intent.setAction(DownloadService.ACTION_STOP);
				intent.putExtra("fileInfo", mFileInfo);
				mContext.startService(intent);	
			}
		});
		
		viewHolder.progressBar.setProgress(mFileInfo.getFinished());

		
		return convertView;
	}
	/**
	 * �����б��еĽ����� 
	 *
	 */
	public void updataProgress(int l , int progress) {
		FileInfo info = mFilelist.get(l);
		info.setFinished(progress);
		notifyDataSetChanged();
	}
	
	static class ViewHolder{
		 TextView textview;
		 Button startButton;
		 Button stopButton;
		 ProgressBar progressBar;
		 RelativeLayout root_layout;
	}

}
