package com.zyitong.AppStore.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zyitong.AppStore.R;
import com.zyitong.AppStore.tools.AppLogger;
import com.zyitong.AppStore.tools.UtilFun;

public class AutoListView extends ListView implements OnScrollListener {
	public static final int REFRESH = 0;
	public static final int LOAD = 1;
	public static final int ERROR = 2;
	public static final int SEARCH = 3;
	public static final int BACK = 4;
	public static final int RESUME = 5;

	private static final int SPACE = 20;

	public static final int NONE = 0;
	public static final int PULL = 1;
	public static final int RELEASE = 2;
	public static final int REFRESHING = 3;
	private int state;

	private LayoutInflater inflater;
	private View header;
	private View footer;
	private TextView tip;
	//private SlideShowView headerViewPager;
	//private TextView lastUpdate;
	private ImageView arrow;
	//private ProgressBar refreshing;
	//public SearchFrame searchFrame;

	private TextView noData;
	private TextView loadFull;
	private TextView more;
	private ProgressBar loading;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;
	
	private int firstVisibleItem;
	//private int totalItemCount;
	private int scrollState;
	private int headerContentInitialHeight;
	private int headerContentHeight;

	private boolean isRecorded;
	private boolean isLoading;
	private boolean loadEnable = true;
	public boolean isLoadFull;
	public static int pageSize = 8;
	public Context context ;

	private OnSearchListener onSearchListener;
	private OnLoadListener onLoadListener;

	public AutoListView(Context context) {
		super(context);
		this.context = context;
		initView(context);
	}

	public AutoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView(context);
	}

	public AutoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initView(context);
	}

	public void setOnRefreshListener(OnSearchListener onSearchListener) {
		this.onSearchListener = onSearchListener;
	}

	public void setOnLoadListener(OnLoadListener onLoadListener) {
		this.loadEnable = true;
		this.onLoadListener = onLoadListener;
	}

	public boolean isLoadEnable() {
		return loadEnable;
	}

	public void setLoadEnable(boolean loadEnable) {
		this.loadEnable = loadEnable;
		this.removeFooterView(footer);
	}

	public int getPageSize() {
		return pageSize;
	}
	
	public int getState(){
		return state;
	}

	public void setPageSize(int pageSize) {
		AutoListView.pageSize = pageSize;
	}

	private void initView(Context context) {

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(100);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(100);
		reverseAnimation.setFillAfter(true);

		inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.listview_footer, null);
		loadFull = (TextView) footer.findViewById(R.id.loadFull);
		noData = (TextView) footer.findViewById(R.id.noData);
		more = (TextView) footer.findViewById(R.id.more);
		loading = (ProgressBar) footer.findViewById(R.id.loading);

		header = inflater.inflate(R.layout.pull_to_refresh_header, null);
		//searchFrame = (SearchFrame) header.findViewById(R.id.searchframe);
		arrow = (ImageView) header.findViewById(R.id.arrow);
		tip = (TextView) header.findViewById(R.id.tip);
		//headerViewPager = (SlideShowView) header.findViewById(R.id.header_viewpager);
		//lastUpdate = (TextView) header.findViewById(R.id.lastUpdate);
		//refreshing = (ProgressBar) header.findViewById(R.id.refreshing);

		headerContentInitialHeight = header.getPaddingTop();
		measureView(header);
		headerContentHeight = header.getMeasuredHeight();
		topPadding(-headerContentHeight);
		this.addHeaderView(header);
		this.addFooterView(footer);
		this.setOnScrollListener(this);
		
		
	}

	public void OnSearch() {
		if (onSearchListener != null) {
			onSearchListener.OnSearch();
		}
	}

	public void onLoad() {
		if (onLoadListener != null) {
			onLoadListener.onLoad();
		}
	}
	
	
	public void onRefreshComplete(String updateTime) {
		//lastUpdate.setText(this.getContext().getString(R.string.lastUpdateTime,UtilFun.getCurrentTime()));
		state = NONE;
		refreshHeaderViewByState();
	}

	public void onRefreshComplete() {
		String currentTime = UtilFun.getCurrentTime();
		onRefreshComplete(currentTime);
	}

	public void onLoadComplete() {
		isLoading = false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		//this.totalItemCount = totalItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		ifNeedLoad(view, scrollState);
	}

	private void ifNeedLoad(AbsListView view, int scrollState) {
		if (!loadEnable) {
			return;
		}
		try {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& !isLoading
					&& view.getLastVisiblePosition() == view
							.getPositionForView(footer) && !isLoadFull) {
				onLoad();
				isLoading = true;
			}
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
			
				if (firstVisibleItem == 0) {
					isRecorded = true;
					startY = (int) ev.getY();
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (state == PULL) {
					state = NONE;
					refreshHeaderViewByState();
				} else if (state == RELEASE) {
					state = REFRESHING;
					refreshHeaderViewByState();
					OnSearch();
				}
				isRecorded = false;
				break;
			case MotionEvent.ACTION_MOVE:
				whenMove(ev);
				break;
			}
			
		} catch (Exception e) {
			AppLogger.e("listview onTouchEvent Exception");
			return super.onTouchEvent(ev);
		}
		return super.onTouchEvent(ev);
	}

	private void whenMove(MotionEvent ev) {
		if (!isRecorded) {
			return;
		}
		int tmpY = (int) ev.getY();
		int space = tmpY - startY;
		
		int topPadding = space - headerContentHeight;
		switch (state) {
		case NONE:
			if (space > 0) {
				state = PULL;
				refreshHeaderViewByState();
			}
			break;
		case PULL:
			topPadding(topPadding);
			if (scrollState == SCROLL_STATE_TOUCH_SCROLL
					&& space > headerContentHeight + SPACE) {
				state = RELEASE;
				refreshHeaderViewByState();
			}
			break;
		case RELEASE:
			topPadding(topPadding);
			if (space > 0 && space < headerContentHeight + SPACE) {
				state = PULL;
				refreshHeaderViewByState();
			} else if (space <= 0) {
				state = NONE;
				refreshHeaderViewByState();
			}
			break;
		}
	}

	private void topPadding(int topPadding) {
		header.setPadding(header.getPaddingLeft(), topPadding,
				header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}

	

	public void setResultSize(int resultSize) {
		
		if (resultSize == 0) {
			isLoadFull = true;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			noData.setVisibility(View.VISIBLE);
		} else if (resultSize > 0 && resultSize < pageSize) {
			isLoadFull = true;
			loadFull.setVisibility(View.VISIBLE);
			loading.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			noData.setVisibility(View.GONE);
		} else if (resultSize == pageSize) {
			isLoadFull = false;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
			more.setVisibility(View.VISIBLE);
			noData.setVisibility(View.GONE);
		} else if (resultSize == pageSize+1){
			isLoadFull = false;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			noData.setVisibility(View.GONE);
		} else if (resultSize > pageSize+1){
			isLoadFull = true;
			loadFull.setVisibility(View.VISIBLE);
			loading.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			noData.setVisibility(View.GONE);
		}
	}

	private void refreshHeaderViewByState() {
		switch (state) {
		case NONE:
			topPadding(-headerContentHeight);
			tip.setText(R.string.pull_to_refresh);
			//refreshing.setVisibility(View.GONE);
			//searchFrame.setVisibility(View.GONE);
			arrow.clearAnimation();
			arrow.setImageResource(R.drawable.pull_to_refresh_arrow);
			break;
		case PULL:
			arrow.setVisibility(View.VISIBLE);
			tip.setVisibility(View.VISIBLE);
			//lastUpdate.setVisibility(View.VISIBLE);
			//refreshing.setVisibility(View.GONE);
			//searchFrame.setVisibility(View.GONE);
			tip.setText(R.string.pull_to_refresh);
			arrow.clearAnimation();
			arrow.setAnimation(reverseAnimation);
			break;
		case RELEASE:
			arrow.setVisibility(View.VISIBLE);
			tip.setVisibility(View.VISIBLE);
			//lastUpdate.setVisibility(View.VISIBLE);
			//refreshing.setVisibility(View.GONE);
			//searchFrame.setVisibility(View.GONE);
			tip.setText(R.string.pull_to_refresh);
			tip.setText(R.string.release_to_refresh);
			arrow.clearAnimation();
			arrow.setAnimation(animation);
			break;
		case REFRESHING:
			topPadding(headerContentInitialHeight);
			//refreshing.setVisibility(View.VISIBLE);
			//searchFrame.setVisibility(View.VISIBLE);
			arrow.clearAnimation();
			arrow.setVisibility(View.GONE);
			tip.setVisibility(View.GONE);
			//lastUpdate.setVisibility(View.GONE);
			break;
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public interface OnSearchListener {
		public void OnSearch();
	}

	public interface OnLoadListener {
		public void onLoad();
	}
}
