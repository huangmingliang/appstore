package com.zyitong.AppStore.adapter;


import com.zyitong.AppStore.R;
import com.zyitong.AppStore.common.ArrayListAdapter;
import com.zyitong.AppStore.common.TypeData;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TypeAdapter extends ArrayListAdapter<TypeData> {


	public TypeAdapter(Activity context) {
		super(context);

	}

	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
			if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater mInflater = mContext.getLayoutInflater();
			convertView = mInflater.inflate(R.layout.type_row, null);
			holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.imageView2 = (ImageView) convertView.findViewById(R.id.imageView2);

			holder.textNameView = (TextView)convertView.findViewById(R.id.textNameView);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.imageView1.setImageResource(mList.get(position).getId());
		holder.textNameView.setText(mList.get(position).getName());
		
		return convertView;
	}

	public final class ViewHolder {
		public ImageView imageView1;
		public ImageView imageView2;
		public TextView textNameView;




	}
}
