package com.ipmph.v.tool;

import java.io.File;

import com.ipmph.v.LoginActivity;
import com.ipmph.v.R;
import com.ipmph.v.object.LoginResultObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;

public class AndroidUtil {

	public static long getFreeSpaces(String strpath) {
		if (strpath == null) {
			// 网盘里默认的情况
			strpath = Environment.getExternalStorageDirectory().getPath();
		}

		File filePath = new File(strpath);
		filePath.mkdirs();
		if (!filePath.exists()) {
			return -1;
		}
		try {
			StatFs stat = new StatFs(strpath);
			long freesize = ((long) stat.getBlockSize() * (long) stat
					.getAvailableBlocks());
			if (freesize == 0 && stat.getBlockCount() == 0) {
				return -1;
			}
			return freesize;
		} catch (Exception e) {
			return -1;
		}
	}

	public static String getVersionName(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pinfo = null;
		try {
			pinfo = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String versionCode = pinfo.versionName;
		return versionCode;
	}

	public static void judgeIfLogined(Context context) {
		String sessionId = LoginResultObject.getInstance().sessionId;
		if (sessionId == null) {
			Toast.makeText(context,
					context.getString(R.string.operate_after_login),
					Toast.LENGTH_LONG).show();
			context.startActivity(new Intent(context, LoginActivity.class));
			return;
		}
	}
}
