package com.zyitong.AppStore.bean;

public class ItemData {
	public static final int APP_UPDATE =1;
	public static final int APP_OPEN = 2;
	public static final int APP_LOADING = 3;
	public static final int APP_FAIL = 4;
	public static final int APP_NETWORKEX = 5;
	public static final int APP_REDOWNLOAD = 6;
	public static final int APP_INSTALL = 7;
	public static final int APP_WAIT=8;

	private int buttonFileflag;
	private AppVerboseBean appInfoBean;

	public AppVerboseBean getAppInfoBean() {
		return appInfoBean;
	}

	public void setAppInfoBean(AppVerboseBean appVerboseBean) {
		this.appInfoBean = appVerboseBean;
	}

	public int getButtonFileflag() {
		return buttonFileflag;
	}

	public void setButtonFileflag(int buttonFileflag) {
		this.buttonFileflag = buttonFileflag;
	}

}
