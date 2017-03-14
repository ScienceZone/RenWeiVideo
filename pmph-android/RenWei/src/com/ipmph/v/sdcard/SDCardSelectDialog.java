package com.ipmph.v.sdcard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipmph.v.R;
import com.ipmph.v.application.APPApplication;
import com.ipmph.v.sdcard.SDCardUtil.SDCardStat;
import com.ipmph.v.tool.CommonUtil;
import com.ipmph.v.tool.FileUtil;

public class SDCardSelectDialog extends Dialog implements View.OnClickListener {
	private Context mContext;

	private ListView mListView;
	private TextView mDownloadPathView;
	private View mOkButton;
	private View mCancelButton;
	private MyAdapter mAdapter;
	private List<SDCardStat> mSDCardList;
	public static String download_dir = "/RenWei/Video";
	public static String download_apk_dir = "/ARenWei/Other";
	private String pathFromSP="";
//	public static String mDownloadPath=SDCardUtil.getSDCardPath() + download_dir;

	public SDCardSelectDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sdcard_select_dialog);
		Window window = this.getWindow();
		// 去掉dialog默认的padding
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// 设置dialog的位置在底部
		lp.gravity = Gravity.BOTTOM;
		window.setAttributes(lp);

		mSDCardList = SDCardUtil.getSDCardStats(mContext);

		initUi();
		SharedPreferences sp = mContext.getSharedPreferences("SdcardPath", 0);
//		mDownloadPath = sp.getString("oldPath", "");
		pathFromSP = sp.getString("rootPath", "");
//		Log.d("gaolei", "mOldPath-------------" + mDownloadPath);
		Log.d("gaolei", "pathFromSP-------------" + pathFromSP);
//		// 显示默认的路径
//		if (mDownloadPath.length() == 0) {
//			mDownloadPath = SDCardUtil.getSDCardPath() + download_dir;
//			mAdapter.setSelectedItem(0);
//		} 
//		else {
			int index = 0;
			for (int i = 0; i < mSDCardList.size(); i++) {
				if (mSDCardList.get(i).rootPath.equals(pathFromSP)) {
					index = i;
				}
//			}
			Log.d("gaolei", "index-------------" + index);
			mAdapter.setSelectedItem(index);
		}
		setDownloadPath(APPApplication.downloadSdcardPath);
	}

	// 初始化
	public void initUi() {
		mListView = (ListView) findViewById(R.id.sdcard_listview);
		mDownloadPathView = (TextView) findViewById(R.id.card_path_textview);
		mOkButton = findViewById(R.id.btn_ok);
		mCancelButton = findViewById(R.id.btn_cancel);
		mOkButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);

		if (mSDCardList.isEmpty()) {
			mListView.setVisibility(View.GONE);
			mDownloadPathView.setVisibility(View.GONE);
			mCancelButton.setVisibility(View.GONE);
		} else {
			mAdapter = new MyAdapter();
			mListView.setAdapter(mAdapter);
			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (mSDCardList.size() != 1
							&& mAdapter.mSelectedItem != position) {
						SDCardStat sdcard = mAdapter.getItem(position);

						final String path = sdcard.rootPath + download_dir;
						mAdapter.setSelectedItem(position);
						setDownloadPath(path);

					}
				}
			});

			mListView.setVisibility(View.VISIBLE);
			mDownloadPathView.setVisibility(View.VISIBLE);
			mCancelButton.setVisibility(View.VISIBLE);
		}
	}

	private void setDownloadPath(String path) {
		final String text = mContext
				.getString(R.string.download_location, path);
		mDownloadPathView.setText(text);
	}

	// 监听事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			onClickOkButton();
			break;
		case R.id.btn_cancel:
			dismiss();
			break;
		default:
			break;
		}
	}

	// 点击确定
	public void onClickOkButton() {
		if (mSDCardList.size() == 0) {
			dismiss();
		} else {
			int index = mAdapter.getSelectedItem();
			if (index != -1) {
				String rootPath = mSDCardList.get(index).rootPath;
				if (pathFromSP.equals(rootPath)) {
					return;
				}
				saveDownloadPath(rootPath + download_dir, rootPath);
				dismiss();
			} else {
				mDownloadPathView.setText(R.string.sdcard_uncheck_tips);
			}
		}
	}

	// 得到文件的目录
	public String GetFilePath(File file) throws IOException {
		return file.getCanonicalPath();
	}

	// 改变下载位置
	private void saveDownloadPath(String fullPath, String rootPath) {
		File file =new File(fullPath); 
		if(!file .exists()  && !file .isDirectory()){
			 file .mkdir();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("oldPath", fullPath);
		map.put("rootPath", rootPath);
		CommonUtil.createSP(mContext, "SdcardPath", map);
		Toast.makeText(mContext, mContext.getString(R.string.sdcard_changed),
				Toast.LENGTH_SHORT).show();
	}

	class MyAdapter extends BaseAdapter {
		private int mSelectedItem =0;

		public MyAdapter() {
		}

		@Override
		public int getCount() {
			return mSDCardList.size();
		}

		@Override
		public SDCardStat getItem(int position) {
			return mSDCardList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.sdcard_item_view, parent, false);
				holder = new ViewHolder();
				holder.nameView = (TextView) convertView
						.findViewById(R.id.sdcard_name);
				holder.freeSpaceView = (TextView) convertView
						.findViewById(R.id.free_space);
				holder.checkView = (ImageView) convertView
						.findViewById(R.id.check_view);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			SDCardStat sdcard = mSDCardList.get(position);
			holder.nameView.setText(sdcard.name);
			holder.freeSpaceView.setText(FileUtil
					.formatFileSize(sdcard.freeSize));

			if (mSelectedItem == position) {
				holder.checkView.setVisibility(View.VISIBLE);
			} else {
				holder.checkView.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}


		public void setSelectedItem(int item) {
			mSelectedItem = item;
			notifyDataSetChanged();
		}

		public int getSelectedItem() {
			return mSelectedItem;
		}

	}

	public class ViewHolder {
		public TextView nameView = null;
		public TextView freeSpaceView = null;
		public ImageView checkView = null;
	}

}
