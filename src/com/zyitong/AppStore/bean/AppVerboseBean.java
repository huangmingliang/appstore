package com.zyitong.AppStore.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Administrator
 *
 */
public class AppVerboseBean {
	@SerializedName("id")
	public String id;
	
	@SerializedName("title")
	public String title;

	@SerializedName("type")
	public String type;

	@SerializedName("body")
	public String body;

	@SerializedName("url")
	public String url;

	@SerializedName("author")
	public String author;

	@SerializedName("thumbnail")
	public String thumbnail;

	@SerializedName("grade")
	public String grade;
	
	@SerializedName("platform")
	public String platform;
	
	@SerializedName("version")
	public String version;
	
	@SerializedName("update_type")
	public String update_type;
	
	@SerializedName("packagename")
	public String packagename;
	


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
	
}
