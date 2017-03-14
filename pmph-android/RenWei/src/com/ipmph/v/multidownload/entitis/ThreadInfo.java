package com.ipmph.v.multidownload.entitis;

/**
 * 
 * �Q����Ϣ����b�Q�̵�ID���Q�̵�url���Q���_ʼλ�ã��Y��λ�ã��Լ��ѽ���ɵ�λ��
 *
 */
public class ThreadInfo {
	private int id;
	private String url;
	private String fileUrl;
	private long start;
	private long end;
	private long finished;

	public ThreadInfo() {
		super();
	}

	/**
	 * 
	 * @param id
	 *            �Q�̵�ID
	 * @param url
	 *            ���d�ļ��ľW�j��ַ
	 * @param start2
	 *            �Q�����d���_ʼλ��
	 * @param end2
	 *            �Q�����d�ĽY��λ��
	 * @param finished
	 *            �Q���ѽ����d���Ă�λ��
	 */
	public ThreadInfo(int id, String url, long start2, long end2, int finished) {
		super();
		this.id = id;
		this.url = url;
		this.start = start2;
		this.end = end2;
		this.finished = finished;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public long getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public long getFinished() {
		return finished;
	}

	public void setFinished(long l) {
		this.finished = l;
	}

	@Override
	public String toString() {
		return "ThreadInfo [id=" + id + ", url=" + url + ", start=" + start + ", end=" + end + ", finished=" + finished
				+ "]";
	}

}
