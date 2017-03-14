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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.ipmph.v.MainActivity;
import com.ipmph.v.R;
import com.ipmph.v.adapter.RankClassAdapter;
import com.ipmph.v.adapter.RankHotAdapter;
import com.ipmph.v.adapter.RankSubClassAdapter;
import com.ipmph.v.application.APPApplication;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.object.HeaderClassObject;
import com.ipmph.v.object.HeaderClassObject.SubClassObject;
import com.ipmph.v.object.VideoDetailObject.OtherVideoObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class RankFragment extends Fragment implements NetRequestIterface {

	private GridView class_gridview, subclass_gridview;
	private PullToRefreshGridView hot_gridview;
	private RankSubClassAdapter rankSubAdapter;
	private List<HeaderClassObject> classList;
	private NetRequest netRequest;
	private RankHotAdapter rankHotAdapter;
	private boolean fisrtLoadRank = true, isLoadMore = false;
	private TextView title1, title2;
	private List<OtherVideoObject> rankVideoList = new ArrayList<OtherVideoObject>();
	private int pageNo = 1;
	private String videoclassID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.rank_fragment, null);
		netRequest = new NetRequest(this, getActivity());
		netRequest.httpRequest(null, CommonUrl.getClassList);
		MainActivity.loadingDialog.show();
		initView(view);
		// initIndicator();
		return view;
	}

	private void initView(View view) {
		class_gridview = (GridView) view.findViewById(R.id.class_gridview);
		subclass_gridview = (GridView) view
				.findViewById(R.id.subclass_gridview);
		hot_gridview = (PullToRefreshGridView) view
				.findViewById(R.id.hot_gridview);
		title1 = (TextView) view.findViewById(R.id.title1);
		title2 = (TextView) view.findViewById(R.id.title2);
		hot_gridview.setOnRefreshListener(new OnRefreshListener2<GridView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<GridView> refreshView) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<GridView> refreshView) {
				// TODO Auto-generated method stub
				isLoadMore=true;
				getHotVideo(videoclassID);
			}
		});

	}

	private void setClassGridView() {
		int numColumns = classList.size();
		LayoutParams params = new LinearLayout.LayoutParams(
				(APPApplication.screenWidth - CommonUtil.dp2px(getActivity(),
						70)) /4 * numColumns, LayoutParams.WRAP_CONTENT);
		class_gridview.setLayoutParams(params);
		class_gridview.setColumnWidth((APPApplication.screenWidth - CommonUtil
				.dp2px(getActivity(), 70)) /4);
		class_gridview.setStretchMode(GridView.NO_STRETCH);
		class_gridview.setNumColumns(numColumns);
		setSubClassGridView(0);
	}

	private void setSubClassGridView(int position) {
		pageNo=1;
		List<SubClassObject> rankSubList = classList.get(position).zclassList;
		int numColumns = rankSubList.size();
		rankSubAdapter.changeList(rankSubList);
		rankSubAdapter.changePosition(0);
		LayoutParams params = new LinearLayout.LayoutParams(
				(APPApplication.screenWidth - CommonUtil.dp2px(getActivity(),
						70)) / 3 * numColumns, LayoutParams.WRAP_CONTENT);
		subclass_gridview.setLayoutParams(params);
		subclass_gridview
				.setColumnWidth((APPApplication.screenWidth - CommonUtil.dp2px(
						getActivity(), 70)) /3);
		subclass_gridview.setStretchMode(GridView.NO_STRETCH);
		subclass_gridview.setNumColumns(numColumns);
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		if (requestUrl.equals(CommonUrl.getClassList)) {
			try {
				title1.setVisibility(View.VISIBLE);
				title2.setVisibility(View.VISIBLE);
				JSONObject object = new JSONObject(result);
				classList = new Gson().fromJson(object.getString("classList"),
						new TypeToken<List<HeaderClassObject>>() {
						}.getType());
				Log.d("gaolei", "result---------getClassList-------" + result);
				final RankClassAdapter rankAdapter = new RankClassAdapter(
						classList, getActivity());
				class_gridview.setAdapter(rankAdapter);
				rankSubAdapter = new RankSubClassAdapter(
						classList.get(0).zclassList, getActivity());
				subclass_gridview.setAdapter(rankSubAdapter);
				if (fisrtLoadRank) {
				String videoclassID= rankSubAdapter.list.get(0).videoclassID;
				getHotVideo(videoclassID);
				setClassGridView();
				}
				class_gridview
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								pageNo = 1;
								rankVideoList.clear();
								rankAdapter.changePosition(position);
								setSubClassGridView(position);
								String videoclassID= rankSubAdapter.list.get(0).videoclassID;
								getHotVideo(videoclassID);
							}
						});
				subclass_gridview
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								pageNo = 1;
								rankVideoList.clear();
								
								rankSubAdapter.changePosition(position);
								String videoclassID= rankSubAdapter.list.get(position).videoclassID;
								getHotVideo(videoclassID);
							}
						});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (requestUrl.equals(CommonUrl.getHotVideo)) {
			try {
				JSONObject object = new JSONObject(result);
				Log.d("gaolei", "RankFragment-------getHotVideo-------"
						+ result);
				List<OtherVideoObject> rankVideoListPart = new Gson().fromJson(
						object.getString("videoList"),
						new TypeToken<List<OtherVideoObject>>() {
						}.getType());
				rankVideoList.addAll(rankVideoListPart);
				if (fisrtLoadRank) {
					rankHotAdapter = new RankHotAdapter(rankVideoList,
							getActivity());
					hot_gridview.setAdapter(rankHotAdapter);
					fisrtLoadRank = false;
				} else {
					if(!isLoadMore)
					rankHotAdapter.changeList(rankVideoList);
				}
				if (rankVideoListPart.size() == 0&&!isLoadMore) {
					CommonUtil.showToast(getActivity(),
							getString(R.string.no_content));
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				stopRefresh(hot_gridview);
				MainActivity.loadingDialog.cancel();
			}
			stopRefresh(hot_gridview);
			MainActivity.loadingDialog.cancel();
		}
	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub
		MainActivity.loadingDialog.cancel();
	}
	private void stopRefresh(PullToRefreshGridView listView) {
		listView.onRefreshComplete();
		isLoadMore = false;
	}

	public void getHotVideo(String videoclassID) {
		this.videoclassID=videoclassID;
		if (isLoadMore) {
			pageNo += 1;
		}
		Log.d("gaolei", "pageNo----------------" + pageNo);
		Map<String, Object> hotVideoMap = new HashMap<String, Object>();
		hotVideoMap.put("videoclassID",videoclassID);
		hotVideoMap.put("type", pageNo);
		hotVideoMap.put("pageSize", 20);
		netRequest.httpRequest(hotVideoMap, CommonUrl.getHotVideo);
		MainActivity.loadingDialog.show();
	}

}
