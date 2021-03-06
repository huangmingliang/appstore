package com.zyitong.AppStore.bean;

import java.io.Serializable;


/**
 * @author Administrator
 *
 */
public class AppVerbaseBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String id;//app id
	public String title;//app name
	public String type;//类型
	public String body;
	public String url;//下载链接
	public String author;//
	public String thumbnail;
	public String grade;//等级（分数）
	public String platform;//平台
	public String version;//版本
	public String update_type;//更新内容
	public String packagename;//包名
	public String version_num;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUpdate_type() {
		return update_type;
	}

	public void setUpdate_type(String update_type) {
		this.update_type = update_type;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}
	
	public int getVersion_num(){
		return Integer.valueOf(version_num);
	}
	
}
