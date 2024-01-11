package com.example.yy.bleupdateshowresult.view;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yy.bleupdateshowresult.R;

/**
 * Created by YY on 2017/1/12.
 */

public class StateDialog implements View.OnClickListener{

    private TextView textView_content;
    private Button button_confirm;
    private ProgressBar progressBar;
    private Dialog dialog;

    public StateDialog(Activity activity){
        LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(R.layout.state_dialog_layout, null);
        textView_content = (TextView)v.findViewById(R.id.textView_content);
        button_confirm = (Button)v.findViewById(R.id.button_confirm);
        progressBar = (ProgressBar)v.findViewById(R.id.progressbar);
        button_confirm.setOnClickListener(this);
        dialog = new Dialog(activity);
        dialog.setContentView(v);
        dialog.setCanceledOnTouchOutside(false);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = activity.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }

    public void setContent(String content){
        textView_content.setText(content);
    }

    public void setProgress(int progress){
        progressBar.setProgress(progress);
    }

    public void setVisibility(boolean showContent, boolean showProgress){
        if(showContent){
            textView_content.setVisibility(View.VISIBLE);
        }else{
            textView_content.setVisibility(View.GONE);
        }

        if(showProgress){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_confirm:
                button_confirm.setVisibility(View.GONE);
                dialog.dismiss();
                break;
        }
    }

    public void showConfirmButton(boolean isShow){
        if(isShow){
            button_confirm.setVisibility(View.VISIBLE);
        }else{
            button_confirm.setVisibility(View.GONE);
        }
    }
}
