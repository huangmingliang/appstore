package com.zyitong.AppStore.bean;

public class ItemData {
	public static final int APP_UPDATE =1;
	public static final int APP_OPEN = 2;
	public static final int APP_LOADING = 3;
	public static final int APP_FAIL = 4;
	public static final int APP_NETWORKEX = 5;
	public static final int APP_REDOWNLOAD = 6;
	public static final int APP_DOWDLOAD = 7;
	public static final int APP_WAIT=8;
	public static final int APP_UPDATE_WAIT = 9;
	public static final int APP_UPDATING = 10;
	public static final int APP_INSTALL=11;

	private int buttonFileflag;
	private AppVerbaseBean appInfoBean;

	public AppVerbaseBean getAppInfoBean() {
		return appInfoBean;
	}

	public void setAppInfoBean(AppVerbaseBean appVerboseBean) {
		this.appInfoBean = appVerboseBean;
	}

	public int getButtonFileflag() {
		return buttonFileflag;
	}

	public void setButtonFileflag(int buttonFileflag) {
		this.buttonFileflag = buttonFileflag;
	}

}
