package com.zyitong.AppStore.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.CurrentDownloadJob;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.bean.ItemData;

public class UtilFun {

	/***
	 * 格式化当前时间 
	 * @param format 格式
	 * @return
	 */
	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	public static String getCurrentTime() {
		return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
	}

	/***
	 * 添加下载
	 * @param packagename 应用包名
	 * @param ratio 当前下载与改应用总大小的比例
	 * @param status 下载状态 ：1准备下载，2下载中，3下载完成,4失败
	 * @param notic
	 */
	public void addCurrentDownloadJob(String packagename, int ratio, int status, FileDownloadJob notic) {
		//判定当前下载管理器中是否存在该应用，true：更新下载进度；false：添加至下载管理器中
		if (AppStoreApplication.getInstance().getCurrentDownloadJobManager().isCurrJobExist(packagename)) {
			AppStoreApplication.getInstance().getCurrentDownloadJobManager().updateDownloadJob(packagename, ratio, status, notic);
		} else {
			CurrentDownloadJob currentDownloadJob = new CurrentDownloadJob();
			currentDownloadJob.setPackageName(packagename);
			currentDownloadJob.setRatio(ratio);
			currentDownloadJob.setFilestatus(status);
			currentDownloadJob.setData(notic);
			AppStoreApplication.getInstance().getCurrentDownloadJobManager().addDownloadJob(currentDownloadJob);
		}

	}

	/***
	 * 重新下载
	 * @param packagename
	 */
	public void setAppReDownLoad(String packagename) {
		AppStoreApplication.getInstance().getCurrentDownloadJobManager().setAppReDownload(packagename);
	}

	public boolean isAppReDownload(String packagename) {
		return AppStoreApplication.getInstance().getCurrentDownloadJobManager().isAppReDownload(packagename);
	}

	/***
	 * 根据下载链接获取 该app相关信息
	 * @param data
	 * @return
	 */
	public FileDownloadJob DataChange(ItemData data) {
		FileDownloadJob dldata = new FileDownloadJob();
		int NOTIFICATION_ID = Integer.valueOf(data.getAppInfoBean().getId());

		if (AppStoreApplication.getInstance().getDownloadLink()
				.findNode(Integer.valueOf(data.getAppInfoBean().getId()))) {
			return AppStoreApplication
					.getInstance()
					.getDownloadLink()
					.getNoticData(Integer.valueOf(data.getAppInfoBean().getId()));
		}

		String name = data.getAppInfoBean().getTitle();

		String filename = AppStoreApplication.getInstance().getFileName(data.getAppInfoBean().getUrl());
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

	/***
	 * 下载完成,移除改线程
	 * 
	 * @param dldata
	 */
	public void DowloadComplete(FileDownloadJob dldata) {

		AppStoreApplication.getInstance().getDownloadLink()
				.delNode(dldata.getId());
	}

	/***
	 * 获取包名
	 * @param uri
	 * @param mContext
	 * @return
	 */
	public String getPackageName(String uri, Context mContext) {
		PackageManager pm = mContext.getPackageManager();
		PackageInfo packageInfo = pm.getPackageArchiveInfo(uri, PackageManager.GET_ACTIVITIES);
		if (packageInfo != null) {
			String packagename = packageInfo.packageName;
			return packagename;
		} else
			return null;

	}

	/***
	 * 获取应用打开权限 
	 * @param packageName
	 * @param mContext
	 * @return
	 */
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
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return className;
	}
	
	public String openApp(String packageName, Context mContext) {
		String mainActivityName = getMainActivityName(packageName, mContext);
		String[] args = { "am", "start", "-n", packageName + "/" + mainActivityName };
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		String str = "";
		for (int i = 0; i < args.length; i++) {
			str += (String) args[i];
		}
		return str;

	}
	
	public void openAppWithoutPermisson(Context context,String packName){
		String appLauncherActivity=getMainActivityName(packName, context);
		ComponentName cn=new ComponentName(packName,appLauncherActivity);
		Intent intent=new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(cn);
		context.startActivity(intent);
	}
	
	/***
	 * 获取未安装应用名称
	 * @param context
	 * @param filePath
	 * @return
	 */
	public boolean getUninatllApkInfo(Context context, String filePath) {
		boolean result = false;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
			if (info != null) {
				result = true;
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/***
	 * 下载完成之后自动安装app
	 * 
	 * @param apkAbsolutePath
	 *            apk存储路径
	 * @return
	 */
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}
	
	public void nonDefaultInstall(Context context,String apkAbsolutePath){
		File file=new File(apkAbsolutePath);
		if (!file.exists()) {
			AppLogger.e("apk is not exists");
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/***
	 * 获取文件名
	 * @param url 下载链接
	 * @return
	 */
	public String getFileName(String url) {
		int index = url.lastIndexOf("/");
		String substr = url;
		if (index > 0) {
			substr = url.substring(index + 1);
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
}
