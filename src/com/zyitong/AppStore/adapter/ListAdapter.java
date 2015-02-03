package com.zyitong.AppStore.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.R;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.bean.NoticData;
import com.zyitong.AppStore.tools.Util;
import com.zyitong.AppStore.tools.UtilFun;
import com.zyitong.AppStore.ui.RemoteImageView;

@SuppressLint("ResourceAsColor")
public class ListAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<ItemData> itemList;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	private ListView listView;
	
	private UtilFun util = null;
	
	
	
	private List<dLoadButtonTextofRadio>dlButtontextlist;

	public ListAdapter(Context context, List<ItemData> itemList,
			ListView listView, String className) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.itemList = itemList;
		this.listView = listView;
	
		init();
	}

	public interface OnLoadButtonListener {
		public int onLoadProgress();
	}

	public ListAdapter(Context context, List<ItemData> itemList,
			ListView listView) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.itemList = itemList;
		this.listView = listView;
		init();
	}

	private void init() {
		util = new UtilFun(mContext);
		mData = new ArrayList<Map<String, Object>>();
		dlButtontextlist = new ArrayList<ListAdapter.dLoadButtonTextofRadio>();
		for (int i = 0; i < itemList.size(); i++) {
			
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", itemList.get(i).getName());
				map.put("star", itemList.get(i).getStar());
				map.put("iconView", itemList.get(i).getImage());
				map.put("filesize",
						Util.getForMartSize(itemList.get(i).getFileSize(), 2));
				map.put("downloadnum",
						Util.getDownloadNum(itemList.get(i).getDownloadnum()));
				
				mData.add(map);			
		}		
	}

	public void addData(List<ItemData> itemDataList) {
		this.itemList = itemDataList;
		for (int i = mData.size(); i < itemDataList.size(); i++) {
			
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", itemList.get(i).getName());
				map.put("star", itemList.get(i).getStar());
				map.put("iconView", itemList.get(i).getImage());
				map.put("filesize",
						Util.getForMartSize(itemList.get(i).getFileSize(), 2));
				map.put("downloadnum",
						Util.getDownloadNum(itemList.get(i).getDownloadnum()));
				mData.add(map);
				dLoadButtonTextofRadio dl = new dLoadButtonTextofRadio();
				dl.setRadio(0);
				dlButtontextlist.add(dl);
		
		}
	}

	public void clearData() {
		itemList.clear();
		mData.clear();
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.e("ListAdapter","getView().position = "+position);
		// TODO Auto-generated method stub
		final ViewHolder holder;
		final ItemData indexData = itemList.get(position);	
		if(convertView==null){					
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_row, null);
			holder.iconView = (RemoteImageView) convertView
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
		}else{
			holder = (ViewHolder) convertView.getTag();							
		}
		setView(holder, position, indexData);		
		return convertView;

	}
	
	private void setView(ViewHolder holder, int position,final ItemData indexData){
		
		final int positionn = position;
		
		String filename = AppStoreApplication.getInstance().getFilePath()+
				AppStoreApplication.getInstance().getFileName(
						indexData.getFilename());
		int star = indexData.getStar();
		setStar(holder, star);
		holder.textFileSizeView.setText(R.string.game);
		holder.uri = filename;

		holder.name.setText(mData.get(position).get("name").toString());
		holder.iconView.setDefaultImage(R.drawable.loading_imageview);
		
		//testpicture
		testSetPicture(positionn, holder);
					
		holder.iconView.setImageUrl(mData.get(position).get("iconView")
					.toString(), position, listView);
				
		/*if(position!=2)
			holder.iconView.setImageUrl("http://img.r1.market.hiapk.com/data/upload/2014/07_16/15/72_72_201407161548386365.png");
		else
			holder.iconView.setImageUrl("http://img.r1.market.hiapk.com/data/upload/2014/12_24/13/72_72_201412241343308502.png");*/
		setDownloadButtonByflag(holder, positionn, indexData);
				
		holder.imageDownloadView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				System.out.println("click button = " + positionn);				
				final Button dlbutton = (Button) v;				
											
					String filename =AppStoreApplication.getInstance().getFilePath()+AppStoreApplication.getInstance().getFileName(indexData.getFilename());
					if(AppStoreApplication.getInstance().isNetWorkConnected){
						if (indexData.getButtonFileflag()==ItemData.APP_INSTALED||indexData.getButtonFileflag()==ItemData.APP_FAIL) {
							//dlbutton.setVisibility(View.GONE);
							setdownloadButtonBackground(dlbutton, "0%", R.drawable.loading_button);
							itemList.get(positionn).setButtonFileflag(3);
							FileDownloadJob data = util.DataChange(indexData);
							if (data != null) {							
								NoticData noticData = new NoticData();
								noticData.setFileDownloadJob(data);
								AppStoreApplication.getInstance().getDownloadLink().addNode(noticData);
							}
							
						} else if (indexData.getButtonFileflag() == ItemData.APP_OPEN) {
							try {
								if (util.isAppInstalled(filename, mContext)) {
									String packageName = util.getPackageName(
											filename, mContext);
									util.openApp(packageName, mContext);
								}
							} catch (NameNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}			
					}else 
						Toast.makeText(mContext, R.string.connectfail, Toast.LENGTH_SHORT).show();
					
			}
		});
								
	}
	
	public void updateSingleRow(int position,int radio,int status){
		
		Log.e("ListAdapter", "updateSingleRow");
	
			//View view = listView.getChildAt(position);	
			Log.e("ListAdapter",dlButtontextlist.get(position).getRadio()+" ");
			
			if(dlButtontextlist.get(position).getRadio()<radio||dlButtontextlist.get(position).getRadio()==radio)
			   dlButtontextlist.get(position).setRadio(radio);
			
			if(status == 4)
				dlButtontextlist.get(position).setRadio(0);
			
			itemList.get(position).setButtonFileflag(status);
			notifyDataSetChanged();
			//getView(position, view, listView);
	}

	public final class ViewHolder {
		public RemoteImageView iconView;
		public TextView name;
		public ProgressBar loadProgressBar;

		public View imageView1;
		public View imageView2;
		public View imageView3;
		public View imageView4;
		public View imageView5;
		

		// public TextView textDownloadNumView;
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

	private void testSetPicture(int position, ViewHolder holder) {
		//Log.e("ListAdapter", "testSetPicture'position = "+position);
		switch (position) {
		case 0:
			holder.iconView.setDefaultImage(R.drawable.qiyi_icon);
			break;
		case 1:
			holder.iconView.setDefaultImage(R.drawable.jingdong_icon);
			break;
		case 2:
			holder.iconView.setDefaultImage(R.drawable.xiecheng_icon);
			break;
		case 3:
			holder.iconView.setDefaultImage(R.drawable.appicon);
			break;
		case 4:
			holder.iconView.setDefaultImage(R.drawable.qq2011);
			break;
		case 5:
			holder.iconView.setDefaultImage(R.drawable.yingxiangbiji_icon);
			break;

		default:
			holder.iconView.setDefaultImage(R.drawable.loading_imageview);
			break;
		}

	}
	
	private void setDownloadButtonByflag(ViewHolder holder,int position,final ItemData indexData){
		switch (indexData.getButtonFileflag()) {
		case 0:
			AppStoreApplication.getInstance().getCurrentDownloadJobManager().completeCurrentDownLoadInfo(indexData);
            break;	
		case 1:
			setdownloadButtonBackground(holder.imageDownloadView, R.string.app_install, R.drawable.load_button);
			break;
		case 2:		
			setdownloadButtonBackground(holder.imageDownloadView, R.string.app_open, R.drawable.open_button);
			AppStoreApplication.getInstance().getCurrentDownloadJobManager().removeDownloadJob(util.getFileName(indexData.getFilename()));
			break;
		case 3:	
			setdownloadButtonBackground(holder.imageDownloadView, dlButtontextlist.get(position).getRadio()+"%", R.drawable.loading_button);	
			break;
		case 4:
			setdownloadButtonBackground(holder.imageDownloadView, R.string.app_install, R.drawable.load_button);			
			//����app������Ϣ�ӵ�ǰ�����б����Ƴ�
			dlButtontextlist.get(position).setRadio(0);
			AppStoreApplication.getInstance().getCurrentDownloadJobManager().removeDownloadJob(util.getFileName(indexData.getFilename()));
			break;
		case 5:	
			setdownloadButtonBackground(holder.imageDownloadView, dlButtontextlist.get(position).getRadio()+"%", R.drawable.loading_button);			
			break;
		default:
			break;
		}
	}
	
	private class dLoadButtonTextofRadio{
		int radio;

		public int getRadio() {
			synchronized (this) {
				return radio;
			}
			
		}

		public void setRadio(int radio) {
			synchronized (this) {
				this.radio = radio;
			}
			
		}
	}
	private void setdownloadButtonBackground(Button dlbutton,int string,int resource){
		dlbutton.setText(string);
		dlbutton.setBackgroundResource(resource);

	}
	private void setdownloadButtonBackground(Button dlbutton,String text,int resource){
		dlbutton.setText(text);
		dlbutton.setBackgroundResource(resource);

	}

}
