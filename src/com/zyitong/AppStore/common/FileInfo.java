package com.zyitong.AppStore.common;

import android.graphics.drawable.Drawable;

public class FileInfo {
	public String path; // 文件路径

	public String fileName; // 文件名

	public int type; // 文件类型

	public boolean checked; // 是否选中

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Drawable getDrawable() {
		return imageId;
	}

	public void setDrawable(Drawable imageId) {
		this.imageId = imageId;
	}

	public Drawable imageId; // 图片资源id

	public FileInfo(String path, String fileName, int type, boolean checked,
			Drawable imageId) {

		this.path = path;

		this.fileName = fileName;

		this.type = type;
		this.checked = checked;
		this.imageId = imageId;

	}

}
