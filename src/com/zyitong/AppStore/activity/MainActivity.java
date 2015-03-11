package com.zyitong.AppStore.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.R;
import com.zyitong.AppStore.adapter.ListAdapter;
import com.zyitong.AppStore.base.BaseActivity;
import com.zyitong.AppStore.base.BaseActivity.OnNetWorkConnectListener;
import com.zyitong.AppStore.base.BaseActivity.OnNetWorkDisConListener;
import com.zyitong.AppStore.bean.AppListBean;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.dao.AppListDao;
import com.zyitong.AppStore.tools.AppLogger;
import com.zyitong.AppStore.tools.UtilFun;
import com.zyitong.AppStore.ui.AutoListView;
import com.zyitong.AppStore.ui.AutoListView.OnLoadListener;
import com.zyitong.AppStore.ui.AutoListView.OnRefreshListener;

public class MainActivity extends BaseActivity implements OnRefreshListener,
		OnLoadListener, OnNetWorkDisConListener, OnNetWorkConnectListener {
	protected static final String[] String = null;
	private AutoListView listView;
	private ListAdapter adapter;
	private List<ItemData> itemList = new ArrayList<ItemData>();
	private int prevFlag = -1;
	private int Flag = 0;
	private int searchTime = 1000;
	private long exitTime = 0;
	private Timer timer;
	private UtilFun util = null;
	private Message msg = null;
	private String install_failed;
	private View emptyView;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				analyzeMsgInfo(msg);
			}
		}
	};
	private final TimerTask updatalistviewtask = new TimerTask() {
		public void run() {
			for (int i = 0; i < itemList.size(); i++) {
				String packagename = itemList.get(i).getAppInfoBean()
						.getPackagename();
				int[] updateinfo = setMsgInfo(i, packagename);
				if (0 != updateinfo[3]) {
					msg = handler.obtainMessage();
					msg.obj = updateinfo;
					msg.sendToTarget();
				}
			}
		}
	};

	private int[] setMsgInfo(int position, String packagename) {
		int[] messageInfo = new int[4];
		messageInfo[3] = 0;

		int radio = AppStoreApplication.getInstance()
				.getCurrentDownloadJobManager().getRatio(packagename);

		if (radio != -1) {
			int status = AppStoreApplication.getInstance()
					.getCurrentDownloadJobManager().getStatus(packagename);
			switch (status) {
		
			case ItemData.APP_OPEN:
				messageInfo[0] = position;
				messageInfo[1] = radio;
				messageInfo[2] = status;
				messageInfo[3] = 1;
				AppStoreApplication.getInstance()
				.getCurrentDownloadJobManager()
				.removeDownloadJob(packagename);
				break;
			case ItemData.APP_FAIL:
				messageInfo[0] = position;
				messageInfo[1] = radio;
				messageInfo[2] = status;
				messageInfo[3] = 1;
				AppStoreApplication.getInstance()
						.getCurrentDownloadJobManager()
						.removeDownloadJob(packagename);
				break;
			case ItemData.APP_REDOWNLOAD:
				messageInfo[0] = position;
				messageInfo[1] = radio;
				messageInfo[2] = status;
				messageInfo[3] = 1;
				
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
		if (status == ItemData.APP_FAIL) {
			Toast.makeText(
					MainActivity.this,
					itemList.get(position).getAppInfoBean().getTitle()
							+ install_failed, Toast.LENGTH_SHORT).show();
			String packagename = itemList.get(position).getAppInfoBean()
					.getPackagename();
			AppStoreApplication.getInstance().getCurrentDownloadJobManager()
					.removeDownloadJob(packagename);
		}
		if (radio != -1) {
			adapter.updateSingleRow(position, radio, status);
		}
	}

	public static void launch(Context c, Bundle bundle) {
		Intent intent = new Intent(c, MainActivity.class);
		intent.putExtras(bundle);
		c.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		AppLogger.e("MainActivity onCreate");
		init();
	}

	@Override
	protected void onStart() {
		System.out.println("MainActivity onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		System.out.println("MainActivity onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		System.out.println("MainActivity onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		System.out.println("MainActivity onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.e("MainActivity", "MainActivity onDestroy");
		timer.cancel();
		updatalistviewtask.cancel();
		super.onDestroy();
	}

	private void init() {
		util = new UtilFun(this);
		timer = new Timer(true);
		timer.schedule(updatalistviewtask, 0, searchTime);
		install_failed = getResources().getString(R.string.app_install_failed);
		listView = (AutoListView) findViewById(R.id.listView);
		emptyView = (View) findViewById(R.id.empty_view);
		emptyView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getAppList(0, 8);
			}
		});
		
		InitList();
		CacheProcc();
		
		setListViewListener();
		setNetWorkListener();
	}

	private void CacheProcc() {
		if (itemList.size() > 0) {
			adapter.addData(itemList);
			adapter.notifyDataSetChanged();
		} else {
			Active();
		}
	}

	private void InitList() {
		listView.setCacheColorHint(0);
		listView.setDivider(getResources().getDrawable(R.drawable.tiao));
		listView.setDividerHeight(3);
		listView.setVerticalScrollBarEnabled(true);
		adapter = new ListAdapter(this, itemList, "MainActivity");
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(false);
		listView.setEmptyView(emptyView);
		
	}

	private void setListViewListener() {
		listView.setOnRefreshListener(this);
		listView.setOnLoadListener(this);
	}

	private void setNetWorkListener() {
		setOnNetWorkConListener(this);
		setOnNetWorkDisConListener(this);
	}

	private void Active() {
		if (prevFlag != Flag || prevFlag == Flag && itemList.size() == 0) {

			adapter.clearData();
			itemList.clear();
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
			listView.setResultSize(itemDataList.size());
			adapter.notifyDataSetChanged();
			break;
		case AutoListView.LOAD:
			listView.onLoadComplete();
			itemList.addAll(itemDataList);
			adapter.addData(itemList);
			listView.setResultSize(itemDataList.size());
			adapter.notifyDataSetChanged();
			break;
		case AutoListView.ERROR:
			listView.onLoadComplete();
			listView.setResultSize(8);
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}

	private void loadDataByCategory(int category) {
		switch (category) {
		case AutoListView.REFRESH:
			break;
		case AutoListView.LOAD:
			if (itemList.size() == 0) {
				getAppList(0, 8);
			} else {
				int timer = itemList.size() / 8;
				getAppList((timer) * 8, 8);
			}

			break;
		default:
			break;
		}
	}

	private void Connect() {
		AppStoreApplication.getInstance().isfirstconnect = false;
		if (AppStoreApplication.getInstance().itemData.size() != 0) {
			List<ItemData> updatelist = new ArrayList<ItemData>();
			updatelist.addAll(AppStoreApplication.getInstance().itemData);
			disPlayList(updatelist, AutoListView.LOAD);
			System.out.println("不为空");
			AppStoreApplication.getInstance().itemData.clear();
		}
	}

	private void getAppList(int startPos, int docNum) {

		AppListDao.getInstance().getAppListRX(startPos, docNum)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<AppListBean>() {
					@Override
					public void onNext(AppListBean bean) {
						if (null != bean) {
							List<ItemData> itemDataList = new ArrayList<ItemData>();
							int resultnumber = Integer.valueOf(bean.result.num);
							for (int i = 0; i < resultnumber; i++) {
								ItemData item = new ItemData();
								item.setAppInfoBean(bean.result.items.get(i));
								AppLogger.e("== getapplist from server:"
										+ bean.result.items.get(i).getTitle());
								AppLogger.e("== getapplist from server:"
										+ bean.result.items.get(i)
												.getPackagename());
								itemDataList.add(item);
								util.setAppState(item);
							}
							disPlayList(itemDataList, AutoListView.LOAD);
						}
					}

					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						if (!AppStoreApplication.getInstance().isNetWorkConnected) {
							Toast.makeText(MainActivity.this,
									R.string.networkerr, Toast.LENGTH_LONG)
									.show();
						} else
							Toast.makeText(MainActivity.this,
									R.string.loaderror, Toast.LENGTH_LONG)
									.show();
						listView.onLoadComplete();
						listView.setResultSize(0);
						AppLogger.e("ERROR===========" + e);
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(),
						R.string.press_again_exit, Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onRefresh() {
		AppLogger.e("==用户下拉了listview！！！！！！！！！！");
	}

	@Override
	public void onLoad() {
		loadDataByCategory(AutoListView.LOAD);
	}

	@Override
	public void onNetWorkConnect() {
		if(!AppStoreApplication.getInstance().isfirstconnect){
			Toast.makeText(this, R.string.connection, Toast.LENGTH_SHORT);
		}
	}

	@Override
	public void onNetWorkDisConnect() {
		Toast.makeText(MainActivity.this, R.string.connectfail,
				Toast.LENGTH_SHORT).show();

	}
}