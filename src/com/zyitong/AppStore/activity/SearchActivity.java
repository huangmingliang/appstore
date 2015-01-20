package com.zyitong.AppStore.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.zyitong.AppStore.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.zyitong.AppStore.WeiBoApplication;
import com.zyitong.AppStore.adapter.ListAdapter;
import com.zyitong.AppStore.common.FileOpt;
import com.zyitong.AppStore.common.ItemData;
import com.zyitong.AppStore.common.PageInfoData;
import com.zyitong.AppStore.common.SearchFrame;
import com.zyitong.AppStore.common.SearchFrame.OnEtSearchClickListener;
import com.zyitong.AppStore.common.SearchFrame.OnSearchButtonClickListener;
import com.zyitong.AppStore.http.HttpApiImple;
import com.zyitong.AppStore.loading.LoadingDialog;
import com.zyitong.AppStore.loading.WSError;
import com.zyitong.AppStore.util.UtilFun;

public class SearchActivity extends Activity {

	private ListView listView=null;
	private ListAdapter adapter;
	private List<ItemData> itemList = new ArrayList() ;
	private int startpos =0;
	private boolean isConnected =false;
	private EditText editSearchText;
	//private ImageView searchButton;
	private int Flag = 0;
	private String keyword = "";	
	private int parttype=3;
	private SearchFrame mSearchFrame;
	
		
	private ItemData itemData;
	private FileOpt opt;
	
	private UtilFun util = null;
	public static void launch(Context c, Bundle bundle) {
		Intent intent = new Intent(c, SearchActivity.class);
		intent.putExtras(bundle);
		c.startActivity(intent);
	}

	/** Called when the activity is first created. */
	@SuppressLint("NewApi") 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("SearchActivity", "onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		/*getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZEw
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);*/
		setContentView(R.layout.search);
		init();
	}

	private void getPageCache() {
		PageInfoData pageData = WeiBoApplication.getInstance()
				.getPamaterCache().getPage();
		if (pageData != null) {
			keyword = pageData.getKeyword();
			itemList = pageData.getItemList();
		}
	}

	private void setPage(int itemID) {
		keyword = editSearchText.getText().toString();
		WeiBoApplication.getInstance().getPamaterCache().removePage();
		PageInfoData pageData = new PageInfoData();
		pageData.setKeyword(keyword);
		pageData.setPageName("SearchActivity");
		pageData.setItemList(itemList);
		pageData.setSelItem(itemID);
		WeiBoApplication.getInstance().getPamaterCache().addPage(pageData);
	}

	private void init() {
		util = new UtilFun(this);
		opt = new FileOpt();
		clearProcc();
		getPageCache();
		mSearchFrame = (SearchFrame) findViewById(R.id.searchframe);
		mSearchFrame.setSearchButtonListener(new OnSearchButtonClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Search();
			}
		});
		mSearchFrame.setEtSearchListener(new OnEtSearchClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				System.out.println("SearchActivity");
			}
		});
		
		listView = (ListView) findViewById(R.id.listView);
		editSearchText = (EditText) findViewById(R.id.etSearch);
		editSearchText.setText(keyword);
		//searchButton = (ImageView) findViewById(R.id.searchButton);*/
	
		
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
		}
	}
	private void InitList()
	{
		listView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		listView.setCacheColorHint(0);
		listView.setDivider(getResources().getDrawable(R.drawable.lnew_fengexian));
		listView.setDividerHeight(1);
		listView.setVerticalScrollBarEnabled(false);
		// 改进
		
		adapter = new ListAdapter(this, itemList, listView,"SearchActivity");
		listView.setAdapter(adapter);
		
		listView.setItemsCanFocus(false);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Entry(position);
			}
		});
		
		listView.setOnScrollListener(scrolllistener);
	}
	
	
	private void Search()
	{
		//keyword
		String text = editSearchText.getText()==null?"":editSearchText.getText().toString();
		if(text.length()==0){
			WeiBoApplication.getInstance().getPamaterCache().clear();
	
			keyword="";
			itemList.clear();
			InitList();
			Toast.makeText(this,"搜索内容为空，请输入内容！" , Toast.LENGTH_LONG).show();	
			return;
		}	
			itemList.clear();
			InitList();
			Connect();		
	}
	
	  
	
	@SuppressLint("NewApi") 
	private void Entry(int position) {
		if (adapter != null) {
			setPage(position);
			
			finish();
			overridePendingTransition(R.layout.apvalue, R.layout.apvalue);  
		}
	}

	@SuppressLint("NewApi") 
	private void disPlayList(List<ItemData> itemDataList) {
		
	
		for(int i=0;i< itemDataList.size();i++)
		{
			itemList.add(itemDataList.get(i));
		}
		
		adapter.addData(itemList);
		adapter.notifyDataSetChanged();  
		isConnected = false;

	}
    private AbsListView.OnScrollListener scrolllistener = new AbsListView.OnScrollListener() {    
	    
        @Override    
        public void onScrollStateChanged(AbsListView view, int scrollState) {    
            if (view.getLastVisiblePosition() == view.getCount() - 1) {    
                loadData();    
               // adapter.notifyDataSetChanged();    
            }    
        }    
    
        @Override    
        public void onScroll(AbsListView view, int firstVisibleItem,    
                int visibleItemCount, int totalItemCount) {    
    
        }    
    };    
    
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

	private void Connect() {
		isConnected = true;
		new NewTask(SearchActivity.this, R.string.connectioning,
				R.string.connectfail,0).execute();
	}

	private class NewTask extends LoadingDialog<Void, List<ItemData>> {
		public NewTask(Activity activity, int loadingMsg, int failMsg,int loadcategory) {
			super(activity, loadingMsg, failMsg,0);
		}

		@Override
		public List<ItemData> doInBackground(Void... params) {
			List<ItemData> itemList = new ArrayList();
			try {
				String url = "shop/shop.jsp?code=search&UserHeader="
						+ WeiBoApplication.UserHeader + "&keyword="
						+ editSearchText.getText().toString() + "&imei="
						+ WeiBoApplication.getInstance().getImei()+"&start="+startpos;

				HttpApiImple imple = new HttpApiImple();
				ItemData[] itemdata = imple.getListItem(url);
				for (int i = 0; itemdata != null && i < itemdata.length; i++) {
					itemList.add(itemdata[i]);
					System.out.println("SearchActivity itemdata[i].getFileSize() = "+itemdata[i].getFileSize());
					util.setFileState(itemdata[i]);
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
			if(itemList.size()==0)
			{
				Toast.makeText(mActivity,getResources().getString(R.string.searchprompt) , Toast.LENGTH_LONG).show();
				return;
			}
			disPlayList(itemList);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("SearchActivity", "SearchActivity onDestroy");
		super.onDestroy();
	}
	
	
	
	
	
}