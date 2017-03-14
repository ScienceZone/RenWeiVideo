package com.ipmph.v.object;

import java.util.List;

import com.ipmph.v.object.AlbumObject.AlbumListObject;

public class SearchResultObject {
	public AlbumObject albumVideo;
	public VideoObject video;

	public class AlbumObject {
		public int count;
		public List<AlbumListObject> albumVideoList;

	}

	public class VideoObject {
		public int count;
		public List<VideoListObject> videoList;
	}

	public class VideoListObject {
		public String videoLength, videoProductTime, videoImgUrl, videoname,
				videoID, suffix;
		public int videopnumber;
	}
	//
	// public class AlbumListObject {
	// public String albumVideoID, albumVideodetails, albumVideoImgUrl,
	// jiuzhen, suffix, bingyin, albumcreateTime, zhengzhuang,
	// albumVideoname, videoEnglishName;
	// public int chuanran, isSeeList, albumPlayNums;
	// }
	// public String jiuzhen, bingyin, updateTime, albumVideoImgUrl,
	// zhengzhuang, albumVideodetails, albumVideoID, suffix,
	// albumVideoname, videoEnglishName;
	// public int chuanran;
}
