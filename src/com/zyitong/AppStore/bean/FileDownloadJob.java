package com.zyitong.AppStore.bean;

import com.zyitong.AppStore.tools.FileOpt;

/***
 * 下载文件 模型
 * @author 周双
 *
 * @function 主要实现功能
 *
 */
public class FileDownloadJob {
	private String packagename;
	private String fileuri;
	private String url;
	private String name;
	private String path;
	private int Id;
	private boolean isRun;
	private int status; //1准备下载，2下载中，3下载完成,4失败

	public FileDownloadJob() {

	}

	public String getPackageName() {
		return packagename;
	}

	public void setPackageName(String packagename) {
		this.packagename = packagename;
	}
	public String getFileuri() {
		return fileuri;
	}

	public void setFileuri(String fileuri) {
		this.fileuri = fileuri;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
