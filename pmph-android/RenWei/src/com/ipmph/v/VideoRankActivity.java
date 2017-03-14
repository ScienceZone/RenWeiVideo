package com.ipmph.v;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.ipmph.v.adapter.RankHotAdapter;
import com.ipmph.v.adapter.RankSubClassAdapter;
import com.ipmph.v.application.APPApplication;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.fragment.HomeFragment;
import com.ipmph.v.object.HeaderClassObject.SubClassObject;
import com.ipmph.v.object.VideoDetailObject.OtherVideoObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class VideoRankActivity extends Activity implements NetRequestIterface {

	private GridView subclass_gridview;
	private PullToRefreshGridView hot_gridview;
	private RankSubClassAdapter rankSubAdapter;
	private NetRequest netRequest;
	private RankHotAdapter rankHotAdapter;
	private boolean fisrtLoadRank = true,isLoadMore = false;
	private LinearLayout linearLayout1;
	private int viewPagerPosition;
	private TextView title2;
	private List<OtherVideoObject> rankVideoList = new ArrayList<OtherVideoObject>();
	private int pageNo = 1;
	private String videoclassID;
	private ImageView back;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rank_fragment);
		netRequest = new NetRequest(this, this);
		Bundle bundle = getIntent().getExtras();
		viewPagerPosition = bundle.getInt("position");

		initView();
	}

	private void initView() {
		back = (ImageView) findViewById(R.id.back);
		back.setVisibility(View.VISIBLE);
		subclass_gridview = (GridView) findViewById(R.id.subclass_gridview);
		hot_gridview = (PullToRefreshGridView) findViewById(R.id.hot_gridview);
		linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
		title2 = (TextView) findViewById(R.id.title2);
		linearLayout1.setVisibility(View.GONE);
		rankSubAdapter = new RankSubClassAdapter(
				HomeFragment.classList.get(viewPagerPosition).zclassList,
				VideoRankActivity.this);
		subclass_gridview.setAdapter(rankSubAdapter);
		setSubClassGridView();
		getHotVideo(rankSubAdapter.list.get(0).videoclassID);
		subclass_gridview
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						pageNo = 1;
						rankVideoList.clear();
						
						rankSubAdapter.changePosition(position);
						String videoclassID= rankSubAdapter.list.get(position).videoclassID;
						getHotVideo(videoclassID);
					}
				});
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

	private void setSubClassGridView() {
		List<SubClassObject> rankSubList = HomeFragment.classList.get(viewPagerPosition).zclassList;
		int numColumns = rankSubList.size();
		
		LayoutParams params = new LinearLayout.LayoutParams(
				(APPApplication.screenWidth - CommonUtil.dp2px(
						VideoRankActivity.this, 70)) / 3 * numColumns,
				LayoutParams.WRAP_CONTENT);
		subclass_gridview.setLayoutParams(params);
		subclass_gridview
				.setColumnWidth((APPApplication.screenWidth - CommonUtil.dp2px(
						VideoRankActivity.this, 70)) / 3);
		subclass_gridview.setStretchMode(GridView.NO_STRETCH);
		subclass_gridview.setNumColumns(numColumns);
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		
		if (requestUrl.equals(CommonUrl.getHotVideo)) {
			try {
				title2.setVisibility(View.VISIBLE);
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
							VideoRankActivity.this);
					hot_gridview.setAdapter(rankHotAdapter);
					fisrtLoadRank = false;
				} else {
					if(!isLoadMore)
					rankHotAdapter.changeList(rankVideoList);
				}
				if (rankVideoListPart.size() == 0&&!isLoadMore) {
					CommonUtil.showToast(this,
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
		MainActivity.loadingDialog.show();
		Map<String, Object> hotVideoMap = new HashMap<String, Object>();
		hotVideoMap.put("videoclassID", videoclassID);
		hotVideoMap.put("type", pageNo);
		hotVideoMap.put("pageSize", 20);
		netRequest.httpRequest(hotVideoMap, CommonUrl.getHotVideo);
	}
	public void onBack(View view) {
		finish();
	}
}
