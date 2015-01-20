package com.zyitong.AppStore.common;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ItemData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3494650720669623411L;

	private Drawable imageId; // 图片资源id
	private long id;//
	private String image; //预览图URL
	private String name; //名称
	private String intro;//介绍
	private int fileSize;//文件大小
	private int downloadnum;//下载次数
	private String pubdate ;//上传日期
	private String filename;//下载文件包括路径
	private int star;//星级
	private int end;
	private String ver;
	private int buttonFileflag;
	
	
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
	private int price;//价格
	private int flag;//0--免费  1--收费;
	private int prevID;//上级菜单
	private int mcid;//1--软件，2--图片，3--游戏,4--铃声,5--主题,6--电子书

	public int getMcid() {
		return mcid;
	}
	public void setMcid(int mcid) {
		this.mcid = mcid;
	}
	public int getPrevID() {
		return prevID;
	}
	public void setPrevID(int prevID) {
		this.prevID = prevID;
	}
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
		this.id = id;
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
