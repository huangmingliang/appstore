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
	//��ȡĿ��app��״̬
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
	//��ȡĿ��app�����ذٷֱ�
	public int getRatio(String filename){
		synchronized (this) {
			for(int i = 0;i<currentDownJobsList.size();i++){
				if(currentDownJobsList.get(i).getFilename().equals(filename))
					return currentDownJobsList.get(i).getRatio();
			}
			return -1;
		}
		
	}
	
	
	//���б��������ļ�
	public void addDownloadJob(CurrentDownloadJob currentDownloadJob){
		//����ļ������ڣ�ֻ���°ٷֱȺ�״̬
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
			//���򣬴���һ���µ���Ϣ�����ҷ����б���
			if(!isexit)
				currentDownJobsList.add(currentDownloadJob);
		}     
	}
	//���б����Ƴ��ļ�
	public void removeDownloadJob(String filename){
		synchronized (this) {
			for(int i = 0;i<currentDownJobsList.size();i++){			
				if(currentDownJobsList.get(i).getFilename().equals(filename))			
					currentDownJobsList.remove(i);	
			}
		}
		
			
	}
	
	//��ǰ���������б����ļ��ĸ���
	public int downloadjobnum(){
		synchronized (this) {
			return currentDownJobsList.size();
		}
		
	}
	//��ʼ����������
	//��updateactivity������ʱ��ȥɨ��AppStore/soft/���Ŀ¼�µ��ļ�
	public void initDownloadjob(){		
		File mfile = new File(AppStoreRoot);
		File files[] = mfile.listFiles();
	
		for(int i = 0; i<files.length; i++){
			if(!util.getUninatllApkInfo(context,files[i].getPath())){
				Log.e("CurrentDownloadJobManager", "�в�ȱ�����������ض���");
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
	
	//��ȡ��ǰ���ض������Ƿ�����Ҫ���صĶ����ҽ��ö��������������;
	//��ʱ�ҵ���Ϊ�쳣�ж����ص�app
	public void addJobToDownloadLink(){
		synchronized (this) {
			int length = downloadjobnum();
			for(int i = 0;i<length;i++){
				if(currentDownJobsList.get(i).getFilestatus() == 5){
					Log.e("CurrentDownloadManager","�����ض����в��ҵ�����Ϊ�쳣�ж����ص�app" );
					NoticData notic = currentDownJobsList.get(i).getData();
					int id = notic.getFileDownloadJob().getId();
					if (notic !=null && !AppStoreApplication.getInstance().getDownloadLink()
							.findNode(id)){
						Log.e("CurrentDownloadManager","����Ϊ�쳣�ж����ص�app���·������ض�������" );
						notic.getFileDownloadJob().setStatus(0);
						AppStoreApplication.getInstance().getDownloadLink().addNode(notic);
						//AppStoreApplication.getInstance().getCurrentDownloadJobManager().removeDownloadJob(util.getFileName(notic.getFileDownloadJob().getFilename()));
					}						
				}
			}
		}
		
	}
	
	//����Itemdata��Ϣ��currentdownload��Ϣ��������
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
