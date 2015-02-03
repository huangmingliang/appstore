package com.zyitong.AppStore.dao;

import java.util.ArrayList;
import java.util.List;

import com.zyitong.AppStore.bean.PageInfoData;

public class PamaterCache {
	private List<PageInfoData> itemList = new ArrayList<PageInfoData>();
	public void clear()
	{
		itemList.clear();
	}
	
	public void addPage(PageInfoData pageData)
	{
		itemList.add(pageData);
	}
	public PageInfoData getPage()
	{
		int size = itemList.size();
		if(size>0)
			return itemList.get(size-1);
		return null;
	}
	public void removePage()
	{
		int size = itemList.size();
		if(size>0)
			itemList.remove(size-1);	
	}
	
}
