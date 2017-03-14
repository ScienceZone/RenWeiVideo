package com.ipmph.v.setting.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ipmph.v.MainActivity;
import com.ipmph.v.R;
import com.ipmph.v.adapter.VideoCollectAdapter;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.object.LoginResultObject;
import com.ipmph.v.object.VideoCollectObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class VideoCollectActivity extends FragmentActivity implements
		NetRequestIterface {

	private NetRequest netRequest;
	private PullToRefreshListView video_collect_listview;
	private LinearLayout empty_prompt;
	private VideoCollectAdapter videoCollectAdapter;
	private TextView delete, delete_All;
	private boolean showDelete = false, isLoadMore = false,
			firstLoadData = true;
	private int pageNo = 1;
	public static final int DELETE_ONE_RECORD = 0;
	List<VideoCollectObject> videoCollectList=new ArrayList<VideoCollectObject>();

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DELETE_ONE_RECORD:
				Bundle bundle = msg.getData();
				String collectionID = bundle.getString("collectionID");
				updateCollectionDelFlag(collectionID);
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceStage) {
		super.onCreate(savedInstanceStage);
		setContentView(R.layout.video_collect_activity);
		video_collect_listview = (PullToRefreshListView) findViewById(R.id.video_collect_listview);
		empty_prompt = (LinearLayout) findViewById(R.id.empty_prompt);
		delete = (TextView) findViewById(R.id.delete);
		delete_All = (TextView) findViewById(R.id.delete_All);
		netRequest = new NetRequest(this, this);
		String sessionId = LoginResultObject.getInstance().sessionId;
		if (sessionId == null) {
			CommonUtil.showToast(this, getString(R.string.look_after_login));
			return;
		}
		collectionList();
		video_collect_listview
		.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(
					PullToRefreshBase<ListView> refreshView) {
				isLoadMore = true;
				Log.d("gaolei",
						"onRefresh------WatchRecordActivity----------");
				String label = DateUtils.formatDateTime(
						getApplicationContext(),
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				// 显示最后更新的时间
				refreshView.getLoadingLayoutProxy()
						.setLastUpdatedLabel(label);

				collectionList();
			}
		});
	}

	public void onBack(View view) {
		finish();
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		Log.d("gaolei", "result-------VideoCollectActivity---------" + result);
		if (requestUrl.equals(CommonUrl.collectionList)) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				// Log.d("gaolei", "result-------------"+result);
				String jsonStr = object.getString("collectionList");
				List<VideoCollectObject> watchRecordListPart = new Gson().fromJson(
						jsonStr, new TypeToken<List<VideoCollectObject>>() {
						}.getType());
				videoCollectList.addAll(watchRecordListPart);
				if (videoCollectList.size() == 0) {
					empty_prompt.setVisibility(View.VISIBLE);
				}
				if (firstLoadData) {
					videoCollectAdapter = new VideoCollectAdapter(videoCollectList, this,
							handler);
					video_collect_listview.setAdapter(videoCollectAdapter);
					firstLoadData = false;
				} else {
					if (watchRecordListPart.size() > 0) {
						videoCollectAdapter.changeData(videoCollectList);
					}
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				MainActivity.loadingDialog.cancel();
				stopRefresh(video_collect_listview);
				e.printStackTrace();
			}
		}
		MainActivity.loadingDialog.cancel();
		stopRefresh(video_collect_listview);
	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub

	}
	private void stopRefresh(PullToRefreshListView listView){
		listView.onRefreshComplete();
		isLoadMore=false;
	}
	public void collectionList() {
		if (isLoadMore) {
			pageNo += 1;
		}
		MainActivity.loadingDialog.show();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pageNo", pageNo);
		map.put("pageSize", 20);
		netRequest.httpRequestWithID(map, CommonUrl.collectionList);
	}

	public void updateCollectionDelFlag(String collectionID) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("collectionID", collectionID);
		netRequest.httpRequestWithID(map, CommonUrl.updateCollectionDelFlag);
	}

	public void showItemDelete(View view) {
		if (videoCollectAdapter == null)
			return;
		if (!showDelete) {
			videoCollectAdapter.showDeleteIcon(true);
			delete.setText(getString(R.string.cancel));
			// delete_All.setVisibility(View.VISIBLE);
			showDelete = true;
		} else {
			videoCollectAdapter.showDeleteIcon(false);
			delete.setText(getString(R.string.delete));
			// delete_All.setVisibility(View.GONE);
			showDelete = false;
		}
	}
	public void onDestroy(){
		super.onDestroy();
		MainActivity.loadingDialog.cancel();
	}
}
