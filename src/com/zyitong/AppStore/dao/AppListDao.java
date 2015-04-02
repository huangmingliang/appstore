package com.zyitong.AppStore.dao;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import com.google.gson.internal.LinkedTreeMap;
import com.zyitong.AppStore.bean.AppListBean;
import com.zyitong.AppStore.bean.AppVerboseBean;
import com.zyitong.AppStore.tools.AppLogger;
import com.zyitong.AppStore.tools.CommonConstant;

public class AppListDao extends CommonDao {
	private static AppListDao appList = new AppListDao();

	private AppListDao() {
		super();
	}

	public static AppListDao getInstance() {
		return appList;
	}

	interface AppListDaoInterface {
		@GET("/search")
		AppListBean getAppList(@QueryMap Map<String, String> options);
	}

	public void display(AppListBean bean) {
		int i = 0;
		int count = 0;

		if (null != bean) {

			if (null != bean.errors && bean.errors.size() > 0
					&& null != bean.errors.get(0)) {
			}

			if (null != bean.result) {

				if (null != bean.result.num && null != bean.result.items) {
					count = bean.result.items.size();
					AppVerboseBean verboseBean;
					for (i = 0; i < count; i++) {
						verboseBean = bean.result.items.get(i);
					}
				}
			}
		}
	}

	public AppListBean searchAppList(String query, int start, int docNum) {
		if (query == null || query.length() == 0) {
			return null;
		}

		if (0 > start || docNum > 50) {
			return null;
		}

		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(
				CommonConstant.REST_URL).build();

		AppListDaoInterface daoInterface = restAdapter
				.create(AppListDaoInterface.class);


		TreeMap<String, String> parameters = new TreeMap<String, String>(
				new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				});

		StringBuffer sb = new StringBuffer();
		sb.append("config=format:json,start:").append(start).append(",hit:")
				.append(docNum).append("&&query=default:'").append(query)
				.append("'").append(" OR title:'").append(query).append("'")
				.append(" AND platform:'all'");

		parameters.put("query", sb.toString());
		parameters.put("index_name", CommonConstant.INDEX_NAME);
		parameters.put("Version", CommonConstant.VERSION);
		parameters.put("AccessKeyId", CommonConstant.ACCESS_KEY_ID);
		parameters.put("Timestamp", formatIso8601Date(new Date()));
		parameters.put("SignatureMethod", CommonConstant.SIGNATURE_METHOD);
		parameters.put("SignatureVersion", CommonConstant.SIGNATURE_VERSION);
		parameters.put("SignatureNonce", UUID.randomUUID().toString());
		parameters.put("Signature", getAliyunSign(parameters));
		AppListBean bean = null;
		try {
			bean = daoInterface.getAppList(parameters);
			if (null != bean) {
				display(bean);

			}
		} catch (Exception e) {
			AppLogger.e("== eeeee " + e.toString());
		}
		return bean;
	}

	public AppListBean getAppList(int start, int docNum) {

		if (0 > start || docNum > 50) {
			AppLogger.e("== failed to getAppList. start " + start + " docNum "
					+ docNum);
			return null;
		}

		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(
				CommonConstant.REST_URL).build();

		AppListDaoInterface daoInterface = restAdapter
				.create(AppListDaoInterface.class);
		LinkedTreeMap l;

		TreeMap<String, String> parameters = new TreeMap<String, String>(
				new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				});

		String haQuery = "config=format:json,start:" + start + ",hit:" + docNum
				+ "&&query=platform:'all'";

		parameters.put("query", haQuery);
		parameters.put("index_name", CommonConstant.INDEX_NAME);
		parameters.put("Version", CommonConstant.VERSION);
		parameters.put("AccessKeyId", CommonConstant.ACCESS_KEY_ID);
		parameters.put("Timestamp", formatIso8601Date(new Date()));
		parameters.put("SignatureMethod", CommonConstant.SIGNATURE_METHOD);
		parameters.put("SignatureVersion", CommonConstant.SIGNATURE_VERSION);
		parameters.put("SignatureNonce", UUID.randomUUID().toString());

		parameters.put("Signature", getAliyunSign(parameters));

		AppListBean bean = daoInterface.getAppList(parameters);
		if (null != bean) {
			display(bean);
		}

		return bean;
	}

	public Observable<AppListBean> searchAppListRX(final String query,
			final int begPos, final int docNum) {
		return Observable.create(new Observable.OnSubscribe<AppListBean>() {
			@Override
			public void call(Subscriber<? super AppListBean> subscriber) {
				AppLogger.e("searchAppListRX  call");

				try {
					AppListBean bean = searchAppList(query, begPos, docNum);

					AppLogger.e("searchAppList after bean call");
					if (null != bean && bean.status.equals("OK")) {
						subscriber.onNext(bean);
					}
				} catch (Exception e) {
					AppLogger.e("searchAppList Exception e:" + e.toString());
					subscriber.onError(e);
				}

				subscriber.onCompleted();
				return;
			}
		}).subscribeOn(Schedulers.newThread());
	}

	public Observable<AppListBean> getAppListRX(final int begPos,
			final int docNum) {
		return Observable.create(new Observable.OnSubscribe<AppListBean>() {
			@Override
			public void call(Subscriber<? super AppListBean> subscriber) {

				try {
					AppListBean bean = getAppList(begPos, docNum);
					if (null != bean && bean.status.equals("OK")) {
						subscriber.onNext(bean);
					}
				} catch (Exception e) {
					subscriber.onError(e);
				}

				subscriber.onCompleted();
				return;
			}
		}).subscribeOn(Schedulers.newThread());
	}
}
