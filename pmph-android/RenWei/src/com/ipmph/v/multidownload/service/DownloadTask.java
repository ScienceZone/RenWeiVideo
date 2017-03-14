package com.ipmph.v.multidownload.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ipmph.v.application.APPApplication;
import com.ipmph.v.multidownload.db.ThreadDAO;
import com.ipmph.v.multidownload.db.ThreadDAOImple;
import com.ipmph.v.multidownload.entitis.FileInfo;
import com.ipmph.v.multidownload.entitis.ThreadInfo;

public class DownloadTask {
	private Context mComtext = null;
	private FileInfo mFileInfo = null;
	public static ThreadDAO mDao = null;
	private long mFinished = 0;
	private int mThreadCount = 1;
	public boolean mIsPause = false;
	private List<DownloadThread> mThreadlist = null;
	public static ExecutorService sExecutorService = Executors
			.newCachedThreadPool();
	private int lastProgress=-1;

	public DownloadTask(Context comtext, FileInfo fileInfo, int threadCount) {
		super();
		this.mThreadCount = threadCount;
		this.mComtext = comtext;
		this.mFileInfo = fileInfo;
		this.mDao = new ThreadDAOImple(mComtext);
	}

	public void download() {
		// �����ݿ��л�ȡ���ص���Ϣ
		List<ThreadInfo> list = mDao.queryThreads(mFileInfo.getUrl());
		if (list.size() == 0) {
			long length = mFileInfo.getLength();
			long block = length / mThreadCount;
			for (int i = 0; i < mThreadCount; i++) {
				// ����ÿ���߳̿�ʼ���غͽ������ص�λ��
				long start = i * block;
				long end = (i + 1) * block - 1;
				if (i == mThreadCount - 1) {
					end = length - 1;
				}
				ThreadInfo threadInfo = new ThreadInfo(i, mFileInfo.getUrl(),
						start, end, 0);
				list.add(threadInfo);
			}
		}
		mThreadlist = new ArrayList<DownloadThread>();
		for (ThreadInfo info : list) {
			DownloadThread thread = new DownloadThread(info);
			// thread.start();
			// ʹ���̳߳�ִ����������
			DownloadTask.sExecutorService.execute(thread);
			mThreadlist.add(thread);
			// ��������첻�������d��Ϣ��������d��Ϣ
			mDao.insertThread(info);
		}
	}

	public synchronized void checkAllFinished() {
		boolean allFinished = true;
		for (DownloadThread thread : mThreadlist) {
			if (!thread.isFinished) {
				allFinished = false;
				break;
			}
		}
		if (allFinished == true) {
//			Log.d("gaolei", "allFinished-----------------");
			// ���d��ɺ󣬄h����������Ϣ
			mDao.deleteThread(mFileInfo.getUrl());
			// ֪ͨUI�ĸ��߳��������
			Intent intent = new Intent(DownloadService.ACTION_FINISHED);
			intent.putExtra("fileInfo", mFileInfo);
			mComtext.sendBroadcast(intent);

		}
	}

	class DownloadThread extends Thread {
		private ThreadInfo threadInfo = null;
		// ��ʶ�߳��Ƿ�ִ�����
		public boolean isFinished = false;

		public DownloadThread(ThreadInfo threadInfo) {
			this.threadInfo = threadInfo;
		}

		@Override
		public void run() {

			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			InputStream is = null;
			try {
				URL url = new URL(mFileInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5 * 1000);
				conn.setRequestMethod("GET");

				long start = threadInfo.getStart() + threadInfo.getFinished();
				// �O�����d�ļ��_ʼ���Y����λ��
				conn.setRequestProperty("Range", "bytes=" + start + "-"
						+ threadInfo.getEnd());
				
				File file = new File(APPApplication.downloadSdcardPath,
						mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);
				mFinished += threadInfo.getFinished();
				Intent intent = new Intent();
				intent.setAction(DownloadService.ACTION_UPDATE);
				int code = conn.getResponseCode();
				if (code == HttpURLConnection.HTTP_PARTIAL) {
					is = conn.getInputStream();
					byte[] buffer = new byte[1024];
					int len = -1;
					// ����UIˢ��ʱ��
					long lastUpdateTime = 0;
					
					while ((len = is.read(buffer)) > 0) {
						raf.write(buffer, 0, len);
						// �ۼ������ļ���ɽ���
						mFinished += len;
						// �ۼ�ÿ���߳���ɵĽ���
						threadInfo.setFinished(threadInfo.getFinished() + len);
						// �O�à�500���׸���һ��
						long time = System.currentTimeMillis();
//						if (Math.abs(time - lastUpdateTime) >= 1000) {
//						Log.d("gaolei","mFinished---------|"+mFinished);
                        int progress=(int) (mFinished * 100 / mFileInfo
								.getLength());
                        if(lastProgress!=progress){
							intent.putExtra("finished",progress);
							// ��ʾ���������ļ���id
							intent.putExtra("id", mFileInfo.getId());
//							Log.d("gaolei","lastProgress----DownloadTask-----|"+lastProgress);
//							Log.d("gaolei","progress----DownloadTask-----|"+progress);
							// �l�͏V���oActivity
							mComtext.sendBroadcast(intent);
							lastProgress=progress;
                        }
//							lastUpdateTime = time;
//						}
						if (mIsPause) {
							mDao.updateThread(threadInfo.getUrl(),
									threadInfo.getId(),
									threadInfo.getFinished());
							return;
						}
					}
				}
				// ��ʶ�߳��Ƿ�ִ�����
				isFinished = true;
				// �ж��Ƿ������̶߳�ִ�����
				checkAllFinished();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
				try {
					if (is != null) {
						is.close();
					}
					if (raf != null) {
						raf.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			super.run();
		}
	}

}