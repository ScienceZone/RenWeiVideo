package com.ipmph.v.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipmph.v.LoginActivity;
import com.ipmph.v.R;
import com.ipmph.v.callback.MyInterface.OnClickAddListener;
import com.ipmph.v.object.AlbumObject.AlbumListObject;
import com.ipmph.v.object.LoginResultObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class AlbumListAdapter extends BaseAdapter {

	List<AlbumListObject> videoList;
	LayoutInflater inflater;
	Context context;
//	private boolean isVideoAdd = true;
	OnClickAddListener onClickAddListener;

	public AlbumListAdapter(List<AlbumListObject> videoList, Context context,
			OnClickAddListener listener) {
		this.videoList = videoList;
		inflater = LayoutInflater.from(context);
		this.context = context;
		onClickAddListener = listener;
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

	public void changeList(List<AlbumListObject> videoList) {
		this.videoList = videoList;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = inflater.inflate(R.layout.album_listview_item, null);
		ImageView video_img = (ImageView) convertView
				.findViewById(R.id.video_img);
		TextView video_name = (TextView) convertView
				.findViewById(R.id.video_name);
		TextView album_desc = (TextView) convertView
				.findViewById(R.id.album_desc);
		TextView album_time = (TextView) convertView
				.findViewById(R.id.album_time);
		TextView english_name = (TextView) convertView
				.findViewById(R.id.english_name);
		TextView medical_department = (TextView) convertView
				.findViewById(R.id.medical_department);
		TextView common_cause = (TextView) convertView
				.findViewById(R.id.common_cause);
		TextView common_symptom = (TextView) convertView
				.findViewById(R.id.common_symptom);
		TextView isInfect = (TextView) convertView.findViewById(R.id.isInfect);
		final TextView album_add_cancel = (TextView) convertView
				.findViewById(R.id.album_add_cancel);
		TextView play = (TextView) convertView.findViewById(R.id.play);
		final AlbumListObject object = videoList.get(position);
		final String albumVideoImgUrl=CommonUrl.baseUrl + object.albumVideoImgUrl +".special"+ object.suffix;
		CommonUtil.getUtilInstance().displayLowQualityInImageVertical(
				albumVideoImgUrl,video_img);
		if (object.updateTime != null)
			album_time.setText(object.updateTime.split(" ")[0]);
		if (object.albumcreateTime != null)
			album_time.setText(object.albumcreateTime.split(" ")[0]);
		
		video_name.setText(object.albumVideoname);
		album_desc.setText(object.albumVideodetails);
		english_name.setText(context.getString(R.string.english_name)
				+ object.videoEnglishName);
		medical_department.setText(context
				.getString(R.string.medical_department) + object.jiuzhen);
		common_cause.setText(context.getString(R.string.common_cause)
				+ object.bingyin);
		common_symptom.setText(context.getString(R.string.common_symptom)
				+ object.zhengzhuang);
		if (object.isSeeList == 1) {
			CommonUtil.changeTextDrawable(context, album_add_cancel, "left",
					R.drawable.video_cancel, R.string.cancel_watch_list,
					"#5c514d");
		} else {
			CommonUtil.changeTextDrawable(context, album_add_cancel, "left",
					R.drawable.video_add, R.string.add_watch_list, "#ffffff");
		}

		String Infect;
		if (object.chuanran == 0)
			Infect = "否";
		else
			Infect = "是";
		isInfect.setText(context.getString(R.string.isInfect) + Infect);
		album_add_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switchStatus(object.albumVideoID, object.seeListID,
						album_add_cancel);
			}
		});
		play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				map.put("albumVideoID", object.albumVideoID);
				map.put("albumVideoname", object.albumVideoname);
				map.put("videoImgUrl", albumVideoImgUrl);
				CommonUtil.jumpToPlayerActivity(context, map);
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView video_img;
		TextView play, album_add_cancel;
		TextView video_name, album_desc, english_name, medical_department,
				common_cause, common_symptom, isInfect, album_time;
	}

	private void switchStatus(String albumId, String seelistID,
			TextView album_add_cancel) {
		String sessionId = LoginResultObject.getInstance().sessionId;
		if (sessionId == null) {
			Toast.makeText(context,
					context.getString(R.string.operate_after_login),
					Toast.LENGTH_LONG).show();
			context.startActivity(new Intent(context, LoginActivity.class));
			return;
		}
		if (album_add_cancel.getText().equals(
				context.getString(R.string.add_watch_list))) {
//			isVideoAdd = false;
			onClickAddListener.onClickAdd(albumId, seelistID, 
					album_add_cancel);
		} else {
//			isVideoAdd = true;
			onClickAddListener.onClickCancel(albumId, seelistID, 
					album_add_cancel);
		}
	}
}
