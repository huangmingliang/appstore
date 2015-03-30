package com.zyitong.AppStore.ui;

import com.zyitong.AppStore.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProgressDialog extends Dialog {
    private static MyProgressDialog customProgressDialog = null;
     
    public MyProgressDialog(Context context){
        super(context);
    }
     
    public MyProgressDialog(Context context, int theme) {
        super(context, theme);
    }
     
    public static MyProgressDialog createDialog(Context context){
        customProgressDialog = new MyProgressDialog(context,R.style.CustomProgressDialog);
        customProgressDialog.setContentView(R.layout.progress_round);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
         
        return customProgressDialog;
    }
  
    public void onWindowFocusChanged(boolean hasFocus){
         
        if (customProgressDialog == null){
            return;
        }
         
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
  
    /**
     *
     * [Summary]
     *       setTitile 标题
     * @param strTitle
     * @return
     *
     */
    public MyProgressDialog setTitile(String strTitle){
        return customProgressDialog;
    }
     
    /**
     *
     * [Summary]
     *       setMessage 提示内容
     * @param strMessage
     * @return
     *
     */
    public MyProgressDialog setMessage(int sourseID){
        TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
         
        if (tvMsg != null){
            tvMsg.setText(sourseID);
        }      
        return customProgressDialog;
    }
}
