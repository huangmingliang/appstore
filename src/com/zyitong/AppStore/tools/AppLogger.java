package com.zyitong.AppStore.tools;

import java.io.File;

import org.apache.log4j.Logger;

import android.os.Environment;


public class AppLogger {
	private static boolean isInitConfig = false;
	private static final Logger log = Logger.getRootLogger();

	private AppLogger() {
	}

	private static void initConfig() {
		if (!isInitConfig) {

			AppLogConfigurator logConfiger = new AppLogConfigurator();

			String rootPath = Environment.getExternalStorageDirectory()
					+ File.separator + CommonConstant.DEBUG_PATH;
			File dirFile = new File(rootPath);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}

			logConfiger.setFileName(rootPath + File.separator + "appstore.log");
			logConfiger.setRootLevel(CommonConstant.DEBUG_LEVEL);

			logConfiger.setUseFileAppender(true);
			logConfiger.setFilePattern("%d - [%p::%t] - %m%n");
			logConfiger.setImmediateFlush(true);
			logConfiger.setInternalDebugging(false);
			logConfiger.setMaxBackupSize(20);
			logConfiger.setMaxFileSize(1024 * 1024);

			logConfiger.setUseLogCatAppender(true);
			logConfiger.setLogCatPattern("%m%n");
			logConfiger.configure();
			isInitConfig = true;
		}
	}

	public static void v(String msg) {
		initConfig();
		log.debug(buildMessage(msg));

	}

	public static void v(String msg, Throwable thr) {
		initConfig();
		log.debug(buildMessage(msg), thr);
	}

	public static void d(String msg) {
		initConfig();
		log.debug(buildMessage(msg));
	}

	public static void d(String msg, Throwable thr) {
		initConfig();
		log.debug(buildMessage(msg), thr);
	}

	public static void i(String msg) {
		initConfig();
		log.info(buildMessage(msg));
	}

	public static void i(String msg, Throwable thr) {
		initConfig();
		log.info(buildMessage(msg), thr);
	}

	public static void w(String msg) {
		initConfig();
		log.warn(buildMessage(msg));
	}

	public static void w(String msg, Throwable thr) {
		initConfig();
		log.warn(buildMessage(msg), thr);
	}

	public static void w(Throwable thr) {
		initConfig();
		log.warn(buildMessage(""), thr);
	}

	public static void e(String msg) {
		initConfig();
		log.error(buildMessage(msg));
	}

	public static void e(String msg, Throwable thr) {
		initConfig();
		log.error(buildMessage(msg), thr);
	}

	protected static String buildMessage(String msg) {
		initConfig();
		StackTraceElement caller = new Throwable().fillInStackTrace()
				.getStackTrace()[2];

		return new StringBuilder().append(caller.getClassName()).append(".")
				.append(caller.getMethodName()).append("(): ").append(msg)
				.toString();
	}
}