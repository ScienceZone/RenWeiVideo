package com.ipmph.v.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.object.VideoDetailObject.VideoPlayObject.VideoLabelObject;

public class LabelAdapter extends BaseAdapter {

	List<VideoLabelObject> list;
	LayoutInflater inflater;
	Context context;
	int selectedPosition =-1;

	public LabelAdapter(List<VideoLabelObject> list, Context context) {
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

	public void changeList(List<VideoLabelObject> list) {
		this.list = list;
	}

	public void changePosition(int position) {
		selectedPosition = position;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.label_item, null);

			holder.subclass_name = (TextView) convertView
					.findViewById(R.id.subclass_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final VideoLabelObject object = list.get(position);
		Log.d("gaolei", "position-------------------------"+position);
//		if (position == selectedPosition) {
//			holder.subclass_name.setTextColor(Color.parseColor("#00a1f1"));
//		} else {
//			holder.subclass_name.setTextColor(Color.parseColor("#8a8a8a"));
//		}
		holder.subclass_name.setText(object.videoLabelName);
		// convertView.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// onClickItemListener.onClickItem(position);
		// Log.d("gaolei", "onClick-------Title1---------------");
		// }
		// });
		return convertView;
	}

	class ViewHolder {
		TextView subclass_name;
	}
}
