package com.zyitong.AppStore.bean;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class ItemData implements Serializable {

	private static final long serialVersionUID = -3494650720669623411L;

	private Drawable imageId;
	private int id;
	private String image;
	private String name;
	private String intro;
	private int fileSize;
	private int downloadnum;
	private String pubdate;
	private String filename;
	private int star;
	private int end;
	private String ver;
	private int buttonFileflag;

	public static int APP_READ = 0;
	public static int APP_INSTALED = 1;
	public static int APP_OPEN = 2;
	public static int APP_LOADING = 3;
	public static int APP_FAIL = 4;
	public static int APP_NETWORKEX = 5;

	public int getButtonFileflag() {
		return buttonFileflag;
	}

	public void setButtonFileflag(int buttonFileflag) {
		this.buttonFileflag = buttonFileflag;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	private int price;
	private int flag;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = (int) id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getDownloadnum() {
		return downloadnum;
	}

	public void setDownloadnum(int downloadnum) {
		this.downloadnum = downloadnum;
	}

	public String getPubdate() {
		return pubdate;
	}

	public void setPubdate(String pubdate) {
		this.pubdate = pubdate;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public Drawable getImageId() {
		return imageId;
	}

	public void setImageId(Drawable imageId) {
		this.imageId = imageId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
}
