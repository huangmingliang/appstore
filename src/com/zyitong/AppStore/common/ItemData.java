package com.zyitong.AppStore.common;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ItemData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3494650720669623411L;

	private Drawable imageId; // ͼƬ��Դid
	private long id;//
	private String image; //Ԥ��ͼURL
	private String name; //����
	private String intro;//����
	private int fileSize;//�ļ���С
	private int downloadnum;//���ش���
	private String pubdate ;//�ϴ�����
	private String filename;//�����ļ�����·��
	private int star;//�Ǽ�
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
	private int price;//�۸�
	private int flag;//0--���  1--�շ�;
	private int prevID;//�ϼ��˵�
	private int mcid;//1--�����2--ͼƬ��3--��Ϸ,4--����,5--����,6--������

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
