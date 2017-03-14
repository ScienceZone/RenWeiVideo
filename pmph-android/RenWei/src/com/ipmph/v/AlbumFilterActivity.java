package com.ipmph.v;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ipmph.v.adapter.AlbumListAdapter;
import com.ipmph.v.adapter.ClassFilter1Adapter;
import com.ipmph.v.adapter.ClassFilter2Adapter;
import com.ipmph.v.adapter.RankSubClassAdapter;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.MyInterface.OnClickAddListener;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.object.AlbumObject.AlbumListObject;
import com.ipmph.v.object.ClassFilterObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class AlbumFilterActivity extends Activity implements
		NetRequestIterface, OnClickAddListener {

	private ListView filter_listview1, filter_listview2_one,
			filter_listview2_second, filter_listview2_third;
	private PullToRefreshListView album_filter_listview;
	private TextView class_filter_text, class_order_text, back_filter1, filter,
			order_new_text, order_hot_text;

	private ImageView class_filter_img, class_order_img;
	private RankSubClassAdapter rankSubAdapter;
	private NetRequest netRequest;
	private LinearLayout filter_layout, order_filter_layout;
	boolean isClassFilterShow = false, isOrderFilterShow = false;
	private List<Map<String, String>> filter1List = new ArrayList<Map<String, String>>();
	private Map<String, String> filter1Map = new HashMap<String, String>();
	private ClassFilter1Adapter filter1Adapter;
	private String currentClassID, rootGradeId,firstGradeId = "", secondGradeId = "",
			thirdGradeId = "";
	private int currentGrade = 1, filter1Position = 0, order = 1, pageNo = 1;
	private boolean isLoadMore, firstLoad = true;
	private List<AlbumListObject> albumFilterList = new ArrayList<AlbumListObject>();
	private AlbumListAdapter albumAdapter;
	private TextView album_add_cancel;
	private String seelistID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_filter_activity);
		album_filter_listview = (PullToRefreshListView) findViewById(R.id.album_filter_listview);
		back_filter1 = (TextView) findViewById(R.id.back_filter1);
		filter = (TextView) findViewById(R.id.filter);
		class_filter_text = (TextView) findViewById(R.id.class_filter_text);
		class_order_text = (TextView) findViewById(R.id.class_order_text);
		order_new_text = (TextView) findViewById(R.id.order_new_text);
		order_hot_text = (TextView) findViewById(R.id.order_hot_text);

		class_filter_img = (ImageView) findViewById(R.id.class_filter_img);
		class_order_img = (ImageView) findViewById(R.id.class_order_img);
		filter_listview1 = (ListView) findViewById(R.id.filter_listview1);
		filter_listview2_one = (ListView) findViewById(R.id.filter_listview2_one);
		filter_listview2_second = (ListView) findViewById(R.id.filter_listview2_second);
		filter_listview2_third = (ListView) findViewById(R.id.filter_listview2_third);
		View footer = LayoutInflater.from(this).inflate(
				R.layout.listview_footer, null);
		filter_listview1.addFooterView(footer, null, false);
		filter_listview2_one.addFooterView(footer, null, false);
		filter_listview2_second.addFooterView(footer, null, false);
		filter_listview2_third.addFooterView(footer, null, false);

		filter_layout = (LinearLayout) findViewById(R.id.filter_layout);
		order_filter_layout = (LinearLayout) findViewById(R.id.order_filter_layout);
		netRequest = new NetRequest(this, this);
		Bundle bundle = getIntent().getExtras();

		currentClassID = bundle.getString("videoclassID");
		rootGradeId=currentClassID;
		Log.d("gaolei", "videoclassID-----------------------" + currentClassID);
		getList(currentClassID, 0);
		filter1Map.put("一级分类", "全部");
		filter1List.add(filter1Map);
		filter1Adapter = new ClassFilter1Adapter(filter1List,
				AlbumFilterActivity.this);
		filter_listview1.setAdapter(filter1Adapter);
		filter_listview1
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						filter1Position = position;

						goToFilter2(position);
					}
				});
		album_filter_listview
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						isLoadMore = true;
						Log.d("gaolei", "currentClassID-----Refresh--------"+currentClassID);
						getList(currentClassID, order);
					}
				});
	}

	public void showClassFilter(View view) {
		if (!isClassFilterShow) {
			filter_layout.setVisibility(View.VISIBLE);
			isClassFilterShow = true;
			isOrderFilterShow = false;
			class_filter_text.setTextColor(Color.parseColor("#00a1f1"));
			class_order_text.setTextColor(Color.parseColor("#303336"));
			class_filter_img.setImageResource(R.drawable.indicator_blue);
			class_order_img.setImageResource(R.drawable.indicator_gray);
			order_filter_layout.setVisibility(View.GONE);
		} else {
			hideFilterLayout(filter_layout);

		}
	}

	public void showOrderFilter(View view) {
		if (!isOrderFilterShow) {
			filter_layout.setVisibility(View.VISIBLE);
			isOrderFilterShow = true;
			isClassFilterShow = false;
			order_filter_layout.setVisibility(View.VISIBLE);
			class_filter_text.setTextColor(Color.parseColor("#303336"));
			class_order_text.setTextColor(Color.parseColor("#00a1f1"));
			class_filter_img.setImageResource(R.drawable.indicator_gray);
			class_order_img.setImageResource(R.drawable.indicator_blue);

		} else {
			hideFilterLayout(filter_layout);

		}
	}

	public void backToFilter1(View view) {
		filter_listview1.setVisibility(View.VISIBLE);
		filter_listview2_one.setVisibility(View.GONE);
		filter_listview2_second.setVisibility(View.GONE);
		filter_listview2_third.setVisibility(View.GONE);
		back_filter1.setVisibility(View.GONE);
		filter.setVisibility(View.VISIBLE);
	}

	public void goToFilter2(int position) {
		filter_listview1.setVisibility(View.GONE);
		if (position == 2) {
			filter_listview2_third.setVisibility(View.VISIBLE);
		}
		if (position == 1) {
			filter_listview2_second.setVisibility(View.VISIBLE);
			filter_listview2_third.setVisibility(View.GONE);
		}
		if (position == 0) {
			filter_listview2_one.setVisibility(View.VISIBLE);
			filter_listview2_second.setVisibility(View.GONE);
			filter_listview2_third.setVisibility(View.GONE);
		}
		back_filter1.setVisibility(View.VISIBLE);
		filter.setVisibility(View.GONE);
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		JSONObject object;
		if (requestUrl.equals(CommonUrl.getList)) {
			try {
				// Log.d("gaolei", "getList-----------------------" + result);
				object = new JSONObject(result);
				JSONObject object2 = new JSONObject(
						object.getString("albumVideo"));

				String albumVideoList = object2.getString("albumVideoList");
				List<AlbumListObject> albumFilterListPart = new Gson()
						.fromJson(albumVideoList,
								new TypeToken<List<AlbumListObject>>() {
								}.getType());
				Log.d("gaolei",
						"albumFilterListPart.size()-----------------------"
								+ albumFilterListPart.size());
				if (albumFilterListPart.size() == 0 && !isLoadMore) {
					CommonUtil.showToast(this, getString(R.string.no_content));
				}
				albumFilterList.addAll(albumFilterListPart);
				if (firstLoad) {
					albumAdapter = new AlbumListAdapter(albumFilterList, this,
							this);
					album_filter_listview.setAdapter(albumAdapter);
					firstLoad = false;
				} else {
					albumAdapter.changeList(albumFilterList);
				}
				//
				// Log.d("gaolei",
				// "getString(videoclassList)-----------------------"
				// + object.getString("videoclassList"));
				String videoclassList = object.getString("videoclassList");
				final List<ClassFilterObject> filter2List = new Gson()
						.fromJson(videoclassList,
								new TypeToken<List<ClassFilterObject>>() {
								}.getType());
//				if (filter2List.size() == 0)
//					return;
				filter2List.add(0, new ClassFilterObject("", "全部"));
				final ClassFilter2Adapter filter2Adapter = new ClassFilter2Adapter(
						filter2List, AlbumFilterActivity.this);
				if (currentGrade == 1) {

					filter_listview2_one.setAdapter(filter2Adapter);
					filter_listview2_one
							.setOnItemClickListener(new AdapterView.OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub
									clearFormerData(false);
									int count = filter1Adapter.getCount() - 1;
									if (filter1Position < count) {
										List<Map<String, String>> subFilter1List = filter1List
												.subList(0, filter1Position + 1);
										filter1List = subFilter1List;
										filter1Adapter
												.changeList(subFilter1List);
										filter1Adapter.notifyDataSetChanged();
										backToFilter1(back_filter1);
									}
									if (position == 0) {
										getList(rootGradeId, 0);

									} else {
										gotoNextGrade(
												filter2List.get(position).videoclassID,
												2);
									}
									Map<String, String> filter1Map = filter1List
											.get(0);
									filter1Map.put(
											"一级分类",
											filter2List.get(position).videoclassName);

									filter2Adapter.changePosition(position);
									filter2Adapter.notifyDataSetChanged();
									filter1Adapter.notifyDataSetChanged();
									backToFilter1(back_filter1);
									firstGradeId = filter2List.get(position).videoclassID;
									Log.d("gaolei", "firstGradeId----------"+firstGradeId);
								}
							});
				}
				if (currentGrade == 2) {
					filter_listview2_second.setAdapter(filter2Adapter);
					filter_listview2_second
							.setOnItemClickListener(new AdapterView.OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub
									clearFormerData(false);
									int count = filter1Adapter.getCount() - 1;
									if (filter1Position < count) {
										List<Map<String, String>> subFilter1List = filter1List
												.subList(0, filter1Position + 1);
										filter1List = subFilter1List;
										filter1Adapter
												.changeList(subFilter1List);
										filter1Adapter.notifyDataSetChanged();
									}
									Log.d("gaolei", "firstGradeId------0---"+firstGradeId);
									if (position == 0) {
										getList(firstGradeId, 0);
									} else {
										gotoNextGrade(
												filter2List.get(position).videoclassID,
												3);
									}
									Map<String, String> filter1Map = filter1List
											.get(1);
									filter1Map.put(
											"二级分类",
											filter2List.get(position).videoclassName);
									filter2Adapter.changePosition(position);
									filter2Adapter.notifyDataSetChanged();
									filter1Adapter.notifyDataSetChanged();
									backToFilter1(back_filter1);
									secondGradeId = filter2List.get(position).videoclassID;
									Log.d("gaolei", "secondGradeId---------------" + secondGradeId);
								}
							});
				}
				if (currentGrade == 3) {
					filter_listview2_third.setAdapter(filter2Adapter);
					filter_listview2_third
							.setOnItemClickListener(new AdapterView.OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub
									clearFormerData(false);
									if (position == 0) {
										Log.d("gaolei", "secondGradeId-------0-------" + secondGradeId);
										getList(secondGradeId, 0);
									} else {
										gotoNextGrade(
												filter2List.get(position).videoclassID,
												4);
									}
									Map<String, String> filter1Map = filter1List
											.get(2);
									filter1Map.put(
											"三级分类",
											filter2List.get(position).videoclassName);
									backToFilter1(back_filter1);
									filter2Adapter.changePosition(position);
									filter2Adapter.notifyDataSetChanged();
									filter1Adapter.notifyDataSetChanged();
									thirdGradeId = filter2List.get(position).videoclassID;
								}
							});
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				stopRefresh(album_filter_listview);
			}

		}
		if (requestUrl.equals(CommonUrl.updateSeeListDelFlag)) {
			try {
				object = new JSONObject(result);
				String message = object.getString("message");
				if (message.equals("成功")) {
					CommonUtil.showToast(this, "取消看单成功");
					CommonUtil.changeTextDrawable(this, album_add_cancel,
							"left", R.drawable.video_add,
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
					CommonUtil.showToast(this, "加入看单成功");
					CommonUtil.changeTextDrawable(this, album_add_cancel,
							"left", R.drawable.video_cancel,
							R.string.cancel_watch_list, "#5c514d");
				}

				if (message.equals("已加入看单"))
					CommonUtil.showToast(this, "已加入看单");
				CommonUtil.changeTextDrawable(this, album_add_cancel, "left",
						R.drawable.video_cancel, R.string.cancel_watch_list,
						"#5c514d");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		stopRefresh(album_filter_listview);
	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub

	}

	private void stopRefresh(PullToRefreshListView listView) {
		listView.onRefreshComplete();
		isLoadMore = false;
	}

	public void gotoNextGrade(String videoclassID, int currentGrade) {
		Log.d("gaolei", "videoclassID--------gotoNextGrade-------"
				+ videoclassID);
		this.currentClassID=videoclassID;
		this.currentGrade = currentGrade;
		int position = filter1Adapter.getCount();

		Map<String, String> nextFilterMap = new HashMap<String, String>();
		if (position == 1) {
			nextFilterMap.put("二级分类", "全部");
			filter1List.add(nextFilterMap);
		}
		if (position == 2) {
			nextFilterMap.put("三级分类", "全部");
			filter1List.add(nextFilterMap);
		}
		filter1Adapter.notifyDataSetChanged();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("videoclassID", videoclassID);
		map.put("sort", 0);
		map.put("pageSize", 20);
		map.put("pageNo", 1);
		netRequest.httpRequest(map, CommonUrl.getList);
	}

	// public void changeHotVideo(int position) {
	// MainActivity.loadingDialog.show();
	// SubClassObject object = rankSubAdapter.list.get(position);
	// Map<String, Object> hotVideoMap = new HashMap<String, Object>();
	// hotVideoMap.put("videoclassID", object.videoclassID);
	// Log.d("gaolei", "videoclassID------------------" + object.videoclassID);
	// hotVideoMap.put("type", 1);
	// hotVideoMap.put("pageSize", 10);
	// netRequest.httpRequest(hotVideoMap, CommonUrl.getHotVideo);
	// }

	public void onBack(View view) {
		finish();
	}

	public void hideFilterLayout(View view) {
		filter_layout.setVisibility(View.GONE);
		isClassFilterShow = false;
		isOrderFilterShow = false;
		class_filter_text.setTextColor(Color.parseColor("#303336"));
		class_order_text.setTextColor(Color.parseColor("#303336"));
		class_filter_img.setImageResource(R.drawable.indicator_gray);
		class_order_img.setImageResource(R.drawable.indicator_gray);
	}

	private void getList(String videoclassID, int order) {
		// 根据传过来的参数进行排序：0：全部 1：最热 2：最新
		if (isLoadMore) {
			pageNo += 1;
		}
		this.currentClassID = videoclassID;
		this.order = order;
		Log.d("gaolei", "videoclassID------order-------" + videoclassID);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("videoclassID", videoclassID);
		map.put("sort", order);
		map.put("pageSize", 20);
		map.put("pageNo", pageNo);
		netRequest.httpRequestWithID(map, CommonUrl.getList);
	}

	public void orderByNew(View view) {
		clearFormerData(true);
		getList(currentClassID, 2);
		order_hot_text.setBackgroundColor(Color.parseColor("#ffffff"));
		order_new_text.setBackgroundColor(Color.parseColor("#f6f6f6"));
		hideFilterLayout(order_new_text);
	}

	public void orderByHot(View view) {
		clearFormerData(true);
		getList(currentClassID, 1);
		order_hot_text.setBackgroundColor(Color.parseColor("#f6f6f6"));
		order_new_text.setBackgroundColor(Color.parseColor("#ffffff"));
		hideFilterLayout(order_hot_text);
	}

	public void clearFormerData(boolean clickOrder) {
		if (!clickOrder) {
			order_hot_text.setBackgroundColor(Color.parseColor("#ffffff"));
			order_new_text.setBackgroundColor(Color.parseColor("#ffffff"));
		}
		albumFilterList.clear();
		pageNo = 1;
		order = 1;
	}

	@Override
	public void onClickAdd(String albumVideoID, String seelistID,
			TextView album_add_cancel) {
		// TODO Auto-generated method stub
		this.album_add_cancel = album_add_cancel;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("albumVideoID", albumVideoID);
		netRequest.httpRequestWithID(map, CommonUrl.addSeeList);
	}

	@Override
	public void onClickCancel(String albumVideoID, String seelistID,
			TextView album_add_cancel) {
		// TODO Auto-generated method stub
		this.album_add_cancel = album_add_cancel;
		Map<String, Object> map = new HashMap<String, Object>();
		if (this.seelistID == null)
			map.put("seelistID", seelistID);
		else
			map.put("seelistID", this.seelistID);
		netRequest.httpRequestWithID(map, CommonUrl.updateSeeListDelFlag);
	}
}
