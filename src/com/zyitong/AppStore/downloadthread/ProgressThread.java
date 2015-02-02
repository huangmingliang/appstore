package com.zyitong.AppStore.downloadthread;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.CurrentDownloadJob;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.bean.NoticData;
import com.zyitong.AppStore.tools.FileOpt;
import com.zyitong.AppStore.tools.UtilFun;

public class ProgressThread extends Thread {

	private static final int REQUEST_TIMEROUT = 30*1000;//请求超时
	private static final int SO_TIMEROUT = 10*1000;
	private int blockSize, downloadSizeMore;
	private int threadNum = 5, _progress = 0;
	private String urlStr, filename;
	//private Message msg = null;
	private long prevsize = 0, downloadedSize = 0;
	//private Handler handler = null;
	private FileDownloadJob dldata;
	private int fileSize = 0;
	private FileOpt opt = new FileOpt();
	private Context context;
	private UtilFun util;
	private String appName;
	private NoticData noticData;
	boolean finished = false;
	public ProgressThread(NoticData noticData, int threadNum, Context context) {
		this.context = context;
		this.noticData = noticData;
		dldata = noticData.getFileDownloadJob();
		
		this.urlStr = dldata.getUrl();
		this.threadNum = threadNum;
		this.filename = dldata.getFilename();
		
		dldata.setStatus(1);
		dldata.setRun(true);
		util = new UtilFun();
		this.appName = util.getFileName(filename);

	}

	@Override
	public void run() {
		FileDownloadThread[] fds = new FileDownloadThread[threadNum];
		AppStoreApplication.getInstance().getDownloadLink().getSize();
		try {
			Log.d("ProgressThread", "ProgressThread is Runing");
			
			if(util.getUninatllApkInfo(context, filename)){
				opt.deleteFile(filename);
			}
				
			URL url = new URL(urlStr);
			URLConnection conn = null;
			
			try {
				conn = url.openConnection();
				// 防止返回-1
				conn.setConnectTimeout(REQUEST_TIMEROUT);
				InputStream in = conn.getInputStream();
				// 获取下载文件的总大小
				fileSize = conn.getContentLength();
				// 计算每个线程要下载的数据量
				blockSize = fileSize / threadNum;
				// 解决整除后百分比计算误差
				downloadSizeMore = (fileSize % threadNum);
				File file = new File(filename);
				for (int i = 0; i < threadNum; i++) {
					// 启动线程，分别下载自己需要下载的部分
					int endpos = (i + 1) * blockSize - 1;
					if (i == (threadNum - 1)) {
						endpos += downloadSizeMore;
					}
					FileDownloadThread fdt = new FileDownloadThread(url, file, i
							* blockSize, endpos);
					fdt.start();
					fds[i] = fdt;
				}
				
			} catch (ConnectTimeoutException e) {
				/*msg = handler.obtainMessage();
				msg.arg1 = -1;
				msg.sendToTarget();*/
				util.addCurrentDownloadJob(appName, _progress, ItemData.APP_NETWORKEX,-1,noticData);
				util.DowloadComplete(dldata);
				dldata.setStatus(4);
				dldata.setRun(false);
				finished = true;
				Log.e("ProgressThread =1=", "网络请求延时");
				System.out.println("ProgressThread =1=   =====    网络请求延时");
				// TODO: handle exception
			}catch(Exception e){
				util.addCurrentDownloadJob(appName, _progress, ItemData.APP_NETWORKEX,-1,noticData);
				util.DowloadComplete(dldata);
				dldata.setStatus(4);
				dldata.setRun(false);
				finished = true;
				
				Log.e("ProgressThread =2=", "网络中断");
				System.out.println("ProgressThread =2=   =====    网络中断");
			}
			
			
			
			
			while (!finished) {
				try {
					downloadedSize = 0;
					// 先把整除的余数搞定
					// downloadedSize = downloadSizeMore;
					finished = true;
					for (int i = 0; i < fds.length; i++) {
						downloadedSize += fds[i].getDownloadSize();
						if (!fds[i].isFinished()) {
							finished = false;
						}
					}

					//Log.e("============","count:" + downloadedSize);
					Log.e("============","%:"+downloadedSize * 100 / fileSize );
					
					if (((downloadedSize - prevsize) * 100 / fileSize >= threadNum)
							|| (downloadedSize == fileSize && fileSize > 0)) {
						_progress = (int) ((downloadedSize * 100) / fileSize);
					    System.out.println("ProgressThread downloadedSize = "+downloadedSize);
						/*handler.sendEmptyMessage(0);
						msg = handler.obtainMessage();
						msg.arg1 = (int)_progress;
						msg.sendToTarget();*/
						
					
						Log.d("ProgressThread", "msg=" + _progress + "\t size="
								+ downloadedSize);
												
						prevsize = downloadedSize;
						
					}
				
					if (!dldata.isRun()) {
						for (int i = 0; i < fds.length; i++) {
							fds[i].setFinished(true);
						}
						util.addCurrentDownloadJob(appName, _progress, ItemData.APP_FAIL,-1,noticData);
						util.DowloadComplete(dldata);
						finished = true;
						dldata.setStatus(4);
						dldata.setRun(false);
						
						// manager.cancel(dldata.getNOTIFICATION_ID());
						/*msg = handler.obtainMessage();
						msg.arg1 = -1;
						msg.sendToTarget();*/
					}
					//线程暂停0.3秒
					sleep(300);
					if(_progress!=100){
						if(!finished)
						{
						Log.e("ProgressThread","创建currentDownloadJob" );
						
						util.addCurrentDownloadJob(appName, _progress, ItemData.APP_LOADING,fileSize,noticData);
						}
						
					}
					if(_progress > 90){
						if(util.getUninatllApkInfo(context, filename))
							_progress = 100;
							
					}
					if(_progress==100){
						
						//静默安装
						//util.DowloadComplete(dldata);
						
						String result = util.install(filename);
						if(result.equals("Success")){
							util.addCurrentDownloadJob(appName, _progress, ItemData.APP_INSTALED,-1,noticData);
						}else{
							util.addCurrentDownloadJob(appName, _progress, ItemData.APP_FAIL,-1,noticData);
							
						}
						util.DowloadComplete(dldata);
						Log.e("ProgressThread install result = ",result);
						//将该文件从下载列表中移除
						//AppStoreApplication.getInstance().getCurrentDownloadJobList().removeDownloadJob(appName);
						
					}
										
				} catch (Exception e) {
					// TODO: handle exception
					/*msg = handler.obtainMessage();
					msg.arg1 = -1;
					msg.sendToTarget();*/
					Log.e("ProgressThread =3=", "网络中断");
					util.addCurrentDownloadJob(appName, _progress, ItemData.APP_NETWORKEX,-1,noticData);
					util.DowloadComplete(dldata);
					dldata.setStatus(4);
					dldata.setRun(false);
					System.out.println("ProgressThread =3=   =====    网络中断");
				} 
				
			}
		} catch (Exception e){
		    
			/*msg = handler.obtainMessage();
			msg.arg1 = -1;
			msg.sendToTarget();*/
			util.addCurrentDownloadJob(appName, _progress, ItemData.APP_NETWORKEX,-1,noticData);
			util.DowloadComplete(dldata);
			dldata.setStatus(4);
			dldata.setRun(false);
			Log.e("ProgressThread =4=", "网络中断");
			System.out.println("ProgressThread =4=   =====    网络中断");
		}

	}

	private class FileDownloadThread extends Thread{  
	    private static final int BUFFER_SIZE=1024;  
	    private URL url;  
	    private File file;  
	    private int startPosition;  
	    private int endPosition;  
	    private int curPosition;  
	    //标识当前线程是否下载完成   
	    private boolean innerfinished=false;  
	    private int downloadSize=0;  

	    public FileDownloadThread(URL url,File file,int startPosition,int endPosition){  
	        this.url=url;  
	        this.file=file;  
	        this.startPosition=startPosition;  
	        this.curPosition=startPosition;  
	        this.endPosition=endPosition;  
	    }  
	    @Override  
	    public void run() {  
	        BufferedInputStream bis = null;  
	        RandomAccessFile fos = null;                                                 
	        byte[] buf = new byte[BUFFER_SIZE];  
	        URLConnection con = null;  
	        try {  
	            con = url.openConnection();
	            con.setConnectTimeout(REQUEST_TIMEROUT);
	            con.setAllowUserInteraction(true);
	           
	            //设置当前线程下载的起止点   
	            con.setRequestProperty("Range", "bytes=" + startPosition + "-" + endPosition);  

	            //使用java中的RandomAccessFile 对文件进行随机读写操作   
	            fos = new RandomAccessFile(file, "rw");  
	            //设置写文件的起始位置   
	            fos.seek(startPosition);  
	            bis = new BufferedInputStream(con.getInputStream());    
	            //开始循环以流的形式读写文件   
	            while (curPosition < endPosition && !innerfinished) {  
	                int len = bis.read(buf, 0, BUFFER_SIZE);                  
	                if (len == -1) {  
	                    break;  
	                }  
	                fos.write(buf, 0, len);  
	                curPosition = curPosition + len;  
	                if (curPosition > endPosition) {  
	                    downloadSize+=len - (curPosition - endPosition) + 1;  
	                } else {  
	                    downloadSize+=len;  
	                }  
	            }  
	            //下载完成设为true   
	            this.innerfinished = true;  
	            bis.close();  
	            fos.close();  
	        } catch (IOException e) {  
	            e.printStackTrace();
	           /* msg = handler.obtainMessage();
				msg.arg1 = -1;
				msg.sendToTarget();*/
	            util.addCurrentDownloadJob(appName, _progress, ItemData.APP_NETWORKEX,-1,noticData);        
	            util.DowloadComplete(dldata);
				dldata.setStatus(4);
				dldata.setRun(false);
				finished = true;
				Log.e("ProgressThread =5=", "网络中断");
				System.out.println("ProgressThread =5=   =====    网络中断");
	        }  
	    }  
	   public void setFinished(boolean finished)
	   {
		   this.innerfinished = finished;
	   }
	    public boolean isFinished(){  
	        return innerfinished;  
	    }  
	   
	    public int getDownloadSize() {  
	        return downloadSize;  
	    }  
	}  

}