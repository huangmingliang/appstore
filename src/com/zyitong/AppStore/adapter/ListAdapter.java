package com.zyitong.AppStore.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.R;
import com.zyitong.AppStore.bean.CurrentDownloadJob;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.tools.AppLogger;
import com.zyitong.AppStore.tools.UtilFun;

@SuppressLint("ResourceAsColor")
public class ListAdapter extends BaseAdapter {

	private Context mContext;
	private List<ItemData> itemList;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	private UtilFun util = null;
	private List<dLoadButtonTextofRadio> dlButtontextlist;

	public ListAdapter(Context context, List<ItemData> itemList,
			String className) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.itemList = itemList;
		init();
	}

	public interface OnLoadButtonListener {
		public int onLoadProgress();
	}

	public ListAdapter(Context context, List<ItemData> itemList) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.itemList = itemList;
		init();
	}

	private void init() {
		util = new UtilFun(mContext);
		mData = new ArrayList<Map<String, Object>>();
		
		dlButtontextlist = new ArrayList<ListAdapter.dLoadButtonTextofRadio>();
		
		for (int i = 0; i < itemList.size(); i++) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", itemList.get(i).getAppInfoBean().getTitle());
			map.put("star", Integer.valueOf(itemList.get(i).getAppInfoBean()
					.getGrade()) / 20);
			map.put("type", itemList.get(i).getAppInfoBean().getType());
			map.put("iconView", itemList.get(i).getAppInfoBean().getThumbnail());
			mData.add(map);
		}
	}

	public void addData(List<ItemData> itemDataList) {
		this.itemList = itemDataList;
		for (int i = mData.size(); i < itemDataList.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", itemList.get(i).getAppInfoBean().getTitle());
			map.put("star", Integer.valueOf(itemList.get(i).getAppInfoBean()
					.getGrade()) / 20);
			map.put("type", itemList.get(i).getAppInfoBean().getType());
			map.put("iconView", itemList.get(i).getAppInfoBean().getThumbnail());
			mData.add(map);
			dLoadButtonTextofRadio dl = new dLoadButtonTextofRadio();
			dl.setRadio(0);
			dlButtontextlist.add(dl);
		}
	}

	public void clearData() {
		itemList.clear();
		mData.clear();
		dlButtontextlist.clear();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final ItemData indexData = itemList.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_row, null);
			holder.iconView = (ImageView) convertView
					.findViewById(R.id.iconView);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.textFileSizeView = (TextView) convertView
					.findViewById(R.id.textFileSizeView);
			holder.imageDownloadView = (Button) convertView
					.findViewById(R.id.imageDownloadView);
			holder.imageView1 = (View) convertView.findViewById(R.id.starView1);
			holder.imageView2 = (View) convertView.findViewById(R.id.starView2);
			holder.imageView3 = (View) convertView.findViewById(R.id.starView3);
			holder.imageView4 = (View) convertView.findViewById(R.id.starView4);
			holder.imageView5 = (View) convertView.findViewById(R.id.starView5);

			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
			
		}
		if (null != mData)
			setView(holder, position, indexData);
		return convertView;
	}

	private void setView(ViewHolder holder, int position,
			final ItemData indexData) {

		final int positionn = position;
		String filename = AppStoreApplication.getInstance().getFilePath()
				+ AppStoreApplication.getInstance().getFileName(
						indexData.getAppInfoBean().getUrl());
		int star = Integer.valueOf(indexData.getAppInfoBean().getGrade()) / 20;
		setStar(holder, star);
		holder.textFileSizeView.setText(mData.get(position).get("type")
				.toString());
		holder.uri = filename;

		holder.name.setText(mData.get(position).get("name").toString());
		Picasso.with(mContext)
				.load(mData.get(position).get("iconView").toString())
				.placeholder(R.drawable.loading_imageview)
				.error(R.drawable.loading_imageview).into(holder.iconView);

		setDownloadButtonByflag(holder, positionn, indexData);

		holder.imageDownloadView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final Button dlbutton = (Button) v;
				AppLogger.e("onClick Button = " +positionn);
				
				String filename = AppStoreApplication.getInstance().getFilePath()
						+ AppStoreApplication.getInstance().getFileName(indexData.getAppInfoBean().getUrl());
				String packageName = indexData.getAppInfoBean().getPackagename();
				if (packageName == null)
					packageName = util.getPackageName(filename, mContext);
				
                if(!AppStoreApplication.getInstance().isNetWorkConnected) {
                	if (indexData.getButtonFileflag() == ItemData.APP_OPEN) {
                		
     					if (util.checkApkExist(mContext, packageName)) {
     						String args = util.openApp(packageName, mContext);
     					}
     					return ;

     				}
                	Toast.makeText(mContext, R.string.connectfail,Toast.LENGTH_SHORT).show();
                	return ;
                }
                AppLogger.e("onClick status ="+indexData.getButtonFileflag());
				if (indexData.getButtonFileflag() == ItemData.APP_INSTALL
					|| indexData.getButtonFileflag() == ItemData.APP_FAIL
					|| indexData.getButtonFileflag() == ItemData.APP_REDOWNLOAD
					|| indexData.getButtonFileflag() == ItemData.APP_NETWORKEX) {
						
					FileDownloadJob data = util.DataChange(indexData);
					
					if (data != null) {
						
						setdownloadButtonBackground(dlbutton, "0%",R.drawable.loading_button);
						AppStoreApplication.getInstance().getDownloadLink().addNode(data);
						
					}
					if (!AppStoreApplication.getInstance().getDownloadLink().hasDownloadFree()) {

						setdownloadButtonBackground(dlbutton,R.string.app_waitinstall,R.drawable.loading_button);
						AppStoreApplication.getInstance().getDownloadLink().addNode(data);
						addCurrentDownLoad(data, ItemData.APP_WAIT, packageName);
						
					}
					itemList.get(positionn).setButtonFileflag(
								ItemData.APP_LOADING);
					return ;
						
				} else if (indexData.getButtonFileflag() == ItemData.APP_LOADING
						|| indexData.getButtonFileflag() == ItemData.APP_UPDATING) {
					if(!AppStoreApplication.getInstance().getCurrentDownloadJobManager().isCurrJobExist(packageName)){
						setdownloadButtonBackground(dlbutton,
								R.string.app_install, R.drawable.load_button);
						return ;
					}

					dlButtontextlist.get(positionn).setRadio(0);
					util.setAppReDownLoad(indexData.getAppInfoBean().getPackagename());
					FileDownloadJob data = util.DataChange(indexData);
					AppStoreApplication.getInstance().getDownloadLink()
								.delNode(data.getId());
					
				} else if (indexData.getButtonFileflag() == ItemData.APP_WAIT) {
					
					setdownloadButtonBackground(dlbutton,R.string.app_install, R.drawable.load_button);
					FileDownloadJob data = util.DataChange(indexData);
					AppStoreApplication.getInstance().getCurrentDownloadJobManager().setStatus(packageName, ItemData.APP_INSTALL);
					dlButtontextlist.get(positionn).setRadio(0);
					if (data != null) {
						AppStoreApplication.getInstance().getDownloadLink()
								.delNode(data);
					}
					return ;
						
				} else if(indexData.getButtonFileflag() == ItemData.APP_UPDATE){
					FileDownloadJob data = util.DataChange(indexData);
					if (data != null) {
						
						setdownloadButtonBackground(dlbutton, "0%",R.drawable.loading_button);
						AppStoreApplication.getInstance().getDownloadLink().addNode(data);
							
					}
					if (!AppStoreApplication.getInstance().getDownloadLink().hasDownloadFree()) {
						setdownloadButtonBackground(dlbutton,R.string.app_waitinstall,R.drawable.loading_button);	
						addCurrentDownLoad(data,ItemData.APP_UPDATE_WAIT,packageName);
						AppStoreApplication.getInstance().getDownloadLink().addNode(data);
							
					}
					itemList.get(positionn).setButtonFileflag(ItemData.APP_UPDATING);
					return ;
						
				} else if(indexData.getButtonFileflag() == ItemData.APP_UPDATE_WAIT){
						
					setdownloadButtonBackground(dlbutton,R.string.app_update, R.drawable.load_button);
	                AppStoreApplication.getInstance().getCurrentDownloadJobManager().setStatus(packageName, ItemData.APP_UPDATE);
	                return ;
	                    
				} else if (indexData.getButtonFileflag() == ItemData.APP_OPEN) {
					
 					if (util.checkApkExist(mContext, packageName)) {
 						String args = util.openApp(packageName, mContext);
 					}
 					return ;
 				}
				
				
			}

		});
	}

	public void updateSingleRow(int position, int radio, int status) {

		if (null == itemList || null == dlButtontextlist) {
			AppLogger.e("itemList == null");
			return;
		}

		ItemData item = itemList.get(position);
		if (null == item) {
			return;
		}

		dLoadButtonTextofRadio button = dlButtontextlist.get(position);
		if (null == button) {
			return;
		}
		switch (status) {
		case ItemData.APP_FAIL:
			radio = 0;
			AppStoreApplication.getInstance().getCurrentDownloadJobManager().removeDownloadJob(itemList.get(position).getAppInfoBean().getPackagename());
			break;
		case ItemData.APP_REDOWNLOAD:
			radio = 0;
			break;
		case ItemData.APP_OPEN:
			radio = 0;
			break;
		case ItemData.APP_NETWORKEX:
			radio = 0;
			break;
		case ItemData.APP_UPDATE:
			AppStoreApplication.getInstance().getCurrentDownloadJobManager().removeDownloadJob(itemList.get(position).getAppInfoBean().getPackagename());
			radio = 0;
			break;
		default:
			break;
		}
		
		item.setButtonFileflag(status);
		button.setRadio(radio);

		notifyDataSetChanged();

		return;
	}

	private final class ViewHolder {
		public ImageView iconView;
		public TextView name;
		public View imageView1;
		public View imageView2;
		public View imageView3;
		public View imageView4;
		public View imageView5;
		public TextView textFileSizeView;
		public Button imageDownloadView;
		public String uri;
	}

	private void setStar(ViewHolder holder, int star) {

		switch (star) {
		case 1:
			holder.imageView1.setBackgroundResource(R.drawable.star01);
			holder.imageView2.setBackgroundResource(R.drawable.star02);
			holder.imageView3.setBackgroundResource(R.drawable.star02);
			holder.imageView4.setBackgroundResource(R.drawable.star02);
			holder.imageView5.setBackgroundResource(R.drawable.star02);
			break;
		case 2:
			holder.imageView1.setBackgroundResource(R.drawable.star01);
			holder.imageView2.setBackgroundResource(R.drawable.star01);
			holder.imageView3.setBackgroundResource(R.drawable.star02);
			holder.imageView4.setBackgroundResource(R.drawable.star02);
			holder.imageView5.setBackgroundResource(R.drawable.star02);
			break;
		case 3:
			holder.imageView1.setBackgroundResource(R.drawable.star01);
			holder.imageView2.setBackgroundResource(R.drawable.star01);
			holder.imageView3.setBackgroundResource(R.drawable.star01);
			holder.imageView4.setBackgroundResource(R.drawable.star02);
			holder.imageView5.setBackgroundResource(R.drawable.star02);
			break;
		case 4:
			holder.imageView1.setBackgroundResource(R.drawable.star01);
			holder.imageView2.setBackgroundResource(R.drawable.star01);
			holder.imageView3.setBackgroundResource(R.drawable.star01);
			holder.imageView4.setBackgroundResource(R.drawable.star01);
			holder.imageView5.setBackgroundResource(R.drawable.star02);
			break;
		case 5:
			holder.imageView1.setBackgroundResource(R.drawable.star01);
			holder.imageView2.setBackgroundResource(R.drawable.star01);
			holder.imageView3.setBackgroundResource(R.drawable.star01);
			holder.imageView4.setBackgroundResource(R.drawable.star01);
			holder.imageView5.setBackgroundResource(R.drawable.star01);
			break;
		default:
			holder.imageView1.setBackgroundResource(R.drawable.star02);
			holder.imageView2.setBackgroundResource(R.drawable.star02);
			holder.imageView3.setBackgroundResource(R.drawable.star02);
			holder.imageView4.setBackgroundResource(R.drawable.star02);
			holder.imageView5.setBackgroundResource(R.drawable.star02);
			break;
		}
	}

	private void setDownloadButtonByflag(ViewHolder holder, int position,
			final ItemData indexData) {
		switch (indexData.getButtonFileflag()) {

		case ItemData.APP_OPEN:
			setdownloadButtonBackground(holder.imageDownloadView,
					R.string.app_open, R.drawable.open_button);
			AppStoreApplication
					.getInstance()
					.getCurrentDownloadJobManager()
					.removeDownloadJob(
							indexData.getAppInfoBean().getPackagename());
			break;
		case ItemData.APP_LOADING:
			setdownloadButtonBackground(holder.imageDownloadView,
					dlButtontextlist.get(position).getRadio() + "%",
					R.drawable.loading_button);
			break;
		case ItemData.APP_FAIL:
			setdownloadButtonBackground(holder.imageDownloadView,
					R.string.app_install, R.drawable.load_button);
			dlButtontextlist.get(position).setRadio(0);
			AppStoreApplication
					.getInstance()
					.getCurrentDownloadJobManager()
					.removeDownloadJob(
							indexData.getAppInfoBean().getPackagename());
			break;
		case ItemData.APP_NETWORKEX:
			setdownloadButtonBackground(holder.imageDownloadView,
					R.string.app_reinstall, R.drawable.loading_button);
			break;
		case ItemData.APP_REDOWNLOAD:
			setdownloadButtonBackground(holder.imageDownloadView,
					R.string.app_install, R.drawable.load_button);
			break;
		case ItemData.APP_INSTALL:
			setdownloadButtonBackground(holder.imageDownloadView,
					R.string.app_install, R.drawable.load_button);
			break;
		case ItemData.APP_WAIT:
			setdownloadButtonBackground(holder.imageDownloadView,
					R.string.app_waitinstall, R.drawable.loading_button);
			break;
		case ItemData.APP_UPDATE:
			setdownloadButtonBackground(holder.imageDownloadView,
					R.string.app_update, R.drawable.load_button);
			break;
		default:
			break;
		}
	}

	private class dLoadButtonTextofRadio {
		int radio;

		public int getRadio() {
			return radio;
		}

		public void setRadio(int radio) {
			this.radio = radio;
		}
	}

	private void setdownloadButtonBackground(Button dlbutton, int string,
			int resource) {
		dlbutton.setText(string);
		dlbutton.setBackgroundResource(resource);

	}

	private void setdownloadButtonBackground(Button dlbutton, String text,
			int resource) {
		dlbutton.setText(text);
		dlbutton.setBackgroundResource(resource);

	}
	
	private void addCurrentDownLoad(FileDownloadJob data , int status, String packageName){
		CurrentDownloadJob currentDownloadJob = new CurrentDownloadJob();
		currentDownloadJob.setData(data);
		currentDownloadJob.setFilestatus(status);
		currentDownloadJob.setPackageName(packageName);
		currentDownloadJob.setRatio(0);
		AppStoreApplication.getInstance()
				.getCurrentDownloadJobManager()
				.addDownloadJob(currentDownloadJob);
	}
	
}
