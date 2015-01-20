package com.zyitong.AppStore.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

import com.zyitong.AppStore.R;


import com.zyitong.AppStore.WeiBoApplication;
import com.zyitong.AppStore.activity.MainActivity;
import com.zyitong.AppStore.activity.SearchActivity;
import com.zyitong.AppStore.common.FileDownloadJob;
import com.zyitong.AppStore.common.FileOpt;
import com.zyitong.AppStore.common.ItemData;
import com.zyitong.AppStore.http.HttpApiImple;
import com.zyitong.AppStore.loading.WSError;
import com.zyitong.AppStore.notify.DownLoadService;

public class UtilFun {
	private Context context;
	private FileOpt opt;
	private String cmd_install = "pm install -r ";
	private String cmd_uninstall = "pm uninstall ";

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

	public FileDownloadJob DataChange(ItemData data, String tag) {
		FileDownloadJob dldata = new FileDownloadJob();
		int NOTIFICATION_ID = (int) data.getId();
		if (WeiBoApplication.getInstance().getDownloadLink()
				.findNode((int) data.getId()))
			return null;

		/*Notification notification = new Notification(
				android.R.drawable.stat_sys_download, data.getName(),
				System.currentTimeMillis());*/
		/*notification.contentView = new RemoteViews(context.getPackageName(),
				R.layout.notify_content);
		notification.when = 0;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notification.contentView.setImageViewResource(R.id.imageView1,
				android.R.drawable.stat_sys_download);

		notification.contentView.setProgressBar(R.id.progressBar1, 100, 0,
				false);
		notification.contentView.setTextViewText(R.id.textView1, data.getName()
				+ "(等待下载)");

		notification.contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(), 0);*/

		Intent urlIntent = null;
		if (tag.equalsIgnoreCase("MainActivity")) {
			urlIntent = new Intent(context, MainActivity.class);
		} else if (tag.equalsIgnoreCase("SearchActivity")) {
			urlIntent = new Intent(context, SearchActivity.class);
		}
		Bundle bundle = new Bundle();
		bundle.putInt("CLEAR", 100);
		urlIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		urlIntent.putExtras(bundle);
		/*notification.deleteIntent = PendingIntent.getActivity(context, 0,
				urlIntent, 0);*/

		String name = data.getName();
		int mcid = data.getMcid();
		String filename = WeiBoApplication.getInstance().getFileName(
				data.getFilename());
		String localPath = WeiBoApplication.getInstance().getFilePath(mcid);
		filename = localPath + filename;
		String url = data.getFilename();
		String updateurl = "shop/shop.jsp?code=download&UserHeader="
				+ WeiBoApplication.UserHeader + "&id=" + data.getId()
				+ "&imei=" + WeiBoApplication.getInstance().getImei();

		dldata.setFilename(filename);
		dldata.setUrl(url);
		dldata.setUpdateurl(updateurl);
		dldata.setName(name);
		//dldata.setNotification(notification);
		dldata.setId(NOTIFICATION_ID);
		dldata.setPath(localPath);
		dldata.setTypeid(mcid);
		dldata.setRun(false);
		dldata.setStatus(0);
		/*WeiBoApplication.getInstance().getManager()
				.notify(NOTIFICATION_ID, notification);*/
		return dldata;

	}

	public void DowloadComplete(FileDownloadJob dldata) {

		HttpApiImple imple = new HttpApiImple();
		try {
			imple.uploadDownNum(dldata.getUpdateurl());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (WSError e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		WeiBoApplication.getInstance().getDownloadLink()
				.delNode(dldata.getId());
		//play(dldata);

	}

	/*private void play(FileDownloadJob dldata) {
		// service 1 主题;2 游戏;3 电子书;4 铃声;5 图片;6 软件
		int mcid = dldata.getTypeid();
		if (mcid == 1) {
			dldata.setName(dldata.getName() + "(主题)");
			setupAPK(dldata);
		} else if (mcid == 2) {
			dldata.setName(dldata.getName() + "(游戏)");
			setupAPK(dldata);
		} else if (mcid == 6) {
			dldata.setName(dldata.getName() + "(软件)");
			setupAPK(dldata);
		} else if (mcid == 3) {
			dldata.setName(dldata.getName() + "(电子书)");
			playBook(dldata);
		} else if (mcid == 4) {
			dldata.setName(dldata.getName() + "(铃声)");
			playMusic(dldata);
		} else if (mcid == 5) {
			dldata.setName(dldata.getName() + "(图片)");
			playLogo(dldata);
		}
	}*/

	/*private void setupAPK(FileDownloadJob dldata) {
		Notification notification = dldata.getNotification();

		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(dldata.getFilename())),
				"application/vnd.android.package-archive");
		notification.contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		// notification.deleteIntent = PendingIntent.getActivity(this ,0,new
		// Intent(this,MainActivity.class), 0);
		notification.setLatestEventInfo(context, dldata.getName(), "下载完成点击安装。",
				notification.contentIntent);
		WeiBoApplication.getInstance().getManager()
				.notify(dldata.getId(), notification);

	}*/

	/*private void playMusic(FileDownloadJob dldata) {
		Notification notification = dldata.getNotification();
		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.parse("file://" + dldata.getFilename());
		intent.setDataAndType(uri, "audio/*");
		notification.contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		// notification.deleteIntent =
		// PendingIntent.getActivity(DownLoadService.this, 0,new Intent(this,
		// DownLoadService.class), 0);
		notification.setLatestEventInfo(context, dldata.getName(), "下载完成点击播放。",
				notification.contentIntent);
		WeiBoApplication.getInstance().getManager()
				.notify(dldata.getId(), notification);

	}*/

	/*private void playLogo(FileDownloadJob dldata) {
		Notification notification = dldata.getNotification();
		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(dldata.getFilename())),
				"image/*");
		notification.contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		// notification.deleteIntent =
		// PendingIntent.getActivity(DownLoadService.this, 0,new Intent(this,
		// DownLoadService.class), 0);
		notification.setLatestEventInfo(context, dldata.getName(), "下载完成点击查看。",
				notification.contentIntent);
		WeiBoApplication.getInstance().getManager()
				.notify(dldata.getId(), notification);

	}*/

	/*private void playBook(FileDownloadJob dldata) {
		Notification notification = dldata.getNotification();
		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(dldata.getFilename())),
				"application/*");
		notification.contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		// notification.deleteIntent =
		// PendingIntent.getActivity(DownLoadService.this, 0,new Intent(this,
		// DownLoadService.class), 0);
		notification.setLatestEventInfo(context, dldata.getName(), "下载完成点击查看。",
				notification.contentIntent);
		WeiBoApplication.getInstance().getManager()
				.notify(dldata.getId(), notification);
	}
*/
	public boolean isAppInstalled(String uri, Context mContext)
			throws PackageManager.NameNotFoundException {
		PackageManager pm = mContext.getPackageManager();
		boolean installed = false;
		// pm.getPackageInfo(uri,PackageManager.GET_ACTIVITIES);
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

	//引用传递
	public boolean getUninatllApkInfo(Context context, String filePath) {

		boolean result = false;
		try {
			PackageManager pm = context.getPackageManager();
			Log.e("archiveFilePath", filePath);
			PackageInfo info = pm.getPackageArchiveInfo(filePath,
					PackageManager.GET_ACTIVITIES);
			String packageName = null;
			if (info != null) {
				result = true;
			}
		} catch (Exception e) {
			result = false;
			Log.e("ListAdapter", "*****  解析未安装的 apk 出现异常 *****");
		}
		return result;
	}


	public void setFileState(ItemData itemData) {
		int mcid = itemData.getMcid();
		String filename = WeiBoApplication.getInstance().getFilePath(mcid)
				+ WeiBoApplication.getInstance().getFileName(
						itemData.getFilename());
		if (!opt.exists(filename))
			itemData.setButtonFileflag(1);
		else if (getUninatllApkInfo(context, filename)) {
			Log.e("MainActivity", "当前文件是可以安装或是打开的");
			try {
				if (isAppInstalled(filename, context))
					itemData.setButtonFileflag(2);
				else
					itemData.setButtonFileflag(1);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			opt.deleteFile(filename);
			itemData.setButtonFileflag(1);
		}
	}

	
	public String install(String apkAbsolutePath)
    {
        String[] args = {
                "pm", "install", "-rf", apkAbsolutePath
        };
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1)
            {
                baos.write(read);
            }
            
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1)
            {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (Exception e)
        {
            // TODO: handle exception
        }

        return result;

    }
	public String excuateShellchmod()
    {
		//chmod 666 /data/local/tmp/temp.apk
		// /sdcard/AppStore/soft/VEBGuard.apk
        String[] args = {
                "chmod", "777", "system"
        };
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1)
            {
                baos.write(read);
            }
            
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1)
            {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (Exception e)
        {
            // TODO: handle exception
        }

        return result;

    }

	public  String execRootCmd(String paramString) {
        String result = "result : ";
        try {
            Process localProcess = Runtime.getRuntime().exec("su ");// 经过Root处理的android系统即有su命令
            OutputStream localOutputStream = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(
                    localOutputStream);
            InputStream localInputStream = localProcess.getInputStream();
            DataInputStream localDataInputStream = new DataInputStream(
                    localInputStream);
            String str1 = String.valueOf(paramString);
            String str2 = str1 + "\n";
            localDataOutputStream.writeBytes(str2);
            localDataOutputStream.flush();
            String str3 = null;
//            while ((str3 = localDataInputStream.readLine()) != null) {
//                Log.d("result", str3);
//            }
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            return result;
        } catch (Exception localException) {
            localException.printStackTrace();
            return result;
        }
    }
	
	public  int execRootCmdSilent(String paramString) {
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

	
	
	

	
	
	

}
