package com.ipmph.v.object;

import java.io.Serializable;
import java.util.ArrayList;

public class VideoClassObject {
	public String name, url;
	public ArrayList<VideoListObject> data;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ArrayList<VideoListObject> getData() {
		return data;
	}

	public void setData(ArrayList<VideoListObject> data) {
		this.data = data;
	}

	public class VideoListObject implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String type, name, url, imgUrl, imgSuffix, category, updateDate;
		int plays;
//		public String videoLength, videoProductTime, videoImgUrl, videoname,
//		videoID, suffix;
//public int videopnumber;
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}

		public String getImgSuffix() {
			return imgSuffix;
		}

		public void setImgSuffix(String imgSuffix) {
			this.imgSuffix = imgSuffix;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public int getPlays() {
			return plays;
		}

		public void setPlays(int plays) {
			this.plays = plays;
		}

		public String getUpdateDate() {
			return updateDate;
		}

		public void setUpdateDate(String updateDate) {
			this.updateDate = updateDate;
		}

		/*
		 * @Override public int describeContents() { // TODO Auto-generated
		 * method stub return 0; }
		 * 
		 * @Override public void writeToParcel(Parcel dest, int flags) { // TODO
		 * Auto-generated method stub dest.writeString(type);
		 * dest.writeString(name); dest.writeString(url);
		 * dest.writeString(imgUrl); dest.writeString(imgSuffix);
		 * dest.writeString(category); dest.writeString(plays);
		 * dest.writeString(updateDate); }
		 * 
		 * public static final Parcelable.Creator<VideoObject> CREATOR = new
		 * Creator<VideoObject>() { // 实现从source中创建出类的实例的功能
		 * 
		 * @Override public VideoObject createFromParcel(Parcel source) {
		 * VideoObject videoObject = new VideoObject(); videoObject.type =
		 * source.readString(); videoObject.name = source.readString();
		 * videoObject.url = source.readString(); videoObject.imgUrl =
		 * source.readString(); videoObject.imgSuffix = source.readString();
		 * videoObject.category = source.readString(); videoObject.plays =
		 * source.readString(); videoObject.name = source.readString();
		 * videoObject.updateDate = source.readString(); return videoObject; }
		 * 
		 * // 创建一个类型为T，长度为size的数组
		 * 
		 * @Override public VideoObject[] newArray(int size) { return new
		 * VideoObject[size]; } };
		 */
	}

}
