<<<<<<< HEAD
package com.zyitong.AppStore.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import android.annotation.SuppressLint;
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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.R;
import com.zyitong.AppStore.adapter.ListAdapter;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.bean.PageInfoData;
import com.zyitong.AppStore.downloadmanager.DownloadLink;
import com.zyitong.AppStore.http.HttpApiImple;
import com.zyitong.AppStore.loading.LoadingDialog;
import com.zyitong.AppStore.loading.WSError;
import com.zyitong.AppStore.tools.AppLogger;
import com.zyitong.AppStore.tools.UtilFun;
import com.zyitong.AppStore.ui.AutoListView;
import com.zyitong.AppStore.ui.AutoListView.OnLoadListener;
import com.zyitong.AppStore.ui.AutoListView.OnRefreshListener;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnRefreshListener,
		OnLoadListener {
	protected static final String[] String = null;
	private AutoListView listView;
	private ListAdapter adapter;
	private List<ItemData> itemList = new ArrayList();// 保存当前listview所显示的所有数据
	private int startpos = 0;
	private boolean isConnected = false;
	private int prevFlag = -1;
	private int Flag = 0;
	private int searchTime = 1000;// 查询间隔时间
	private long exitTime = 0;
	private Timer timer;
	private UtilFun util = null;
	private Message msg = null;
	private BroadcastReceiver connectionReceiver = null;
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.obj != null) {
				analyzeMessageInfo(msg);
			}
		}
	};

	private final TimerTask updatalistviewtask = new TimerTask() {
		public void run() {
			for (int i = 0; i < itemList.size(); i++) {
				String appName = util
						.getFileName(itemList.get(i).getFilename());
				int[] updateinfo = setMessageInfo(i, appName);
				if (updateinfo != null) {
					msg = handler.obtainMessage();
					msg.obj = updateinfo;
					msg.sendToTarget();
				}

			}
		}
	};

	private int[] setMessageInfo(int position, String appName) {
		int radio = AppStoreApplication.getInstance()
				.getCurrentDownloadJobManager().getRatio(appName);
		int[] messageInfo;
		messageInfo = new int[5];
		if (radio != -1) {
			int status = AppStoreApplication.getInstance()
					.getCurrentDownloadJobManager().getStatus(appName);

			switch (status) {
			case 0:
				messageInfo[0] = position;
				messageInfo[1] = -2;
				messageInfo[2] = status;
				break;
			case 3:
				messageInfo[0] = position;
				messageInfo[1] = radio;
				messageInfo[2] = status;
				break;
			case 4:
				messageInfo[0] = position;
				messageInfo[1] = radio;
				messageInfo[2] = status;
				AppStoreApplication.getInstance()
						.getCurrentDownloadJobManager()
						.removeDownloadJob(appName);
				break;
			default:
				messageInfo[0] = position;
				messageInfo[1] = radio;
				messageInfo[2] = status;
				break;
			}
		}
		return messageInfo;
	}

	private void analyzeMessageInfo(Message msg) {
		int updateinfo[] = (int[]) msg.obj;
		int position = updateinfo[0];
		int radio = updateinfo[1];
		int status = updateinfo[2];
		if (status == 4)
			Toast.makeText(MainActivity.this,
					itemList.get(position).getName() + "安装失败",
					Toast.LENGTH_SHORT).show();
		if (radio != -1)
			adapter.updateSingleRow(position, radio, status);
	}

	public static void launch(Context c, Bundle bundle) {
		Intent intent = new Intent(c, MainActivity.class);
		intent.putExtras(bundle);
		c.startActivity(intent);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		listenNetWork();
		setContentView(R.layout.main);
		init();
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
					Toast.makeText(MainActivity.this, "网络异常，请检查网络！",
							Toast.LENGTH_LONG).show();
					Log.e("MainActivity", "网络断开了");

				} else {
					AppStoreApplication.getInstance().isNetWorkConnected = true;
					if (itemList.size() == 0)
						new NewTask(MainActivity.this, R.string.connectioning,
								R.string.connectfail, AutoListView.LOAD)
								.execute();
					Log.e("MainActivity", "网络连接上了");

				}
			}
		};
		registerReceiver(connectionReceiver, intentFilter);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity onRestart");
		/*
		 * new NewTask(MainActivity.this, R.string.connectioning,
		 * R.string.connectfail,AutoListView.REFRESH).execute();
		 */
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity onResume");
		// 每次activity生命周期执行到onResume的时候初始化定时器

		super.onResume();

		getappList();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity onDestroy");
		if (connectionReceiver != null) {
			unregisterReceiver(connectionReceiver);
		}
		timer.cancel();
		updatalistviewtask.cancel();
		super.onDestroy();
	}

	public void dialogProcc() {
		setPage(0);

	}

	private void GetImei() {
		String imei = AppStoreApplication.getInstance().getImei();
		if (imei == null || imei.length() == 0) {
			TelephonyManager telephonyManager = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId();

			AppStoreApplication.getInstance().setImei(imei);
		}
	}

	private void init() {
		util = new UtilFun(this);
		timer = new Timer(true);
		timer.schedule(updatalistviewtask, 0, searchTime);
		GetImei();// 获取设备的deviceid
		clearProcc();
		getPageCache();
		listView = (AutoListView) findViewById(R.id.listView);
		InitList();
		CacheProcc();
	}

	private void clearProcc() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			int clear = bundle.getInt("CLEAR");
			if (clear == 100)
				AppStoreApplication.getInstance().getDownloadLink()
						.intrrentDown();
		}
	}

	private void CacheProcc() {
		if (itemList.size() > 0) {
			adapter.addData(itemList);
			adapter.notifyDataSetChanged();
		} else {
			Active();
			System.out.println("MainActivity itemList.size = ");
		}
	}

	private void InitList() {

		listView.setCacheColorHint(0);
		listView.setDivider(getResources().getDrawable(R.drawable.tiao));
		listView.setDividerHeight(1);

		listView.setVerticalScrollBarEnabled(true);
		// 改进
		adapter = new ListAdapter(this, itemList, listView, "MainActivity");
		listView.setAdapter(adapter);
		listView.setOnRefreshListener(this);
		listView.setOnLoadListener(this);
		listView.setItemsCanFocus(false);
	}

	private void getPageCache() {
		PageInfoData pageData = AppStoreApplication.getInstance()
				.getPamaterCache().getPage();
		if (pageData != null) {
			prevFlag = Flag = pageData.getFlag();
			itemList = pageData.getItemList();
		}
	}

	private void setPage(int itemID) {
		AppStoreApplication.getInstance().getPamaterCache().removePage();
		PageInfoData pageData = new PageInfoData();
		pageData.setFlag(Flag);
		pageData.setPageName("MainActivity");
		pageData.setItemList(itemList);
		pageData.setSelItem(itemID);
		AppStoreApplication.getInstance().getPamaterCache().addPage(pageData);
	}

	private void Active() {
		if (prevFlag != Flag || prevFlag == Flag && itemList.size() == 0) {

			adapter.clearData();
			itemList.clear();
			startpos = 0;
			Connect();
		}
		prevFlag = Flag;
	}

	private void disPlayList(List<ItemData> itemDataList, int loadcategory) {

		switch (loadcategory) {
		case AutoListView.REFRESH:
			listView.onRefreshComplete();
			adapter.clearData();
			System.out.println("refresh = " + itemList.size());
			itemList.addAll(itemDataList);
			adapter.addData(itemList);

			break;
		case AutoListView.LOAD:
			listView.onLoadComplete();
			// itemList.clear();
			itemList.addAll(itemDataList);
			adapter.addData(itemList);
			break;

		default:
			break;
		}
		// System.out.println("itemDataList.size() = "+itemList.size());
		listView.setResultSize(itemDataList.size());
		Log.v("MainActivity", "listview getitemcount = " + listView.getCount());
		Log.v("MainActivity",
				"listview getitemcount = " + listView.getChildCount());

		adapter.notifyDataSetChanged();
		isConnected = false;
	}

	private void loadData() {
		startpos = itemList.size();
		if (startpos > 0) {
			ItemData temData = itemList.get(startpos - 1);
			if (temData.getEnd() == 0 && !isConnected) {
				Connect();
			}
		}
	}

	private void loadDataByCategory(int category) {
		switch (category) {
		case AutoListView.REFRESH:
			new NewTask(MainActivity.this, R.string.connectioning,
					R.string.connectfail, AutoListView.REFRESH).execute();
			break;
		case AutoListView.LOAD:
			new NewTask(MainActivity.this, R.string.connectioning,
					R.string.connectfail, AutoListView.LOAD).execute();
			break;

		default:
			break;
		}

	}

	private void Connect() {
		isConnected = true;
		new NewTask(MainActivity.this, R.string.connectioning,
				R.string.connectfail, AutoListView.LOAD).execute();
	}

	private void getappList() {
		AppLogger.e("========== in getappList");
		String accesskey = "lflMmtFvvNt5YobP";
		String secret = "aYpz8CtTVSMWFnqaH1Q7uhki1NNQcL";

		System.out.println("===========");

	}

	private class NewTask extends LoadingDialog<Void, List<ItemData>> {
		int loadcategory;

		public NewTask(Activity activity, int loadingMsg, int failMsg,
				int loadcategory) {
			super(activity, loadingMsg, failMsg, loadcategory);
			this.loadcategory = loadcategory;
		}

		@Override
		public List<ItemData> doInBackground(Void... params) {
			List<ItemData> itemDataList = new ArrayList();
			DownloadLink download = AppStoreApplication.getInstance()
					.getDownloadLink();
			try {
				String url = "shop/shop.jsp?code=tj&UserHeader="
						+ AppStoreApplication.UserHeader + "&flag="
						+ (Flag + 1) + "&start=" + startpos + "&imei="
						+ AppStoreApplication.getInstance().getImei();
				HttpApiImple imple = new HttpApiImple();
				ItemData[] itemdata = imple.getListItem(url);
				switch (loadcategory) {
				case AutoListView.REFRESH:

					for (int i = 0; itemdata != null && i < itemdata.length; i++) {

						util.setFileState(itemdata[i]);
						itemDataList.add(itemdata[i]);
					}
					break;
				case AutoListView.LOAD:
					// getappList();

					Log.v("MainActivity", "getHttpApicount = "
							+ itemdata.length);
					for (int i = 0; itemdata != null && i < itemdata.length; i++) {

						util.setFileState(itemdata[i]);
						if (i == 1)
							itemdata[i]
									.setFilename("http://apk.r1.market.hiapk.com/data/upload/apkres/2015/1_22/21/com.tencent.mm_093944461.apk");
						else if (i == 2)
							itemdata[i]
									.setFilename("http://apk.r1.market.hiapk.com/data/upload/2015/01_16/23/com.tencent.mobileqq_233135.apk");
						else if (i == 3)
							itemdata[i]
									.setFilename("http://apk.r1.market.hiapk.com/data/upload/2015/01_22/18/com.qiyi.video_182142.apk");
						itemDataList.add(itemdata[i]);
					}
					break;

				default:
					break;
				}

			} catch (JSONException e) {
				String outText = e.getMessage();
				WSError error = new WSError();
				error.setMessage(outText);
				publishProgress(error);
			} catch (WSError e) {
				e.setMessage(getResources().getString(R.string.networkerr));
				publishProgress(e);
			}

			return itemDataList;
		}

		@Override
		public void doStuffWithResult(List<ItemData> itemList) {
			disPlayList(itemList, loadcategory);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onRefresh() {
		System.out.println("MainActivity onRefresh");
		loadDataByCategory(0);
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity  onLoad");
		loadDataByCategory(1);
	}

=======
package com.zyitong.AppStore.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;


import android.app.Activity;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.opensearch.javasdk.CloudsearchClient;
import com.opensearch.javasdk.CloudsearchSearch;
import com.opensearch.javasdk.object.KeyTypeEnum;
import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.R;
import com.zyitong.AppStore.adapter.ListAdapter;
import com.zyitong.AppStore.base.BaseActivity;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.bean.PageInfoData;
import com.zyitong.AppStore.http.HttpApiImple;
import com.zyitong.AppStore.http.async.LoadingDialog;
import com.zyitong.AppStore.http.async.WSError;
import com.zyitong.AppStore.tools.AppLogger;
import com.zyitong.AppStore.tools.UtilFun;
import com.zyitong.AppStore.ui.AutoListView;
import com.zyitong.AppStore.ui.AutoListView.OnLoadListener;
import com.zyitong.AppStore.ui.AutoListView.OnRefreshListener;
import com.zyitong.AppStore.base.BaseActivity.OnNetWorkConnectListener;
import com.zyitong.AppStore.base.BaseActivity.OnNetWorkDisConListener;

public class MainActivity extends BaseActivity implements OnRefreshListener,
		OnLoadListener ,OnNetWorkDisConListener,OnNetWorkConnectListener{
	protected static final String[] String = null;
	private AutoListView listView;
	private ListAdapter adapter;
	private List<ItemData> itemList = new ArrayList<ItemData>();// 保存当前listview所显示的所有数据
	private int startpos = 0;
	private int prevFlag = -1;
	private int Flag = 0;
	private int searchTime = 1000;// 查询间隔时间
	private long exitTime = 0;
	private Timer timer;
	private UtilFun util = null;
	private Message msg = null;
	
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.obj != null) {
				analyzeMsgInfo(msg);
			}
		}
	};

	private final TimerTask updatalistviewtask = new TimerTask() {
		public void run() {
			for (int i = 0; i < itemList.size(); i++) {
				String appName = util.getFileName(itemList.get(i).getFilename());
				int[] updateinfo = setMsgInfo(i, appName);
				if (updateinfo[3] != 0) {					
					msg = handler.obtainMessage();
					msg.obj = updateinfo;
					msg.sendToTarget();
				}

			}
		}
	};

	private int[] setMsgInfo(int position, String appName) {
		int radio = AppStoreApplication.getInstance()
				.getCurrentDownloadJobManager().getRatio(appName);
		int[] messageInfo;
		messageInfo = new int[4];
		messageInfo[3] = 0;
		if (radio != -1) {
			int status = AppStoreApplication.getInstance()
					.getCurrentDownloadJobManager().getStatus(appName);
			
			switch (status) {
			case 0:
				messageInfo[0] = position;
				messageInfo[1] = -2;
				messageInfo[2] = status;
				messageInfo[3] = 1;
				break;
			case 3:
				messageInfo[0] = position;
				messageInfo[1] = radio;
				messageInfo[2] = status;
				messageInfo[3] = 1;
				break;
			case 4:
				messageInfo[0] = position;
				messageInfo[1] = radio;
				messageInfo[2] = status;
				messageInfo[3] = 1;
				AppStoreApplication.getInstance()
						.getCurrentDownloadJobManager()
						.removeDownloadJob(appName);
				break;
			default:
				messageInfo[0] = position;
				messageInfo[1] = radio;
				messageInfo[2] = status;
				messageInfo[3] = 1;
				break;
			}
		}
		
		return messageInfo;
	}

	private void analyzeMsgInfo(Message msg) {
		int updateinfo[] = (int[]) msg.obj;
		int position = updateinfo[0];
		int radio = updateinfo[1];
		int status = updateinfo[2];
		if (status == 4)
			Toast.makeText(MainActivity.this,
					itemList.get(position).getName() + "安装失败",
					Toast.LENGTH_SHORT).show();
		if (radio != -1)
			adapter.updateSingleRow(position, radio, status);
	}

	public static void launch(Context c, Bundle bundle) {
		Intent intent = new Intent(c, MainActivity.class);
		intent.putExtras(bundle);
		c.startActivity(intent);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.main);
		Log.e("MainActivity","MainActivity onCreate");
		init();
	}

	

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//getappList();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("MainActivity","MainActivity onDestroy");	
		timer.cancel();
		updatalistviewtask.cancel();
		super.onDestroy();
	}

	public void dialogProcc() {
		setPage(0);

	}

	private void GetImei() {
		String imei = AppStoreApplication.getInstance().getImei();
		if (imei == null || imei.length() == 0) {
			TelephonyManager telephonyManager = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId();

			AppStoreApplication.getInstance().setImei(imei);
		}
	}

	private void init() {
		util = new UtilFun(this);
		timer = new Timer(true);
		timer.schedule(updatalistviewtask, 0, searchTime);
		GetImei();// 获取设备的deviceid
		clearProcc();
		getPageCache();
		listView = (AutoListView) findViewById(R.id.listView);
		InitList();
		setListViewListener();
		setNetWorkListener();
		CacheProcc();
	}

	private void clearProcc() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			int clear = bundle.getInt("CLEAR");
			if (clear == 100)
				AppStoreApplication.getInstance().getDownloadLink()
						.intrrentDown();
		}
	}

	private void CacheProcc() {
		if (itemList.size() > 0) {
			adapter.addData(itemList);
			adapter.notifyDataSetChanged();
		} else {
			Active();
			System.out.println("MainActivity itemList.size = ");
		}
	}

	private void InitList() {
		listView.setCacheColorHint(0);
		listView.setDivider(getResources().getDrawable(R.drawable.tiao));
		listView.setDividerHeight(3);
		listView.setVerticalScrollBarEnabled(true);
		adapter = new ListAdapter(this, itemList, listView, "MainActivity");
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(false);	
	}
	private void setListViewListener(){
		listView.setOnRefreshListener(this);
		listView.setOnLoadListener(this);
	}
	private void setNetWorkListener(){
		setOnNetWorkConListener(this);
		setOnNetWorkDisConListener(this);
	}

	private void getPageCache() {
		PageInfoData pageData = AppStoreApplication.getInstance()
				.getPamaterCache().getPage();
		if (pageData != null) {
			prevFlag = Flag = pageData.getFlag();
			itemList = pageData.getItemList();
		}
	}

	private void setPage(int itemID) {
		AppStoreApplication.getInstance().getPamaterCache().removePage();
		PageInfoData pageData = new PageInfoData();
		pageData.setFlag(Flag);
		pageData.setPageName("MainActivity");
		pageData.setItemList(itemList);
		pageData.setSelItem(itemID);
		AppStoreApplication.getInstance().getPamaterCache().addPage(pageData);
	}

	private void Active() {
		if (prevFlag != Flag || prevFlag == Flag && itemList.size() == 0) {

			adapter.clearData();
			itemList.clear();
			startpos = 0;
			Connect();
		}
		prevFlag = Flag;
	}

	private void disPlayList(List<ItemData> itemDataList, int loadcategory) {

		switch (loadcategory) {
		case AutoListView.REFRESH:
			listView.onRefreshComplete();
			adapter.clearData();
			itemList.addAll(itemDataList);
			adapter.addData(itemList);

			break;
		case AutoListView.LOAD:
			listView.onLoadComplete();
			// itemList.clear();
			itemList.addAll(itemDataList);
			adapter.addData(itemList);
			break;

		default:
			break;
		}
		// System.out.println("itemDataList.size() = "+itemList.size());
		listView.setResultSize(itemDataList.size());
		Log.v("MainActivity", "listview getitemcount = " + listView.getCount());
		adapter.notifyDataSetChanged();
	}

	private void loadDataByCategory(int category) {
		switch (category) {
		case AutoListView.REFRESH:
			new NewTask(MainActivity.this, R.string.connectioning,
					R.string.connectfail, AutoListView.REFRESH).execute();
			break;
		case AutoListView.LOAD:
			new NewTask(MainActivity.this, R.string.connectioning,
					R.string.connectfail, AutoListView.LOAD).execute();
			break;

		default:
			break;
		}
	}

	private void Connect() {
		new NewTask(MainActivity.this, R.string.connectioning,
				R.string.connectfail, AutoListView.LOAD).execute();
	}

	/*private void getappList() {
		AppLogger.e("========== in getappList");
		String accesskey = "lflMmtFvvNt5YobP";
		String secret = "aYpz8CtTVSMWFnqaH1Q7uhki1NNQcL";

		Map<String, Object> opts = new HashMap<String, Object>();
		// 这里的host需要根据访问应用基本详情中的API入口来确定
		opts.put("host", "http://opensearch-cn-hangzhou.aliyuncs.com");
		CloudsearchClient client = new CloudsearchClient(accesskey, secret,
				opts, KeyTypeEnum.OPENSEARCH);
		CloudsearchSearch search = new CloudsearchSearch(client);

		search.addIndex("AppStore");
		search.setQueryString("platform:'all'");

		search.setFormat("json");
		String test = "";
		try {

			AppLogger.e("========== before search");
			test = search.search();

			AppLogger.e("========== after search");
		} catch (Exception e) {
			AppLogger.e("========== search Exception");
			e.printStackTrace();
		}

		System.out.println("===========" + test);

	}*/

	private class NewTask extends LoadingDialog<Void, List<ItemData>> {
		int loadcategory;

		public NewTask(Activity activity, int loadingMsg, int failMsg,
				int loadcategory) {
			super(activity, loadingMsg, failMsg, loadcategory);
			this.loadcategory = loadcategory;
		}
		@Override
		public List<ItemData> doInBackground(Void... params) {
			List<ItemData> itemDataList = new ArrayList<ItemData>();
			
			try {
				String url = "shop/shop.jsp?code=tj&UserHeader="
						+ AppStoreApplication.UserHeader + "&flag="
						+ (Flag + 1) + "&start=" + startpos + "&imei="
						+ AppStoreApplication.getInstance().getImei();
				HttpApiImple imple = new HttpApiImple();
				ItemData[] itemdata = imple.getListItem(url);
				switch (loadcategory) {
				case AutoListView.REFRESH:

					for (int i = 0; itemdata != null && i < itemdata.length; i++) {

						util.setFileState(itemdata[i]);
						itemDataList.add(itemdata[i]);
					}
					break;
				case AutoListView.LOAD:
					// getappList();

					Log.v("MainActivity", "getHttpApicount = "
							+ itemdata.length);
					for (int i = 0; itemdata != null && i < itemdata.length; i++) {

						util.setFileState(itemdata[i]);
						if (i == 1)
							itemdata[i]
									.setFilename("http://apk.r1.market.hiapk.com/data/upload/apkres/2015/1_22/21/com.tencent.mm_093944461.apk");
						else if (i == 2)
							itemdata[i]
									.setFilename("http://apk.r1.market.hiapk.com/data/upload/2015/01_16/23/com.tencent.mobileqq_233135.apk");
						else if (i == 3)
							itemdata[i]
									.setFilename("http://apk.r1.market.hiapk.com/data/upload/2015/01_22/18/com.qiyi.video_182142.apk");
						itemDataList.add(itemdata[i]);
					}
					break;

				default:
					break;
				}

			} catch (JSONException e) {
				String outText = e.getMessage();
				WSError error = new WSError();
				error.setMessage(outText);
				publishProgress(error);
			} catch (WSError e) {
				e.setMessage(getResources().getString(R.string.networkerr));	
				publishProgress(e);		
			}

			return itemDataList;
		}

		@Override
		public void doStuffWithResult(List<ItemData> itemList) {
			disPlayList(itemList, loadcategory);
 		}
		@Override
		public void onLoadfail() {
			// TODO Auto-generated method stub
			listView.onLoadComplete();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				//finish();
				onDestroy();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity onRefresh");
		loadDataByCategory(0);
	}

	@Override
	public void onLoad() {	
		System.out.println("MainActivity  onLoad");
		loadDataByCategory(1);
	}

	@Override
	public void onNetWorkConnect() {
		// TODO Auto-generated method stub
        if(itemList.size()==0){   	
        	new NewTask(MainActivity.this, R.string.connectioning,
					R.string.connectfail, AutoListView.LOAD).execute();
		}
	}

	@Override
	public void onNetWorkDisConnect() {
		// TODO Auto-generated method stub
		Toast.makeText(MainActivity.this, R.string.connectfail, Toast.LENGTH_SHORT);
	}

>>>>>>> 3d44718803fd14689aa12749f3eecebb75c31fe6
}