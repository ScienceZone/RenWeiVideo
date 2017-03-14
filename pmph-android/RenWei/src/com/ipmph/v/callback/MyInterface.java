package com.ipmph.v.callback;

import java.io.IOException;

import android.widget.TextView;

/**
 * Created by Administrator on 2015/11/16.
 */
public class MyInterface {

	public interface NetRequestIterface {
		void changeView(String result, String requestUrl);

		void exception(IOException e, String requestUrl);
		// void showProgressBar(int bytesWritten, int totalSize);
	}

	public interface OnLogoutListener {
		void onLogout();
	}

	public interface OnClickItemListener {
		void onClickItem(int position);
	}

	public interface OnClickAddListener {
		void onClickAdd(String albumVideoID,String seelistID,TextView album_add_cancel);
		void onClickCancel(String albumVideoID,String seelistID,TextView album_add_cancel);
	}
}
