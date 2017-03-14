package com.ipmph.v;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.ipmph.v.fragment.AlbumFragment;
import com.ipmph.v.fragment.SearchFragment;
import com.ipmph.v.fragment.HomeFragment;
import com.ipmph.v.fragment.MyFragment;
import com.ipmph.v.fragment.RankFragment;
import com.ipmph.v.tool.CommonUtil;

public class MainActivity extends BaseActivity  {

	/** 请求CODE */
	public final static int CHANNELREQUEST = 1;
	/** 调整返回的RESULTCODE */
	public final static int CHANNELRESULT = 10;
	private Fragment homeFragment, albumFragment, rankFragment, findFragment,
			myFragment;
	private FrameLayout home_framelayout, album_framelayout, rank_framelayout,
			find_framelayout, my_framelayout;
	private ImageView home_tabimg, album_tabimg, rank_tabimg, find_tabimg,
			my_tabimg;
	public static Dialog loadingDialog;

	public void onCreate(Bundle savedInstanceStage) {
		super.onCreate(savedInstanceStage);
		setContentView(R.layout.main_activity);
		Log.d("gaolei", "MainActivity--------onCreate---------");
		loadingDialog = CommonUtil.createLoadingDialog(this);
		loadingDialog.setCancelable(true);
		loadingDialog.setCanceledOnTouchOutside(true);	// getAllInterface();
		homeFragment = new HomeFragment();
		albumFragment = new AlbumFragment();
		rankFragment = new RankFragment();
		findFragment = new SearchFragment();
		myFragment = new MyFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.home_framelayout, homeFragment);

		
		transaction.commit();
		home_framelayout = (FrameLayout) findViewById(R.id.home_framelayout);
		album_framelayout = (FrameLayout) findViewById(R.id.album_framelayout);
		rank_framelayout = (FrameLayout) findViewById(R.id.rank_framelayout);
		find_framelayout = (FrameLayout) findViewById(R.id.find_framelayout);
		my_framelayout = (FrameLayout) findViewById(R.id.my_framelayout);

		home_tabimg = (ImageView) findViewById(R.id.home_tabimg);
		album_tabimg = (ImageView) findViewById(R.id.album_tabimg);
		rank_tabimg = (ImageView) findViewById(R.id.rank_tabimg);
		find_tabimg = (ImageView) findViewById(R.id.find_tabimg);
		my_tabimg = (ImageView) findViewById(R.id.my_tabimg);
	}

	public void onStart() {
		super.onStart();
	}

	public void clickHomeTab(View v) {
		changeTab(0);
	}

	public void clickAlbumTab(View v) {
		changeTab(1);
	}

	public void clickRankTab(View v) {
		changeTab(2);
	}

	public void clickFindTab(View v) {
		changeTab(3);
	}

	public void clickMyTab(View v) {
		changeTab(4);
	}

	public void changeTab(int i) {
		clearSelect();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		switch (i) {
		case 0:
			home_tabimg.setImageResource(R.drawable.home_select);
			home_framelayout.setVisibility(View.VISIBLE);
			break;
		case 1:
			album_tabimg.setImageResource(R.drawable.album_select);
			album_framelayout.setVisibility(View.VISIBLE);
			transaction.replace(R.id.album_framelayout, albumFragment);
			transaction.commit();
			break;
		case 2:
			rank_tabimg.setImageResource(R.drawable.rank_select);
			rank_framelayout.setVisibility(View.VISIBLE);
			transaction.replace(R.id.rank_framelayout, rankFragment);
			transaction.commit();
			break;
		case 3:
			find_tabimg.setImageResource(R.drawable.find_select);
			find_framelayout.setVisibility(View.VISIBLE);
			transaction.replace(R.id.find_framelayout, findFragment);
			transaction.commit();
			// Intent intent = new Intent(this, SearchActivity.class);
			// startActivity(intent);
			break;
		case 4:
			my_tabimg.setImageResource(R.drawable.my_select);
			my_framelayout.setVisibility(View.VISIBLE);
			transaction.replace(R.id.my_framelayout, myFragment);
			transaction.commit();
			break;
		}
	}

	public void clearSelect() {
		home_tabimg.setImageResource(R.drawable.home);
		home_framelayout.setVisibility(View.GONE);
		album_tabimg.setImageResource(R.drawable.album);
		album_framelayout.setVisibility(View.GONE);
		rank_tabimg.setImageResource(R.drawable.rank);
		rank_framelayout.setVisibility(View.GONE);
		find_tabimg.setImageResource(R.drawable.find);
		find_framelayout.setVisibility(View.GONE);
		my_tabimg.setImageResource(R.drawable.my);
		my_framelayout.setVisibility(View.GONE);
	}

	private long mExitTime;

	// @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		// 拦截MENU按钮点击事件，让他无任何操作
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case CHANNELREQUEST:
			if (resultCode == CHANNELRESULT) {
				// setChangelView();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
