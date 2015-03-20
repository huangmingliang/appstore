package com.zyitong.AppStore.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import android.app.ProgressDialog;
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
import android.widget.EditText;
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
import com.zyitong.AppStore.ui.AutoListView.OnSearchListener;
import com.zyitong.AppStore.ui.SearchFrame;
import com.zyitong.AppStore.ui.SearchFrame.OnCancelButtonListener;
import com.zyitong.AppStore.ui.SearchFrame.OnEtSearchClickListener;
import com.zyitong.AppStore.ui.SearchFrame.OnEtTextChangedListener;

public class MainActivity extends BaseActivity implements OnSearchListener,
		OnLoadListener, OnNetWorkDisConListener, OnNetWorkConnectListener {

	private AutoListView listView;
	private ListAdapter adapter;
	private List<ItemData> itemList = new ArrayList<ItemData>();
	//private List<ItemData> searchBeforeList = new ArrayList<ItemData>();
	private int searchTime = 1000;
	private final int TRIGGER_SERACH = 1;
	private final long SEARCH_TRIGGER_DELAY_IN_MS = 1000;
	private Timer timer;
	private UtilFun util = null;
	private Message msg = null;
	private String install_failed;
	private View emptyView;
	private SearchFrame searchFrame;
	private String searchString = new String();
	private static int operate = 0;
	private boolean emViewIsOnClick = false;
	ProgressDialog progressDialog = null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				analyzeMsgInfo(msg);
			}
		}
	};
	
	private Handler edChangedHander = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			 if (msg.what == TRIGGER_SERACH) {
				 AppLogger.e("edittext changed query ==" + searchString);		
					
					if (null != adapter) {
						adapter.clearData();
					}
					progressDialog = ProgressDialog.show(MainActivity.this, "等待加载", "数据加载中...");
					SearchAppList(searchString, 0, AutoListView.pageSize, true);
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
			String packagename;
			packagename = itemList.get(position).getAppInfoBean()
					.getPackagename();
			Toast.makeText(
					MainActivity.this,
					itemList.get(position).getAppInfoBean().getTitle()
							+ install_failed, Toast.LENGTH_SHORT).show();
			AppStoreApplication.getInstance().getCurrentDownloadJobManager()
					.removeDownloadJob(packagename);
		}
		if (radio != -1) {
			adapter.updateSingleRow(position, radio, status);
		}
	}

	public static void startActivity(Context c, Bundle bundle) {
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

		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {

		for (int i = 0; i < itemList.size(); i++) {
			util.setResumeAppState(itemList.get(i));
		}
		adapter.addData(itemList);
		adapter.notifyDataSetChanged();

		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
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
				if (!emViewIsOnClick) {
					emViewIsOnClick = true;
					getAppList(0, 8, false);
				}
			}
		});

		searchFrame = (SearchFrame) findViewById(R.id.main_searchframe);
		final EditText editText = (EditText) searchFrame
				.findViewById(R.id.edit_search);
		searchFrame.setEtSearchListener(new OnEtSearchClickListener() {
			@Override
			public void onClick() {

			}
		});
	
		searchFrame.setEtTextChangedListener(new OnEtTextChangedListener() {

			@Override
			public void chanaged() {
				String query = editText.getText().toString().trim();
				searchString = query;
				operate = 1;
				edChangedHander.removeMessages(TRIGGER_SERACH);
				edChangedHander.sendEmptyMessageDelayed(TRIGGER_SERACH, SEARCH_TRIGGER_DELAY_IN_MS);
			}
		});
		searchFrame.setCancelButtonListener(new OnCancelButtonListener() {		
			@Override
			public void onClick() {
				operate = 0;
				listView.onRefreshComplete();
				searchFrame.setVisibility(View.GONE);	
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
		adapter.clearData();
		itemList.clear();
		if (AppStoreApplication.getInstance().itemData.size() != 0) {
			List<ItemData> updatelist = new ArrayList<ItemData>();
			updatelist.addAll(AppStoreApplication.getInstance().itemData);
			displayList(updatelist);
			AppStoreApplication.getInstance().itemData.clear();
		} else {
			emptyView.setVisibility(View.VISIBLE);

		}
	}

	private void displayList(List<ItemData> itemDataList) {
		listView.onLoadComplete();
		itemList.addAll(itemDataList);
		adapter.addData(itemList);
		listView.setResultSize(itemDataList.size());
		AppLogger.e("disPlayGetList itemList = " + itemList.size());
		adapter.notifyDataSetChanged();
		return;
	}

	private void loadGetDataByCategory(int category, boolean dlCencel) {
		switch (category) {
		case AutoListView.LOAD:
			progressDialog = ProgressDialog.show(MainActivity.this, "等待加载", "数据加载中...");
			if (itemList.size() == 0) {
				getAppList(0, AutoListView.pageSize, dlCencel);
			} else {
				int timer = itemList.size() / AutoListView.pageSize;
				getAppList((timer) * AutoListView.pageSize,
						AutoListView.pageSize, dlCencel);
			}
			break;
		default:
			break;
		}
	}

	private void loadSearchDataByCategory(int category, boolean dialogcancel) {
		switch (category) {
		case AutoListView.LOAD:
			progressDialog = ProgressDialog.show(MainActivity.this, "等待加载", "数据加载中...");
			if (itemList.size() == 0) {
				// lock();
				SearchAppList(searchString, 0, AutoListView.pageSize,
						dialogcancel);
			} else if (itemList.size() < AutoListView.pageSize) {
				break;
			} else if (itemList.size() >= AutoListView.pageSize) {
				int timer = itemList.size() / AutoListView.pageSize;
				SearchAppList(searchString, (timer) * AutoListView.pageSize,
						AutoListView.pageSize, dialogcancel);
			}
			break;
		default:
			break;
		}
	}

	private void getAppList(int startPos, int docNum, final boolean dialogcancel) {
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
							displayList(itemDataList);
							emptyView.setVisibility(View.GONE);
							if (dialogcancel) {
								progressDialog.cancel();
							}
						}
					}

					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						if (!AppStoreApplication.getInstance().isNetWorkConnected) {
							Toast.makeText(MainActivity.this,
									R.string.networkerr, 1000).show();
						} else
							Toast.makeText(MainActivity.this,
									R.string.loaderror, Toast.LENGTH_LONG)
									.show();
						listView.onLoadComplete();
						listView.setResultSize(AutoListView.pageSize + 1);
						//stopProgressDialog();
						if (dialogcancel) {
							progressDialog.cancel();
						}
						emViewIsOnClick = false;
						AppLogger.e("ERROR===========" + e);
					}
				});
	}

	private void SearchAppList(String query, int startPos, int docNum,
			final boolean dialogcancel) {
		AppLogger.e("SearchAppList  query = "+query+"  startPos = "+startPos +"docNum = "+docNum);

		AppListDao.getInstance().searchAppListRX(query, startPos, docNum)
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
							displayList(itemDataList);
							if (dialogcancel) {
								progressDialog.cancel();
							}
						}
					}
					@Override
					public void onCompleted() {
						
					}
					@Override
					public void onError(Throwable e) {
						if (!AppStoreApplication.getInstance().isNetWorkConnected) {
							Toast.makeText(MainActivity.this,
									R.string.networkerr, 1000).show();
						} else
							Toast.makeText(MainActivity.this,
									R.string.loaderror, Toast.LENGTH_LONG)
									.show();
						listView.onLoadComplete();
						listView.setResultSize(AutoListView.pageSize + 1);
						//stopProgressDialog();
						if (dialogcancel) {
							progressDialog.cancel();
						}
						emViewIsOnClick = false;
						AppLogger.e("ERROR===========" + e);
					}
				});
	}

	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			AppLogger.e("listView.getState = " + listView.getState());
			if (listView.getState() != AutoListView.REFRESHING) {
				
				 * if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() ==
				 * KeyEvent.ACTION_DOWN) { if ((System.currentTimeMillis() -
				 * exitTime) > 2000) { Toast.makeText(getApplicationContext(),
				 * R.string.press_again_exit, Toast.LENGTH_SHORT).show();
				 * exitTime = System.currentTimeMillis(); } else { finish(); }
				 * return true; }
				 
				finish();
			} else {
				operate = 0;
				if (null != adapter) {
					adapter.clearData();
				}
				listView.onRefreshComplete();
				searchFrame.setVisibility(View.GONE);
				loadGetDataByCategory(AutoListView.LOAD, false);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}*/

	@Override
	public void OnSearch() {
		
		searchFrame.setVisibility(View.VISIBLE);
		
	}

	@Override
	public void onLoad() {
		if (operate == 0) {
			loadGetDataByCategory(AutoListView.LOAD, true);
		} else {
			loadSearchDataByCategory(AutoListView.LOAD, true);
		}
	}

	@Override
	public void onNetWorkConnect() {
		/*
		 * if(operate == 0){ loadGetDataByCategory(AutoListView.LOAD,false);
		 * }else{ loadSearchDataByCategory(AutoListView.LOAD,false); }
		 */
	}

	@Override
	public void onNetWorkDisConnect() {
		Toast.makeText(MainActivity.this, R.string.connectfail,
				Toast.LENGTH_SHORT).show();

	}

	
}