package com.ipmph.v.fragment;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ipmph.v.R;
import com.ipmph.v.SearchActivity;
import com.ipmph.v.VideoFilterActivity;
import com.ipmph.v.VideoMoreActivity;
import com.ipmph.v.adapter.NewUpdateAdapter;
import com.ipmph.v.adapter.RecommendPagerAdapter;
import com.ipmph.v.adapter.SubClassTitle1Adapter;
import com.ipmph.v.adapter.SubClassTitle2Adapter;
import com.ipmph.v.adapter.VideoAdapter;
import com.ipmph.v.adapter.VideoClass1Adapter;
import com.ipmph.v.adapter.VideoClass2Adapter;
import com.ipmph.v.application.APPApplication;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.MyInterface.OnClickItemListener;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.object.LoginResultObject;
import com.ipmph.v.object.VideoClassObject;
import com.ipmph.v.object.VideoClassObject.VideoListObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;
import com.ipmph.v.viewpagerindicator.IndicatorView;

public class HomePageFragment extends Fragment implements NetRequestIterface,
		OnClickListener, OnClickItemListener {

	private ScrollView scroll_view;
	private ViewPager recommend_viewpager;
	private IndicatorView viewpager_indicator;
	private final int RECYCLE_SHOW = 1;
	private int position = 0;
	private NetRequest netRequest;
	private RelativeLayout search_layout;
	private List<VideoListObject> carouselList;
	private List<VideoListObject> recommendList;
	private LinearLayout recommend_title_layout, new_update_title_layout;
	private GridView recommend_gridview, new_update_gridview,
			subclass1_title_gridview, subclass1_video_gridview,
			subclass2_title_gridview, subclass2_video_gridview;
	private TextView title1, title2, recommend_more, new_update_title1,
			new_update_title2, first_class_title, second_class_title,
			teach_more, operation_more;
	private NewUpdateAdapter newUpdateAdapter;
	private VideoClass1Adapter videoClass1Adapter;
	private VideoClass2Adapter videoClass2Adapter;
	private boolean updateAdapterSet = false, classAdapterSet = false,
			class2AdapterSet = false;
	private List<VideoClassObject> new_update_list;
	private List<VideoClassObject> firstClassList, secondClassList;
	private View divider1, divider2, divider3, more_divider1, more_divider2;
	private String videoClassId = "",videoClassId2="";
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RECYCLE_SHOW:
				if (position > carouselList.size() - 1) {
					position = 0;
				}
				// Log.d("gaolei", "position-----handleMessage------"+position);
				recommend_viewpager.setCurrentItem(position, false);
				sendEmptyMessageDelayed(RECYCLE_SHOW, 3000);
				position++;
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.homepage_fragment, null);
		Log.d("gaolei", "HomePageFragment--------onCreateView---------");

		initViews(view);
		return view;
	}

	public void onResume() {
		super.onResume();
		scroll_view.scrollTo(0, 0);
	}

	private void initViews(View view) {

		netRequest = new NetRequest(this, getActivity());
		scroll_view = (ScrollView) view.findViewById(R.id.scroll_view);
		recommend_viewpager = (ViewPager) view
				.findViewById(R.id.recommend_viewpager);
		recommend_title_layout = (LinearLayout) view
				.findViewById(R.id.recommend_title_layout);
		new_update_title_layout = (LinearLayout) view
				.findViewById(R.id.new_update_title_layout);

		viewpager_indicator = (IndicatorView) view
				.findViewById(R.id.viewpager_indicator);
		search_layout = (RelativeLayout) view.findViewById(R.id.search_layout);
		recommend_gridview = (GridView) view
				.findViewById(R.id.recommend_gridview);
		new_update_gridview = (GridView) view
				.findViewById(R.id.new_update_gridview);
		subclass1_title_gridview = (GridView) view
				.findViewById(R.id.subclass1_title_gridview);
		subclass2_title_gridview = (GridView) view
				.findViewById(R.id.subclass2_title_gridview);
		subclass1_video_gridview = (GridView) view
				.findViewById(R.id.subclass1_video_gridview);
		subclass2_video_gridview = (GridView) view
				.findViewById(R.id.subclass2_video_gridview);
		title1 = (TextView) view.findViewById(R.id.title1);
		title2 = (TextView) view.findViewById(R.id.title2);
		recommend_more = (TextView) view.findViewById(R.id.recommend_more);
		new_update_title1 = (TextView) view
				.findViewById(R.id.new_update_title1);
		new_update_title2 = (TextView) view
				.findViewById(R.id.new_update_title2);
		first_class_title = (TextView) view
				.findViewById(R.id.first_class_title);
		second_class_title = (TextView) view
				.findViewById(R.id.second_class_title);
		teach_more = (TextView) view.findViewById(R.id.teach_more);
		operation_more = (TextView) view.findViewById(R.id.operation_more);
		divider1 = (View) view.findViewById(R.id.divider1);
		divider2 = (View) view.findViewById(R.id.divider2);
		divider3 = (View) view.findViewById(R.id.divider3);
		more_divider1 = (View) view.findViewById(R.id.more_divider1);
		more_divider2 = (View) view.findViewById(R.id.more_divider2);

		search_layout.setOnClickListener(this);
		recommend_more.setOnClickListener(this);
		new_update_title1.setOnClickListener(this);
		new_update_title2.setOnClickListener(this);
		teach_more.setOnClickListener(this);
		operation_more.setOnClickListener(this);
		netRequest.httpRequest(null, CommonUrl.getAllInterfaceUrl);
		getAccountSharedPreferences();
	}

	private void getVideoInfo() {
		netRequest.httpRequest(null, CommonUrl.recommendVideoUrl);
		netRequest.httpRequest(null, CommonUrl.newUpdateUrl);
		netRequest.httpRequest(null, CommonUrl.firstClassUrl);
		netRequest.httpRequest(null, CommonUrl.secondClassUrl);
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		if (requestUrl.equals(CommonUrl.getAllInterfaceUrl)) {
			try {
				JSONArray array = new JSONArray(result);
				String jsonStr = array.getString(1);
				JSONArray array2 = new JSONArray(jsonStr);
				CommonUrl.recommendVideoUrl = CommonUrl.baseUrl
						+ array2.get(0).toString();
				CommonUrl.newUpdateUrl = CommonUrl.baseUrl
						+ array2.get(3).toString();
				CommonUrl.firstClassUrl = CommonUrl.baseUrl
						+ array2.get(4).toString();
				CommonUrl.secondClassUrl = CommonUrl.baseUrl
						+ array2.get(6).toString();
				getVideoInfo();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(CommonUrl.login)) {
			try {
				JSONObject object = new JSONObject(result);
				String status = object.getString("status");
				String message = object.getString("message");
				String sessionId = null;
				if (object.has("jeesite.session.id")) {
					sessionId = object.getString("jeesite.session.id");

				}
				Gson gson = new Gson();
				LoginResultObject loginObject = gson.fromJson(result,
						LoginResultObject.class);
				LoginResultObject.getInstance().status = status;
				LoginResultObject.getInstance().message = message;
				LoginResultObject.getInstance().sessionId = sessionId;

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (requestUrl.equals(CommonUrl.recommendVideoUrl)) {
			try {
				recommend_title_layout.setVisibility(View.VISIBLE);
				title1.setVisibility(View.VISIBLE);
				JSONArray array = new JSONArray(result);
				String jsonStr = array.getString(1);
				List<VideoClassObject> recommend_list = new Gson().fromJson(
						jsonStr, new TypeToken<List<VideoClassObject>>() {
						}.getType());
				carouselList = recommend_list.get(1).getData();
				// Log.d("gaolei",
				// "result--------recommendVideoUrl-------"+result);

				RecommendPagerAdapter imagePagerAdapter = new RecommendPagerAdapter(
						"HomepageFragment", getFragmentManager(), carouselList);
				// Log.d("gaolei",
				// "carouselList.size()--------recommendVideoUrl-------"+carouselList.size());
				recommend_viewpager.setAdapter(imagePagerAdapter);
				viewpager_indicator.setViewPager(recommend_viewpager);
				recommend_viewpager.setOffscreenPageLimit(carouselList.size());
				handler.sendEmptyMessageDelayed(RECYCLE_SHOW, 3000);
				recommendList = recommend_list.get(4).getData();
				List<VideoListObject> subRecommendList = recommendList.subList(
						0, 4);
				VideoAdapter gridAdapter = new VideoAdapter(subRecommendList,
						getActivity());
				recommend_gridview.setAdapter(gridAdapter);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(CommonUrl.newUpdateUrl)) {
			try {
				divider1.setVisibility(View.VISIBLE);
				new_update_title_layout.setVisibility(View.VISIBLE);
				JSONArray array = new JSONArray(result);
				// Log.d("gaolei", "result--------newUpdateUrl-------"+result);
				String jsonStr = array.getString(1);
				new_update_list = new Gson().fromJson(jsonStr,
						new TypeToken<List<VideoClassObject>>() {
						}.getType());
				new_update_title1.setText(new_update_list.get(1).getName());
				new_update_title2.setText(new_update_list.get(2).getName());
				setNewUpdate(1);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("gaolei",
						"e.getMessage() --------------" + e.getMessage());
			}

		}
		if (requestUrl.equals(CommonUrl.firstClassUrl)) {
			try {
				divider2.setVisibility(View.VISIBLE);
				more_divider1.setVisibility(View.VISIBLE);
				teach_more.setVisibility(View.VISIBLE);
				JSONArray array = new JSONArray(result);
				// Log.d("gaolei", "result--------firstClassUrl-------"+result);
				String jsonStr = array.getString(1);
				firstClassList = new Gson().fromJson(jsonStr,
						new TypeToken<List<VideoClassObject>>() {
						}.getType());
				String url=firstClassList.get(0).getUrl();
				videoClassId=url.split("=")[1];
				first_class_title.setText(firstClassList.get(0).name);
				final SubClassTitle1Adapter titleAdapter = new SubClassTitle1Adapter(
						firstClassList.subList(1, firstClassList.size() - 1),
						getActivity());
				titleAdapter.setOnClickItemListener(this);
				subclass1_title_gridview.setAdapter(titleAdapter);
				// List<VideoListObject> subclass1VideoList =
				// class_list.get(1).data;
				// subclass1_video_gridview.setAdapter(new SubClass1Adapter(
				// subclass1VideoList, getActivity()));
				setClassAdapter(firstClassList.get(1).data,
						subclass1_video_gridview, "firstClass");

				subclass1_title_gridview
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								titleAdapter.changePosition(position);
								setClassAdapter(
										firstClassList.get(position + 1).data,
										subclass1_video_gridview, "firstClass");
							}
						});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (requestUrl.equals(CommonUrl.secondClassUrl)) {
			try {
				divider3.setVisibility(View.VISIBLE);
				more_divider2.setVisibility(View.VISIBLE);
				operation_more.setVisibility(View.VISIBLE);
				JSONArray array = new JSONArray(result);
				// Log.d("gaolei",
				// "result--------secondClassUrl-------"+result);
				String jsonStr = array.getString(1);
				secondClassList = new Gson().fromJson(jsonStr,
						new TypeToken<List<VideoClassObject>>() {
						}.getType());
				String url=secondClassList.get(0).getUrl();
				videoClassId2=url.split("=")[1];
				second_class_title.setText(secondClassList.get(0).name);
				final SubClassTitle2Adapter title2Adapter = new SubClassTitle2Adapter(
						secondClassList.subList(1, secondClassList.size() - 1),
						getActivity());
				subclass2_title_gridview.setAdapter(title2Adapter);
				setClassAdapter(secondClassList.get(1).data,
						subclass2_video_gridview, "secondClass");
				subclass2_title_gridview
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								title2Adapter.changePosition(position);
								setClassAdapter(
										secondClassList.get(position + 1).data,
										subclass2_video_gridview, "secondClass");
							}
						});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("gaolei",
						"e.getMessage() --------------" + e.getMessage());
			}
		}
	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub

	}

	private void setClassAdapter(List<VideoListObject> videoList,
			GridView gridView, String flag) {
		int count = videoList.size();
		int numColumns = (count % 2 == 0) ? count / 2 : count / 2 + 1;
		LayoutParams params = new LinearLayout.LayoutParams(
				(APPApplication.screenWidth - CommonUtil.dp2px(getActivity(),
						20)) / 2 * numColumns, LayoutParams.WRAP_CONTENT);
		gridView.setLayoutParams(params);
		gridView.setColumnWidth((APPApplication.screenWidth - CommonUtil.dp2px(
				getActivity(), 30)) / 2);
		gridView.setStretchMode(GridView.NO_STRETCH);
		gridView.setNumColumns(numColumns);
		if (flag.equals("firstClass")) {
			if (!classAdapterSet) {
				videoClass1Adapter = new VideoClass1Adapter(videoList,
						getActivity());
				gridView.setAdapter(videoClass1Adapter);
				classAdapterSet = true;
			} else {
				videoClass1Adapter.changeList(videoList);
				videoClass1Adapter.notifyDataSetChanged();
			}
		}
		if (flag.equals("secondClass")) {
			if (!class2AdapterSet) {
				videoClass2Adapter = new VideoClass2Adapter(videoList,
						getActivity());
				gridView.setAdapter(videoClass2Adapter);
				class2AdapterSet = true;
			} else {
				videoClass2Adapter.changeList(videoList);
				videoClass2Adapter.notifyDataSetChanged();
			}
		}
	}

	private void setNewUpdate(int position) {
		switchNewUpdate(position);
		if (new_update_list.size() > 0) {
			List<VideoListObject> videoList = new_update_list.get(position)
					.getData();
			int count = videoList.size();
			int numColumns = (count % 2 == 0) ? count / 2 : count / 2 + 1;
			LayoutParams params = new LinearLayout.LayoutParams(
					(APPApplication.screenWidth - CommonUtil.dp2px(
							getActivity(), 20)) / 2 * numColumns,
					LayoutParams.WRAP_CONTENT);
			new_update_gridview.setLayoutParams(params);
			new_update_gridview
					.setColumnWidth((APPApplication.screenWidth - CommonUtil
							.dp2px(getActivity(), 30)) / 2);
			new_update_gridview.setStretchMode(GridView.NO_STRETCH);
			new_update_gridview.setNumColumns(numColumns);
			if (!updateAdapterSet) {
				newUpdateAdapter = new NewUpdateAdapter(videoList,
						getActivity());
				new_update_gridview.setAdapter(newUpdateAdapter);
				updateAdapterSet = true;
			} else {
				newUpdateAdapter.changeList(videoList);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search_layout:
			jumpToSearchActivity();
			break;
		case R.id.recommend_more:
			Intent intent = new Intent(getActivity(), VideoMoreActivity.class);
			intent.putExtra("recommendList", (Serializable) recommendList);
			startActivity(intent);
			break;
		case R.id.new_update_title1:
			setNewUpdate(1);
			break;
		case R.id.new_update_title2:
			setNewUpdate(2);
			break;
		case R.id.teach_more:
			toTeachClassMore();
			break;
		case R.id.operation_more:
			toOperationClassMore();
			break;
		}
	}

	private void switchNewUpdate(int position) {
		new_update_title1
				.setBackgroundResource(R.drawable.class_title_unselect);
		new_update_title2
				.setBackgroundResource(R.drawable.class_title_unselect);
		if (position == 1)
			new_update_title1
					.setBackgroundResource(R.drawable.class_title_select);
		if (position == 2)
			new_update_title2
					.setBackgroundResource(R.drawable.class_title_select);
	}

	private void jumpToSearchActivity() {
		Intent intent = new Intent(getActivity(), SearchActivity.class);
		startActivity(intent);
	}

	public void onDestroyView() {
		super.onDestroyView();
		// Log.d("gaolei", "HomePageFragment--------onDestroyView----------");
		handler.removeMessages(RECYCLE_SHOW);
		updateAdapterSet = false;
		classAdapterSet = false;
		class2AdapterSet = false;
		position = 0;
	}

	@Override
	public void onClickItem(int position) {
		// TODO Auto-generated method stub

	}

	public void toTeachClassMore() {
		Log.d("gaolei", "videoClassId---------------" + videoClassId);
		Bundle bundle = new Bundle();
		bundle.putString("videoclassID",videoClassId);
		Intent intent = new Intent(getActivity(), VideoFilterActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);

	}

	public void toOperationClassMore() {
		Log.d("gaolei", "videoClassId2---------------" + videoClassId2);
		Bundle bundle = new Bundle();
		bundle.putString("videoclassID",videoClassId2);
		Intent intent = new Intent(getActivity(), VideoFilterActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void getAccountSharedPreferences() {
		String username = getActivity().getSharedPreferences("account", 0)
				.getString("username", "");
		String password = getActivity().getSharedPreferences("account", 0)
				.getString("password", "");
		
		Log.d("gaolei", "username------SP---------" + username);
		Log.d("gaolei", "password------SP---------" + password);
		if (username != null && password != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("username", username);
			map.put("password", password);
			netRequest.httpRequest(map, CommonUrl.login);
		}
	}
}
