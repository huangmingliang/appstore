package com.zyitong.AppStore.bean;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageProcc {
	public static Bitmap[] getImageSplit(Resources resources, int id, int w,
			int h) {
		Bitmap textresource = BitmapFactory.decodeResource(resources, id);
		int width = textresource.getWidth();
		int height = textresource.getHeight();
		int col = width / w;
		int row = height / h;
		Bitmap[] bitmap = new Bitmap[col * row];
		int cnt = 0;
		int x = 0;
		int y = 0;
		for (int i = 0; i < row; i++) {
			y = i * h;
			for (int j = 0; j < col; j++) {
				x = j * w;
				bitmap[cnt++] = Bitmap.createBitmap(textresource, x, y, w, h);
			}
		}
		return bitmap;
	}

	public static Bitmap getResId(Resources resources, int id) {
		Bitmap textresource = BitmapFactory.decodeResource(resources, id);
		return textresource;
	}
}
