package com.zyitong.AppStore.ui;
	
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zyitong.AppStore.R;

public class SearchFrame extends RelativeLayout {
	
	private EditText etSearch;
	private Button cancelButton;
	private OnEtSearchClickListener mOnEtSearchClickListener;
	private OnEtTextChangedListener mOnEtTextChangedListener;
	private OnCancelButtonListener mCancelButtonListener;
	
	public interface OnEtSearchClickListener {
		public void onClick();
	}
	public interface OnEtTextChangedListener {
		public void chanaged();
	}
	public interface OnCancelButtonListener {
		public void onClick();
	}
	
	public SearchFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        infater.inflate(R.layout.search_exit,this,true);      
		etSearch = (EditText) this.findViewById(R.id.edit_search);
		cancelButton = (Button) this.findViewById(R.id.button_cancel);
		etSearch.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mOnEtSearchClickListener.onClick();
				//AppLogger.e("===========etSearch onTouch!++++++===============");
				return false;
			}
		});
		
		etSearch.addTextChangedListener(new TextWatcher() {
			
						
			@Override
			public void afterTextChanged(Editable s) {
							
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mOnEtTextChangedListener.chanaged();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				mCancelButtonListener.onClick();
			}
		});	
	}
	
	public void setEtSearchListener(OnEtSearchClickListener listener){
		mOnEtSearchClickListener = listener;
	}
	public void setEtTextChangedListener(OnEtTextChangedListener listener){
		mOnEtTextChangedListener = listener;
	}
	public void setCancelButtonListener(OnCancelButtonListener listener){
		mCancelButtonListener = listener;
	}

}
