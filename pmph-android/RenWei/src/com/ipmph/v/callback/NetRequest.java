package com.ipmph.v.callback;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.util.Log;

import com.ipmph.v.R;
import com.ipmph.v.object.LoginResultObject;
import com.ipmph.v.tool.CommonUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class NetRequest {
	private MyInterface.NetRequestIterface netRequestIterface;
	private Activity context;
	OkHttpClient okHttpClient;

	public NetRequest(MyInterface.NetRequestIterface netRequestIterface,
			Activity context) {
		this.netRequestIterface = netRequestIterface;
		this.context = context;
	}

	private OkHttpClient getInstance() {
		if (okHttpClient == null)
			okHttpClient = new OkHttpClient();
		return okHttpClient;
	}

	public void httpRequest(Map<String, Object> map, final String requestUrl) {
		if (!CommonUtil.isConnectingToInternet(context)) {
			CommonUtil.showLongToast(context,
					context.getString(R.string.internet_fail_connect));
			return;
		}
		try {
			OkHttpClient mOkHttpClient = getInstance();
			FormEncodingBuilder builder = new FormEncodingBuilder();
			if (null != map && !map.isEmpty())
				for (String key : map.keySet()) {
					builder.add(key, map.get(key) + "");
				}

			Request request = new Request.Builder().url(requestUrl)
					.post(builder.build()).build();

			mOkHttpClient.setConnectTimeout(5000, TimeUnit.MILLISECONDS);
			mOkHttpClient.setReadTimeout(5000, TimeUnit.MILLISECONDS);
			mOkHttpClient.setWriteTimeout(5000, TimeUnit.MILLISECONDS);

			mOkHttpClient.newCall(request).enqueue(new Callback() {

				@Override
				public void onFailure(Request request, IOException e) {
					netRequestIterface.exception(e, requestUrl);
				}

				@Override
				public void onResponse(final Response response)
						throws IOException {
					final String result = response.body().string();
					context.runOnUiThread(new Runnable() {
						public void run() {
							netRequestIterface.changeView(result, requestUrl);
							Log.d("gaolei", "cancel----------------------");
						}

					});
				}
			});
		} catch (Exception e) {
			Log.d("gaolei", "e.getMessage()-----------------" + e.getMessage());
		}
	}

	public void httpRequestWithID(Map<String, Object> map,
			final String requestUrl) {
		if (!CommonUtil.isConnectingToInternet(context)) {
			CommonUtil.showLongToast(context,
					context.getString(R.string.internet_fail_connect));
			return;
		}
		try {
			OkHttpClient mOkHttpClient = getInstance();
			FormEncodingBuilder builder = new FormEncodingBuilder();
			if (null != map && !map.isEmpty()) {
				String sessionId = LoginResultObject.getInstance().sessionId;
				map.put("jeesite.session.id", sessionId);
				for (String key : map.keySet()) {
					builder.add(key, map.get(key) + "");
				}
			}
			Request request = new Request.Builder().url(requestUrl)
					.post(builder.build()).build();

			mOkHttpClient.setConnectTimeout(5000, TimeUnit.MILLISECONDS);
			mOkHttpClient.setReadTimeout(5000, TimeUnit.MILLISECONDS);
			mOkHttpClient.setWriteTimeout(5000, TimeUnit.MILLISECONDS);

			mOkHttpClient.newCall(request).enqueue(new Callback() {

				@Override
				public void onFailure(Request request, IOException e) {
					netRequestIterface.exception(e, requestUrl);
				}

				@Override
				public void onResponse(final Response response)
						throws IOException {
					final String result = response.body().string();
					context.runOnUiThread(new Runnable() {
						public void run() {
							netRequestIterface.changeView(result, requestUrl);
							// loadingDialog.cancel();
							Log.d("gaolei", "cancel----------------------");
						}

					});
				}
			});
		} catch (Exception e) {
			Log.d("gaolei", "e.getMessage()-----------------" + e.getMessage());
		}
	}

	public void httpRequestGet(Map<String, Object> map, final String requestUrl) {
		if (!CommonUtil.isConnectingToInternet(context)) {
			CommonUtil.showLongToast(context,
					context.getString(R.string.internet_fail_connect));
			return;
		}
		try {
			OkHttpClient mOkHttpClient = getInstance();
//			FormEncodingBuilder builder = new FormEncodingBuilder();
//			if (null != map && !map.isEmpty()) {
//				for (String key : map.keySet()) {
//					builder.add(key, map.get(key) + "");
//				}
//			}
			Request request = new Request.Builder().url(requestUrl).build();

			mOkHttpClient.setConnectTimeout(5000, TimeUnit.MILLISECONDS);
			mOkHttpClient.setReadTimeout(5000, TimeUnit.MILLISECONDS);
			mOkHttpClient.setWriteTimeout(5000, TimeUnit.MILLISECONDS);

			mOkHttpClient.newCall(request).enqueue(new Callback() {

				@Override
				public void onFailure(Request request, IOException e) {
					netRequestIterface.exception(e, requestUrl);
				}

				@Override
				public void onResponse(final Response response)
						throws IOException {
					final String result = response.body().string();
					 context.runOnUiThread(new Runnable() {
					 public void run() {
					 netRequestIterface.changeView(result, requestUrl);
					 // loadingDialog.cancel();
					 Log.d("gaolei", "cancel----------------------");
					 }
					
					 });
				}
			});
		} catch (Exception e) {
			Log.d("gaolei", "e.getMessage()-----------------" + e.getMessage());
		}
	}
}
