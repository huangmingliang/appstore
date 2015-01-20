package com.zyitong.AppStore.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.RejectedExecutionException;

import com.zyitong.AppStore.WeiBoApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;



public class RemoteImageView extends ImageView{
	
	/**
	 * Maximum number of unsuccesful tries of downloading an image
	 */
	private static int MAX_FAILURES = 5;

	public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RemoteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RemoteImageView(Context context) {
		super(context);
		init();
	}

	/**
	 * Sharable code between constructors
	 */
	private void init(){
	}
	
	/**
	 * Remote image location
	 */
	private String mUrl;
	
	/**
	 * Currently successfully grabbed url
	 */
	private String mCurrentlyGrabbedUrl;
	
	/**
	 * Remote image download failure counter
	 */
	private int mFailure;

	/**
	 * Position of the image in the mListView
	 */
	private int mPosition;

	/**
	 * ListView containg this image
	 */
	private ListView mListView;
	
	/**
	 * Default image shown while loading or on url not found
	 */
	private Integer mDefaultImage;

	/**
	 * Loads image from remote location
	 * 
	 * @param url eg. http://random.com/abz.jpg
	 */
	public void setImageUrl(String url){
		
		if(mListView == null && mCurrentlyGrabbedUrl != null && mCurrentlyGrabbedUrl.equals(url)){
			// do nothing image is grabbed & loaded, we are golden
			return;
		}
		
		if(mUrl != null && mUrl.equals(url)){
			mFailure++;
			if(mFailure > MAX_FAILURES){
				Log.e(WeiBoApplication.TAG, "Failed to download "+url+", falling back to default image");
				loadDefaultImage();
				return;
			}
		} else {
			mUrl = url;
			mFailure = 0;
		}

		ImageCache imageCache = WeiBoApplication.getInstance().getImageCache();
		if(imageCache.isCached(url)){
			this.setImageBitmap(imageCache.get(url));
		}
		else {
			try{
				new DownloadTask().execute(url);
			} catch (RejectedExecutionException e) {
				// do nothing, just don't crash
			}
		}
	}
	public void setImageUrl2(String url){
		url = WeiBoApplication.GETIMAGE_API+url;
		setImageUrlSingle(url);
	}
	public void setImageUrl4(String url){
		url = WeiBoApplication.GET_API+"shop/image.jsp?filename="+url;
		setImageUrlSingle(url);
	}
	public void setImageUrlSingle(String url){
		//url = WeiBoApplication.GET_API+"shop/image.jsp?filename="+url;
		if( mCurrentlyGrabbedUrl != null && mCurrentlyGrabbedUrl.equals(url)){
			// do nothing image is grabbed & loaded, we are golden
			return;
		}
		
		if(mUrl != null && mUrl.equals(url)){
			mFailure++;
			if(mFailure > MAX_FAILURES){
				Log.e(WeiBoApplication.TAG, "Failed to download "+url+", falling back to default image");
				loadDefaultImage();
				return;
			}
		} else {
			mUrl = url;
			mFailure = 0;
		}

		ImageCache imageCache = WeiBoApplication.getInstance().getImageCache();
		if(imageCache.isCached(url)){
			this.setImageBitmap(imageCache.get(url));
		}
		else {
			try{
				new DownloadTask().execute(url);
			} catch (RejectedExecutionException e) {
				// do nothing, just don't crash
			}
		}
	}
	/**
	 * Sets default local image shown when remote one is unavailable
	 * 
	 * @param resid
	 */
	public void setDefaultImage(Integer resid){
		mDefaultImage = resid;
	}
	
	/**
	 * Loads default image
	 */
	private void loadDefaultImage(){
		if(mDefaultImage != null)
			setImageResource(mDefaultImage);
	}
	
	/**
	 * Loads image from remote location in the ListView
	 * 
	 * @param url eg. http://random.com/abz.jpg
	 * @param position ListView position where the image is nested
	 * @param listView ListView to which this image belongs
	 */
	public void setImageUrl(String url, int position, ListView listView){
		mPosition = position;
		mListView = listView;
		//url = WeiBoApplication.GET_API+"shop/image.jsp?filename="+url;
		url = WeiBoApplication.GETIMAGE_API+url;
		setImageUrl(url);
	}

	public void setImageUrl3(String url, int position, ListView listView){
		mPosition = position;
		mListView = listView;
		url = WeiBoApplication.GET_API+"shop/image.jsp?filename="+url;

		setImageUrl(url);
	}
	/**
	 * Asynchronous image download task
	 * 
	 * @author Lukasz Wisniewski
	 */
	class DownloadTask extends AsyncTask<String, Void, String>{
		
		private String mTaskUrl;

		@Override
		public void onPreExecute() {
			loadDefaultImage();
			super.onPreExecute();
		}

		@Override
		public String doInBackground(String... params) {

			mTaskUrl = params[0];
			InputStream stream = null;
			URL imageUrl;
			Bitmap bmp = null;

			try {
				imageUrl = new URL(mTaskUrl);
				try {
					stream = imageUrl.openStream();
					bmp = BitmapFactory.decodeStream(stream);
					try {
						if(bmp != null){
							WeiBoApplication.getInstance().getImageCache().put(mTaskUrl, bmp);
							Log.d(WeiBoApplication.TAG, "Image cached "+mTaskUrl);
						} else {
							Log.w(WeiBoApplication.TAG, "Failed to cache "+mTaskUrl);
						}
					} catch (NullPointerException e) {
						Log.w(WeiBoApplication.TAG, "Failed to cache "+mTaskUrl);
					}
				} catch (IOException e) {
					Log.w(WeiBoApplication.TAG, "Couldn't load bitmap from url: " + mTaskUrl);
				} finally {
					try {
						if(stream != null){
							stream.close();
						}
					} catch (IOException e) {}
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return mTaskUrl;
		}

		@Override
		public void onPostExecute(String url) {
			super.onPostExecute(url);
			
			// target url may change while loading
			if(!mTaskUrl.equals(mUrl))
				return;
			
			Bitmap bmp = WeiBoApplication.getInstance().getImageCache().get(url);
			if(bmp == null){
				Log.w(WeiBoApplication.TAG, "Trying again to download " + url);
				RemoteImageView.this.setImageUrl(url);
			} else {
				
				// if image belongs to a list update it only if it's visible
				if(mListView != null)
					if(mPosition < mListView.getFirstVisiblePosition() || mPosition > mListView.getLastVisiblePosition())
						return;
				
				RemoteImageView.this.setImageBitmap(bmp);
				mCurrentlyGrabbedUrl = url;
			}
		}

	};

}