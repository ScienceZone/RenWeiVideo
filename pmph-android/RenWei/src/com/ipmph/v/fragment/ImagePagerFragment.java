package com.ipmph.v.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipmph.v.R;
import com.ipmph.v.VideoPlayerActivity;
import com.ipmph.v.object.VideoClassObject.VideoListObject;
import com.ipmph.v.tool.CommonUrl;
import com.ipmph.v.tool.CommonUtil;

public class ImagePagerFragment extends Fragment implements OnClickListener {

	private ImageView pager_image;
	private TextView pager_text;
	private RelativeLayout pager_fragment_layout;
	public static boolean showNetImg = true;
	private String from;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.pager_fragment, null);
		initView(view);

		return view;
	}

	public ImagePagerFragment create(String from, VideoListObject object) {
		ImagePagerFragment imageDisplayFragment = new ImagePagerFragment();
		Bundle bundle = new Bundle();
		String imageUrlComplete = null;
		bundle.putString("url", object.getUrl());
		bundle.putString("type", object.getType());
		bundle.putString("name", object.getName());
		bundle.putString("imageUrl", object.getImgUrl());
			if (object.getType().equals("5")) {
				imageUrlComplete = CommonUrl.baseUrl + object.getImgUrl() + ".super"
						+ object.getImgSuffix();
			} else {
				imageUrlComplete = CommonUrl.baseUrl + object.getImgUrl()
						+ object.getImgSuffix();
			}
//		Log.d("gaolei", "object.getImgUrl()-----------" + from
//				+ "-------------" + object.getImgUrl());
//		Log.d("gaolei", "imageUrlComplete-----------" + from + "-------------"
//				+ imageUrlComplete);
		bundle.putString("imageUrlComplete", imageUrlComplete);

		// Log.d("gaolei", "url-----------put-------------"+url);
		imageDisplayFragment.setArguments(bundle);
		return imageDisplayFragment;
	}

	private void initView(View view) {
		pager_image = (ImageView) view.findViewById(R.id.pager_image);
		pager_text = (TextView) view.findViewById(R.id.pager_text);
		pager_fragment_layout = (RelativeLayout) view
				.findViewById(R.id.pager_fragment_layout);
		Bundle bundle = getArguments();
		String imageUrl = bundle.getString("imageUrl");
		String imageUrlComplete = bundle.getString("imageUrlComplete");
		final String url = bundle.getString("url");
		final String type = bundle.getString("type");
		final String name = bundle.getString("name");
		pager_text.setText(name);
		pager_image.setTag(url);
//		 Log.d("gaolei", "imageUrl-----------ImagePagerFragment-------------"
//		 + imageUrl);
		//
		// Log.d("gaolei", "showNetImg-------------"+showNetImg);
		if(imageUrl.equals("")||imageUrl.equals(" ")){
//		if (pager_image.getTag().equalse(url)) {
			pager_image.setImageResource(R.drawable.default_img_horizontal);
		}else{
			CommonUtil.getUtilInstance().displayLowQualityInImage(imageUrlComplete,
					pager_image);
			
		}
		pager_fragment_layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						VideoPlayerActivity.class);
				Log.d("gaolei", "url---------------" + url);
				if(url.split("=").length>1){
				if (type.equals("4")) {
					intent.putExtra("albumVideoID", url.split("=")[1]);
					intent.putExtra("albumVideoname",name);
				} else {
					intent.putExtra("videoID", url.split("=")[1]);
					intent.putExtra("albumVideoname",name);
				}
				startActivity(intent);
				}
			}
		});

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

	}

}
