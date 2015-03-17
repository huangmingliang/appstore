package com.zyitong.AppStore.activity;

import java.io.InputStream;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.welcome_activity);
		ImageView mImageView = (ImageView) findViewById(R.id.animation_iv);
		mImageView.setImageBitmap(readBitMap(this, R.drawable.start_screen));
		init();
	}

	@Override
	protected void onDestroy() {
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
					@Override
					public void onNext(AppListBean bean) {
						if (null != bean) {
							List<ItemData> itemDataList = AppStoreApplication.getInstance().itemData;
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
								utilFun.setAppState(item);
							}
							
							Bundle bundle = new Bundle();
							MainActivity.startActivity(WelcomeActivity.this, bundle);
							finish();
							overridePendingTransition(R.layout.apvalue, R.layout.apvalue);
						}
					}

					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						AppLogger.e("ERROR===========" + e);
						
						Bundle bundle = new Bundle();
						MainActivity.startActivity(WelcomeActivity.this, bundle);
						finish();
						overridePendingTransition(R.layout.apvalue, R.layout.apvalue);
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
