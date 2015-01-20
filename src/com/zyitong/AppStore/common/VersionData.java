package com.zyitong.AppStore.common;

import java.io.Serializable;

public class VersionData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7473669164060005710L;
	private String version;
	private String url;
	private String description;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
