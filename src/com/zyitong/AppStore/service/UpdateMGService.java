package com.zyitong.AppStore.service;

import com.zyitong.AppStore.util.UtilFun;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import android.util.Log;

public class UpdateMGService extends Service{

	private UtilFun utilFun;
	private static String WeiBoRoot = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/AppStore/";
	private NotificationManager notificationManager;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		//PackageManager pm = getPackageManager();
		utilFun = new UtilFun(this);
		utilFun.excuateShellchmod();
		
	
    	String result = utilFun.install(WeiBoRoot+"soft/VEBGuard.apk");
    	Log.e("UpdateMGService", result);
    	Log.e("UpdateMGService", "cilentinstall excuate");
    	notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
       
    	super.onCreate();
	}
	

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		
		utilFun.excuateShellchmod();
    	String result = utilFun.install(WeiBoRoot+"soft/VEBGuard.apk");
    	Log.e("UpdateMGService", result);
    	Log.e("UpdateMGService", "cilentinstall excuate");
    	super.onStart(intent, startId);
	}



	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
