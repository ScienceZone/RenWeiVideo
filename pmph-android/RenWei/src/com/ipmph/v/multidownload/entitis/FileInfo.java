package com.ipmph.v.multidownload.entitis;

import java.io.Serializable;

/**
 * 
 * ���b�����d�ļ�����Ϣ ��FileInfo���л��������Intent���f����
 * 
 */
public class FileInfo implements Serializable {
	private int id;
	private String url, fileUrl;
	private String fileName,videoImgUrl;
	private long length;
	private int finished;
	private boolean isComplete;

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public FileInfo() {
		super();
	}

	/**
	 * 
	 * @param id
	 *            �ļ���ID
	 * @param url
	 *            �ļ������d��ַ
	 * @param fileName
	 *            �ļ�������
	 * @param length
	 *            �ļ��Ŀ���С
	 * @param finished
	 *            �ļ��ѽ�����˶���
	 */
	public FileInfo(int id, String url, String fileUrl,String fileName,
			long length, int finished,boolean isComplete,String videoImgUrl) {
		super();
		this.id = id;
		this.url = url;
		this.fileUrl = fileUrl;
		this.fileName = fileName;
		this.length = length;
		this.finished = finished;
		this.isComplete=isComplete;
		this.videoImgUrl=videoImgUrl;
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

	public String getFileName() {
		return fileName;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	public String getVideoImgUrl() {
		return videoImgUrl;
	}

	public void setVideoImgUrl(String videoImgUrl) {
		this.videoImgUrl = videoImgUrl;
	}

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", url=" + url + ",fileUrl=" + fileUrl
				+ ", fileName=" + fileName + ", length=" + length
				+ ", finished=" + finished + "]";
	}

}
