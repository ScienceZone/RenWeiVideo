package com.ipmph.v.tool;

public class CommonUrl {
	public static final String mVideoPath_root = "http://mushi.chinacloudapp.cn:8080";
	public static final String mVideoPath_env = "http://mushi.chinacloudapp.cn:8080/test-enc.flv";
	public static final String mVideoPath = "http://mushi.chinacloudapp.cn:8080/test.flv";
	public static final String mVideoPath2 = "http://mushi.chinacloudapp.cn:8080/bofang?vid=f65a7d2861e543e0bc0fd1fc51536ce1";
//	public static final String baseUrl2 = "http://192.168.1.105:8280";
//	public static final String baseUrl = "http://mushi.chinacloudapp.cn";
	public static final String baseUrl = "http://v.ipmph.com/";
	public static final String getAllInterfaceUrl = baseUrl + "/argus";
	public static final String getfirstClassInterfaceUrl = baseUrl
			+ "/list/argus";
	public static String recommendVideoUrl = null;
	public static String newUpdateUrl = null;
	public static String firstClassUrl = null;
	public static String secondClassUrl = null;

	public static final String getClassList = baseUrl
			+ "/home/list/getClassList";
	public static final String getVideo = baseUrl + "/home/video/getVideo";
	public static final String getAlbumHotVideo = baseUrl
			+ "/home/list/getClassListOrderByPlayNum";
	public static final String getHotVideo = baseUrl
			+ "/home/list/getClassListOrderByPlayNum";
	public static final String getAlbumVideoList = baseUrl
			+ "/home/list/getAlbumVideoList";
	public static final String getAlbumVideo = baseUrl
			+ "/home/albumvideo/getAlbumVideo";
	public static final String search = baseUrl + "/home/video/search";
	// 分类筛选
	public static final String getList = baseUrl + "/home/list/getList";

	// private String loginUrl =
	// "http://mushi.chinacloudapp.cn/login?username=lijin&password=lijin";
	public static final String login = baseUrl+"/login";
	public static final String logout = "http://mushi.chinacloudapp.cn/logout";
	public static final String getWatchRecordList = baseUrl
			+ "/percen/watchrecord/getWatchRecordList";
	public static final String getUserInfo = baseUrl+ "/home/user/getUserInfo";
	public static final String addWatchRecord = baseUrl+ "/percen/watchrecord/addWatchRecord";
	public static final String updateWatchRecord = baseUrl+ "/percen/watchrecord/updateWatchRecord";
	public static final String updateWatchRecordAll = baseUrl+ "/percen/watchrecord/updateWatchRecordAll";
	public static final String addCollection = baseUrl+ "/home/collection/addCollection";
	public static final String collectionList = baseUrl+ "/home/collection/collectionList";
	public static final String updateCollectionDelFlag = baseUrl+ "/home/collection/updateCollectionDelFlag";
	public static final String getMessageList = baseUrl+ "/percen/message/getMessageList";
	public static final String updateMessage = baseUrl+ "/percen/message/updateMessage";
	public static final String addSeeList =baseUrl+"/percen/seelist/addSeeList";
	public static final String getSeeList = baseUrl+ "/percen/seelist/getSeeList";
	public static final String updateSeeListDelFlag = baseUrl+ "/percen/seelist/updateSeeListDelFlag";
	public static final String appagreement ="http://mushi.chinacloudapp.cn/appagreements.html";
	public static final String appagreement2 ="http://www.baidu.com";
	
	public static final String homeRegister =baseUrl+"/home/register/homeRegister";
	public static final String updatePassword =baseUrl+"/home/user/updatePassword";
	public static final String updateUserImg =baseUrl+"/home/user/updateUserImg";
	
	public static final String updateUserPhoto =baseUrl+"home/user/updateUserPhoto?jeesite.session.id=";
	
	public static final String getHomeUser =baseUrl+"/home/user/getHomeUser";
	public static final String detectNewVersion =baseUrl+"/app/new?fr=android&ver=1.0.2";
	// --------------------------------------------------------------------------------------

	// 获取图片轮播和最新推荐数据的接口
	public static String recommendVideoUrl2 = "http://mushi.chinacloudapp.cn/inte/content?id=fd12bbd5008f49fe84a69cea8f4f8758";
	public static String newUpdateUrl2 = "http://mushi.chinacloudapp.cn/inte/content?id=43ff171291844144bb579552be039d2a";
	public static String firstClassUrl2 = "http://mushi.chinacloudapp.cn/inte/content?id=24b9000f9bec4e72baad455dcd441bd6";
	public static String secondClassUrl2 = "http://mushi.chinacloudapp.cn/inte/content?id=5920206a181f49be9ac267a6b5beb79d";

}
