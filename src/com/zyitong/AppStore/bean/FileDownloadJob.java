package com.zyitong.AppStore.bean;

import com.zyitong.AppStore.tools.FileOpt;

public class FileDownloadJob {
	private String filename;
	private String url;
	private String updateurl;
	private String name;
	private String path;
	private int Id;
	private int typeid;
	private boolean isRun;
	private int status; // 1准备下载，2下载中，3下载完成,4失败

	public FileDownloadJob() {

	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUpdateurl() {
		return updateurl;
	}

	public void setUpdateurl(String updateurl) {
		this.updateurl = updateurl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		FileOpt opt = new FileOpt();
		opt.CreateFilePath(path);
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getTypeid() {
		return typeid;
	}

	public void setTypeid(int typeid) {
		this.typeid = typeid;
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
