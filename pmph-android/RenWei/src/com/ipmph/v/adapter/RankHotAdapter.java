package com.ipmph.v.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class RankHotAdapter extends BaseAdapter {

	List<OtherVideoObject> list;
	LayoutInflater inflater;
	Context context;

	public RankHotAdapter(List<OtherVideoObject> list, Context context) {
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
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.rank_gridview_item, null);
			holder.video_img = (ImageView) convertView
					.findViewById(R.id.video_img);
			holder.video_play_num = (TextView) convertView
					.findViewById(R.id.video_play_num);
			holder.video_desc = (TextView) convertView
					.findViewById(R.id.video_desc);
			holder.video_category = (TextView) convertView
					.findViewById(R.id.video_category);
			holder.video_rank = (TextView) convertView
					.findViewById(R.id.video_rank);
			holder.video_rank.setTag(position+"");
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final OtherVideoObject object = list.get(position);
		Log.d("gaolei", "url--------------" + CommonUrl.baseUrl
				+ object.videoImgUrl + object.suffix);
		CommonUtil.getUtilInstance().displayLowQualityInImage(
				CommonUrl.baseUrl + object.videoImgUrl +".small"+ object.suffix,
				holder.video_img);
		holder.video_play_num.setText(object.videopnumber + "");
		holder.video_desc.setText(object.videoname);
		holder.video_category.setText(object.videodetails);
		if(position==0){
		holder.video_rank.setText("1");
		holder.video_rank.setTextColor(Color.parseColor("#ffffff"));
		holder.video_rank.setBackgroundColor(Color.parseColor("#fe0000"));
		}
		else if(position==1){
			holder.video_rank.setText("2");
			holder.video_rank.setTextColor(Color.parseColor("#ffffff"));
			holder.video_rank.setBackgroundColor(Color.parseColor("#ff6100"));
		}else if(position==2){
			holder.video_rank.setText("3");
			holder.video_rank.setTextColor(Color.parseColor("#ffffff"));
			holder.video_rank.setBackgroundColor(Color.parseColor("#ff7e00"));
		}else{
			holder.video_rank.setText(""+(position+1));
			holder.video_rank.setTextColor(Color.parseColor("#4a4a4a"));
			holder.video_rank.setBackgroundColor(Color.parseColor("#eeeeee"));
		
		}
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
		TextView video_play_num, video_desc,video_category,video_rank;
	}
}
