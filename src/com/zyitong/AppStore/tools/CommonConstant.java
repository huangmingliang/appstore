package com.zyitong.AppStore.tools;

import java.io.File;

import org.apache.log4j.Level;

public class CommonConstant {
	public static final String REST_URL = "http://opensearch-cn-hangzhou.aliyuncs.com";
	
	public static final String INDEX_NAME = "AppStore";
	public static final String VERSION = "v2";
	public static final String SIGNATURE_METHOD = "HMAC-SHA1";
	public static final String SIGNATURE_VERSION = "1.0";
	
	public static final String ACCESS_KEY_ID = "lflMmtFvvNt5YobP";
	public static final String ACCESS_KEY_SECRET = "aYpz8CtTVSMWFnqaH1Q7uhki1NNQcL";
	
	public static final int MAXDOWN = 2;
	public static final int MAXTHREADNUM = 4;

	public static final String DEBUG_PATH = "AppStoreLog";

	public static final Level DEBUG_LEVEL = Level.INFO;
	

}
