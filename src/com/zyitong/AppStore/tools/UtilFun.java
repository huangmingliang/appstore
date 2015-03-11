package com.zyitong.AppStore.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.CurrentDownloadJob;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.bean.ItemData;

public class UtilFun {
	private Context context;

	public UtilFun(Context context) {
		this.context = context;
	}

	public UtilFun() {
	};

	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	public static String getCurrentTime() {
		return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
	}

	public void addCurrentDownloadJob(String packagename, int ratio,
			int status, FileDownloadJob notic) {
		CurrentDownloadJob currentDownloadJob = new CurrentDownloadJob();
		currentDownloadJob.setPackageName(packagename);
		currentDownloadJob.setRatio(ratio);
		currentDownloadJob.setFilestatus(status);
		currentDownloadJob.setData(notic);
		AppStoreApplication.getInstance().getCurrentDownloadJobManager()
				.addDownloadJob(currentDownloadJob);
	}

	public void setAppReDownLoad(String filename) {
		AppStoreApplication.getInstance().getCurrentDownloadJobManager()
				.setAppReDownload(filename);
	}

	public boolean isAppReDownload(String packagename) {
		return AppStoreApplication.getInstance().getCurrentDownloadJobManager()
				.isAppReDownload(packagename);
	}

	public FileDownloadJob DataChange(ItemData data) {
		FileDownloadJob dldata = new FileDownloadJob();
		int NOTIFICATION_ID = Integer.valueOf(data.getAppInfoBean().getId());

		if (AppStoreApplication.getInstance().getDownloadLink()
				.findNode(Integer.valueOf(data.getAppInfoBean().getId()))) {
			return AppStoreApplication
					.getInstance()
					.getDownloadLink()
					.getNoticData(
							Integer.valueOf(data.getAppInfoBean().getId()));
		}

		String name = data.getAppInfoBean().getTitle();

		String filename = AppStoreApplication.getInstance().getFileName(
				data.getAppInfoBean().getUrl());
		String localPath = AppStoreApplication.getInstance().getFilePath();
		String fileuri = localPath + filename;
		String url = data.getAppInfoBean().getUrl();

		dldata.setPackageName(data.getAppInfoBean().getPackagename());
		dldata.setFileuri(fileuri);
		dldata.setUrl(url);
		dldata.setName(name);
		dldata.setId(NOTIFICATION_ID);
		dldata.setPath(localPath);
		dldata.setRun(false);
		dldata.setStatus(0);
		return dldata;
	}

	public void DowloadComplete(FileDownloadJob dldata) {

		AppStoreApplication.getInstance().getDownloadLink()
				.delNode(dldata.getId());
	}

	public boolean isAppInstalled(String uri, Context mContext)
			throws PackageManager.NameNotFoundException {
		PackageManager pm = mContext.getPackageManager();
		boolean installed = false;
		PackageInfo packageInfo = pm.getPackageArchiveInfo(uri,
				PackageManager.GET_ACTIVITIES);
		String packagename = packageInfo.packageName;
		AppLogger.e("isAppInstalled packageName = " + packagename);
		if (checkApkExist(mContext, packagename))
			installed = true;
		AppLogger.e("isAppInstalled packageName = " + installed);
		return installed;
	}

	public String getPackageName(String uri, Context mContext) {
		PackageManager pm = mContext.getPackageManager();
		PackageInfo packageInfo = pm.getPackageArchiveInfo(uri,
				PackageManager.GET_ACTIVITIES);
		if (packageInfo != null) {
			String packagename = packageInfo.packageName;
			return packagename;
		} else
			return null;

	}

	public boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	private String getMainActivityName(String packageName, Context mContext) {
		PackageInfo pi;
		String className = null;
		try {
			pi = mContext.getPackageManager().getPackageInfo(packageName, 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);
			PackageManager pm = mContext.getPackageManager();
			List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				className = ri.activityInfo.name;
				AppLogger.e("open class name = " + className);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return className;
	}

	public String  openApp(String packageName, Context mContext) {

		String mainActivityName = getMainActivityName(packageName, mContext);
		AppLogger.e("getMainActivityName = " + mainActivityName);
		String[] args = { "am", "start", "-n",packageName+"/"+mainActivityName };
		AppLogger.e(""+args.toString());
		// String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}

			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			// result = new String(data);
			// AppLogger.e("install(String apkAbsolutePath) result = " +
			// result);
		} catch (Exception e) {
			// TODO: handle exception
		}
		String str="";
		for(int i=0;i<args.length;i++){	
	        str+=(String)args[i];
		}	
		return str;


	}

	// @SuppressLint("NewApi")
	// public void openApp(String packageName, Context mContext) {
	// PackageInfo pi;
	// // try {
	// // pi = mContext.getPackageManager().getPackageInfo(packageName, 0);
	// // Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
	// // resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	// // resolveIntent.setPackage(pi.packageName);
	// //
	// // PackageManager pm = mContext.getPackageManager();
	// // List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
	// // ResolveInfo ri = apps.iterator().next();
	// // if (ri != null) {
	// //
	// // String className = ri.activityInfo.name;
	// // AppLogger.e("open class name = "+className);
	// // Intent intent ;
	// // //intent.addCategory(Intent.CATEGORY_LAUNCHER);
	// // intent = mContext.getPackageManager()
	// // .getLaunchIntentForPackage(packageName);
	// // intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
	// // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	// //
	// //
	// // //ComponentName cn = new ComponentName(packageName, className);
	// //
	// // //intent.setComponent(cn);
	// // mContext.startActivity(intent);
	// // }
	// //
	// // } catch (NameNotFoundException e) {
	// // e.printStackTrace();
	// // }
	//
	// Intent intent = mContext.getPackageManager()
	// .getLaunchIntentForPackage(packageName);
	// intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
	// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	// mContext.startActivity(intent);
	//
	// }

	public boolean getUninatllApkInfo(Context context, String filePath) {

		boolean result = false;
		try {
			PackageManager pm = context.getPackageManager();
			AppLogger.e("" + filePath);
			PackageInfo info = pm.getPackageArchiveInfo(filePath,
					PackageManager.GET_ACTIVITIES);
			if (info != null) {
				result = true;
			}
		} catch (Exception e) {
			result = false;
			AppLogger.e("Exception :" + e.toString());
		}
		return result;
	}

	public void setAppState(ItemData itemData) {
		String packagename = itemData.getAppInfoBean().getPackagename();

		if (!checkApkExist(context, packagename))
			itemData.setButtonFileflag(ItemData.APP_INSTALL);
		else
			itemData.setButtonFileflag(ItemData.APP_OPEN);
	}

	public String install(String apkAbsolutePath) {
		String[] args = { "pm", "install", "-rf", apkAbsolutePath };
		String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}

			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
			AppLogger.e("install(String apkAbsolutePath) result = " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;

	}

	public String excuateShellchmod() {
		// chmod 666 /data/local/tmp/temp.apk
		// /sdcard/AppStore/soft/VEBGuard.apk
		String[] args = { "chmod", "777", "system" };
		String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}

			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;

	}

	public String execRootCmd(String paramString) {
		String result = "result : ";
		try {
			Process localProcess = Runtime.getRuntime().exec("su ");// ����Root�����androidϵͳ����su����
			OutputStream localOutputStream = localProcess.getOutputStream();
			DataOutputStream localDataOutputStream = new DataOutputStream(
					localOutputStream);
			String str1 = String.valueOf(paramString);
			String str2 = str1 + "\n";
			localDataOutputStream.writeBytes(str2);
			localDataOutputStream.flush();
			// while ((str3 = localDataInputStream.readLine()) != null) {
			// Log.d("result", str3);
			// }
			localDataOutputStream.writeBytes("exit\n");
			localDataOutputStream.flush();
			localProcess.waitFor();
			return result;
		} catch (Exception localException) {
			localException.printStackTrace();
			return result;
		}
	}

	public int execRootCmdSilent(String paramString) {
		try {
			Process localProcess = Runtime.getRuntime().exec("su");
			Object localObject = localProcess.getOutputStream();
			DataOutputStream localDataOutputStream = new DataOutputStream(
					(OutputStream) localObject);
			String str = String.valueOf(paramString);
			localObject = str + "\n";
			localDataOutputStream.writeBytes((String) localObject);
			localDataOutputStream.flush();
			localDataOutputStream.writeBytes("exit\n");
			localDataOutputStream.flush();
			localProcess.waitFor();
			int result = localProcess.exitValue();
			return (Integer) result;
		} catch (Exception localException) {
			localException.printStackTrace();
			return -1;
		}
	}

	public String getFileName(String filename) {
		int index = filename.lastIndexOf("/");
		String substr = filename;
		if (index > 0) {
			substr = filename.substring(index + 1);
		}
		return substr;
	}

	public void makeAppStoreDir() {
		String dirs = android.os.Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/AppStore/soft";
		File file = new File(dirs);
		if (!file.exists())
			file.mkdirs();
	}

	public boolean slientInstall(String uri) {
		boolean result = false;
		Process process = null;
		OutputStream out = null;
		try {
			process = Runtime.getRuntime().exec("su");
			out = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(out);
			dataOutputStream.writeBytes("chmod 777 " + uri + "\n");
			dataOutputStream
					.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r "
							+ uri);
			// 提交命令
			dataOutputStream.flush();
			// 关闭流操作
			dataOutputStream.close();
			out.close();
			int value = process.waitFor();

			// 代表成功
			if (value == 0) {
				result = true;
			} else if (value == 1) { // 失败
				result = false;
			} else { // 未知情况
				result = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return result;
	}

}
