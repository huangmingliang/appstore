package com.zyitong.AppStore.activity;


import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import com.zyitong.AppStore.R;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.zyitong.AppStore.WeiBoApplication;
import com.zyitong.AppStore.Dialog.DownloadDialog;
import com.zyitong.AppStore.adapter.ListAdapter;
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

@TargetApi(Build.VERSION_CODES.ECLAIR) 
@SuppressLint("NewApi") 
public class MainActivity extends Activity implements OnRefreshListener,OnLoadListener{
	private AutoListView listView;
	private ListAdapter adapter;
	private List<ItemData> itemList = new ArrayList() ;
	private int startpos =0;
	private boolean isConnected =false;
	private int prevFlag=-1;
	private int parttype=0;
	private int Flag = 0;
	//private SearchFrame mSearchFrame;
	
	
	private FileOpt opt;
	
	
	private ItemData itemData;
	private UtilFun util = null;
	

	public static void launch(Context c, Bundle bundle) {
		Intent intent = new Intent(c, MainActivity.class);
		intent.putExtras(bundle);
		c.startActivity(intent);
	}
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			//	WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
		new NewTask(MainActivity.this, R.string.connectioning,
				R.string.connectfail,AutoListView.REFRESH).execute();
		super.onRestart();
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		System.out.println("MainActivity onResume");
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

	public void dialogProcc()
	{
		setPage(0);
		
	}

	private void GetImei() {
		String imei = WeiBoApplication.getInstance().getImei();
		if (imei == null || imei.length() == 0) {
			TelephonyManager telephonyManager = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId();

			WeiBoApplication.getInstance().setImei(imei);
		}

	}

	private void init() {
		util = new UtilFun(this);
		opt = new FileOpt();
		/*mSearchFrame = (SearchFrame) findViewById(R.id.searchframe);
		mSearchFrame.setSearchButtonListener(new OnSearchButtonClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				System.out.println("SearchButton onclick!");
			}
		});
		mSearchFrame.setEtSearchListener(new OnEtSearchClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				System.out.println("exittext onclick!");
				WeiBoApplication.getInstance().getPamaterCache().clear();
				SearchActivity.launch(MainActivity.this, new Bundle());
				overridePendingTransition(R.layout.apvalue, R.layout.apvalue);
			}
		});*/
		GetImei();//获取设备的deviceid
		clearProcc();
		getPageCache();	
		listView = (AutoListView) findViewById(R.id.listView);
		//listView.setEnabled(false);
		InitOnclick();
		InitList();
		CacheProcc();				
	}
	
	
    
	
	private void clearProcc()
	{
		Bundle bundle = this.getIntent().getExtras();
		if(bundle !=null)
		{
		  int clear= bundle.getInt("CLEAR");
		  if(clear ==100)
			  WeiBoApplication.getInstance().getDownloadLink().intrrentDown();
		}
	}
		
	
	private void CacheProcc()
	{
		if(itemList.size()>0)
		{		
			adapter.addData(itemList);
			adapter.notifyDataSetChanged();  
		}else
		{
			Active();
			System.out.println("MainActivity itemList.size = ");
		}
	}
	
	private void InitList()
	{
		listView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		listView.setCacheColorHint(0);
		listView.setDivider(getResources().getDrawable(R.drawable.tiao));
		listView.setDividerHeight(1);
	
		listView.setVerticalScrollBarEnabled(true);
		// 改进		
		adapter = new ListAdapter(this, itemList, listView,"MainActivity");
		listView.setAdapter(adapter);
		
		listView.setOnRefreshListener(this);
		listView.setOnLoadListener(this);		
		listView.setItemsCanFocus(false);		
	}
	private void InitOnclick()
	{
				
	}
		
   
	private void getPageCache() {
		PageInfoData pageData = WeiBoApplication.getInstance()
				.getPamaterCache().getPage();
		if (pageData != null) {
			prevFlag = Flag = pageData.getFlag();
			itemList = pageData.getItemList();
		}
	}

	private void setPage(int itemID) {
		WeiBoApplication.getInstance().getPamaterCache().removePage();
		PageInfoData pageData = new PageInfoData();
		pageData.setFlag(Flag);
		pageData.setPageName("MainActivity");
		pageData.setItemList(itemList);
		pageData.setSelItem(itemID);
		WeiBoApplication.getInstance().getPamaterCache().addPage(pageData);
	}

	private void setTitle() {
		
	
	}
	private void setDaoHangTitle() {
		
	}
	private void Active() {
		if(prevFlag != Flag || prevFlag==Flag && itemList.size()==0)
		{
			setDaoHangTitle();
			adapter.clearData();
			itemList.clear();
			startpos =0;
			Connect();
		}
		prevFlag = Flag;
	}

	private void disPlayList(List<ItemData> itemDataList,int loadcategory) {
		
		switch (loadcategory) {
		case AutoListView.REFRESH:
			listView.onRefreshComplete();
			adapter.clearData();
			System.out.println("refresh = "+itemList.size());
			itemList.addAll(itemDataList);
			adapter.addData(itemList);
			
			break;
		case AutoListView.LOAD:
		    listView.onLoadComplete();
		    itemList.addAll(itemDataList);
		    adapter.addData(itemList);
			break;

		default:
			break;
		}
		//System.out.println("itemDataList.size() = "+itemList.size());
		listView.setResultSize(itemDataList.size());
		Log.v("MainActivity", "listview getitemcount = "+listView.getCount());
		Log.v("MainActivity", "listview getitemcount = "+listView.getChildCount());

		
		adapter.notifyDataSetChanged();  
		isConnected = false;
	}
	
    
    private void loadData()
    {
    	startpos = itemList.size();
    	if(startpos>0)
    	{
	    	ItemData temData =  itemList.get(startpos-1);
	    	if(temData.getEnd()==0 && !isConnected)
	    	{
	    		Connect();
	    	}
    	}
    }
    
    private void loadDataByCategory(int category){
    	switch (category) {
		case AutoListView.REFRESH:
			new NewTask(MainActivity.this, R.string.connectioning,
					R.string.connectfail,AutoListView.REFRESH).execute();
			break;
        case AutoListView.LOAD:
        	new NewTask(MainActivity.this, R.string.connectioning,
    				R.string.connectfail,AutoListView.LOAD).execute();
			break;

		default:
			break;
		}
    	
    }
    

	private void Connect() {
		isConnected = true;
		new NewTask(MainActivity.this, R.string.connectioning,
				R.string.connectfail,AutoListView.LOAD).execute();
	}

	private class NewTask extends LoadingDialog<Void, List<ItemData>> {
		int loadcategory;
		public NewTask(Activity activity, int loadingMsg, int failMsg,int loadcategory) {			
			super(activity, loadingMsg, failMsg,loadcategory);
			this.loadcategory = loadcategory;
		}

		
		@Override
		public List<ItemData> doInBackground(Void... params)  {
			List<ItemData> itemList = new ArrayList();
			try {
				String url = "shop/shop.jsp?code=tj&UserHeader="
						+ WeiBoApplication.UserHeader + "&flag=" + (Flag + 1)+"&start="+startpos
						+ "&imei=" + WeiBoApplication.getInstance().getImei();
				HttpApiImple imple = new HttpApiImple();
				ItemData[] itemdata = imple.getListItem(url);
			    switch (loadcategory) {
				case AutoListView.REFRESH:
					
					for (int i = 0; itemdata != null && i < itemdata.length; i++) {
						
						util.setFileState(itemdata[i]);
						itemList.add(itemdata[i]);						
					}
					break;
				case AutoListView.LOAD:	
					Log.v("MainActivity", "getHttpApicount = "+itemdata.length);
					for (int i = 0; itemdata != null && i < itemdata.length; i++) {
						util.setFileState(itemdata[i]);
						itemList.add(itemdata[i]);
						System.out.println("MainActivity itemdata[i].getFileSize() = "+itemdata[i].getFileSize());
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

			return itemList;
		}

		@Override
		public void doStuffWithResult(List<ItemData> itemList) {
			disPlayList(itemList,loadcategory);
		}
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