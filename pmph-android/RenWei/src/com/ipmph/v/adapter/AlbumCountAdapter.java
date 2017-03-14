package com.ipmph.v.adapter;

import java.util.ArrayList;
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
import com.ipmph.v.object.AlbumDetailObject.AlbumContentObject.AlbumVideoObject;

public class AlbumCountAdapter extends BaseAdapter {

	List<AlbumVideoObject> list;
	LayoutInflater inflater;
	Context context;
	public int selectedPosition = 0;
	List<String> videoCountList = new ArrayList<String>();

	public AlbumCountAdapter(List<AlbumVideoObject> list, Context context) {
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
		int size = list.size();
		int quotient = size / 5;
		int remainder = size % 5;
		int length;
		if (remainder == 0)
			length = quotient;
		else
			length = quotient + 1;
		
		for (int i = 0; i < length; i++) {
			if (i == quotient) {
				videoCountList.add((5 * i + 1) + "-" + size);
				return;
			}
			videoCountList.add((5 * i + 1) + "-" + 5 * (i + 1));
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return videoCountList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return videoCountList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void changeList(List<AlbumVideoObject> list) {
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
			convertView = inflater.inflate(R.layout.album_count_item, null);

			holder.subclass_name = (TextView) convertView
					.findViewById(R.id.subclass_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final String count = videoCountList.get(position);
		Log.d("gaolei", "position-------------------------" + position);
		if (position == selectedPosition) {
			holder.subclass_name.setTextColor(Color.parseColor("#fc5e4f"));
		} else {
			holder.subclass_name.setTextColor(Color.parseColor("#8a8a8a"));
		}
		holder.subclass_name.setText(count);
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
