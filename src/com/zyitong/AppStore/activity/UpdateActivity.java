package com.zyitong.AppStore.activity;

import java.util.Timer;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.R;
import com.zyitong.AppStore.tools.UtilFun;

public class UpdateActivity extends Activity {
	private long startTime;
	private boolean touched = false;
	private Timer timer;
	private UtilFun utilFun;
	private Thread thread;

	private static String WeiBoRoot = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/AppStore/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.update);
		init();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
	}

	private void init() {
		ImageView mImageView = (ImageView) findViewById(R.id.animation_iv);
		mImageView.setBackgroundResource(R.drawable.start_screen);
		utilFun = new UtilFun(UpdateActivity.this);
		AppStoreApplication.getInstance().clearCache();
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				AppStoreApplication.getInstance()
						.getCurrentDownloadJobManager().initDownloadjob();
				try {
					thread.sleep(2 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!touched)
					entryMain();

			}
		});
		thread.start();

	}

	/**  
     */
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touched = true;
			entryMain();
		}
		return true;
	}

	private void entryMain() {
		MainActivity.launch(UpdateActivity.this, new Bundle());
		finish();
		overridePendingTransition(R.layout.apvalue, R.layout.apvalue);
	}

}
