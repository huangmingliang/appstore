package com.zyitong.AppStore.common;

public class CurrentDownloadJob {
	
	private String filename;//�ļ���
	private int ratio;//��ǰ���ص��ļ��İٷֱ�
	private int filestatus;//��ǰ����״̬
	private long filelength;//��ǰ�ļ���С
	public long getFilelength() {
		return filelength;
	}
	public void setFilelength(long filelength) {
		this.filelength = filelength;
	}
	public NoticData getData() {
		return data;
	}
	public void setData(NoticData data) {
		this.data = data;
	}
	private NoticData data;//Я����������Ϣ
	
	
	public int getFilestatus() {
		return filestatus;
	}
	public void setFilestatus(int filestatus) {
		this.filestatus = filestatus;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getRatio() {
		return ratio;
	}
	public void setRatio(int ratio) {
		this.ratio = ratio;
	}
	

}
