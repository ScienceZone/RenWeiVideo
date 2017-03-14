package com.ipmph.v.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.object.VideoCollectObject;
import com.ipmph.v.setting.activity.WatchRecordActivity;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class VideoCollectAdapter extends BaseAdapter {

	List<VideoCollectObject> videoList;
	LayoutInflater inflater;
	private Context context;
	private boolean showDeleteIcon = false;
	private Handler handler;

	public VideoCollectAdapter(List<VideoCollectObject> videoList, Context context,Handler handler) {
		this.videoList = videoList;
		this.context = context;
		this.handler=handler;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return videoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return videoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void showDeleteIcon(boolean bool) {
		showDeleteIcon = bool;
		notifyDataSetChanged();
	}
	public void changeData(List<VideoCollectObject> videoList){
		this.videoList=videoList;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.watch_record_item, null);
			holder.video_img = (ImageView) convertView
					.findViewById(R.id.video_img);
			holder.video_name = (TextView) convertView
					.findViewById(R.id.video_name);
			holder.album_name = (TextView) convertView
					.findViewById(R.id.album_name);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.delete_layout = (RelativeLayout) convertView
					.findViewById(R.id.delete_layout);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (showDeleteIcon)
			holder.delete_layout.setVisibility(View.VISIBLE);
		else
			holder.delete_layout.setVisibility(View.GONE);
		final VideoCollectObject object = videoList.get(position);
		CommonUtil.getUtilInstance().displayLowQualityInImage(
				CommonUrl.baseUrl + object.videoImg +".small"+ object.suffix,
				holder.video_img);
		holder.video_name.setText(object.videoName);
		if (object.albumVideoID != null) {
			holder.album_name.setVisibility(View.VISIBLE);
			holder.album_name.setText(object.albumVideoName);
		}
		String time = object.collectiontime;
		if (time.contains(" ")) {
			holder.time.setText(time.split(" ")[0]);
		} else {
			holder.time.setText(time);
		}
		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				if (object.albumVideoID == null) {
					map.put("videoID", object.videoID);
					map.put("videoname", object.videoName);
				} else {
					map.put("albumVideoID", object.albumVideoID);
					map.put("albumVideoname", object.albumVideoName);
				}

				CommonUtil.jumpToPlayerActivity(context, map);
			}
		});
		holder.delete_layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				videoList.remove(position);
				notifyDataSetChanged();
				Message msg=handler.obtainMessage();
				String collectionID=object.collectionID;
				Bundle bundle=new Bundle();
				bundle.putString("collectionID", collectionID);
				msg.setData(bundle);
				msg.what=WatchRecordActivity.DELETE_ONE_RECORD;
				handler.sendMessage(msg);
				
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView video_img;
		TextView video_name, album_name,time;
		RelativeLayout delete_layout;
	}

}
