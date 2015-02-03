package com.zyitong.AppStore.http.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

public abstract class LoadingDialog<Input, Result> extends
		AsyncTask<Input, WSError, Result> {

	// private ProgressDialog mProgressDialog;
	@SuppressLint("NewApi")
	protected Activity mActivity;
	private int mFailMsg;

	public LoadingDialog(Activity activity, int loadingMsg, int failMsg,
			int loadcategory) {
		this.mActivity = activity;
		this.mFailMsg = failMsg;
	}

	@Override
	public void onCancelled() {
		failMsg();
		super.onCancelled();
	}

	@Override
	public void onPreExecute() {

		super.onPreExecute();
	}

	@Override
	public abstract Result doInBackground(Input... params);

	@Override
	public void onPostExecute(Result result) {
		super.onPostExecute(result);

		if (result != null) {
			doStuffWithResult(result);
		} else {
			failMsg();
			onLoadfail();
		}
	}

	protected void failMsg() {
		Toast.makeText(mActivity, mFailMsg, Toast.LENGTH_LONG).show();
	}
	
	public abstract void onLoadfail();

	/**
	 * Very abstract function hopefully very meaningful name, executed when
	 * result is other than null
	 * 
	 * @param result
	 * @return
	 */

	public abstract void doStuffWithResult(Result result);

	@Override
	protected void onProgressUpdate(WSError... values) {
		Toast.makeText(mActivity, values[0].getMessage(), Toast.LENGTH_LONG)
				.show();
		this.cancel(true);
		// mProgressDialog.dismiss();
		super.onProgressUpdate(values);
	}
}