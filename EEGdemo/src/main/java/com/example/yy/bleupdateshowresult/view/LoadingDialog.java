package com.example.yy.bleupdateshowresult.view;

/**
 * Created by User on 2016/3/22.
 */

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yy.bleupdateshowresult.R;


public class LoadingDialog {

    private Dialog dialog;
    private AnimationDrawable animationDrawable;
    private View v;
    private TextView loadingText;

    public LoadingDialog(Activity activity){
        dialog = new Dialog(activity, R.style.dialog_style);
        dialog.setCanceledOnTouchOutside(false);
        v = activity.getLayoutInflater().inflate(R.layout.loading_dialog_layout, null);
        loadingText = (TextView) v.findViewById(R.id.tv_loading_text);
        dialog.setCanceledOnTouchOutside(false);
        ImageView imageView = (ImageView)v.findViewById(R.id.progress_view);
        animationDrawable = (AnimationDrawable)imageView.getBackground();
    }

    public void setLoadingText(String text){
        loadingText.setVisibility(View.VISIBLE);
        loadingText.setText(text);
    }

    public void setLoadingText(int resId){
        loadingText.setVisibility(View.VISIBLE);
        loadingText.setText(resId);
    }

    public void show(){
        animationDrawable.start();
        dialog.setContentView(v);
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

    public Boolean isShowing(){
        return dialog.isShowing();
    }

}