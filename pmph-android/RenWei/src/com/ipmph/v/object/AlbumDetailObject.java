package com.ipmph.v.object;

import java.util.ArrayList;
import java.util.List;

import com.ipmph.v.object.VideoDetailObject.VideoPlayObject.VideoPathObject;

public class AlbumDetailObject {
	public String message, state, commentSwitch;
	public AlbumContentObject albumvideo;
	public List<AlbumOtherVideoObject> otherVideoList;

	public class AlbumContentObject {
		public String albumvideoname, seeListID, updateTime, albumvideoid,
		albumvideonewdetails,albumvideodetails;
		public int isSeeList, albumvideopnumber;

		public ArrayList<VideoClassObject> albumvideoclassList;
		public ArrayList<VideoPathObject> videopathList;
		public ArrayList<VideoLabelObject> videoLabelList;
		public ArrayList<AlbumVideoObject> albumvideolist;

		public class VideoClassObject {
			public String videoclassID, videoclassName, videoclassPid,
					videoclassLeavel;

		}

//		public class VideoPathObject {
//			public String videoUrl, videoStatus;
//		}

		public class VideoLabelObject {
			public String videoLabelName, videoLabelIDvideoLabelID;
		}

		public class AlbumVideoObject {
			public String videoname, videoHtml, videolenght, videoFlash,
					videoJs, videoid,collectionID;
			public int videodowns, iscollection, videoIndex, videoups,
					videopnumber;
			public ArrayList<VideoPathObject> videopathList;
		}
	}

	public  class AlbumOtherVideoObject {
		public String jiuzhen,bingyin,albumVideoImgUrl, zhengzhuang,
		albumcreateTime, suffix,albumVideoname,albumVideodetails,albumVideoID,
		videoEnglishName;
		public int albumPlayNums=-1,albumVideopnumber=-1,isSeeList,chuanran;
	}
}
