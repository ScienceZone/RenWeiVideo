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
import com.ipmph.v.adapter.MessageAdapter;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.object.LoginResultObject;
import com.ipmph.v.object.MessageObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class MessageActivity extends FragmentActivity implements
		NetRequestIterface {

	private NetRequest netRequest;
	private LinearLayout empty_prompt;
	private PullToRefreshListView message_listview;
	private TextView delete, delete_All;
	private boolean showDelete = false, isLoadMore = false,
			firstLoadData = true;
	public static final int DELETE_ONE_RECORD = 0;
	private MessageAdapter messageAdapter;
	private int pageNo = 1;
	List<MessageObject> messageList=new ArrayList<MessageObject>();
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DELETE_ONE_RECORD:
				Bundle bundle = msg.getData();
				String messageID = bundle.getString("messageID");
				updateMessage(messageID);
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceStage) {
		super.onCreate(savedInstanceStage);
		setContentView(R.layout.message_activity);
		empty_prompt = (LinearLayout) findViewById(R.id.empty_prompt);
		message_listview = (PullToRefreshListView) findViewById(R.id.message_listview);
		delete = (TextView) findViewById(R.id.delete);
		netRequest = new NetRequest(this, this);
		String sessionId = LoginResultObject.getInstance().sessionId;
		if (sessionId == null) {
		CommonUtil.showToast(this, getString(R.string.look_after_login));
		return;
		}
		getMessageList();
		message_listview
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

				getMessageList();
			}
		});

	}

	public void onBack(View view) {
		finish();
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		Log.d("gaolei", "result--------MessageActivity--------" + result);
		JSONObject object;
		try {
			object = new JSONObject(result);
			// Log.d("gaolei", "result-------------"+result);
			String jsonStr = object.getString("messageList");
			List<MessageObject> messageListPart = new Gson().fromJson(jsonStr,
					new TypeToken<List<MessageObject>>() {
					}.getType());
			messageList.addAll(messageListPart);
			if (messageList.size() == 0) {
				empty_prompt.setVisibility(View.VISIBLE);
			}
			if (firstLoadData) {
				messageAdapter = new MessageAdapter(messageList, this, handler);
				message_listview.setAdapter(messageAdapter);
				firstLoadData = false;
			} else {
				if (messageListPart.size() > 0) {
					messageAdapter.changeData(messageList);
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MainActivity.loadingDialog.cancel();
			stopRefresh(message_listview);
		}
		MainActivity.loadingDialog.cancel();
		stopRefresh(message_listview);
	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub
		MainActivity.loadingDialog.cancel();
	}
	private void stopRefresh(PullToRefreshListView listView){
		listView.onRefreshComplete();
		isLoadMore=false;
	}
	public void getMessageList() {
		if (isLoadMore) {
			pageNo += 1;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("messageType", 0);
		map.put("pageNo", pageNo);
		map.put("pageSize", 20);
		netRequest.httpRequestWithID(map, CommonUrl.getMessageList);
		MainActivity.loadingDialog.show();
	}

	public void updateMessage(String messageID) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("messigeIDStr", messageID);
		netRequest.httpRequestWithID(map, CommonUrl.updateMessage);

	}

	public void showItemDelete(View view) {
		if (messageAdapter == null)
			return;
		if (!showDelete) {
			messageAdapter.showDeleteIcon(true);
			delete.setText(getString(R.string.cancel));
			// delete_All.setVisibility(View.VISIBLE);
			showDelete = true;
		} else {
			messageAdapter.showDeleteIcon(false);
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
