package com.zyitong.AppStore.dao;

import java.util.LinkedList;

import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.downloadthread.FileDownLoadMonitorThread;

public class DownloadLink {
	LinkedList<FileDownloadJob> data = new LinkedList<FileDownloadJob>();

	private int downloadNum = 0;

	public int getSize() {
		return data.size();
	}
	
	public boolean hasDownloadFree() {
		if(data.size()>FileDownLoadMonitorThread.MAXDOWN)
			return false;
		else
			return true;
	}

	public int getDownloadNum() {
		return downloadNum;
	}

	private void setAddNum() {
		this.downloadNum += 1;
	}

	private void setDelNum() {
		if (downloadNum > 0)
			downloadNum--;
	}

	public FileDownloadJob getNoticData(int id) {
		for(int i = 0;i<data.size();i++){
			if(data.get(i).getId()==id)
				return data.get(i);
		}
		return null;
	}

	public FileDownloadJob getNode() {

		for (int i = 0; i < getSize(); i++) {
			if (data.get(i).getStatus()== 0) {
				setAddNum();
				return data.get(i);
			}
		}
		return null;
	}

	public void addNode(FileDownloadJob itemData) {
		if(!data.contains(itemData)){
			 data.add(itemData);
		}	   
	}

	public void delNode(FileDownloadJob itemData) {
		if(data.contains(itemData)){
			data.remove(itemData);
		}	
	}

	public void delNodeItem(int i) {
		if (i > (data.size() - 1))
			return;
		data.remove(i);
		setDelNum();
	}

	public void delNode(int id) {
		int index = -1;
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getId() == id) {
				index = i;
				break;
			}
		}
		if (index >= 0) {
			delNodeItem(index);
		}
	}

	public boolean findNode(int id) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getId() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean findNode(FileDownloadJob itemData) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getId() == itemData
					.getId()) {
				return true;
			}
		}
		return false;
	}

	public void intrrentDown() {
		for (int i = 0; i < data.size(); i++) {
			data.get(i).setRun(false);
			if (data.get(i).getStatus() == 0) {
				data.remove(i);
			}
		}
	}

	public void moveAll() {
		data.clear();
	}

}
