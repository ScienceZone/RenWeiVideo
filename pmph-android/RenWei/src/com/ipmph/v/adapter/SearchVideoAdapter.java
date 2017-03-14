package com.ipmph.v.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.object.SearchResultObject.VideoListObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class SearchVideoAdapter extends BaseAdapter {

	List<VideoListObject> videoList;
	LayoutInflater inflater;
	private Context context;

	public SearchVideoAdapter(List<VideoListObject> videoList, Context context) {
		this.videoList = videoList;
		this.context = context;
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
	public void changeData(List<VideoListObject> videoList){
		this.videoList=videoList;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.more_listview_item, null);
			holder.video_img = (ImageView) convertView
					.findViewById(R.id.video_img);
			holder.video_name = (TextView) convertView
					.findViewById(R.id.video_name);
			holder.video_class = (TextView) convertView
					.findViewById(R.id.video_class);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.times = (TextView) convertView.findViewById(R.id.times);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final VideoListObject object = videoList.get(position);
		CommonUtil.getUtilInstance().displayLowQualityInImage(
				CommonUrl.baseUrl + object.videoImgUrl +".small"+ object.suffix,
				holder.video_img);
		holder.video_name.setText(object.videoname);
//		holder.video_class.setText(object.videoname);
		String time = object.videoProductTime;
		if (time.contains(" ")) {
			holder.video_class.setText(time.split(" ")[0]);
		} else {
			holder.video_class.setText(object.videoProductTime);
		}
		holder.times.setText(object.videopnumber + "");
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
		TextView video_name,video_class, time, times;
	}
}
