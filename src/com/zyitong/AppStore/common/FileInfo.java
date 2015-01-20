package com.zyitong.AppStore.common;

import android.graphics.drawable.Drawable;

public class FileInfo {
	public String path; // �ļ�·��

	public String fileName; // �ļ���

	public int type; // �ļ�����

	public boolean checked; // �Ƿ�ѡ��

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

	public Drawable imageId; // ͼƬ��Դid

	public FileInfo(String path, String fileName, int type, boolean checked,
			Drawable imageId) {

		this.path = path;

		this.fileName = fileName;

		this.type = type;
		this.checked = checked;
		this.imageId = imageId;

	}

}
