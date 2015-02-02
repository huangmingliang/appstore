package com.zyitong.AppStore.bean;

import java.util.List;

public class PageInfoData {
	private String pageName;
	private int flag;
	private String keyword;
	private List<ItemData> itemList;
	private int selItem;
	public int getSelItem() {
		return selItem;
	}
	public void setSelItem(int selItem) {
		this.selItem = selItem;
	}
	public List<ItemData> getItemList() {
		return itemList;
	}
	public void setItemList(List<ItemData> itemList) {
		this.itemList = itemList;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
}
