package com.zyitong.AppStore.receiver;

import java.io.File;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zyitong.AppStore.service.UpdateMGService;

public class StartBroadcastReceiver extends BroadcastReceiver {

	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		File f=new File("/sdcard/testfile.txt");
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file=new File("/sdcard/test/");
		file.mkdirs();
		Log.e(this.getClass().getName(), "onReceive");
		Intent i = new Intent();
		i.setClass(context, UpdateMGService.class);
		context.startService(i);

	}

}
