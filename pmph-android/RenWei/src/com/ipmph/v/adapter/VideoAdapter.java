package com.ipmph.v.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.VideoPlayerActivity;
import com.ipmph.v.object.VideoClassObject.VideoListObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class VideoAdapter extends BaseAdapter {

	List<VideoListObject> videoList;
	LayoutInflater inflater;
	Context context;

	public VideoAdapter(List<VideoListObject> videoList, Context context) {
		this.videoList = videoList;
		this.context=context;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.video_gridview_item, null);
			holder.video_img = (ImageView) convertView
					.findViewById(R.id.video_img);
			holder.video_name = (TextView) convertView
					.findViewById(R.id.video_name);
			holder.video_desc = (TextView) convertView
					.findViewById(R.id.video_desc);
//			holder.video_class = (TextView) convertView
//					.findViewById(R.id.video_class);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final VideoListObject object = videoList.get(position);
//		Log.d("gaolei",
//				"url--------------" + CommonUrl.baseUrl + object.getImgUrl()
//						+ object.getImgSuffix());
		CommonUtil.getUtilInstance().displayLowQualityInImage(
				CommonUrl.baseUrl + object.getImgUrl() + object.getImgSuffix(),
				holder.video_img);
		holder.video_name.setText(object.getCategory());
		holder.video_desc.setText(object.getName());
//		holder.video_class.setText(object.getCategory());
		convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Map<String,String>map=new HashMap<String,String>();
				String[] strArr = object.getUrl().split("=");
				if(strArr.length>1)
				map.put("videoID", strArr[1]);
				map.put("videoname", object.getName());
				String albumVideoImgUrl=CommonUrl.baseUrl + object.getImgUrl() +".small"+ object.getImgSuffix();
				map.put("videoImgUrl", albumVideoImgUrl);
				CommonUtil.jumpToPlayerActivity(context, map);
			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView video_img;
		TextView video_name, video_desc,video_class;
	}
}
