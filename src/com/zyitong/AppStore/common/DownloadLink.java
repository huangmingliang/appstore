package com.zyitong.AppStore.common;

import java.util.LinkedList;

import com.zyitong.AppStore.AppStoreApplication;

public class DownloadLink {
	LinkedList<NoticData> data = new LinkedList<NoticData>();


	private int downloadNum =0;
	
	
	public int getSize()
	{
		return data.size();
	}

	public int getDownloadNum() {
		return downloadNum;
	}
	private void setAddNum() {
		this.downloadNum += 1;
	}
	private void setDelNum()
	{
		if(downloadNum>0)
			downloadNum--;
	}
	public NoticData getNode(int i) {
		setAddNum();
		return data.get(i);
	}
	
	public NoticData getNoticData(int i){
		return data.get(i);
	}
	public NoticData getNode() {
	
		for(int i=0;i<getSize();i++)
		{
			if( data.get(i).getFileDownloadJob().getStatus() == 0)
			{
				setAddNum();
				return data.get(i);
			}
		}
		return null;
	}
	public void addNode(NoticData itemData)
	{
		//setAddNum();
		data.add(itemData);
	}
	public void delNode(NoticData itemData)
	{
		data.remove(itemData);
		setDelNum();
	}
	public void delNodeItem(int i)
	{
		if(i>(data.size()-1))
			return;
		data.remove(i);
		setDelNum();
	}
	public void delNode(int id)
	{
		int index=-1;
		for(int i=0;i<data.size();i++)
		{
			if(data.get(i).getFileDownloadJob().getId()==id)
			{
				index=i;
				break;
			}
		}
		if(index>=0)
		{
			delNodeItem(index);
		}
	}
	
	public boolean findNode(int id)
	{
		for(int i=0;i<data.size();i++)
		{
			if(data.get(i).getFileDownloadJob().getId()==id)
			{
				return true;
			}
		}
		return false;
	}
	public boolean findNode(NoticData itemData)
	{
		for(int i=0;i<data.size();i++)
		{
			if(data.get(i).getFileDownloadJob().getId()==itemData.getFileDownloadJob().getId())
			{
				return true;
			}
		}
		return false;
	}
	
	public void intrrentDown()
	{
		for(int i=0;i<data.size();i++)
		{
			data.get(i).getFileDownloadJob().setRun(false);
			if(data.get(i).getFileDownloadJob().getStatus()==0)
			{
				//AppStoreApplication.getInstance().getManager().cancel(data.get(i).getFileDownloadJob().getId());
				data.remove(i);
			}
			
		}
		
	}
	public void moveAll()
	{
		data.clear();
	}
	
	 
}
