package com.zyitong.AppStore.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.R;

import com.zyitong.AppStore.common.FileDownloadJob;
import com.zyitong.AppStore.common.FileOpt;
import com.zyitong.AppStore.common.ItemData;
import com.zyitong.AppStore.common.NoticData;
import com.zyitong.AppStore.common.RemoteImageView;
import com.zyitong.AppStore.common.Util;
import com.zyitong.AppStore.util.UtilFun;

@SuppressLint("ResourceAsColor")
public class ListAdapter extends BaseAdapter {
	
	HashMap<Integer,View> lmap = new HashMap<Integer,View>();
	private View[] views = new View[36];
	private int mIconSize;
	private Context mContext;
	private List<ItemData> itemList;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	private ListView listView;
	private String className;
	private OnLoadButtonListener monLoadButtonListener;
	private UtilFun util = null;
	private FileOpt opt;
	
	private static String TEXT_INSTALL = "安  装";
	private static String TEXT_OPEN = "打  开";
	
	private List<dLoadButtonTextofRadio>dlButtontextlist;

	public ListAdapter(Context context, List<ItemData> itemList,
			ListView listView, String className) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.itemList = itemList;
		this.listView = listView;
		this.className = className;
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
		opt = new FileOpt();
		mIconSize = (int) mContext.getResources().getDimension(
				R.dimen.icon_size);
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
				map.put("mic", itemList.get(i).getMcid());
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
				map.put("mic", itemList.get(i).getMcid());
				mData.add(map);
				dLoadButtonTextofRadio dl = new dLoadButtonTextofRadio();
				dl.setRadio("0");
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
		int mcid = indexData.getMcid();
		String filename = AppStoreApplication.getInstance().getFilePath(mcid)+
				AppStoreApplication.getInstance().getFileName(
						indexData.getFilename());
		int star = indexData.getStar();
		setStar(holder, star);

		holder.textFileSizeView.setText(mData.get(position).get("filesize")
				.toString());
		holder.uri = filename;

		holder.name.setText(mData.get(position).get("name").toString());
		holder.iconView.setDefaultImage(R.drawable.loading_imageview);
		
		// 测试图片分辨率
		//testSetPicture(positionn, holder);
				
		int mic = Integer.parseInt(mData.get(position).get("mic")
				.toString());
		if (mic == 5) {
			holder.iconView.setImageUrl3(mData.get(position)
					.get("iconView").toString(), position, listView);
		} else {
			holder.iconView.setImageUrl(mData.get(position).get("iconView")
					.toString(), position, listView);
		}
		
		if(position!=2)
			holder.iconView.setImageUrl("http://img.r1.market.hiapk.com/data/upload/2014/07_16/15/72_72_201407161548386365.png");
		else
			holder.iconView.setImageUrl("http://img.r1.market.hiapk.com/data/upload/2014/12_24/13/72_72_201412241343308502.png");

		//testSetPicture(position, holder);
		setDownloadButtonByflag(holder, positionn, indexData);
				
		holder.imageDownloadView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				System.out.println("click button = " + positionn);				
				final Button dlbutton = (Button) v;				
					int mcid = indexData.getMcid();									
					String filename =AppStoreApplication.getInstance().getFilePath(mcid)+AppStoreApplication.getInstance().getFileName(indexData.getFilename());
					if (dlbutton.getText().equals(TEXT_INSTALL)) {
						
						setdownloadButtonBackground(dlbutton, "0%", R.drawable.loading_button);
						
						String contextstring = mContext.toString();
						FileDownloadJob data = util.DataChange(indexData,
								contextstring.substring(
										contextstring.lastIndexOf(".") + 1,
										contextstring.indexOf("@")));
						if (data != null) {							
							NoticData noticData = new NoticData();
							noticData.setFileDownloadJob(data);
							AppStoreApplication.getInstance().getDownloadLink().addNode(noticData);
						}
						
					} else if (dlbutton.getText().equals(TEXT_OPEN)) {
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
			}
		});
								
	}
	
	public void updateSingleRow(int position,String radio,int status){
		
		Log.e("ListAdapter", "updateSingleRow");
	
			//View view = listView.getChildAt(position);	
			Log.e("ListAdapter",dlButtontextlist.get(position).getRadio() );
			dlButtontextlist.get(position).setRadio(radio);
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
			holder.iconView.setDefaultImage(R.drawable.ucliulanqi);
			break;
		case 2:
			holder.iconView.setDefaultImage(R.drawable.yidongbangong);
			break;
		case 3:
			holder.iconView.setDefaultImage(R.drawable.appicon);
			break;
		case 4:
			holder.iconView.setDefaultImage(R.drawable.qq2011);
			break;
		case 5:
			holder.iconView.setDefaultImage(R.drawable.qq2012);
			break;

		default:
			holder.iconView.setDefaultImage(R.drawable.loading_imageview);
			break;
		}

	}
	
	private void setDownloadButtonByflag(ViewHolder holder,int position,final ItemData indexData){
		String getText = (String) holder.imageDownloadView.getText();
		switch (indexData.getButtonFileflag()) {
		//需要初始化的下载任务
		case 0:
			AppStoreApplication.getInstance().getCurrentDownloadJobManager().completeCurrentDownLoadInfo(indexData);
            break;
		
		//未安装
		case 1:
			setdownloadButtonBackground(holder.imageDownloadView, TEXT_INSTALL, R.drawable.load_button);
			break;
		//安装好，可打开
		case 2:		
			setdownloadButtonBackground(holder.imageDownloadView, TEXT_OPEN, R.drawable.open_button);
			AppStoreApplication.getInstance().getCurrentDownloadJobManager().removeDownloadJob(util.getFileName(indexData.getFilename()));
			break;
		//下载中
		case 3:	
			setdownloadButtonBackground(holder.imageDownloadView, dlButtontextlist.get(position).getRadio()+"%", R.drawable.loading_button);	
			break;
		//安装失败
		case 4:
			setdownloadButtonBackground(holder.imageDownloadView, TEXT_INSTALL, R.drawable.load_button);
			
			//将该app下载信息从当前下载列表中移除
			AppStoreApplication.getInstance().getCurrentDownloadJobManager().removeDownloadJob(util.getFileName(indexData.getFilename()));
			break;
		//app下载过程中网络请求超时或者网络突然断开
		case 5:
			//当网络连接之后重新下载该app，但是下载百分比不变，就把它放在下载队列中，重新下载
			//判断当前下载百分比，如果当前百分比如果大于获取到的百分比，那么不变，否则改变
			
			if(getText.endsWith("%")){
				int charposition = getText.indexOf('%')-1;
				String currentRadioStr = getText.substring(0, charposition);
				
				int currentradio = Integer.valueOf(currentRadioStr);
				int getradio = Integer.valueOf(dlButtontextlist.get(position).getRadio());
				
				if(currentradio<getradio||currentradio==getradio){
					setdownloadButtonBackground(holder.imageDownloadView, dlButtontextlist.get(position).getRadio()+"%", R.drawable.loading_button);
				}
			}	
			break;
		default:
			break;
		}
	}
	
	private class dLoadButtonTextofRadio{
		String radio;

		public String getRadio() {
			synchronized (this) {
				return radio;
			}
			
		}

		public void setRadio(String radio) {
			synchronized (this) {
				this.radio = radio;
			}
			
		}
	}
	private void setdownloadButtonBackground(Button dlbutton,String text,int resource){
		dlbutton.setText(text);
		dlbutton.setBackgroundResource(resource);

	}

}
