package com.zyitong.AppStore.common;

import java.util.ArrayList;
import java.util.List;

public class PamaterCache {
	private List<PageInfoData> itemList = new ArrayList();
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
