package com.zyitong.AppStore.common;

import java.io.Serializable;

public class TypeData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4374859075325273737L;
	private int id;//
	private String image; //‘§¿¿ÕºURL
	private String name; //√˚≥∆
	private String intro;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
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
}
