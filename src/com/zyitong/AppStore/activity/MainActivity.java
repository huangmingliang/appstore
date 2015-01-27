package com.zyitong.AppStore.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import com.zyitong.AppStore.R;

import android.R.integer;
import android.R.string;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.Dialog.DownloadDialog;
import com.zyitong.AppStore.adapter.ListAdapter;
import com.zyitong.AppStore.common.CurrentDownloadJob;
import com.zyitong.AppStore.common.CurrentDownloadJobManager;
import com.zyitong.AppStore.common.DownloadLink;
import com.zyitong.AppStore.common.FileDownloadJob;
import com.zyitong.AppStore.common.FileOpt;
import com.zyitong.AppStore.common.ItemData;
import com.zyitong.AppStore.common.NoticData;
import com.zyitong.AppStore.common.PageInfoData;
import com.zyitong.AppStore.common.SearchFrame;
import com.zyitong.AppStore.common.SearchFrame.OnEtSearchClickListener;
import com.zyitong.AppStore.common.SearchFrame.OnSearchButtonClickListener;
import com.zyitong.AppStore.http.HttpApiImple;
import com.zyitong.AppStore.listview.AutoListView;
import com.zyitong.AppStore.listview.AutoListView.OnLoadListener;
import com.zyitong.AppStore.listview.AutoListView.OnRefreshListener;
import com.zyitong.AppStore.loading.LoadingDialog;
import com.zyitong.AppStore.loading.WSError;
import com.zyitong.AppStore.util.UtilFun;

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
	private int parttype = 0;
	private int Flag = 0;
	private int testTimes = 0;
	// private SearchFrame mSearchFrame;
	private int searchTime = 1000;// 查询间隔时间
	private long exitTime  = 0;

	// 定时器，定失去查询下载列表的更新状态，定为1秒钟查询一次
	private Timer timer;

	private FileOpt opt;

	private ItemData itemData;
	private UtilFun util = null;

	private Message msg = null;

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			if (msg.obj != null) {
				int updateinfo[] = (int[]) msg.obj;
				int position = updateinfo[0];
				String radio = Integer.toString(updateinfo[1]);
				int status = updateinfo[2];
				if(status == 4)
					Toast.makeText(MainActivity.this, itemList.get(position).getName()+"安装失败", Toast.LENGTH_LONG).show();
				if(status == 5)
					Toast.makeText(MainActivity.this, "网络出现了问题", Toast.LENGTH_LONG).show();
				if (updateinfo[0] != -1)
					adapter.updateSingleRow(position, radio , status);
			}
		}
	};

	private final TimerTask updatalistviewtask = new TimerTask() {
		public void run() {
			// 查询下载列表，看那些下载状态需要更新
			Log.e("MainActivity", "定时器在检查更新");

			for (int i = 0; i < itemList.size(); i++) {
				String appName = util.getFileName(itemList.get(i).getFilename());
				//检查
				
				int raido = AppStoreApplication.getInstance()
						.getCurrentDownloadJobManager()
						.getRatio(appName);
				int[] updateinfo = new int[3];
				if (raido != -1){
					int status = AppStoreApplication.getInstance().getCurrentDownloadJobManager().getStatus(appName);
					
					
					switch (status) {
					//状态为0说明是一个需要初始化的下载任务
					case 0:
						updateinfo[0] = i;
    					updateinfo[1] = -2;
    					updateinfo[2] = status;
						break;
                    
                    case 3:
                    	updateinfo[0] = i;
    					updateinfo[1] = raido;
    					updateinfo[2] = status;
						break;
                    case 4:
                    	updateinfo[0] = i;
    					updateinfo[1] = raido;
    					updateinfo[2] = status;
                        AppStoreApplication.getInstance().getCurrentDownloadJobManager().removeDownloadJob(appName);
						break;
                    case 5:
                    	updateinfo[0] = i;
    					updateinfo[1] = raido;
    					updateinfo[2] = status;
						break;

					default:
						updateinfo[0] = i;
    					updateinfo[1] = raido;
    					updateinfo[2] = status;
						break;
					}
					
					Log.e("MainActivity", "下载进度有更新");
					Log.e("MainActivity", "i=" + i);
					Log.e("MainActivity", "status=" + status);
					// adapter.updateSingleRow(i,raido);
					
					
					msg = handler.obtainMessage();
					msg.obj = updateinfo;
					msg.sendToTarget();
				}
			}

		}

	};

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
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);
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
		opt = new FileOpt();
		/*
		 * mSearchFrame = (SearchFrame) findViewById(R.id.searchframe);
		 * mSearchFrame.setSearchButtonListener(new
		 * OnSearchButtonClickListener() {
		 * 
		 * @Override public void onClick() { // TODO Auto-generated method stub
		 * System.out.println("SearchButton onclick!"); } });
		 * mSearchFrame.setEtSearchListener(new OnEtSearchClickListener() {
		 * 
		 * @Override public void onClick() { // TODO Auto-generated method stub
		 * System.out.println("exittext onclick!");
		 * AppStoreApplication.getInstance().getPamaterCache().clear();
		 * SearchActivity.launch(MainActivity.this, new Bundle());
		 * overridePendingTransition(R.layout.apvalue, R.layout.apvalue); } });
		 */
		timer = new Timer(true);
		timer.schedule(updatalistviewtask, 0,searchTime);
		GetImei();// 获取设备的deviceid
		clearProcc();
		getPageCache();
		listView = (AutoListView) findViewById(R.id.listView);
		// listView.setEnabled(false);

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

	private void setTitle() {

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
					
					Log.v("MainActivity", "getHttpApicount = "
							+ itemdata.length);
					for (int i = 0; itemdata != null && i < itemdata.length; i++) {
											
						util.setFileState(itemdata[i]);
						if(i == 1)
							itemdata[i].setFilename("http://apk.r1.market.hiapk.com/data/upload/apkres/2015/1_22/21/com.tencent.mm_093944461.apk");
						else if(i == 2)
							itemdata[i].setFilename("http://apk.r1.market.hiapk.com/data/upload/2015/01_16/23/com.tencent.mobileqq_233135.apk");
						else if(i == 3)
							itemdata[i].setFilename("http://apk.r1.market.hiapk.com/data/upload/2015/01_22/18/com.qiyi.video_182142.apk");
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
		
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
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
}