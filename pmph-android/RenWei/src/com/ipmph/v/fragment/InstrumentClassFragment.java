package com.ipmph.v.fragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

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
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ipmph.v.AlbumFilterActivity;
import com.ipmph.v.R;
import com.ipmph.v.VideoFilterActivity;
import com.ipmph.v.VideoRankActivity;
import com.ipmph.v.adapter.FirstClassPagerAdapter;
import com.ipmph.v.adapter.SubClassTitle2Adapter;
import com.ipmph.v.adapter.VideoClass1Adapter;
import com.ipmph.v.application.APPApplication;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.object.VideoClassObject;
import com.ipmph.v.object.VideoClassObject.VideoListObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;
import com.ipmph.v.viewpagerindicator.IndicatorView;

public class InstrumentClassFragment extends Fragment implements NetRequestIterface,
		OnClickListener {

	private ScrollView scroll_view;
	private ViewPager first_class_viewpager;
	private IndicatorView viewpager_indicator_firstclass;
	private final int RECYCLE_SHOW = 1;
	private int position = 0;
	private NetRequest netRequest;
	// private List<VideoListObject> carouselList;
	private GridView subclass_title_gridview,subclass_title_gridview2,subclass_title_gridview3,
	subclass_title_gridview4;
	private GridView secondclass_gridview1, secondclass_gridview2,
			secondclass_gridview3, secondclass_gridview4;
	private TextView secondclass_title1, secondclass_title2,
			secondclass_title3, secondclass_title4;
	private String interface0, interface1, interface2, interface3, interface4;
	private String videoclassID, videoclassName;
	private int viewPagerPosition;
	private LinearLayout hot_rank_layout, album_filter_layout,
			video_filter_layout;
	private int carouselListSize = 1;
	private VideoClass1Adapter videoClass1Adapter;
	private boolean classAdapterSet = false;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RECYCLE_SHOW:
				if (position > carouselListSize - 1) {
					position = 0;
				}
				// Log.d("gaolei", "position-----handleMessage------" +
				// position);

				first_class_viewpager.setCurrentItem(position, false);
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
				R.layout.instrument_class_fragment, null);
		Bundle bundle = getArguments();
		videoclassName = bundle.getString("videoclassName");
		videoclassID = bundle.getString("videoclassID");
		viewPagerPosition = bundle.getInt("position");
		// Log.d("gaolei", "FirstClassFragment--------videoclassName---------"
		// + videoclassName);
		Log.d("gaolei", "FirstClassFragment--------videoclassID---------"
				+ videoclassID);
		initViews(view);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", videoclassID);
		netRequest.httpRequest(map, CommonUrl.getfirstClassInterfaceUrl);
		return view;
	}

	private void initViews(View view) {

		netRequest = new NetRequest(this, getActivity());
		scroll_view = (ScrollView) view.findViewById(R.id.scroll_view);
		first_class_viewpager = (ViewPager) view
				.findViewById(R.id.instrument_class_viewpager);

		viewpager_indicator_firstclass = (IndicatorView) view
				.findViewById(R.id.viewpager_indicator_firstclass);
		subclass_title_gridview = (GridView) view
				.findViewById(R.id.subclass_title_gridview);
		subclass_title_gridview2 = (GridView) view
				.findViewById(R.id.subclass_title_gridview2);
		subclass_title_gridview3 = (GridView) view
				.findViewById(R.id.subclass_title_gridview3);
		subclass_title_gridview4 = (GridView) view
				.findViewById(R.id.subclass_title_gridview4);
		secondclass_gridview1 = (GridView) view
				.findViewById(R.id.secondclass_gridview1);
		secondclass_gridview2 = (GridView) view
				.findViewById(R.id.secondclass_gridview2);
		secondclass_gridview3 = (GridView) view
				.findViewById(R.id.secondclass_gridview3);
		secondclass_gridview4 = (GridView) view
				.findViewById(R.id.secondclass_gridview4);
		secondclass_title1 = (TextView) view
				.findViewById(R.id.secondclass_title1);
		secondclass_title2 = (TextView) view
				.findViewById(R.id.secondclass_title2);
		secondclass_title3 = (TextView) view
				.findViewById(R.id.secondclass_title3);
		secondclass_title4 = (TextView) view
				.findViewById(R.id.secondclass_title4);
		hot_rank_layout = (LinearLayout) view
				.findViewById(R.id.hot_rank_layout);
		album_filter_layout = (LinearLayout) view
				.findViewById(R.id.album_filter_layout);
		video_filter_layout = (LinearLayout) view
				.findViewById(R.id.video_filter_layout);
		hot_rank_layout.setOnClickListener(this);
		album_filter_layout.setOnClickListener(this);
		video_filter_layout.setOnClickListener(this);


	}

	public void onResume() {
		super.onResume();
		scroll_view.scrollTo(0, 0);
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		if (requestUrl.equals(CommonUrl.getfirstClassInterfaceUrl)) {
			try {
				JSONArray array = new JSONArray(result);
				String jsonStr = array.getString(1);
				JSONArray array2 = new JSONArray(jsonStr);
				interface0 = CommonUrl.baseUrl + array2.get(0).toString();
				interface1 = CommonUrl.baseUrl + array2.get(2).toString();
				interface2 = CommonUrl.baseUrl + array2.get(3).toString();
				interface3 = CommonUrl.baseUrl + array2.get(5).toString();
				interface4 = CommonUrl.baseUrl + array2.get(6).toString();
				// Log.d("gaolei", "interface0---------------" + interface0);
				// Log.d("gaolei", "interface1---------------" + interface1);
				// Log.d("gaolei", "interface2---------------" + interface2);
				// Log.d("gaolei", "interface3---------------" + interface3);
				// Log.d("gaolei", "interface4---------------" + interface4);
				/*
				 * "/inte/content?id=88401617f90645c79e6a946d3c0e5115", "",
				 * "/inte/content?id=9397121fa16f4768baf650b83a47b7a3",
				 * "/inte/content?id=3aa5552f64074c77a0335a2ad95664c5", "",
				 * "/inte/content?id=714be7d64b704e55aaaeb710f0eb9b1d",
				 * "/inte/content?id=a9c81443d4044575a1ad1e24d5748bb2"
				 */

				netRequest.httpRequest(null, interface0);
				netRequest.httpRequest(null, interface1);
				netRequest.httpRequest(null, interface2);
				netRequest.httpRequest(null, interface3);
				netRequest.httpRequest(null, interface4);
				// getVideoInfo();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(interface0)) {
			try {
				JSONArray array = new JSONArray(result);
				String jsonStr = array.getString(1);
				List<VideoClassObject> recommend_list = new Gson().fromJson(
						jsonStr, new TypeToken<List<VideoClassObject>>() {
						}.getType());
				List<VideoListObject> carouselList = recommend_list.get(1)
						.getData();
				carouselListSize = carouselList.size();
				Log.d("gaolei", "result-------" + videoclassName
						+ "------interface0-------" + result);
				Log.d("gaolei", "carouselList.size()-------" + videoclassName
						+ "------interface0-------" + carouselList.size());
				List<VideoListObject> subList = carouselList.subList(0, 3);
				FirstClassPagerAdapter imagePagerAdapter = new FirstClassPagerAdapter(
						videoclassName, getFragmentManager(), carouselList);
				// Log.d("gaolei", "subList.size()--------interface0-------"
				// + subList.size());
				first_class_viewpager.setAdapter(imagePagerAdapter);
				viewpager_indicator_firstclass
						.setViewPager(first_class_viewpager);

				// recommend_viewpager_firstclass.setOffscreenPageLimit(subList.size());
				handler.sendEmptyMessageDelayed(RECYCLE_SHOW, 3000);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(interface1)) {
			try {
				JSONArray array = new JSONArray(result);
				String jsonStr = array.getString(1);
				final List<VideoClassObject> secondClassList = new Gson()
						.fromJson(jsonStr,
								new TypeToken<List<VideoClassObject>>() {
								}.getType());
				/*
				 * final List<VideoListObject> videoList =
				 * secondClassList.get(1) .getData(); List<VideoListObject>
				 * subVideoList = videoList.subList(0, 4); RecommendAdapter
				 * gridAdapter = new RecommendAdapter( subVideoList,
				 * getActivity());
				 * secondclass_gridview1.setAdapter(gridAdapter);
				 * secondclass_title1.setText(secondClassList.get(1).getName());
				 * video_more1.setOnClickListener(new View.OnClickListener() {
				 * 
				 * @Override public void onClick(View v) { // TODO
				 * Auto-generated method stub Intent intent = new
				 * Intent(getActivity(), RecommendMoreActivity.class);
				 * intent.putExtra("recommendList", (Serializable) videoList);
				 * startActivity(intent); } });
				 */
				secondclass_title1.setText(secondClassList.get(0).name);
				final SubClassTitle2Adapter title2Adapter = new SubClassTitle2Adapter(
						secondClassList.subList(1, secondClassList.size() - 1),
						getActivity());
				subclass_title_gridview.setAdapter(title2Adapter);
				setClassAdapter(secondClassList.get(1).data,
						secondclass_gridview1, 1);
				subclass_title_gridview
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								title2Adapter.changePosition(position);
								setClassAdapter(
										secondClassList.get(position + 1).data,
										secondclass_gridview1, position);
							}
						});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(interface2)) {
			try {
				JSONArray array = new JSONArray(result);
				String jsonStr = array.getString(1);
				final List<VideoClassObject> secondClassList = new Gson().fromJson(
						jsonStr, new TypeToken<List<VideoClassObject>>() {
						}.getType());
				secondclass_title2.setText(secondClassList.get(0).name);
				final SubClassTitle2Adapter title2Adapter = new SubClassTitle2Adapter(
						secondClassList.subList(1, secondClassList.size() - 1),
						getActivity());
				subclass_title_gridview2.setAdapter(title2Adapter);
				setClassAdapter(secondClassList.get(1).data,
						secondclass_gridview2, 1);
				subclass_title_gridview2
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								title2Adapter.changePosition(position);
								setClassAdapter(
										secondClassList.get(position + 1).data,
										secondclass_gridview2, position);
							}
						});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(interface3)) {
			try {
				JSONArray array = new JSONArray(result);
				String jsonStr = array.getString(1);
				final List<VideoClassObject> secondClassList = new Gson().fromJson(
						jsonStr, new TypeToken<List<VideoClassObject>>() {
						}.getType());
				secondclass_title3.setText(secondClassList.get(0).name);
				final SubClassTitle2Adapter title2Adapter = new SubClassTitle2Adapter(
						secondClassList.subList(1, secondClassList.size() - 1),
						getActivity());
				subclass_title_gridview3.setAdapter(title2Adapter);
				setClassAdapter(secondClassList.get(1).data,
						secondclass_gridview3, 1);
				subclass_title_gridview3
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								title2Adapter.changePosition(position);
								setClassAdapter(
										secondClassList.get(position + 1).data,
										secondclass_gridview3, position);
							}
						});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestUrl.equals(interface4)) {
			try {
				JSONArray array = new JSONArray(result);
				String jsonStr = array.getString(1);
				final List<VideoClassObject> secondClassList = new Gson().fromJson(
						jsonStr, new TypeToken<List<VideoClassObject>>() {
						}.getType());
				secondclass_title4.setText(secondClassList.get(0).name);
				final SubClassTitle2Adapter title2Adapter = new SubClassTitle2Adapter(
						secondClassList.subList(1, secondClassList.size() - 1),
						getActivity());
				subclass_title_gridview4.setAdapter(title2Adapter);
				setClassAdapter(secondClassList.get(1).data,
						secondclass_gridview4, 1);
				subclass_title_gridview4
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								title2Adapter.changePosition(position);
								setClassAdapter(
										secondClassList.get(position + 1).data,
										secondclass_gridview4, position);
							}
						});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub

	}

	private void setClassAdapter(List<VideoListObject> videoList,
			GridView gridView, int  position) {
		Log.d("gaolei", "setClassAdapter-------position--------"+position);
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
		// if (!classAdapterSet) {
		videoClass1Adapter = new VideoClass1Adapter(videoList, getActivity());
		gridView.setAdapter(videoClass1Adapter);
		// classAdapterSet = true;
		// } else {
		// subClass1Adapter.changeList(videoList);
		// subClass1Adapter.notifyDataSetChanged();
		// }
	}

	public void toHotRank() {
		Bundle bundle = new Bundle();
		bundle.putInt("position", viewPagerPosition);
		Intent intent = new Intent(getActivity(), VideoRankActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void onDestroyView() {
		super.onDestroyView();
		Log.d("gaolei", "HomePageFragment--------onDestroyView----------");
		handler.removeMessages(RECYCLE_SHOW);
		position = 0;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		Intent intent;
		switch (v.getId()) {
		case R.id.hot_rank_layout:
			toHotRank();
			break;
		case R.id.album_filter_layout:

			bundle.putString("videoclassID", videoclassID);
			intent = new Intent(getActivity(), AlbumFilterActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case R.id.video_filter_layout:
			bundle.putString("videoclassID", videoclassID);
			intent = new Intent(getActivity(), VideoFilterActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;

		}
	}

}
