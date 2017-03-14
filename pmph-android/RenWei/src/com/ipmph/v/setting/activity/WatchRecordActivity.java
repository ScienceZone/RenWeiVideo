package com.ipmph.v.setting.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.ipmph.v.adapter.WatchRecordAdapter;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.object.LoginResultObject;
import com.ipmph.v.object.WatchRecordObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class WatchRecordActivity extends FragmentActivity implements
		NetRequestIterface {

	private NetRequest netRequest;
	private PullToRefreshListView play_history_listview;
	private LinearLayout empty_prompt;
	private WatchRecordAdapter recordAdapter;
	private TextView delete, delete_All;
	private boolean showDelete = false, isLoadMore = false,
			firstLoadData = true;
	public static final int DELETE_ONE_RECORD = 0;
	private int pageNo = 1;
	List<WatchRecordObject> watchRecordList = new ArrayList<WatchRecordObject>();

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DELETE_ONE_RECORD:
				Bundle bundle = msg.getData();
				String watchrecordID = bundle.getString("watchrecordID");
				updateWatchRecord(watchrecordID);
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceStage) {
		super.onCreate(savedInstanceStage);
		setContentView(R.layout.watch_record_activity);
		play_history_listview = (PullToRefreshListView) findViewById(R.id.play_history_listview);
		empty_prompt = (LinearLayout) findViewById(R.id.empty_prompt);
		delete = (TextView) findViewById(R.id.delete);
		delete_All = (TextView) findViewById(R.id.delete_All);
		netRequest = new NetRequest(this, this);
		String sessionId = LoginResultObject.getInstance().sessionId;
		Log.d("gaolei", "sessionId ------WatchRecordActivity--------"
				+ sessionId);
		if (sessionId == null) {
			CommonUtil.showToast(this, getString(R.string.look_after_login));
			return;
		}
		getWatchRecordList();
		// 设置监听事件
		play_history_listview
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

						getWatchRecordList();
					}
				});

	}

	public void onBack(View view) {
		finish();
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		Log.d("gaolei", "result------WatchRecordActivity----------" + result);
		if (requestUrl.equals(CommonUrl.getWatchRecordList)) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				// Log.d("gaolei", "result-------------"+result);
				String jsonStr = object.getString("WatchRecordList");
				List<WatchRecordObject> watchRecordListPart = new Gson()
						.fromJson(jsonStr,
								new TypeToken<List<WatchRecordObject>>() {
								}.getType());
				watchRecordList.addAll(watchRecordListPart);
				if (watchRecordList.size() == 0) {
					empty_prompt.setVisibility(View.VISIBLE);
				}
				if (firstLoadData) {
					recordAdapter = new WatchRecordAdapter(watchRecordList,
							this, handler);
					play_history_listview.setAdapter(recordAdapter);
					firstLoadData = false;
				} else {
					if (watchRecordListPart.size() > 0) {
						recordAdapter.changeData(watchRecordList);
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				MainActivity.loadingDialog.cancel();
				stopRefresh(play_history_listview);
				e.printStackTrace();
			}
		}
		MainActivity.loadingDialog.cancel();
		stopRefresh(play_history_listview);
	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub

	}

	private void stopRefresh(PullToRefreshListView listView){
		listView.onRefreshComplete();
		isLoadMore=false;
	}
	
	public void getWatchRecordList() {
		if (isLoadMore) {
			pageNo += 1;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pageNo", pageNo);
		map.put("pageSize", 10);
		netRequest.httpRequestWithID(map, CommonUrl.getWatchRecordList);
		MainActivity.loadingDialog.show();
	}

	public void updateWatchRecord(String watchrecordID) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("watchrecordID", watchrecordID);
		netRequest.httpRequestWithID(map, CommonUrl.updateWatchRecord);
	}

	public void updateWatchRecordAll() {
		Map<String, Object> map = new HashMap<String, Object>();
		String sessionId = LoginResultObject.getInstance().sessionId;
		map.put("jeesite.session.id", sessionId);
		netRequest.httpRequest(map, CommonUrl.updateWatchRecordAll);
	}

	public void deleteRecordAll() {
		updateWatchRecordAll();
		recordAdapter.changeData(new ArrayList<WatchRecordObject>());
		empty_prompt.setVisibility(View.VISIBLE);
	}

	public void confirmDelAll(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.confirm_delete_all));
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteRecordAll();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}

		);
		builder.setCancelable(true);
		builder.create().show();
	}

	public void showItemDelete(View view) {
		if (recordAdapter == null)
			return;
		if (!showDelete) {
			recordAdapter.showDeleteIcon(true);
			delete.setText(getString(R.string.cancel));
			delete_All.setVisibility(View.VISIBLE);
			showDelete = true;
		} else {
			recordAdapter.showDeleteIcon(false);
			delete.setText(getString(R.string.delete));
			delete_All.setVisibility(View.GONE);
			showDelete = false;
		}
	}

	public void onDestroy() {
		super.onDestroy();
		MainActivity.loadingDialog.cancel();
	}
}
