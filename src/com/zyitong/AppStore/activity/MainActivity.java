package com.zyitong.AppStore.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.R;
import com.zyitong.AppStore.adapter.ListAdapter;
import com.zyitong.AppStore.bean.AppListBean;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.dao.AppListDao;
import com.zyitong.AppStore.tools.AppLogger;
import com.zyitong.AppStore.tools.UtilFun;
import com.zyitong.AppStore.ui.AutoListView;
import com.zyitong.AppStore.ui.SearchFrame;
import com.zyitong.AppStore.ui.AutoListView.OnLoadListener;
import com.zyitong.AppStore.ui.AutoListView.OnSearchListener;
import com.zyitong.AppStore.ui.SearchFrame.OnEtTextChangedListener;

public class MainActivity extends BaseActivity implements OnSearchListener,
		OnLoadListener {

	private AutoListView listView;
	private ListAdapter adapter;
	private List<ItemData> itemList = new ArrayList<ItemData>();
	private final int searchTime = 1000;
	private final int TRIGGER_SERACH = 3000;
	private final int TRIGGER_GET = 2000;
	private final int TRIGGER_UPDATE = 4000;
	private final long SEARCH_TRIGGER_DELAY_IN_MS = 200;
	private Timer timer;
	private Message msg = null;
	private String install_failed;//安装失败
	private FrameLayout emptyView;
	private ImageButton imageButton;
	private SearchFrame searchFrame;
	private EditText editText;
	private String searchString = new String();
	private int operate = 0;
	private boolean emViewIsOnClick = false;
	private boolean isSearch = true;
	private boolean firstStart = false;
	ProgressDialog progressDialog = null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TRIGGER_UPDATE) {
				handleButtonUpdate();

			} else if (msg.what == TRIGGER_SERACH) {
				if (isSearch) {
					isSearch = false;
					if (null != adapter) {
						adapter.clearData();
						adapter.notifyDataSetChanged();
					}

					showProgressDialog();
					SearchAppList(searchString, 0, AutoListView.pageSize, true);
				}
			} else if (msg.what == TRIGGER_GET) {
				if (isSearch) {
					isSearch = false;

					if (null != adapter) {
						adapter.clearData();
						adapter.notifyDataSetChanged();
					}
					getAppList(0, AutoListView.pageSize, true);
				}
			}
		}
	};

	private void handleButtonUpdate() {

		for (int i = 0; i < itemList.size(); i++) {

			String packagename = itemList.get(i).getAppInfoBean()
					.getPackagename();

			int[] updateinfo = setMsgInfo(i, packagename);
			if(updateinfo[3] == 0){
				continue;
			}
			int position = updateinfo[0];
			int radio = updateinfo[1];
			int status = updateinfo[2];
		
			if (status == ItemData.APP_FAIL) {
				
				AppStoreApplication.getInstance()
						.getCurrentDownloadJobManager()
						.removeDownloadJob(packagename);
			}
			if (radio != -1) {
				adapter.updateSingleRow(position, radio, status);
			}
		}

		return;
	}

	private final TimerTask updatalistviewtask = new TimerTask() {
		public void run() {
			msg = handler.obtainMessage();
			msg.what = TRIGGER_UPDATE;
			msg.sendToTarget();

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

			case ItemData.APP_OPEN://打开app
				messageInfo[0] = position;
				messageInfo[1] = radio;
				messageInfo[2] = status;
				messageInfo[3] = 1;
				AppStoreApplication.getInstance()
						.getCurrentDownloadJobManager()
						.removeDownloadJob(packagename);
				break;
			case ItemData.APP_FAIL://安装app失败
				messageInfo[0] = position;
				messageInfo[1] = radio;
				messageInfo[2] = status;
				messageInfo[3] = 1;
				Toast.makeText(
						MainActivity.this,
						itemList.get(position).getAppInfoBean().getTitle()
								+ install_failed, Toast.LENGTH_SHORT).show();
				AppStoreApplication.getInstance()
						.getCurrentDownloadJobManager()
						.removeDownloadJob(packagename);
				int appGrade1 = AppStoreApplication.getInstance().getAppGrade(packagename);
				if(itemList.get(position).getAppInfoBean().getVersion_num() > appGrade1){
					if(appGrade1 != -1)
					   messageInfo[2] = ItemData.APP_UPDATE;
				}
				break;
			case ItemData.APP_INSTALL://安装app
				messageInfo[0] = position;
				messageInfo[1] = radio;
				messageInfo[2] = status;
				messageInfo[3] = 1;
				int appGrade2 = AppStoreApplication.getInstance().getAppGrade(packagename);
				if(itemList.get(position).getAppInfoBean().getVersion_num() > appGrade2){
					if(appGrade2 != -1)
					   messageInfo[2] = ItemData.APP_UPDATE;
				}
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

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		firstStart = true;
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
		if(!firstStart){
			AppStoreApplication.getInstance().setResumeAppState(itemList);
			adapter.addData(itemList);
			adapter.notifyDataSetChanged();
		}
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		firstStart = false;
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		timer.cancel();
		updatalistviewtask.cancel();
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	private void init() {
		new UtilFun();
		timer = new Timer(true);
		timer.schedule(updatalistviewtask, 0, searchTime);
		install_failed = getResources().getString(R.string.app_install_failed);
		listView = (AutoListView) findViewById(R.id.listView);
		emptyView = (FrameLayout) findViewById(R.id.empty_view);
		imageButton = (ImageButton) emptyView.findViewById(R.id.empty_view1);
		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!emViewIsOnClick) {
					emViewIsOnClick = true;
					getAppList(0, 8, false);
				}
			}
		});

		searchFrame = (SearchFrame) findViewById(R.id.main_searchframe);

		editText = (EditText) searchFrame.findViewById(R.id.edit_search);

		searchFrame.setEtTextChangedListener(new OnEtTextChangedListener() {
			@Override
			public void chanaged() {
				AppLogger.e("SearchFrame afterTextChanged");
				
				if (null != editText && null != editText.getText()) {
					if(!searchString.equals(editText.getText().toString().trim())){
						searchString = editText.getText().toString().trim();
						searchString = searchString.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
					}else{
						return;
					}
					
				}

				operate = 1;
				if (searchString.equals("")) {
					operate = 0;
					if (null != adapter) {
						adapter.clearData();
						adapter.notifyDataSetChanged();
					}

					handler.removeMessages(TRIGGER_SERACH);
					handler.removeMessages(TRIGGER_GET);
					handler.sendEmptyMessageDelayed(TRIGGER_GET, 1500);
				} else {
					if (null != adapter) {
						adapter.clearData();
						adapter.notifyDataSetChanged();
					}
					handler.removeMessages(TRIGGER_SERACH);
					handler.removeMessages(TRIGGER_GET);
					handler.sendEmptyMessageDelayed(TRIGGER_SERACH,
							SEARCH_TRIGGER_DELAY_IN_MS);
				}

			}
		});
		
		InitList();
		CacheProcc();
		setListViewListener();
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
		adapter.notifyDataSetChanged();
		return;
	}

	private void loadGetDataByCategory(int category, boolean dlCencel) {
		switch (category) {
		case AutoListView.LOAD:
			if (itemList.size() == 0) {
				if (isSearch) {
					isSearch = false;
					getAppList(0, AutoListView.pageSize, dlCencel);
				}
			} else {
				if (isSearch) {
					isSearch = false;
					int timer = itemList.size() / AutoListView.pageSize;
					getAppList((timer) * AutoListView.pageSize,
							AutoListView.pageSize, dlCencel);
				}
			}
			break;
		default:
			break;
		}
	}

	private void loadSearchDataByCategory(int category, boolean dialogcancel) {
		switch (category) {
		case AutoListView.LOAD:
			if (itemList.size() == 0) {
				if (isSearch) {
					isSearch = false;
					SearchAppList(searchString, 0, AutoListView.pageSize,
							dialogcancel);
				}
			} else if (itemList.size() < AutoListView.pageSize) {
				break;
			} else if (itemList.size() >= AutoListView.pageSize) {
				int timer = itemList.size() / AutoListView.pageSize;
				if (isSearch) {
					isSearch = false;
					SearchAppList(searchString,
							(timer) * AutoListView.pageSize,
							AutoListView.pageSize, dialogcancel);
				}
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
					AppListBean appListBean = null;
					@Override
					public void onNext(AppListBean bean) {
						appListBean = bean;
					}

					@Override
					public void onCompleted() {
						if (null != appListBean) {
							List<ItemData> itemDataList = new ArrayList<ItemData>();
							int resultnumber = Integer
									.valueOf(appListBean.result.num);
							for (int i = 0; i < resultnumber; i++) {
								ItemData item = new ItemData();
								item.setAppInfoBean(appListBean.result.items
										.get(i));
								itemDataList.add(item);
								
							}
							AppStoreApplication.getInstance().setAppState(itemDataList);
							displayList(itemDataList);
							emptyView.setVisibility(View.GONE);
							if (dialogcancel) {
								isSearch = true;
							}
						}
					}

					@Override
					public void onError(Throwable e) {
						if (dialogcancel) {
							if (!AppStoreApplication.getInstance().isNetWorkConnected) {
								Toast.makeText(MainActivity.this,
										R.string.networkerr, searchTime).show();
							} else {
								Toast.makeText(MainActivity.this,
										R.string.loaderror, Toast.LENGTH_LONG)
										.show();
							}
						}
						listView.onLoadComplete();
						listView.setResultSize(AutoListView.pageSize + 1);
						adapter.notifyDataSetChanged();
						if (dialogcancel) {
							isSearch = true;
						}
						emViewIsOnClick = false;
						AppLogger.e("ERROR===========" + e.getMessage());
					}
				});
	}

	private void SearchAppList(String query, int startPos, int docNum,
			final boolean dialogcancel) {

		AppListDao.getInstance().searchAppListRX(query, startPos, docNum)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<AppListBean>() {
					AppListBean appListBean = null;

					@Override
					public void onNext(AppListBean bean) {
						appListBean = bean;
					}

					@Override
					public void onCompleted() {
						if (null != appListBean) {
							List<ItemData> itemDataList = new ArrayList<ItemData>();
							int resultnumber = Integer
									.valueOf(appListBean.result.num);
							for (int i = 0; i < resultnumber; i++) {
								ItemData item = new ItemData();
								item.setAppInfoBean(appListBean.result.items
										.get(i));
								itemDataList.add(item);
								
							}
							AppStoreApplication.getInstance().setAppState(itemDataList);
							displayList(itemDataList);
							if (dialogcancel) {
								progressDialog.cancel();
								isSearch = true;
							}
						} else {
							if (dialogcancel) {
								progressDialog.cancel();
								isSearch = true;
							}
							if (null != adapter) {
								adapter.clearData();
								adapter.notifyDataSetChanged();
							}
							loadGetDataByCategory(AutoListView.LOAD, true);
						}

					}

					@Override
					public void onError(Throwable e) {
						if (!AppStoreApplication.getInstance().isNetWorkConnected) {
							Toast.makeText(MainActivity.this,
									R.string.networkerr, searchTime).show();
						} else
							Toast.makeText(MainActivity.this,
									R.string.loaderror, Toast.LENGTH_LONG)
									.show();
						listView.onLoadComplete();
						listView.setResultSize(AutoListView.pageSize + 1);
						adapter.notifyDataSetChanged();
						if (dialogcancel) {
							progressDialog.cancel();
							isSearch = true;
						}
						emViewIsOnClick = false;
						AppLogger.e("ERROR===========" + e);
					}
				});
	}

	@Override
	public void OnSearch() {
		searchFrame.setVisibility(View.VISIBLE);
		editText.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onLoad() {
		if (operate == 0) {
			loadGetDataByCategory(AutoListView.LOAD, true);
		} else {
			loadSearchDataByCategory(AutoListView.LOAD, true);
		}
	}

	public void NetWorkDisConnect() {
		Toast.makeText(MainActivity.this, R.string.connectfail,
				Toast.LENGTH_SHORT).show();

		AppLogger.e("MainActivity NetWorkDisConnect");
	}


	public void NetWorkConnect() {

		AppLogger.e("MainActivity NetWorkConnect");
		if (operate == 0) {
			if (itemList.size() == 0)
				loadGetDataByCategory(AutoListView.LOAD, true);
		} else {
			if (itemList.size() == 0)
				loadSearchDataByCategory(AutoListView.LOAD, true);
		}

	}



	private void showProgressDialog(){
		progressDialog = ProgressDialog.show(MainActivity.this, "等待加载", "数据加载中...");
		//progressDialog.setCancelable(true);
	}	
}


