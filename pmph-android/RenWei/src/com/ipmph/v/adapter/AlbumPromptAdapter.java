package com.ipmph.v.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.ipmph.v.object.AlbumPromptObject;
import com.ipmph.v.setting.activity.WatchRecordActivity;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class AlbumPromptAdapter extends BaseAdapter {

	private List<AlbumPromptObject> videoList;
	private LayoutInflater inflater;
	private Context context;
	private boolean isVideoAdd = true;
	private Handler handler;
	private boolean showDeleteIcon = false;

	public AlbumPromptAdapter(List<AlbumPromptObject> videoList,
			Context context, Handler handler) {
		this.videoList = videoList;
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.handler = handler;
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

	public void changeData(List<AlbumPromptObject> videoList) {
		this.videoList = videoList;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.album_prompt_item, null);
			holder.video_img = (ImageView) convertView
					.findViewById(R.id.video_img);
			holder.video_name = (TextView) convertView
					.findViewById(R.id.video_name);
			holder.album_time = (TextView) convertView
					.findViewById(R.id.album_time);
			holder.update_episode = (TextView) convertView
					.findViewById(R.id.update_episode);
			holder.delete_layout = (RelativeLayout) convertView
					.findViewById(R.id.delete_layout);

			holder.album_add_cancel = (TextView) convertView
					.findViewById(R.id.album_add_cancel);
			holder.play = (TextView) convertView.findViewById(R.id.play);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AlbumPromptObject object = videoList.get(position);
		CommonUtil.getUtilInstance().displayLowQualityInImage(
				CommonUrl.baseUrl + object.videoImgUrl+".small"+ object.suffix,
				holder.video_img);
		if (object.updateTime != null)
			holder.album_time.setText(object.updateTime.split(" ")[0]);
		holder.video_name.setText(object.albumVideoName);
		holder.update_episode.setText(object.albumVideoDescribe);
		if (showDeleteIcon)
			holder.delete_layout.setVisibility(View.VISIBLE);
		else
			holder.delete_layout.setVisibility(View.GONE);
		holder.album_add_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				videoList.remove(position);
				notifyDataSetChanged();
				Message msg = handler.obtainMessage();
				String seelistID = object.seelistID;
				Bundle bundle = new Bundle();
				bundle.putString("seelistID", seelistID);
				msg.setData(bundle);
				msg.what = WatchRecordActivity.DELETE_ONE_RECORD;
				handler.sendMessage(msg);

			}
		});
		// holder.album_add_cancel.setOnClickListener(new View.OnClickListener()
		// {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// switchStatus(holder.album_add_cancel);
		// }
		// });
		holder.play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				map.put("albumVideoID", object.albumVideoID);
				map.put("albumVideoname", object.albumVideoName);
				String albumVideoImgUrl=CommonUrl.baseUrl + object.videoImgUrl +".small"+ object.suffix;
				map.put("videoImgUrl", albumVideoImgUrl);
				CommonUtil.jumpToPlayerActivity(context, map);
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView video_img;
		TextView play, album_add_cancel;
		TextView video_name, album_time, update_episode;
		RelativeLayout delete_layout;
	}

	private void switchStatus(TextView textView) {
		if (isVideoAdd) {
			Drawable video_cancel = context.getResources().getDrawable(
					R.drawable.video_cancel);
			video_cancel.setBounds(0, 0, video_cancel.getMinimumWidth(),
					video_cancel.getMinimumHeight());
			textView.setCompoundDrawables(video_cancel, null, null, null);
			textView.setTextColor(Color.parseColor("#5c514d"));
			textView.setText(context.getString(R.string.cancelAlbum));
			isVideoAdd = false;
		} else {
			Drawable video_add = context.getResources().getDrawable(
					R.drawable.video_add);
			video_add.setBounds(0, 0, video_add.getMinimumWidth(),
					video_add.getMinimumHeight());
			textView.setCompoundDrawables(video_add, null, null, null);
			textView.setTextColor(Color.parseColor("#ffffff"));
			textView.setText(context.getString(R.string.addAlbum));
			isVideoAdd = true;
		}
	}
}
