package com.ipmph.v.adapter;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.object.MessageObject;
import com.ipmph.v.setting.activity.WatchRecordActivity;

public class MessageAdapter extends BaseAdapter {

	List<MessageObject> messageList;
	LayoutInflater inflater;
	private Context context;
	private boolean showDeleteIcon = false;
	private Handler handler;

	public MessageAdapter(List<MessageObject> messageList, Context context,Handler handler) {
		this.messageList = messageList;
		this.context = context;
		this.handler=handler;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messageList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return messageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	public void showDeleteIcon(boolean bool) {
		showDeleteIcon = bool;
		notifyDataSetChanged();
	}
	public void changeData(List<MessageObject> messageList){
		this.messageList=messageList;
		notifyDataSetChanged();
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.message_item, null);
			holder.user_name = (TextView) convertView
					.findViewById(R.id.user_name);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.delete_layout = (RelativeLayout) convertView
					.findViewById(R.id.delete_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final MessageObject object = messageList.get(position);
		String time = object.createTime;
		holder.time.setText(time);
		holder.message.setText(object.message);
		if (showDeleteIcon)
			holder.delete_layout.setVisibility(View.VISIBLE);
		else
			holder.delete_layout.setVisibility(View.GONE);
		holder.delete_layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				messageList.remove(position);
				notifyDataSetChanged();
				Message msg=handler.obtainMessage();
				String messageID=object.messageID;
				Bundle bundle=new Bundle();
				bundle.putString("messageID", messageID);
				msg.setData(bundle);
				msg.what=WatchRecordActivity.DELETE_ONE_RECORD;
				handler.sendMessage(msg);
				
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView user_name, time, message;
		RelativeLayout delete_layout;
	}
}
