package com.ipmph.v.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.object.AlbumDetailObject.AlbumContentObject.AlbumVideoObject;

public class AlbumSeriesAdapter extends BaseAdapter {

	List<AlbumVideoObject> list;
	LayoutInflater inflater;
	Context context;
	int selectedPosition = 0;
	List<AlbumVideoObject> subVideoList = new ArrayList<AlbumVideoObject>();

	public AlbumSeriesAdapter(List<AlbumVideoObject> list, Context context) {
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
		if(list.size()>4){
		subVideoList = list.subList(0, 5);
		}else{
			subVideoList = list;	
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return subVideoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return subVideoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void changeList(int position) {
		int quotient = list.size() / 5;
		if (position < quotient) {
			subVideoList = list.subList(5 * position, 5 * position + 5);
		} else {
			subVideoList = list.subList(5 * position, list.size());
		}
		notifyDataSetChanged();
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
			convertView = inflater.inflate(R.layout.album_series_item, null);

			holder.subclass_name = (TextView) convertView
					.findViewById(R.id.subclass_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AlbumVideoObject object = subVideoList.get(position);
		if (position == selectedPosition) {
			holder.subclass_name.setTextColor(Color.parseColor("#fc5e4f"));
		} else {
			holder.subclass_name.setTextColor(Color.parseColor("#8a8a8a"));
		}
		holder.subclass_name.setText(object.videoname);
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
