package com.ipmph.v.object;

import java.util.ArrayList;

public class AlbumObject {
	public String message, state;
	public int count;
	ArrayList<AlbumListObject> albumVideoList;

	public class AlbumListObject {
		public String jiuzhen, bingyin, updateTime,albumcreateTime, albumVideoImgUrl,
				zhengzhuang, albumVideodetails, albumVideoID,seeListID, suffix,
				albumVideoname, videoEnglishName;
		public int chuanran,isSeeList;
	}

}
