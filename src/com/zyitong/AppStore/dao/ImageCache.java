package com.zyitong.AppStore.dao;

import java.io.Serializable;
import java.util.WeakHashMap;

import android.graphics.Bitmap;

public class ImageCache extends WeakHashMap<String, Bitmap>implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public boolean isCached(String url){
		return containsKey(url) && get(url) != null;
	}

}