package com.ipmph.v.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ipmph.v.MainActivity;
import com.ipmph.v.R;
import com.ipmph.v.adapter.AlbumListAdapter;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.MyInterface.OnClickAddListener;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.object.AlbumObject.AlbumListObject;
import com.ipmph.v.tool.AndroidUtil;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class AlbumFragment extends Fragment implements NetRequestIterface,
		OnClickAddListener {

	private NetRequest netRequest;
	private PullToRefreshListView album_listview;
	private boolean isLoadMore = false, firstLoadData = true;
	private int pageNo = 1;
	private List<AlbumListObject> albumList = new ArrayList<AlbumListObject>();
	private AlbumListAdapter albumAdapter;
	private TextView album_add_cancel;
	private String seelistID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.album_fragment, null);
		album_listview = (PullToRefreshListView) view
				.findViewById(R.id.album_listview);
		netRequest = new NetRequest(this, getActivity());
		getAlbumVideoList();
		album_listview.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				isLoadMore = true;
				Log.d("gaolei", "onRefresh------WatchRecordActivity----------");
				String label = DateUtils.formatDateTime(getActivity(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				// 显示最后更新的时间
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				getAlbumVideoList();
			}
		});
		return view;
	}

	private void getAlbumVideoList() {
		if (isLoadMore) {
			pageNo += 1;
		}
		Map<String, Object> albumMap = new HashMap<String, Object>();
		albumMap.put("pageSize", 20);
		albumMap.put("pageNo", pageNo);
		netRequest.httpRequestWithID(albumMap, CommonUrl.getAlbumVideoList);

		MainActivity.loadingDialog.show();
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		JSONObject object;
		Log.d("gaolei", "result-------------" + result);
		if (requestUrl.equals(CommonUrl.getAlbumVideoList)) {
			try {
				object = new JSONObject(result);
				String jsonStr = object.getString("albumVideoList");
				List<AlbumListObject> albumListPart = new Gson().fromJson(
						jsonStr, new TypeToken<List<AlbumListObject>>() {
						}.getType());
				albumList.addAll(albumListPart);
				Log.d("gaolei",
						"albumList.size()-------------" + albumList.size());
				if (firstLoadData) {
					albumAdapter = new AlbumListAdapter(albumList,
							getActivity(), this);
					album_listview.setAdapter(albumAdapter);
					firstLoadData = false;
				} else {
					if (albumListPart.size() > 0) {
						albumAdapter.changeList(albumList);
						albumAdapter.notifyDataSetChanged();
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MainActivity.loadingDialog.cancel();
				stopRefresh(album_listview);
			}
			MainActivity.loadingDialog.cancel();
			stopRefresh(album_listview);
		}
		if (requestUrl.equals(CommonUrl.updateSeeListDelFlag)) {
			try {
				object = new JSONObject(result);
				String message = object.getString("message");
				
				if (message.equals("成功")) {
					CommonUtil.showToast(getActivity(), "取消看单成功");
					CommonUtil.changeTextDrawable(getActivity(),
							album_add_cancel, "left", R.drawable.video_add,
							R.string.add_watch_list, "#ffffff");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(CommonUrl.addSeeList)) {
			// Log.d("gaolei", "result-------addSeeList-------" + result);
			try {
				object = new JSONObject(result);
				String message = object.getString("message");
				seelistID = object.getString("seelistID");
				if (message.equals("成功")) {
					CommonUtil.showToast(getActivity(), "加入看单成功");
					CommonUtil.changeTextDrawable(getActivity(),
							album_add_cancel, "left", R.drawable.video_cancel,
							R.string.cancel_watch_list, "#5c514d");
				}

				if (message.equals("已加入看单"))
					CommonUtil.showToast(getActivity(), "已加入看单");
				CommonUtil.changeTextDrawable(getActivity(), album_add_cancel,
						"left", R.drawable.video_cancel,
						R.string.cancel_watch_list, "#5c514d");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	@Override
	public void onClickAdd(String albumVideoID, String seelistID,
			TextView album_add_cancel) {
		// TODO Auto-generated method stub
		this.album_add_cancel = album_add_cancel;
		AndroidUtil.judgeIfLogined(getActivity());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("albumVideoID", albumVideoID);
		netRequest.httpRequestWithID(map, CommonUrl.addSeeList);
	}

	@Override
	public void onClickCancel(String albumVideoID, String seelistID,
			TextView album_add_cancel) {
		// TODO Auto-generated method stub
		AndroidUtil.judgeIfLogined(getActivity());
		this.album_add_cancel = album_add_cancel;
		Map<String, Object> map = new HashMap<String, Object>();
		if (this.seelistID == null)
			map.put("seelistID", seelistID);
		else
			map.put("seelistID", this.seelistID);
		netRequest.httpRequestWithID(map, CommonUrl.updateSeeListDelFlag);
	}

}
