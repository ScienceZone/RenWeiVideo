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
import com.ipmph.v.adapter.AlbumPromptAdapter;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.fragment.MyFragment;
import com.ipmph.v.object.AlbumPromptObject;
import com.ipmph.v.object.LoginResultObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class AlbumPromptActivity extends FragmentActivity implements
		NetRequestIterface {

	private NetRequest netRequest;
	private LinearLayout empty_prompt;
	private PullToRefreshListView album_prompt_listview;
	private TextView delete, delete_All;
	private boolean showDelete = false, isLoadMore = false,
			firstLoadData = true;
	public static final int DELETE_ONE_RECORD = 0;
	private AlbumPromptAdapter messageAdapter;
	private int pageNo = 1;
	List<AlbumPromptObject> albumList = new ArrayList<AlbumPromptObject>();

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DELETE_ONE_RECORD:
				Bundle bundle = msg.getData();
				String seelistID = bundle.getString("seelistID");
				updateSeeListDelFlag(seelistID);
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceStage) {
		super.onCreate(savedInstanceStage);
		setContentView(R.layout.album_prompt_activity);
		netRequest = new NetRequest(this, this);
		empty_prompt = (LinearLayout) findViewById(R.id.empty_prompt);
		album_prompt_listview = (PullToRefreshListView) findViewById(R.id.album_prompt_listview);
		delete = (TextView) findViewById(R.id.delete);
		String sessionId = LoginResultObject.getInstance().sessionId;
		if (sessionId == null) {
			CommonUtil.showToast(this, getString(R.string.look_after_login));
			return;
		}
		getSeeList();
		album_prompt_listview
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						isLoadMore = true;

						getSeeList();
					}
				});
	}

	public void onBack(View view) {
		finish();
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		Log.d("gaolei", "result-------AlbumPromptActivity---------" + result);
		JSONObject object;
		try {
			object = new JSONObject(result);
			// Log.d("gaolei", "result-------------"+result);
			String jsonStr = object.getString("seeList");
			List<AlbumPromptObject> albumListPart = new Gson().fromJson(
					jsonStr, new TypeToken<List<AlbumPromptObject>>() {
					}.getType());
			albumList.addAll(albumListPart);
			if (albumList.size() == 0) {
				empty_prompt.setVisibility(View.VISIBLE);
			}
			if (firstLoadData) {
				messageAdapter = new AlbumPromptAdapter(albumList, this,
						handler);
				album_prompt_listview.setAdapter(messageAdapter);
				firstLoadData = false;
			} else {
				if (albumListPart.size() > 0) {
					messageAdapter.changeData(albumList);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MainActivity.loadingDialog.cancel();
			stopRefresh(album_prompt_listview);
		}
		MainActivity.loadingDialog.cancel();
		stopRefresh(album_prompt_listview);
	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub
		MainActivity.loadingDialog.cancel();

	}

	private void stopRefresh(PullToRefreshListView listView) {
		listView.onRefreshComplete();
		isLoadMore = false;
	}

	public void getSeeList() {
		if (isLoadMore) {
			pageNo += 1;
		}
		MainActivity.loadingDialog.show();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pageNo", pageNo);
		map.put("pageSize", 20);
		netRequest.httpRequestWithID(map, CommonUrl.getSeeList);
	}

	public void updateSeeListDelFlag(String seelistID) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seelistID", seelistID);
		netRequest.httpRequestWithID(map, CommonUrl.updateSeeListDelFlag);
	}

	public void showItemDelete(View view) {
		MyFragment.changeGaoLei(29);
		MyFragment.zhaoyue = 27;
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

	public void onDestroy() {
		super.onDestroy();
		MainActivity.loadingDialog.cancel();
	}
}
