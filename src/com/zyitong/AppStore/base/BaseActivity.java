package com.zyitong.AppStore.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.tools.AppLogger;

public class BaseActivity extends Activity {

	private BroadcastReceiver connectionReceiver = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		AppLogger.e("BaseActivity oncreate");
		listenNetWork();
	}
	
	Handler netWorkConnectHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 2){
				AppStoreApplication.getInstance().isNetWorkConnected = true;
				NetWorkConnect();
				AppLogger.e("wifiNetInfo isConnected");
			}	
		}
	};

	private void listenNetWork() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		connectionReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo mobNetInfo = connectMgr
						.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				NetworkInfo wifiNetInfo = connectMgr
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

				if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
					AppStoreApplication.getInstance().isNetWorkConnected = false;
					NetWorkDisConnect();
				} else if( wifiNetInfo.isConnected()) {
					netWorkConnectHandler.removeMessages(2);
					netWorkConnectHandler.sendEmptyMessageDelayed(2, 500);
				} else if(mobNetInfo.isConnected()){
					AppStoreApplication.getInstance().isNetWorkConnected = true;
					netWorkConnectHandler.removeMessages(2);
					netWorkConnectHandler.sendEmptyMessageDelayed(2, 500);
				}
			}
		};
		registerReceiver(connectionReceiver, intentFilter);
	}

	@Override
	protected void onDestroy() {
		if (connectionReceiver != null) {
			unregisterReceiver(connectionReceiver);
		}
		AppLogger.i("BaseActivity onDestroy");
		super.onDestroy();
	}

	public void NetWorkDisConnect(){
		AppLogger.e("BaseActivity NetWorkDisConnect");
	};
	public void NetWorkConnect(){
		AppLogger.e("BaseActivity NetWorkDisConnect");
	};
}
