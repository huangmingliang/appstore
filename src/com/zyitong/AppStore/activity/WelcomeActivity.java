package com.zyitong.AppStore.activity;

import java.io.InputStream;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.R;
import com.zyitong.AppStore.bean.AppListBean;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.dao.AppListDao;
import com.zyitong.AppStore.tools.AppLogger;
import com.zyitong.AppStore.tools.UtilFun;

public class WelcomeActivity extends Activity {
	private UtilFun utilFun;
	private static final int MESS_WHAT = 10;
	private static final int DELAY_TIME = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.welcome_activity);
		ImageView mImageView = (ImageView) findViewById(R.id.animation_iv);
		mImageView.setImageBitmap(readBitMap(this, R.drawable.start_screen));
		init();
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 10){
				Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
				WelcomeActivity.this.startActivity(intent);
				finish();
			}
		}
		
	};

	@Override
	protected void onDestroy() {
		System.gc();
		super.onDestroy();
	}

	private void init() {
		utilFun = new UtilFun(this);
		utilFun.makeAppStoreDir();

		getAppList(0, 8);
	}
	
	private void getAppList(int startPos, int docNum) {

		AppListDao.getInstance().getAppListRX(startPos, docNum)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<AppListBean>() {
					AppListBean getListBean = null;
					@Override
					public void onNext(AppListBean bean) {
						getListBean = bean;
						
					}

					@Override
					public void onCompleted() {
						if (null != getListBean) {
							List<ItemData> itemDataList = AppStoreApplication.getInstance().itemData;
							int resultnumber = Integer.valueOf(getListBean.result.num);
							for (int i = 0; i < resultnumber; i++) {
								ItemData item = new ItemData();
								item.setAppInfoBean(getListBean.result.items.get(i));
								itemDataList.add(item);
								utilFun.setAppState(item);
							}
							
							Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
							WelcomeActivity.this.startActivity(intent);
							finish();
						}
					}

					@Override
					public void onError(Throwable e) {
						AppLogger.e("ERROR===========" + e);
						handler.sendEmptyMessageDelayed(MESS_WHAT, DELAY_TIME);
					}
				});
	}
	
	public  static  Bitmap readBitMap(Context  context, int resId){ 

        BitmapFactory.Options opt = new  BitmapFactory.Options();

        opt.inPreferredConfig =  Bitmap.Config.RGB_565;

        opt.inPurgeable = true;

        opt.inInputShareable = true;

        //  获取资源图片

       InputStream is =  context.getResources().openRawResource(resId);

        return  BitmapFactory.decodeStream(is, null, opt);

        }
}
