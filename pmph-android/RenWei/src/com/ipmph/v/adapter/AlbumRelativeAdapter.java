package com.ipmph.v.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.object.AlbumDetailObject.AlbumOtherVideoObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class AlbumRelativeAdapter extends BaseAdapter {

	List<AlbumOtherVideoObject> list;
	LayoutInflater inflater;
	Context context;

	public AlbumRelativeAdapter(List<AlbumOtherVideoObject> list, Context context) {
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

	public void changeList(List<AlbumOtherVideoObject> list) {
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
			holder.video_play_num = (TextView) convertView
					.findViewById(R.id.video_play_num);
			holder.video_desc = (TextView) convertView
					.findViewById(R.id.video_desc);
			holder.video_category = (TextView) convertView
					.findViewById(R.id.video_category);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AlbumOtherVideoObject  object = list.get(position);
		CommonUtil.getUtilInstance().displayLowQualityInImage(
				CommonUrl.baseUrl + object.albumVideoImgUrl +".small"+ object.suffix,
				holder.video_img);
		if(object.albumPlayNums!=-1)
		holder.video_play_num.setText(object.albumPlayNums+"");
		if(object.albumVideopnumber!=-1)
		holder.video_play_num.setText(object.albumVideopnumber+"");
		holder.video_desc.setText(object.albumVideoname);
		holder.video_category.setText(object.albumVideodetails);
		return convertView;
	}

	class ViewHolder {
		ImageView video_img;
		TextView video_play_num, video_desc,video_category;
	}
}
