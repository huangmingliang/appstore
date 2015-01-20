package com.zyitong.AppStore.Dialog;

import com.zyitong.AppStore.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.zyitong.AppStore.WeiBoApplication;
import com.zyitong.AppStore.activity.MainActivity;
import com.zyitong.AppStore.activity.SearchActivity;
import com.zyitong.AppStore.common.FileDownloadJob;
import com.zyitong.AppStore.common.FileOpt;
import com.zyitong.AppStore.common.ItemData;
import com.zyitong.AppStore.common.NoticData;
import com.zyitong.AppStore.util.UtilFun;


public class DownloadDialog extends Dialog {
	private Activity context;
	private Button okButton;
	private Button cancelButton;
	private String title;
	private String prompt;
	private String className="";
	private ItemData itemData;
	private UtilFun util;
	private FileOpt opt;
	

	public DownloadDialog(Activity context,ItemData itemdata,UtilFun util,String title,String prompt,String className)
	{
		super(context);
		this.title = title;
		this.prompt = prompt;
		this.className = className;
		this.itemData = itemdata;
		this.util = util;
		opt = new FileOpt();
		init(context);
	}
	private void init(Activity context){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.downloaddialog);
		this.context = context;

		TextView textView1 = (TextView)findViewById(R.id.textView1);
		TextView textView2 = (TextView)findViewById(R.id.textView2);
		textView1.setText(title);
		textView2.setText(prompt);
    	okButton = (Button)findViewById(R.id.okButton);
    	cancelButton = (Button)findViewById(R.id.cancelButton);
    	cancelButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				DownloadDialog.this.dismiss();
			}

		});

		okButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				DownloadDialog.this.dismiss();
				FileDownloadJob data = util.DataChange(itemData,"MainActivity");
				if(data !=null)
				{
					
					MyHandler handler = new MyHandler(data);
					NoticData noticData = new NoticData();
					noticData.setFileDownloadJob(data);
					noticData.setHandler(handler);
					WeiBoApplication.getInstance().getDownloadLink().addNode(noticData);
				}
				//send();
			}

		});
	
		
	}
	
	private class MyHandler extends Handler {  
    	private Notification notification;
    	private FileDownloadJob dldata;
    	public MyHandler(FileDownloadJob dldata)
    	{
    		this.dldata = dldata;
    		//notification = this.dldata.getNotification();
    	}
        @Override  
        public void handleMessage(Message msg) { 
        	
        	if(msg.arg1>0 && msg.arg1<=100)
        	{
        		Log.d("MainActivity", "values="+msg.arg1);
        		notification.contentView.setProgressBar(R.id.progressBar1, 100, msg.arg1, false);  
        		notification.contentView.setTextViewText(R.id.textView1,  dldata.getName()+"  ½ø¶È"+msg.arg1 + "%");
        		//WeiBoApplication.getInstance().getManager().notify(dldata.getId(), notification);
        		System.out.println("MainActivity download progress = "+msg.arg1);
        	}
        	
            if (msg.arg1 == 100) {  
            	// WeiBoApplication.getInstance().getManager().cancel(dldata.getId()); 
                 util.DowloadComplete(dldata);
            }else if(msg.arg1 == -1) { 
                  opt.deleteFile(dldata.getFilename());
                  WeiBoApplication.getInstance().getDownloadLink().delNode(dldata.getId());
                  //WeiBoApplication.getInstance().getManager().cancel(dldata.getId()); 
                  
            }
         
        }  
    };
	private void send()
	{
		if(className.equalsIgnoreCase("MainActivity"))
			((MainActivity)context).dialogProcc();
		
			
					
		
	}
	
	

}