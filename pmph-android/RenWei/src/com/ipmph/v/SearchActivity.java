package com.ipmph.v;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ipmph.v.adapter.AlbumListAdapter;
import com.ipmph.v.adapter.SearchVideoAdapter;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.MyInterface.OnClickAddListener;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.object.AlbumObject.AlbumListObject;
import com.ipmph.v.object.SearchResultObject;
import com.ipmph.v.object.SearchResultObject.VideoListObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class SearchActivity extends FragmentActivity implements
		OnClickListener, NetRequestIterface, OnClickAddListener {

	private TextView search_btn, album_count, video_count;
	private EditText search_edit;
	private ImageView search_clear;
	private NetRequest netRequest;
	private PullToRefreshListView search_album_listview, search_video_listview;
	private View divider;
	private LinearLayout album_video_title, video_listview_layout;
	private int pageNo = 1;
	private boolean isLoadMore = false, firstLoadAlbum = true,
			firstLoadVideo = true;
	private List<AlbumListObject> albumVideoList = new ArrayList<AlbumListObject>();
	private List<VideoListObject> videoList = new ArrayList<VideoListObject>();
	private AlbumListAdapter albumAdapter;
	private SearchVideoAdapter videoAdapter;
	private TextView album_add_cancel;
	private String searchText="";
	private String seelistID;

	public void onCreate(Bundle savedInstanceStage) {
		super.onCreate(savedInstanceStage);
		setContentView(R.layout.search_activity);
		netRequest = new NetRequest(this, this);
		search_btn = (TextView) findViewById(R.id.search_btn);
		album_count = (TextView) findViewById(R.id.album_count);
		video_count = (TextView) findViewById(R.id.video_count);
		search_edit = (EditText) findViewById(R.id.search_edit);
		search_clear = (ImageView) findViewById(R.id.search_clear);
		divider = (View) findViewById(R.id.divider);
		album_video_title = (LinearLayout) findViewById(R.id.album_video_title);
		video_listview_layout = (LinearLayout) findViewById(R.id.video_listview_layout);
		search_album_listview = (PullToRefreshListView) findViewById(R.id.search_album_listview);
		search_video_listview = (PullToRefreshListView) findViewById(R.id.search_video_listview);
		search_clear.setOnClickListener(this);
		search_btn.setOnClickListener(this);
		album_count.setOnClickListener(this);
		video_count.setOnClickListener(this);
		Intent intent = getIntent();
		String label = intent.getStringExtra("label");
		if (label != null) {
			search_edit.setText(label);
			search_btn.setText(getString(R.string.search));
			search_clear.setVisibility(View.VISIBLE);
		}
		search_edit.addTextChangedListener(new EditChangedListener());
		search_edit
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							Log.d("gaolei", "IME_ACTION_SEARCH--------------");
							search();
							new CommonUtil(SearchActivity.this).imm
									.hideSoftInputFromWindow(
											search_edit.getWindowToken(), 0);
							return true;
						}
						return false;
					}
				});
		search_album_listview
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						isLoadMore = true;

						search();
					}
				});
		search_video_listview
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						isLoadMore = true;

						search();
					}
				});
	}

	class EditChangedListener implements TextWatcher {
		private CharSequence temp;// 监听前的文本

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (search_edit.getText().length() == 0) {
				search_btn.setText(getString(R.string.cancel));
				search_clear.setVisibility(View.INVISIBLE);
			}
			if (search_edit.getText().length() > 0) {
				search_btn.setText(getString(R.string.search));
				search_clear.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search_btn:
			if (search_btn.getText().equals(getString(R.string.cancel))) {
				finish();
			}
			if (search_btn.getText().equals(getString(R.string.search))) {
				if (search_edit.getText().toString().length() == 0) {
					CommonUtil
							.showToast(this, getString(R.string.please_input));
					return;
				}
				search();
			}
			break;
		case R.id.search_clear:
			if (search_edit.getText().toString().length() > 0) {
				search_edit.setText("");
			}
			break;
		case R.id.album_count:
			video_listview_layout.setVisibility(View.GONE);
			album_count.setTextColor(Color.parseColor("#00a1f1"));
			video_count.setTextColor(Color.parseColor("#2c2c2c"));
			break;
		case R.id.video_count:
			video_listview_layout.setVisibility(View.VISIBLE);
			album_count.setTextColor(Color.parseColor("#2c2c2c"));
			video_count.setTextColor(Color.parseColor("#00a1f1"));
			break;
		}
	}

	private void search() {
		String searchText=search_edit.getText().toString();
		
		if (isLoadMore) {
			pageNo += 1;
		} else {
			if(this.searchText.equals(search_edit.getText().toString()))return;
			albumVideoList.clear();
			videoList.clear();
			pageNo=1;
			album_count.setText(getString(R.string.album));
			video_count.setText(getString(R.string.video));
			searchText=search_edit.getText().toString();
		}
		MainActivity.loadingDialog.show();
		Map<String, Object> albumMap = new HashMap<String, Object>();
		albumMap.put("videoname",searchText );
		albumMap.put("pageSize", 20);
		albumMap.put("pageNo", pageNo);
		netRequest.httpRequestWithID(albumMap, CommonUrl.search);
		new CommonUtil(this).imm.hideSoftInputFromWindow(
				search_edit.getWindowToken(), 0);
		this.searchText=searchText;
	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		Log.d("gaolei", "result-------search-------" + result);
		JSONObject object;
		if (requestUrl.equals(CommonUrl.search)) {
			SearchResultObject searchObject = new Gson().fromJson(result,
					SearchResultObject.class);
			if (searchObject.albumVideo.albumVideoList.size() > 0
					|| searchObject.video.videoList.size() > 0) {
				album_video_title.setVisibility(View.VISIBLE);
				divider.setVisibility(View.VISIBLE);
				List<AlbumListObject> albumVideoListPart = searchObject.albumVideo.albumVideoList;
				albumVideoList.addAll(albumVideoListPart);

				List<VideoListObject> videoListPart = searchObject.video.videoList;
				videoList.addAll(videoListPart);

				album_count.setText(String.format(
						getString(R.string.album_count),
						searchObject.albumVideo.count, 0));
				video_count.setText(String.format(
						getString(R.string.video_count),
						searchObject.video.count, 0));
				if (firstLoadAlbum) {
					albumAdapter = new AlbumListAdapter(albumVideoList,
							SearchActivity.this, this);
					search_album_listview.setAdapter(albumAdapter);
					firstLoadAlbum = false;
				} else {
					if (albumVideoListPart.size() > 0)
						albumAdapter.changeList(albumVideoList);
				}

				if (firstLoadVideo) {
					videoAdapter = new SearchVideoAdapter(videoList,
							SearchActivity.this);
					search_video_listview.setAdapter(videoAdapter);
				} else {
					if (videoListPart.size() > 0)
						videoAdapter.changeData(videoList);
				}
			} else {
				if (!isLoadMore)
					CommonUtil.showToast(this,
							getString(R.string.search_no_result));
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
				MainActivity.loadingDialog.cancel();
				stopRefresh(search_album_listview);
				stopRefresh(search_video_listview);
			}
		}
		MainActivity.loadingDialog.cancel();
		stopRefresh(search_album_listview);
		stopRefresh(search_video_listview);
	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub
		MainActivity.loadingDialog.cancel();
		stopRefresh(search_album_listview);
		stopRefresh(search_video_listview);
	};

	private void stopRefresh(PullToRefreshListView listView) {
		listView.onRefreshComplete();
		isLoadMore = false;
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
		netRequest.httpRequestWithID(map,
				CommonUrl.updateSeeListDelFlag);
	}
}
