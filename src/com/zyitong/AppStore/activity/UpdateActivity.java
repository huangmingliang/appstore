package com.zyitong.AppStore.activity;



import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.R;


public class UpdateActivity extends Activity {
	private boolean touched = false;
	private Thread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
		AppStoreApplication.getInstance().clearCache();
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				AppStoreApplication.getInstance()
						.getCurrentDownloadJobManager().initDownloadjob();
				try {
					Thread.sleep(2 * 1000);
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
