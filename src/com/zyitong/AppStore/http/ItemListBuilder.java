package com.zyitong.AppStore.http;

import org.json.JSONException;
import org.json.JSONObject;

import com.zyitong.AppStore.common.ItemData;
import com.zyitong.AppStore.util.UtilFun;

public class ItemListBuilder  extends JSONBuilder<ItemData> {
	
	@Override
	public ItemData build(JSONObject jsonObject) throws JSONException {
		ItemData itemData = new ItemData();
		String name="";
		long id=0;
		String image="";
		String pubdate="";
		int flag=0;
		int price=0;
		int fileSize=0;
		int downloadnum=0;
		int star =0;
		int mcid=1;
		String filename="";
		String intro="";
		String ver ="";
		int end=0;
		try{
			name = jsonObject.getString(root+"name");
		}catch(Exception e){}
		try{
			intro = jsonObject.getString(root+"intro");
		}catch(Exception e){}
		try{
			image = jsonObject.getString(root+"image");
		}catch(Exception e){}
		try{
			pubdate = jsonObject.getString(root+"pubdate");
		}catch(Exception e){}
		try{
			filename = jsonObject.getString(root+"filename");
		}catch(Exception e){}
		try{
			ver = jsonObject.getString(root+"ver");
		}catch(Exception e){}
		try{
			flag = jsonObject.getInt(root+"flag");
		}catch(Exception e){}		
		try{
			price = jsonObject.getInt(root+"price");
		}catch(Exception e){}	
		try{
			id = jsonObject.getLong(root+"id");
		}catch(Exception e){}
		try{
			fileSize = jsonObject.getInt(root+"fileSize");
		}catch(Exception e){}
		try{
			downloadnum = jsonObject.getInt(root+"downloadnum");
		}catch(Exception e){}	
		try{
			mcid = jsonObject.getInt(root+"mcid");
		}catch(Exception e){}
		
		try{
			star = jsonObject.getInt(root+"star");
		}catch(Exception e){}
		
		try{
			end = jsonObject.getInt(root+"end");
		}catch(Exception e){}
		
		itemData.setId(id);
		itemData.setIntro(intro);
		itemData.setName(name);
		itemData.setImage(image);
		itemData.setPubdate(pubdate);
		itemData.setFlag(flag);
		itemData.setPrice(price);
		itemData.setFilename("http://down.360safe.com/360ap/360freewifi_wifi.apk");
		itemData.setFileSize(fileSize);
		itemData.setDownloadnum(downloadnum);
		itemData.setMcid(mcid);
		itemData.setStar(star);
		itemData.setEnd(end);
		itemData.setVer(ver);
		//如果文件不存在或者是存在了不能下载就设置为“下载”
		
		return itemData;
	}
}
