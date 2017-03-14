package com.ipmph.v.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.object.ClassFilterObject;

public class ClassFilter2Adapter extends BaseAdapter {

	List<ClassFilterObject> list;
	LayoutInflater inflater;
	Context context;
	private int selectPosition;
	private String selectText = "";

	public ClassFilter2Adapter(List<ClassFilterObject> list, Context context) {
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

	public void changeList(List<ClassFilterObject> list) {
		this.list = list;
	}

	public void changePosition(int position) {
		selectPosition = position;
	}

	public void setSelectText(String selectText) {
		this.selectText = selectText;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.class_filter2_item, null);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.root_layout = (RelativeLayout) convertView
					.findViewById(R.id.root_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ClassFilterObject object = list.get(position);
//		Log.d("gaolei", "selectText--------------" + selectText);
//		Log.d("gaolei", "object.videoclassName--------------"
//				+ object.videoclassName);
		if (selectPosition == position) {
			holder.root_layout.setBackgroundColor(Color.parseColor("#f6f6f6"));
		} else {
			holder.root_layout.setBackgroundColor(Color.parseColor("#ffffff"));
		}
		// if (selectText.equals(object.videoclassName)) {
		// holder.root_layout.setBackgroundColor(Color
		// .parseColor("#f6f6f6"));
		// } else {
		// holder.root_layout.setBackgroundColor(Color
		// .parseColor("#ffffff"));
		// }
		holder.content.setText(object.videoclassName);

		return convertView;
	}

	class ViewHolder {
		RelativeLayout root_layout;
		TextView title, content;
	}
}
