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
import com.ipmph.v.callback.MyInterface.OnClickItemListener;
import com.ipmph.v.object.VideoClassObject;

public class SubClassTitle1Adapter extends BaseAdapter {

	List<VideoClassObject> list;
	LayoutInflater inflater;
	Context context;
	int selectedPosition = 0;
	public OnClickItemListener onClickItemListener;

	public void setOnClickItemListener(OnClickItemListener listener) {
		onClickItemListener = listener;
	}

	public SubClassTitle1Adapter(List<VideoClassObject> list, Context context) {
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

	public void changeList(List<VideoClassObject> list) {
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
			convertView = inflater.inflate(R.layout.subclass_title_item, null);

			holder.subclass_name = (TextView) convertView
					.findViewById(R.id.subclass_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final VideoClassObject object = list.get(position);
		if (position == selectedPosition) {
			convertView.setBackgroundResource(R.drawable.class_title_select2);
			holder.subclass_name.setTextColor(Color.parseColor("#bbffffff"));
		} else {
			convertView.setBackgroundResource(R.drawable.class_title_unselect2);
			holder.subclass_name.setTextColor(Color.parseColor("#bb000000"));
		}
		holder.subclass_name.setText(object.name);
//		 convertView.setOnClickListener(new View.OnClickListener() {
//		
//		 @Override
//		 public void onClick(View v) {
//		 // TODO Auto-generated method stub
//			 onClickItemListener.onClickItem(position);
//		 Log.d("gaolei", "onClick-------Title1---------------");
//		 }
//		 });
		return convertView;
	}

	
	class ViewHolder {
		TextView subclass_name;
	}
}
