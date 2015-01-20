package com.zyitong.AppStore.loading;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;



@TargetApi(Build.VERSION_CODES.CUPCAKE) @SuppressLint("NewApi") 
public abstract class LoadingDialog<Input, Result> extends AsyncTask<Input, WSError, Result>{

	//private ProgressDialog mProgressDialog;
	@SuppressLint("NewApi") 
	protected Activity mActivity;
	private int mLoadingMsg;
	private int mFailMsg;

	public LoadingDialog(Activity activity, int loadingMsg, int failMsg,int loadcategory){
		this.mActivity = activity;
		this.mLoadingMsg = loadingMsg;
		this.mFailMsg = failMsg;
	}

	@Override
	public void onCancelled() {
		failMsg();
		super.onCancelled();
	}

	@SuppressLint("NewApi") 
	@Override
	public void onPreExecute() {
		String title = "";
		String message = mActivity.getString(mLoadingMsg);
	/*	mProgressDialog = ProgressDialog.show(mActivity, title, message, true, true, new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface dialogInterface) {
				LoadingDialog.this.cancel(true);
			}

		});*/
		super.onPreExecute();
	}

	@Override
	public abstract Result doInBackground(Input... params);

	@Override
	public void onPostExecute(Result result) {
		super.onPostExecute(result);

		/*mProgressDialog.dismiss();*/

		if(result != null){
			doStuffWithResult(result);
		} else {
			failMsg();
		}
	}
	
	protected void failMsg(){
		Toast.makeText(mActivity, mFailMsg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Very abstract function hopefully very meaningful name,
	 * executed when result is other than null
	 * 
	 * @param result
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE) 
	@SuppressLint("NewApi") 
	public abstract void doStuffWithResult(Result result);
	
	@Override
	protected void onProgressUpdate(WSError... values) {
		Toast.makeText(mActivity, values[0].getMessage(), Toast.LENGTH_LONG).show();
		this.cancel(true);
		//mProgressDialog.dismiss();
		super.onProgressUpdate(values);
	}
}