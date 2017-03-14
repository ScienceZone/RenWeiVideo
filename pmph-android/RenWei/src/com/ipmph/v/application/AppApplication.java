package com.ipmph.v.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.ipmph.v.multidownload.entitis.FileInfo;
import com.ipmph.v.multidownload.notification.NotificationUtil;
import com.ipmph.v.sdcard.SDCardSelectDialog;
import com.ipmph.v.sdcard.SDCardUtil;
import com.ipmph.v.sdcard.SDCardUtil.SDCardStat;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class APPApplication extends Application {
	
	public static int screenWidth,screenHeight;
	public static float density;
	private static APPApplication mAppApplication;
	public static List<FileInfo> mFileList= new ArrayList<FileInfo>();
	public static String downloadSdcardPath="",downloadApkPath="",cacheImgPath;
	public static String internalSDPath="";
	public ArrayList<SDCardStat> sdCardList;
	public static NotificationUtil mNotificationUtil = null;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mNotificationUtil = new NotificationUtil(this);
		initData();
		getScreenDimension();
		initImageLoader(this);
	}
	
	private void initData(){
		sdCardList=SDCardUtil.getSDCardStats(this);
		SharedPreferences sp = getSharedPreferences("SdcardPath", 0);
		downloadSdcardPath = sp.getString("oldPath", "");
		downloadApkPath=sdCardList.get(0).rootPath+SDCardSelectDialog.download_apk_dir;
		if (downloadSdcardPath.length()== 0) {
			internalSDPath=sdCardList.get(0).rootPath;
			downloadSdcardPath = internalSDPath+SDCardSelectDialog.download_dir;
			
		} 
		File file =new File(downloadSdcardPath); 
		if(!file .exists()  && !file .isDirectory()){
			 file .mkdirs();
		}
		File file2 =new File(downloadApkPath); 
		if(!file2.exists()&&!file2.isDirectory()){
			 file2.mkdirs();
		}
		
		Log.d("gaolei", "AppApplication--------sdcard---------"+SDCardUtil.getSDCardPath());
		Log.d("gaolei", "AppApplication--------internalPath---------"+internalSDPath);
	}
	
	/** 获取Application */
	public static APPApplication getApp() {
		return mAppApplication;
	}
	/** 初始化ImageLoader */
	public static void initImageLoader(Context context) {
		cacheImgPath=internalSDPath+"/RenWei/uil-img";
		File cacheDir =new File(cacheImgPath);//获取到缓存的目录地址
		if(!cacheDir.exists())
			cacheDir.mkdirs();
		Log.d("cacheDir", cacheDir.getPath());
		//创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(context)
				//.memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
				//.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)设置缓存的详细信息，最好不要设置这个
				.threadPoolSize(3)//线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				//.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation你可以通过自己的内存缓存实现
				//.memoryCacheSize(2 * 1024 * 1024)  
				///.discCacheSize(50 * 1024 * 1024)  
				.discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
				//.discCacheFileNameGenerator(new HashCodeFileNameGenerator())//将保存的时候的URI名称用HASHCODE加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				//.discCacheFileCount(100) //缓存的File数量
				.discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
				//.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				//.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);//全局初始化此配置
	}
	public void getScreenDimension(){
		WindowManager mWM=((WindowManager) getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		mWM.getDefaultDisplay().getMetrics(mDisplayMetrics);
		screenWidth = mDisplayMetrics.widthPixels;
		screenHeight = mDisplayMetrics.heightPixels;
		density = mDisplayMetrics.density;
	}
}
