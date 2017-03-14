package com.ipmph.v.object;

import java.util.ArrayList;

public class VideoDetailObject {

	public VideoPlayObject video;
	public String state, message, commentSwitch;
	public ArrayList<OtherVideoObject> otherVideoList;

	public static class VideoPlayObject {
		public ArrayList<VideoClassObject> videoclassList;
		public ArrayList<VideoPathObject> videopathList;
		public ArrayList<VideoLabelObject> videoLabelList;
		public String videoShou, videolenght, videoID, videoStatus, videoPath,
				createTime, videourl,videoImgUrl,suffix, videoname, videoauthor, videodetails,
				collectionID;
		public int iscollection, videopnumber, videozan, videocai;

		public class VideoClassObject {
			public String videoclassLeavel, videoclassName;
			public String videoclassID;
			public String videoclassPid;
		}

		public static class VideoPathObject {
			public String videoUrl, videoStatus;
		}

		public class VideoLabelObject {
			public String videoLabelName, videoLabelIDvideoLabelID;
		}
	}

	public  static class OtherVideoObject {
		public String videoLength, videoProductTime, videodetails,videoImgUrl, videoname, videoID,
				suffix;
		public int videopnumber;
	}
}
