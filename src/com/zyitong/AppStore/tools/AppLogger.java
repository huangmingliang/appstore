package com.zyitong.AppStore.tools;

public class AppLogger {
	protected static final String TAG = "VEBSOFT";

	public static final boolean DEBUG = true;
	public static final int DEBUG_LEVEL = 0; // 0 verbose 1 debug 2 info 3
												// warnning 4 error

	private AppLogger() {
	}

	private static boolean canDisplay(int level) {
		if (DEBUG) {
			if (level >= DEBUG_LEVEL) {
				return true;
			}
		}
		return false;

	}

	public static void v(String msg) {
		if (canDisplay(0))
			android.util.Log.v(TAG, buildMessage(msg));
	}

	public static void v(String msg, Throwable thr) {
		if (canDisplay(0))
			android.util.Log.v(TAG, buildMessage(msg), thr);
	}

	public static void d(String msg) {
		if (canDisplay(1))
			android.util.Log.d(TAG, buildMessage(msg));
	}

	public static void d(String msg, Throwable thr) {
		if (canDisplay(1))
			android.util.Log.d(TAG, buildMessage(msg), thr);
	}

	public static void i(String msg) {
		if (canDisplay(2))
			android.util.Log.i(TAG, buildMessage(msg));
	}

	public static void i(String msg, Throwable thr) {
		if (canDisplay(2))
			android.util.Log.i(TAG, buildMessage(msg), thr);
	}

	public static void e(String msg) {
		if (canDisplay(4))
			android.util.Log.e(TAG, buildMessage(msg));
	}

	public static void w(String msg) {
		if (canDisplay(3))
			android.util.Log.w(TAG, buildMessage(msg));
	}

	public static void w(String msg, Throwable thr) {
		if (canDisplay(3))
			android.util.Log.w(TAG, buildMessage(msg), thr);
	}

	public static void w(Throwable thr) {
		if (canDisplay(3))
			android.util.Log.w(TAG, buildMessage(""), thr);
	}

	public static void e(String msg, Throwable thr) {
		if (canDisplay(4))
			android.util.Log.e(TAG, buildMessage(msg), thr);
	}

	protected static String buildMessage(String msg) {
		StackTraceElement caller = new Throwable().fillInStackTrace()
				.getStackTrace()[2];

		return new StringBuilder().append(caller.getClassName()).append(".")
				.append(caller.getMethodName()).append("(): ").append(msg)
				.toString();
	}
}