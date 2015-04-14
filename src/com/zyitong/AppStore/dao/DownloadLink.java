package com.zyitong.AppStore.dao;

import java.util.ArrayList;
import java.util.List;

import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.downloadthread.FileDownLoadMonitorThread;

public class DownloadLink {
	List<FileDownloadJob> data = new ArrayList<FileDownloadJob>();

	private int downloadNum = 0;

	public int getSize() {
		synchronized (this) {
			return data.size();
		}

	}

	public boolean hasDownloadFree() {
		synchronized (this) {
			if (data.size() > FileDownLoadMonitorThread.MAXDOWN)
				return false;
			else
				return true;
		}

	}

	public int getDownloadNum() {
		synchronized (this) {
			return downloadNum;
		}

	}

	private void setAddNum() {
		synchronized (this) {
			this.downloadNum += 1;
		}

	}

	private void setDelNum() {
		synchronized (this) {
			if (downloadNum > 0)
				downloadNum--;
		}

	}

	public FileDownloadJob getNoticData(int id) {
		synchronized (this) {
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getId() == id)
					return data.get(i);
			}
			return null;
		}

	}

	public FileDownloadJob getNode() {
		synchronized (this) {
			for (int i = 0; i < getSize(); i++) {
				if (data.get(i).getStatus() == 0) {
					setAddNum();
					return data.get(i);
				}
			}
			return null;
		}
	}

	public void addNode(FileDownloadJob itemData) {
		synchronized (this) {
			if (!data.contains(itemData)) {
				data.add(itemData);
			}
		}

	}

	public void delNode(FileDownloadJob itemData) {
		synchronized (this) {
			if (data.contains(itemData)) {
				data.remove(itemData);
			}
		}

	}

	public void delNode(int id) {
		synchronized (this) {
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getId() == id) {
					data.remove(i);
					setDelNum();
					break;
				}
			}
		}

	}

	public boolean findNode(int id) {
		synchronized (this) {
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getId() == id) {
					return true;
				}
			}
			return false;
		}

	}

	public boolean findNode(FileDownloadJob itemData) {
		synchronized (this) {
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getId() == itemData.getId()) {
					return true;
				}
			}
			return false;
		}

	}

	public void intrrentDown() {
		synchronized (this) {
			for (int i = 0; i < data.size(); i++) {
				data.get(i).setRun(false);
				if (data.get(i).getStatus() == 0) {
					data.remove(i);
				}
			}
		}

	}

	public void moveAll() {
		data.clear();
	}

}
