package com.zyitong.AppStore.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.CurrentDownloadJob;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.bean.NoticData;
import com.zyitong.AppStore.tools.UtilFun;

import android.content.Context;
import android.util.Log;

public class CurrentDownloadJobManager {

	private List<CurrentDownloadJob>currentDownJobsList =new ArrayList<CurrentDownloadJob>();
	private Context context;
	private String AppStoreRoot = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/AppStore/soft";
	private UtilFun util = new UtilFun();
	//获取目标app的状态
	public CurrentDownloadJobManager(Context context){
		this.context = context;
	}
	
	public int getStatus(String filename){
		synchronized (this) {
			for(int i = 0;i<currentDownJobsList.size();i++){
				if(currentDownJobsList.get(i).getFilename().equals(filename))
					return currentDownJobsList.get(i).getFilestatus();
				
			}
		return -1;	 
		}
	}
	//获取目标app的下载百分比
	public int getRatio(String filename){
		synchronized (this) {
			for(int i = 0;i<currentDownJobsList.size();i++){
				if(currentDownJobsList.get(i).getFilename().equals(filename))
					return currentDownJobsList.get(i).getRatio();
			}
			return -1;
		}
		
	}
	
	
	//向列表中增加文件
	public void addDownloadJob(CurrentDownloadJob currentDownloadJob){
		//如果文件名存在，只更新百分比和状态
		synchronized (this) {
			boolean isexit = false;
			for(int i = 0;i<currentDownJobsList.size();i++){
			
				if(currentDownJobsList.get(i).getFilename().equals(currentDownloadJob.getFilename()))
				{
					Log.e("CurrentDownloadJobManager", "");
					currentDownJobsList.get(i).setRatio(currentDownloadJob.getRatio());
					currentDownJobsList.get(i).setFilestatus(currentDownloadJob.getFilestatus());
					currentDownJobsList.get(i).setFilelength(currentDownloadJob.getFilelength());
					currentDownJobsList.get(i).setData(currentDownloadJob.getData());
					isexit = true;
				}
			}
			//否则，创建一个新的信息，并且放入列表中
			if(!isexit)
				currentDownJobsList.add(currentDownloadJob);
		}     
	}
	//从列表中移除文件
	public void removeDownloadJob(String filename){
		synchronized (this) {
			for(int i = 0;i<currentDownJobsList.size();i++){			
				if(currentDownJobsList.get(i).getFilename().equals(filename))			
					currentDownJobsList.remove(i);	
			}
		}
		
			
	}
	
	//当前下载任务列表中文件的个数
	public int downloadjobnum(){
		synchronized (this) {
			return currentDownJobsList.size();
		}
		
	}
	//初始化下载任务
	//当updateactivity创建的时候去扫描AppStore/soft/这个目录下的文件
	public void initDownloadjob(){		
		File mfile = new File(AppStoreRoot);
		File files[] = mfile.listFiles();
	
		for(int i = 0; i<files.length; i++){
			if(!util.getUninatllApkInfo(context,files[i].getPath())){
				Log.e("CurrentDownloadJobManager", "有残缺包，加入下载队列");
				String filename = util.getFileName(files[i].getPath());	
				Log.e("CurrentDownloadManager filename = ", filename);
				//long filelength = files[i].length();
				CurrentDownloadJob currentDownloadJob = new CurrentDownloadJob();
				currentDownloadJob.setFilename(filename);
				//currentDownloadJob.setFilelength(filelength);
				currentDownloadJob.setFilestatus(ItemData.APP_READ);
				
				currentDownloadJob.setRatio(-2);
				addDownloadJob(currentDownloadJob);
			}			
		}
	}
	
	//获取当前下载队列中是否有需要下载的对象并且将该对象放入下载器中;
	//此时找到因为异常中断下载的app
	public void addJobToDownloadLink(){
		synchronized (this) {
			int length = downloadjobnum();
			for(int i = 0;i<length;i++){
				if(currentDownJobsList.get(i).getFilestatus() == 5){
					Log.e("CurrentDownloadManager","在下载队列中查找到了因为异常中断下载的app" );
					NoticData notic = currentDownJobsList.get(i).getData();
					int id = notic.getFileDownloadJob().getId();
					if (notic !=null && !AppStoreApplication.getInstance().getDownloadLink()
							.findNode(id)){
						Log.e("CurrentDownloadManager","将因为异常中断下载的app重新放入下载队列下载" );
						notic.getFileDownloadJob().setStatus(0);
						AppStoreApplication.getInstance().getDownloadLink().addNode(notic);
						//AppStoreApplication.getInstance().getCurrentDownloadJobManager().removeDownloadJob(util.getFileName(notic.getFileDownloadJob().getFilename()));
					}						
				}
			}
		}
		
	}
	
	//根据Itemdata信息将currentdownload信息补充完整
	public void completeCurrentDownLoadInfo(ItemData itemData){
		int length = downloadjobnum();
		for(int i = 0 ; i < length ;i++){
			String appname = util.getFileName(itemData.getFilename());
			if(currentDownJobsList.get(i).getFilename().equals(appname)){
				//long applength = itemData.getFileSize();
				//long appcurrentlength = currentDownJobsList.get(i).getFilelength();
				//int ratio = (int) (appcurrentlength*100/applength);
				currentDownJobsList.get(i).setRatio(0);
				currentDownJobsList.get(i).setFilestatus(5);
				FileDownloadJob data = util.DataChange(itemData);
				NoticData noticData = new NoticData();
				noticData.setFileDownloadJob(data);		
				currentDownJobsList.get(i).setData(noticData);
			}
		}			
	}	
}
