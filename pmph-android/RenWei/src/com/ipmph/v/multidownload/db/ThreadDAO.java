package com.ipmph.v.multidownload.db;

import java.util.List;

import com.ipmph.v.multidownload.entitis.ThreadInfo;

/**
 * ����������Ľӿ��
 *
 */
public interface ThreadDAO {
	// ����Q��
	public void insertThread(ThreadInfo info);
	// �h���Q��
	public void deleteThread(String url);
	// ���¾Q��
	public void updateThread(String url, int thread_id, long finished);
	// ��ԃ�Q��
	public List<ThreadInfo> queryThreads(String url);
	public List<ThreadInfo> getAllData(String table);
	// �Д�Q���Ƿ����
	public boolean isExists(String url, int threadId);
//	public boolean updateStatus(String url, int threadId);
}
