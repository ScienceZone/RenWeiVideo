package com.ipmph.v.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.object.HeaderClassObject.SubClassObject;

public class RankSubClassAdapter extends BaseAdapter {

	public List<SubClassObject> list;
	LayoutInflater inflater;
	Context context;
	int selectedPosition = 0;

	public RankSubClassAdapter(List<SubClassObject> list, Context context) {
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

	public void changeList(List<SubClassObject> list) {
		this.list = list;
	}
	public void changePosition(int position) {
		selectedPosition = position;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.rankclass_title_item, null);

			holder.subclass_name = (TextView) convertView
					.findViewById(R.id.subclass_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final SubClassObject object = list.get(position);
		if (position == selectedPosition) {
			holder.subclass_name.setTextColor(Color.parseColor("#00a1f1"));
		} else {
			holder.subclass_name.setTextColor(Color.parseColor("#bb000000"));
		}
		holder.subclass_name.setText(object.videoclassName);
		return convertView;
	}

	class ViewHolder {
		TextView subclass_name;
	}
}
