package com.ipmph.v;

import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.ipmph.v.adapter.RecommendMoreAdapter;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.object.VideoClassObject.VideoListObject;

public class VideoMoreActivity extends BaseActivity implements
		NetRequestIterface {

	private NetRequest requestFragment;
	private ArrayList<VideoListObject> recommendList;
	private ListView more_recommend_listview;

	public void onCreate(Bundle savedInstanceStage) {
		super.onCreate(savedInstanceStage);
		setContentView(R.layout.video_more_activity);
		recommendList = (ArrayList<VideoListObject>) getIntent()
				.getSerializableExtra("recommendList");
		Log.d("gaolei", "recommendList------MoreRecommendActivity--------"+recommendList);
		requestFragment = new NetRequest(this, this);
		more_recommend_listview = (ListView) findViewById(R.id.more_recommend_listview);
		if(recommendList!=null){
		RecommendMoreAdapter moreAdapter = new RecommendMoreAdapter(
				recommendList, this);
		more_recommend_listview.setAdapter(moreAdapter);
//		more_recommend_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				final VideoListObject object = recommendList.get(position);
//					
//			}
//		});
		}
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub

	}
	public void onBack(View view){
		finish();
	}
}
