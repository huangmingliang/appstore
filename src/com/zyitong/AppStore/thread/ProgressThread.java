package com.zyitong.AppStore.thread;

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

import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zyitong.AppStore.WeiBoApplication;
import com.zyitong.AppStore.common.FileDownloadJob;
import com.zyitong.AppStore.common.FileOpt;
import com.zyitong.AppStore.common.NoticData;
import com.zyitong.AppStore.util.UtilFun;

public class ProgressThread extends Thread {

	private static final int REQUEST_TIMEROUT = 6*1000;//����ʱ
	private static final int SO_TIMEROUT = 10*1000;
	private int blockSize, downloadSizeMore;
	private int threadNum = 5;
	private String urlStr, filename;
	private Message msg = null;
	private long prevsize = 0, downloadedSize = 0, _progress = 0;
	private Handler handler = null;
	private FileDownloadJob dldata;
	private int fileSize = 0;
	private FileOpt opt = new FileOpt();
	private Context context;
	private UtilFun util;

	public ProgressThread(NoticData noticData, int threadNum, Context context) {
		this.context = context;
		dldata = noticData.getFileDownloadJob();
		handler = noticData.getHandler();
		this.urlStr = dldata.getUrl();
		this.threadNum = threadNum;
		this.filename = dldata.getFilename();
		dldata.setStatus(1);
		dldata.setRun(true);
		util = new UtilFun();

	}

	@Override
	public void run() {
		FileDownloadThread[] fds = new FileDownloadThread[threadNum];
		WeiBoApplication.getInstance().getDownloadLink().getSize();
		try {
			Log.d("ProgressThread", "ProgressThread is Runing");
			if (opt.exists(filename))
				opt.deleteFile(filename);
			URL url = new URL(urlStr);
			URLConnection conn = null;
			boolean finished = false;
			try {
				conn = url.openConnection();
				// ��ֹ����-1
				conn.setConnectTimeout(REQUEST_TIMEROUT);
				InputStream in = conn.getInputStream();
				// ��ȡ�����ļ����ܴ�С
				fileSize = conn.getContentLength();
				// ����ÿ���߳�Ҫ���ص�������
				blockSize = fileSize / threadNum;
				// ���������ٷֱȼ������
				downloadSizeMore = (fileSize % threadNum);
				File file = new File(filename);
				for (int i = 0; i < threadNum; i++) {
					// �����̣߳��ֱ������Լ���Ҫ���صĲ���
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
				msg = handler.obtainMessage();
				msg.arg1 = -1;
				msg.sendToTarget();
				dldata.setStatus(4);
				dldata.setRun(false);
				finished = true;
				System.out.println("connectTimeout");
				// TODO: handle exception
			}
			
			
			
			
			while (!finished) {
				try {
					downloadedSize = 0;
					// �Ȱ������������㶨
					// downloadedSize = downloadSizeMore;
					finished = true;
					for (int i = 0; i < fds.length; i++) {
						downloadedSize += fds[i].getDownloadSize();
						if (!fds[i].isFinished()) {
							finished = false;
						}
					}

					Log.e("============","count:" + downloadedSize);
					Log.e("============","%:"+downloadedSize * 100 / fileSize );
					if (((downloadedSize - prevsize) * 100 / fileSize >= threadNum)
							|| (downloadedSize == fileSize && fileSize > 0)) {
						_progress = (downloadedSize * 100) / fileSize;
					    System.out.println("ProgressThread downloadedSize = "+downloadedSize);
						handler.sendEmptyMessage(0);
						msg = handler.obtainMessage();
						msg.arg1 = (int)_progress;
						if(_progress == 100)
						{
							Thread.sleep(1000);
							String result = util.install(filename);
							Log.e("ProgressThread install result = ",result);
						}
					
						Log.d("ProgressThread", "msg=" + _progress + "\t size="
								+ downloadedSize);
						msg.sendToTarget();
						prevsize = downloadedSize;
						
					}
					// �߳���ͣһ��
					if (!dldata.isRun()) {
						for (int i = 0; i < fds.length; i++) {
							fds[i].setFinished(true);
						}
						finished = true;
						dldata.setStatus(4);
						dldata.setRun(false);
						// manager.cancel(dldata.getNOTIFICATION_ID());
						msg = handler.obtainMessage();
						msg.arg1 = -1;
						msg.sendToTarget();
					}
					sleep(300);
				} catch (Exception e) {
					// TODO: handle exception
					msg = handler.obtainMessage();
					msg.arg1 = -1;
					msg.sendToTarget();
					dldata.setStatus(4);
					dldata.setRun(false);
				} 
				
			}
		} catch (ConnectTimeoutException e) {
			// e.printStackTrace();
			msg = handler.obtainMessage();
			msg.arg1 = -1;
			msg.sendToTarget();
			dldata.setStatus(4);
			dldata.setRun(false);
		}catch (Exception e){
			msg = handler.obtainMessage();
			msg.arg1 = -1;
			msg.sendToTarget();
			dldata.setStatus(4);
			dldata.setRun(false);
		}

	}

	private class FileDownloadThread extends Thread{  
	    private static final int BUFFER_SIZE=1024;  
	    private URL url;  
	    private File file;  
	    private int startPosition;  
	    private int endPosition;  
	    private int curPosition;  
	    //��ʶ��ǰ�߳��Ƿ��������   
	    private boolean finished=false;  
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
	           
	            //���õ�ǰ�߳����ص���ֹ��   
	            con.setRequestProperty("Range", "bytes=" + startPosition + "-" + endPosition);  

	            //ʹ��java�е�RandomAccessFile ���ļ����������д����   
	            fos = new RandomAccessFile(file, "rw");  
	            //����д�ļ�����ʼλ��   
	            fos.seek(startPosition);  
	            bis = new BufferedInputStream(con.getInputStream());    
	            //��ʼѭ����������ʽ��д�ļ�   
	            while (curPosition < endPosition && !finished) {  
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
	            //���������Ϊtrue   
	            this.finished = true;  
	            bis.close();  
	            fos.close();  
	        } catch (IOException e) {  
	            e.printStackTrace();
	            msg = handler.obtainMessage();
				msg.arg1 = -1;
				msg.sendToTarget();
				dldata.setStatus(4);
				dldata.setRun(false);
	        }  
	    }  
	   public void setFinished(boolean finished)
	   {
		   this.finished = finished;
	   }
	    public boolean isFinished(){  
	        return finished;  
	    }  
	   
	    public int getDownloadSize() {  
	        return downloadSize;  
	    }  
	}  

}