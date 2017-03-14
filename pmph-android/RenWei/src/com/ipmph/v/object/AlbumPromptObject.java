package com.ipmph.v.object;

import java.util.List;

public class AlbumPromptObject {
	public String albumVideoDescribe, albumVideoID, videoImgUrl,
			albumVideoName, updateTime, suffix, seelistID;
	public List<VideoListObject> albumvideolist;
	public WatchRecord watchrecord;

	public class VideoListObject {
		public int videodowns, iscollection, videopnumber, videoups,
				videoIndex;
		public String videolenght, videoname, videoid;
		public List<VideoPathObject> videopathList;

	}

	public class VideoPathObject {
		public String videoUrl, videoStatus;
	}

	public class WatchRecord {
		public String videoname, videoid;
		public int videoIndex;
	}
}
