package com.ipmph.v.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ipmph.v.R;
import com.ipmph.v.adapter.HomeFragmentAdapter;
import com.ipmph.v.application.APPApplication;
import com.ipmph.v.callback.MyInterface.NetRequestIterface;
import com.ipmph.v.callback.NetRequest;
import com.ipmph.v.object.HeaderClassObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.view.ColumnHorizontalScrollView;

/**
 * （android高仿系列）今日头条 --新闻阅读器 author:RA blog : http://blog.csdn.net/vipzjyno1/
 */
public class HomeFragment extends Fragment implements OnClickListener,
		NetRequestIterface {
	/** 自定义HorizontalScrollView */
	private ColumnHorizontalScrollView mColumnHorizontalScrollView;
	LinearLayout mRadioGroup_content;
	private ViewPager mViewPager;
	/** 当前选中的栏目 */
	private int columnSelectIndex = 0;
	private ImageView indicator;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private NetRequest netRequest;
	public static List<HeaderClassObject> classList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.home_fragment, null);
		netRequest = new NetRequest(this, getActivity());
		
		initView(v);
		Drawable drawable = getResources().getDrawable(
				R.drawable.indicator_blue);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		new TextView(getActivity()).setCompoundDrawables(drawable, null, null,
				null);
		netRequest.httpRequest(null, CommonUrl.getClassList);
		return v;
	}

	/** 初始化layout控件 */
	private void initView(View v) {
		mColumnHorizontalScrollView = (ColumnHorizontalScrollView) v
				.findViewById(R.id.mColumnHorizontalScrollView);
		mRadioGroup_content = (LinearLayout) v
				.findViewById(R.id.mRadioGroup_content);
		mViewPager = (ViewPager) v.findViewById(R.id.mViewPager);
		indicator = (ImageView) v.findViewById(R.id.indicator);
	}

	/**
	 * 初始化Column栏目项
	 * */
	private void initTabColumn() {
		mRadioGroup_content.removeAllViews();
		int count = classList.size();
		mColumnHorizontalScrollView.setParam(getActivity(),
				APPApplication.screenWidth, mRadioGroup_content);
		try{
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		for (int i = 0; i <= count; i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			View channel_layout = inflater.inflate(
					R.layout.title_channel_layout, null);
			TextView channel_textview = (TextView) channel_layout
					.findViewById(R.id.channel_name);
			ImageView channel_indicator = (ImageView) channel_layout
					.findViewById(R.id.channel_indicator);
			channel_layout.setId(i);
			if (i == 0)
				channel_textview.setText(getString(R.string.home));
			else
				channel_textview.setText(classList.get(i - 1).videoclassName);
			if (columnSelectIndex == i) {
				channel_textview.setTextColor(Color.parseColor("#00a1f1"));
				channel_indicator.setVisibility(View.VISIBLE);
			}
			mRadioGroup_content.addView(channel_layout, i, params);
			channel_layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
						View localView = mRadioGroup_content.getChildAt(i);
						if (localView.getId() == v.getId()) {
							mViewPager.setCurrentItem(i);
							selectTab(i);
						}
					}
				}
			});

		}
		}catch(Exception e){
			
		}
	}

	/**
	 * 选择的Column里面的Tab
	 * */
	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			View checkView = mRadioGroup_content.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - APPApplication.screenWidth / 2;
			mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
		}
		// 判断是否选中
		for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {

			TextView checkTextView = (TextView) mRadioGroup_content.getChildAt(
					j).findViewById(R.id.channel_name);
			ImageView checkImageView = (ImageView) mRadioGroup_content
					.getChildAt(j).findViewById(R.id.channel_indicator);
			if (j == tab_postion) {
				checkTextView.setTextColor(Color.parseColor("#00a1f1"));
				checkImageView.setVisibility(View.VISIBLE);
			} else {
				checkTextView.setTextColor(Color.parseColor("#303336"));
				checkImageView.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 初始化Fragment
	 * */
	private void initFragment() {
		fragments.clear();// 清空
		int count = classList.size();
		for (int i = 0; i <= count; i++) {
			Bundle bundle = new Bundle();
			if (i == 0) {
				if(isAdded()){
				bundle.putString("videoclassName", getString(R.string.home));
				bundle.putString("videoclassID", "");
				HomePageFragment homePageFragment = new HomePageFragment();
				homePageFragment.setArguments(bundle);
				fragments.add(homePageFragment);
				}
			} 
//			else if (i == 1) {
//				bundle.putString("videoclassName",
//						classList.get(i - 1).videoclassName);
//				bundle.putString("videoclassID",
//						classList.get(i - 1).videoclassID);
//				bundle.putInt("position", i - 1);
//				FirstClassFragment firstClassFragment = new FirstClassFragment();
//				firstClassFragment.setArguments(bundle);
//				fragments.add(firstClassFragment);
//			} else if (i == 2) {
//				bundle.putString("videoclassName",
//						classList.get(i - 1).videoclassName);
//				bundle.putString("videoclassID",
//						classList.get(i - 1).videoclassID);
//				bundle.putInt("position", i - 1);
//				OperationClassFragment operationClassFragment = new OperationClassFragment();
//				operationClassFragment.setArguments(bundle);
//				fragments.add(operationClassFragment);
//			} else if (i == 3) {
//				bundle.putString("videoclassName",
//						classList.get(i - 1).videoclassName);
//				bundle.putString("videoclassID",
//						classList.get(i - 1).videoclassID);
//				bundle.putInt("position", i - 1);
//				InstrumentClassFragment instrumentClassFragment = new InstrumentClassFragment();
//				instrumentClassFragment.setArguments(bundle);
//				fragments.add(instrumentClassFragment);
//			}
			 else{
				 bundle.putString("videoclassName",
							classList.get(i - 1).videoclassName);
					bundle.putString("videoclassID",
							classList.get(i - 1).videoclassID);
					bundle.putInt("position", i - 1);
					FirstClassFragment firstClassFragment = new FirstClassFragment();
					firstClassFragment.setArguments(bundle);
					fragments.add(firstClassFragment);
			 }
		}
		if(isAdded()){
		HomeFragmentAdapter mAdapter = new HomeFragmentAdapter(getActivity()
				.getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(pageListener);
		mViewPager.setOffscreenPageLimit(fragments.size());
	}
	}
	/**
	 * ViewPager切换监听方法
	 * */
	public OnPageChangeListener pageListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			mViewPager.setCurrentItem(position);
			selectTab(position);
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeView(String result, String requestUrl) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			classList = new Gson().fromJson(object.getString("classList"),
					new TypeToken<List<HeaderClassObject>>() {
					}.getType());
			// Log.d("gaolei", "result---------getClassList-------" + result);
			initTabColumn();
			initFragment();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void exception(IOException e, String requestUrl) {
		// TODO Auto-generated method stub

	}

}
