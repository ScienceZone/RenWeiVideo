package com.ipmph.v.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import com.ipmph.v.fragment.ImagePagerFragment;
import com.ipmph.v.object.VideoClassObject.VideoListObject;

public class FirstClassPagerAdapter extends FragmentPagerAdapter {
	private List<VideoListObject> list;
	private String from;

	public FirstClassPagerAdapter(String from, FragmentManager fm,
			List<VideoListObject> list) {
		super(fm);
		this.list = list;
		this.from = from;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		VideoListObject object = list.get(position);
//		Log.d("gaolei", "getImgUrl()---------"+from+"----------"+object.getImgUrl());
		return new ImagePagerFragment().create(from,object);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return PagerAdapter.POSITION_NONE;
	}

	public void changeList(List<VideoListObject> list) {
		this.list = list;
	}

}
