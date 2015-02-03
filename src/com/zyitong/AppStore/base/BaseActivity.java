package com.zyitong.AppStore.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.tools.AppLogger;

public class BaseActivity extends Activity {

	private BroadcastReceiver connectionReceiver = null;
	private OnNetWorkConnectListener onNetWorkConnectListener = null;
	private OnNetWorkDisConListener onNetWorkDisConListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppLogger.i("BaseActivity oncreate");
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
				} else {
					AppStoreApplication.getInstance().isNetWorkConnected = true;
					onNetWorkConnectListener.onNetWorkConnect();
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

	public void setOnNetWorkConListener(
			OnNetWorkConnectListener onNetWorkConnectListener) {
		this.onNetWorkConnectListener = onNetWorkConnectListener;
	}

	public void setOnNetWorkDisConListener(
			OnNetWorkDisConListener onNetWorkDisConListener) {
		this.onNetWorkDisConListener = onNetWorkDisConListener;
	}

	public interface OnNetWorkConnectListener {
		public void onNetWorkConnect();
	}

	public interface OnNetWorkDisConListener {
		public void onNetWorkDisConnect();
	}
}
