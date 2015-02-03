package com.zyitong.AppStore.base;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.tools.AppLogger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;


public class BaseActivity extends Activity {

	private BroadcastReceiver connectionReceiver = null;
	private OnNetWorkConnectListener onNetWorkConnectListener = null;
	private OnNetWorkDisConListener onNetWorkDisConListener = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_base);
		Log.e("BaseActivity","BaseActivity oncreate");
		listenNetWork();
	}

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
					onNetWorkDisConListener.onNetWorkDisConnect();
					Log.e("BaseActivity", "网络断开了");

				} else{
					AppStoreApplication.getInstance().isNetWorkConnected = true;
					onNetWorkConnectListener.onNetWorkConnect();
					Log.e("BaseActivity", "网络连接上了");
				}
			}
		};
		registerReceiver(connectionReceiver, intentFilter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (connectionReceiver != null) {
			unregisterReceiver(connectionReceiver);
		}
		Log.e("BaseActivity","BaseActivity onDestroy");
		super.onDestroy();
	}
	public void setOnNetWorkConListener(OnNetWorkConnectListener onNetWorkConnectListener){
		this.onNetWorkConnectListener = onNetWorkConnectListener;
	}
	public void setOnNetWorkDisConListener(OnNetWorkDisConListener onNetWorkDisConListener){
		this.onNetWorkDisConListener = onNetWorkDisConListener;
	}
	public interface OnNetWorkConnectListener{
		public void onNetWorkConnect();
	}
	public interface OnNetWorkDisConListener{
		public void onNetWorkDisConnect();
	}
	
}
