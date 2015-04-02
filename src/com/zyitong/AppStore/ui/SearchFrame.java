package com.zyitong.AppStore.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zyitong.AppStore.R;
import com.zyitong.AppStore.tools.AppLogger;

public class SearchFrame extends RelativeLayout {

	private EditText etSearch;
	private ImageView ivDeleteText;
	private OnEtTextChangedListener mOnEtTextChangedListener;

	public interface OnEtSearchClickListener {
		public void onClick();
	}

	public interface OnEtTextChangedListener {
		public void chanaged();
	}

	public SearchFrame(final Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater infater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		infater.inflate(R.layout.search_exit, this, true);
		etSearch = (EditText) this.findViewById(R.id.edit_search);
		ivDeleteText = (ImageView) this.findViewById(R.id.ivDeleteText);

		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					ivDeleteText.setVisibility(View.GONE);
				} else {
					ivDeleteText.setVisibility(View.VISIBLE);
				}
				
				mOnEtTextChangedListener.chanaged();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}
		});

		ivDeleteText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etSearch.setText("");
			}
		});
	}

	public void setEtTextChangedListener(OnEtTextChangedListener listener) {
		mOnEtTextChangedListener = listener;
	}
}
