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

import com.zyitong.AppStore.R;
import com.zyitong.AppStore.WeiBoApplication;
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
	private ViewHolder holder;

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
		for (int i = 0; i < itemList.size(); i++) {
			if (itemList.get(i).getName() != null) {
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

	}

	public void addData(List<ItemData> itemDataList) {
		this.itemList = itemDataList;
		for (int i = mData.size(); i < itemDataList.size(); i++) {
			if (itemList.get(i).getName() != null) {
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
		// Log.e("ListAdapter","getView()");
		// TODO Auto-generated method stub
		if(views[position]!=null){
			return views[position];
		}else{
			
			final ItemData indexData = itemList.get(position);
			int mcid = indexData.getMcid();
			String filename = WeiBoApplication.getInstance().getFilePath(mcid)
					+ WeiBoApplication.getInstance().getFileName(
							indexData.getFilename());
			final int positionn = position;
			
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_row, null);

			holder.iconView = (RemoteImageView) convertView
					.findViewById(R.id.iconView);
			LayoutParams lp = holder.iconView.getLayoutParams();
			lp.height = 65;
			lp.width = 65;

			holder.iconView.setLayoutParams(lp);

			holder.name = (TextView) convertView.findViewById(R.id.name);
			/*
			 * holder.loadProgressBar = (ProgressBar) convertView
			 * .findViewById(R.id.progressBar1);
			 */

			holder.textFileSizeView = (TextView) convertView
					.findViewById(R.id.textFileSizeView);
			// holder.textDownloadNumView = (TextView) convertView
			// .findViewById(R.id.textDownloadNumView);
			holder.imageDownloadView = (Button) convertView
					.findViewById(R.id.imageDownloadView);

			holder.imageView1 = (View) convertView.findViewById(R.id.starView1);
			holder.imageView2 = (View) convertView.findViewById(R.id.starView2);
			holder.imageView3 = (View) convertView.findViewById(R.id.starView3);
			holder.imageView4 = (View) convertView.findViewById(R.id.starView4);
			holder.imageView5 = (View) convertView.findViewById(R.id.starView5);
			
			int star = Integer.parseInt(mData.get(position).get("star")
					.toString());
			setStar(holder, star);

			holder.textFileSizeView.setText(mData.get(position).get("filesize")
					.toString());
			holder.uri = filename;

			holder.name.setText(mData.get(position).get("name").toString());

			// setRatingBarStar(holder, star);

			int mic = Integer.parseInt(mData.get(position).get("mic")
					.toString());
			if (mic == 5) {
				holder.iconView.setImageUrl3(mData.get(position)
						.get("iconView").toString(), position, listView);
			} else {
				holder.iconView.setImageUrl(mData.get(position).get("iconView")
						.toString(), position, listView);
			}

			// 测试图片分辨率
			testSetPicture(position, holder);

			switch (indexData.getButtonFileflag()) {
			/*
			 * case 0: holder.imageDownloadView.setText("下  载"); break;
			 */
			case 1:
				holder.imageDownloadView.setText("安  装");
				holder.imageDownloadView
						.setBackgroundResource(R.drawable.load_button);
				break;
			case 2:
				holder.imageDownloadView.setText("打  开");
				holder.imageDownloadView
						.setBackgroundResource(R.drawable.open_button);
				break;

			default:
				break;
			}

			holder.imageDownloadView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					System.out.println("click button = " + positionn);

					final ListView lv = (ListView) (v.getParent().getParent()
							.getParent());
					final Button dlbutton = (Button) v;
					if (lv != null) {
						int mcid = indexData.getMcid();
						String filename = WeiBoApplication.getInstance()
								.getFilePath(mcid)
								+ WeiBoApplication.getInstance().getFileName(
										indexData.getFilename());

						if (dlbutton.getText().equals("安  装")) {
							if (opt.exists(filename))
								opt.deleteFile(filename);
							int position = lv.getPositionForView(v);
							dlbutton.setText("0%");
							dlbutton.setBackgroundResource(R.drawable.loading_button);
							String contextstring = mContext.toString();
							FileDownloadJob data = util.DataChange(indexData,
									contextstring.substring(
											contextstring.lastIndexOf(".") + 1,
											contextstring.indexOf("@")));
							if (data != null) {

								MyHandler handler = new MyHandler(data,
										dlbutton);
								NoticData noticData = new NoticData();
								noticData.setFileDownloadJob(data);
								noticData.setHandler(handler);
								WeiBoApplication.getInstance()
										.getDownloadLink().addNode(noticData);
							}
							/*
							 * System.out.println("ListAdapter setup filename = "
							 * + filename);
							 * 
							 * Intent intent = new
							 * Intent(android.content.Intent.ACTION_VIEW);
							 * intent.setDataAndType(Uri.fromFile(new
							 * File(filename)),
							 * "application/vnd.android.package-archive");
							 * mContext.startActivity(intent); try {
							 * if(util.isAppInstalled(filename,mContext))
							 * dlbutton.setText("打  开"); } catch
							 * (NameNotFoundException e) { // TODO
							 * Auto-generated catch block e.printStackTrace(); }
							 */
						} else /*
								 * if (dlbutton.getText().equals("下  载")) {
								 * if(opt.exists(filename))
								 * opt.deleteFile(filename);
								 * dlbutton.setText("安装中..."); int position =
								 * lv.getPositionForView(v);
								 * System.out.println("ListAdapter download app : "
								 * + indexData.getFilename()); //
								 * clickProcc(position); String contextstring =
								 * mContext.toString();
								 * 
								 * FileDownloadJob data =
								 * util.DataChange(indexData,
								 * contextstring.substring(
								 * contextstring.lastIndexOf(".") + 1,
								 * contextstring.indexOf("@")));
								 * 
								 * if (data != null) {
								 * 
								 * MyHandler handler = new MyHandler(data,
								 * position); NoticData noticData = new
								 * NoticData();
								 * noticData.setFileDownloadJob(data);
								 * noticData.setHandler(handler);
								 * WeiBoApplication
								 * .getInstance().getDownloadLink()
								 * .addNode(noticData); } } else
								 */if (dlbutton.getText().equals("打  开")) {
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
				}
			});
			
			convertView.setTag(holder);
			
			views[position] = convertView;
			
		}
		return views[position];
		
		
		
		
	
		

		

		// holder.ratingBar1 = (RatingBar)
		// convertView.findViewById(R.id.ratingBar1);
		// holder.imageView6 = (ImageView)
		// convertView.findViewById(R.id.imageView6);

		// holder.textDownloadNumView.setText(mData.get(position)
		// .get("downloadnum").toString());

	}

	private class MyHandler extends Handler {
		// private Notification notification;
		private FileDownloadJob dldata;
		private int index;
		private Button buttonView;

		public MyHandler(FileDownloadJob dldata, Button buttonView) {
			this.dldata = dldata;
			// notification = this.dldata.getNotification();
			this.index = index;
			this.buttonView = buttonView;
		}

		@Override
		public void handleMessage(Message msg) {

			int progress = msg.arg1;
			// ViewHolder holder = (ViewHolder) view.getTag();

			if (msg.arg1 > 0 && msg.arg1 <= 100) {
				System.out.println("current item = " + index);

				Log.d("ListAdapter", "values=" + msg.arg1);
				/*
				 * notification.contentView.setProgressBar(R.id.progressBar1,
				 * 100, msg.arg1, false);
				 * notification.contentView.setTextViewText(R.id.textView1,
				 * dldata.getName() + "  进度" + msg.arg1 + "%");
				 */
				/*
				 * WeiBoApplication.getInstance().getManager()
				 * .notify(dldata.getId(), notification);
				 */
				// View view = listView.getChildAt(index);

				// holder.imageDownloadView.setText(msg.arg1+"%");
				// holder.imageDownloadView.setBackgroundResource(R.drawable.loading_button);
				buttonView.setText(msg.arg1 + "%");
				buttonView.setBackgroundResource(R.drawable.loading_button);

				// holder.loadProgressBar.setVisibility(View.VISIBLE);
				// holder.loadProgressBar.setProgress(msg.arg1);
				// holder.imageDownloadView.setText("下载中...");
				// System.out.println("ListAdapter handleMessage : "+holder.imageDownloadView.getText());
				// holder.imageDownloadView.setEnabled(false);
				updateView(index, msg.arg1);
			}

			if (msg.arg1 == 100) {
				// WeiBoApplication.getInstance().getManager().cancel(dldata.getId());
				util.DowloadComplete(dldata);

				/*
				 * holder.loadProgressBar.setVisibility(View.GONE);
				 * holder.imageDownloadView.setText("打  开");
				 * holder.imageDownloadView
				 * .setBackgroundResource(R.drawable.open_button);
				 */
				buttonView.setText("打  开");
				buttonView.setBackgroundResource(R.drawable.open_button);

				// util.onClick_install(holder.uri);
				// util.onClick_install(holder.uri);
				// System.out.println("ListAdapter holder.uri : "+holder.uri);
				// System.out.println("ListAdapter handleMessage : "+holder.imageDownloadView.getText());
				// holder.imageDownloadView.setEnabled(true);
				updateView(index, progress);
			} else if (msg.arg1 == -1) {
				opt.deleteFile(dldata.getFilename());
				WeiBoApplication.getInstance().getDownloadLink()
						.delNode(dldata.getId());
				/*
				 * WeiBoApplication.getInstance().getManager()
				 * .cancel(dldata.getId());
				 */
				// Log.e("ListAdapter","咦，你的网络出现问题了");
				Toast.makeText(mContext, "咦，你的网络出现问题了!", Toast.LENGTH_SHORT)
						.show();

				/*
				 * holder.loadProgressBar.setVisibility(View.GONE);
				 * holder.imageDownloadView
				 * .setBackgroundResource(R.drawable.load_button);
				 * holder.imageDownloadView.setText("安  装");
				 */
				buttonView.setText("安  装");
				buttonView.setBackgroundResource(R.drawable.load_button);

			}

		}
	};

	public final class ViewHolder {
		public RemoteImageView iconView;
		public TextView name;
		public ProgressBar loadProgressBar;

		public View imageView1;
		public View imageView2;
		public View imageView3;
		public View imageView4;
		public View imageView5;
		public RatingBar ratingBar1;

		// public TextView textDownloadNumView;
		public TextView textFileSizeView;
		public Button imageDownloadView;
		public String uri;

	}

	private void updateView(int index, int progress) {

	}

	private void setStar(ViewHolder holder, int star) {

		switch (star) {

		case 1:
			holder.imageView1.setBackgroundResource(R.drawable.star01);
			break;
		case 2:
			holder.imageView1.setBackgroundResource(R.drawable.star01);
			holder.imageView2.setBackgroundResource(R.drawable.star01);
			break;
		case 3:
			holder.imageView1.setBackgroundResource(R.drawable.star01);
			holder.imageView2.setBackgroundResource(R.drawable.star01);
			holder.imageView3.setBackgroundResource(R.drawable.star01);
			break;
		case 4:
			holder.imageView1.setBackgroundResource(R.drawable.star01);
			holder.imageView2.setBackgroundResource(R.drawable.star01);
			holder.imageView3.setBackgroundResource(R.drawable.star01);
			holder.imageView4.setBackgroundResource(R.drawable.star01);
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
			//holder.iconView.setDefaultImage(R.drawable.loading_imageview);
			break;
		}

	}

}
