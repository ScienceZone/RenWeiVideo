package com.ipmph.v.sdcard;

import java.util.HashMap;
import java.util.Map;

import com.ipmph.v.R;
import com.ipmph.v.application.APPApplication;
import com.ipmph.v.tool.CommonUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SDCardMonitorReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 String action = intent.getAction();  
         
	        if (action.equals(Intent.ACTION_MEDIA_EJECT)) {  
	        	Log.d("gaolei", "ACTION_MEDIA_EJECT--------------");
	        	saveDownloadPath(context);
	            //TODO:  
	        } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
	        	
	        	Log.d("gaolei", "ACTION_MEDIA_MOUNTED--------------");
	        }  
	        
	}
	// 改变下载位置
		private void saveDownloadPath(Context context) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("oldPath", APPApplication.internalSDPath+SDCardSelectDialog.download_dir);
			map.put("rootPath", APPApplication.internalSDPath);
			CommonUtil.createSP(context, "SdcardPath", map);
			Toast.makeText(context, context.getString(R.string.sdcard_changed),
					Toast.LENGTH_SHORT).show();
		}
}
