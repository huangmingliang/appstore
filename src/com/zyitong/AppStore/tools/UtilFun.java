package com.zyitong.AppStore.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.CurrentDownloadJob;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.bean.NoticData;
import com.zyitong.AppStore.http.HttpApiImple;
import com.zyitong.AppStore.http.async.WSError;

public class UtilFun {
	private Context context;
	private FileOpt opt;

	public UtilFun(Context context) {
		this.context = context;
		opt = new FileOpt();
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

	public void addCurrentDownloadJob(String name, int ratio, int status,
			long fileSize, NoticData notic) {
		CurrentDownloadJob currentDownloadJob = new CurrentDownloadJob();
		currentDownloadJob.setFilename(name);
		currentDownloadJob.setRatio(ratio);
		currentDownloadJob.setFilestatus(status);
		currentDownloadJob.setFilelength(fileSize);
		currentDownloadJob.setData(notic);

		AppStoreApplication.getInstance().getCurrentDownloadJobManager()
				.addDownloadJob(currentDownloadJob);
	}

	public FileDownloadJob DataChange(ItemData data) {
		FileDownloadJob dldata = new FileDownloadJob();
		int NOTIFICATION_ID = (int) data.getId();
		// ����������б��Ѿ����ڸ�����
		if (AppStoreApplication.getInstance().getDownloadLink()
				.findNode((int) data.getId()))
			return null;

		String name = data.getName();

		String filename = AppStoreApplication.getInstance().getFileName(
				data.getFilename());
		String localPath = AppStoreApplication.getInstance().getFilePath();
		filename = localPath + filename;
		String url = data.getFilename();
		String updateurl = "shop/shop.jsp?code=download&UserHeader="
				+ AppStoreApplication.UserHeader + "&id=" + data.getId()
				+ "&imei=" + AppStoreApplication.getInstance().getImei();

		dldata.setFilename(filename);
		dldata.setUrl(url);
		dldata.setUpdateurl(updateurl);
		dldata.setName(name);
		dldata.setId(NOTIFICATION_ID);
		dldata.setPath(localPath);
		dldata.setRun(false);
		dldata.setStatus(0);
		return dldata;

	}

	public void DowloadComplete(FileDownloadJob dldata) {

		HttpApiImple imple = new HttpApiImple();
		try {
			imple.uploadDownNum(dldata.getUpdateurl());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (WSError e) {
			e.printStackTrace();
		}
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
		System.out.println("ListAdapter packageName = " + packagename);
		if (checkApkExist(mContext, packagename))
			installed = true;

		return installed;
	}

	public String getPackageName(String uri, Context mContext) {
		PackageManager pm = mContext.getPackageManager();
		PackageInfo packageInfo = pm.getPackageArchiveInfo(uri,
				PackageManager.GET_ACTIVITIES);
		String packagename = packageInfo.packageName;
		return packagename;
	}

	public boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	@SuppressLint("NewApi")
	public void openApp(String packageName, Context mContext) {
		PackageInfo pi;
		try {
			pi = mContext.getPackageManager().getPackageInfo(packageName, 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);

			PackageManager pm = mContext.getPackageManager();
			List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {

				String className = ri.activityInfo.name;
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);

				ComponentName cn = new ComponentName(packageName, className);

				intent.setComponent(cn);
				mContext.startActivity(intent);
			}

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ���ô���
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

	public void setFileState(ItemData itemData) {

		String filename = AppStoreApplication.getInstance().getFilePath()
				+ AppStoreApplication.getInstance().getFileName(
						itemData.getFilename());
		if (!opt.exists(filename))
			itemData.setButtonFileflag(ItemData.APP_INSTALED);
		else if (getUninatllApkInfo(context, filename)) {
			try {
				if (isAppInstalled(filename, context))
					itemData.setButtonFileflag(ItemData.APP_OPEN);
				else
					itemData.setButtonFileflag(ItemData.APP_INSTALED);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			itemData.setButtonFileflag(ItemData.APP_INSTALED);
		}
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

}
