package com.ipmph.v.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.VideoPlayerActivity;
import com.ipmph.v.object.VideoDetailObject.OtherVideoObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class HotAdapter extends BaseAdapter {

	List<OtherVideoObject> list;
	LayoutInflater inflater;
	Context context;

	public HotAdapter(List<OtherVideoObject> list, Context context) {
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void changeList(List<OtherVideoObject> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.video_gridview_item2, null);
			holder.video_img = (ImageView) convertView
					.findViewById(R.id.video_img);
			holder.video_name = (TextView) convertView
					.findViewById(R.id.video_name);
			holder.video_desc = (TextView) convertView
					.findViewById(R.id.video_desc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final OtherVideoObject object = list.get(position);
		Log.d("gaolei", "url--------------" + CommonUrl.baseUrl
				+ object.videoImgUrl + object.suffix);
		CommonUtil.getUtilInstance().displayLowQualityInImage(
				CommonUrl.baseUrl + object.videoImgUrl + object.suffix,
				holder.video_img);
		holder.video_name.setText(object.videopnumber + "");
		holder.video_desc.setText(object.videoname);
		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Map<String,String>map=new HashMap<String,String>();
				map.put("videoID", object.videoID);
				map.put("videoname", object.videoname);
				String albumVideoImgUrl=CommonUrl.baseUrl + object.videoImgUrl +".small"+ object.suffix;
				map.put("videoImgUrl", albumVideoImgUrl);
				CommonUtil.jumpToPlayerActivity(context, map);
			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView video_img;
		TextView video_name, video_desc;
	}
}
