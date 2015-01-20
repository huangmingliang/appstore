package com.zyitong.AppStore.common;


import com.zyitong.AppStore.R;
import android.R.interpolator;
import android.content.Context;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;

public class SearchFrame extends RelativeLayout implements View.OnClickListener{
	

	private Button searchButton;
	private EditText etSearch;
	private ImageView ivDeleteText;
	private OnSearchButtonClickListener mOnSearchButtonClickListener;
	private OnEtSearchClickListener mOnEtSearchClickListener;
	
	public interface OnSearchButtonClickListener {
		public void onClick();
	}
	
	public interface OnEtSearchClickListener {
		public void onClick();
	}
	

	public SearchFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        infater.inflate(R.layout.search_exit,this,true);
		searchButton = (Button) this.findViewById(R.id.btnSearch);
		etSearch = (EditText) this.findViewById(R.id.etSearch);
		ivDeleteText = (ImageView) this.findViewById(R.id.ivDeleteText);
		
		searchButton.setOnClickListener(this);
		
		//ExitText获取焦点后的响应事件
		etSearch.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mOnEtSearchClickListener.onClick();
				return false;
			}
		});
		//ExitText输入监控的事件
		etSearch.addTextChangedListener(new TextWatcher() {
			
						
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(s.length() == 0)
				   ivDeleteText.setVisibility(View.GONE);
				else 
				   ivDeleteText.setVisibility(View.VISIBLE);				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub				
			}
		});
		ivDeleteText.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etSearch.setText("");
			}
		});
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnSearch:
			mOnSearchButtonClickListener.onClick();			
			break;
		default:
			break;
		}
	}
	
	public void setSearchButtonListener(OnSearchButtonClickListener listener) {
		mOnSearchButtonClickListener = listener;	
	}
	
	public void setEtSearchListener(OnEtSearchClickListener listener){
		mOnEtSearchClickListener = listener;
	}

}
