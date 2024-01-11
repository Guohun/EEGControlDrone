package com.example.yy.bleupdateshowresult.view;

/**
 * Created by taoxingming on 2016/12/3.
 */

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.yy.bleupdateshowresult.R;
import com.example.yy.bleupdateshowresult.util.ToastUtil;


public class FFTValueDialog implements View.OnClickListener{

    private IFFTValueListener ifftValueListener;
    private Dialog dialog;

    private EditText editText;



    public FFTValueDialog(Activity activity, IFFTValueListener ifftValueListener){
        Button button_cancel;
        Button button_confirm;
        this.ifftValueListener = ifftValueListener;
        View v = LayoutInflater.from(activity).inflate(R.layout.fft_value_set_dialog, null, false);
        button_cancel = (Button)v.findViewById(R.id.button_cancel);
        button_confirm = (Button)v.findViewById(R.id.button_confirm);
        this.editText = (EditText)v.findViewById(R.id.editText);

        dialog = new Dialog(activity, R.style.normal_dialog_style);
        button_cancel.setOnClickListener(FFTValueDialog.this);
        button_confirm.setOnClickListener(FFTValueDialog.this);
        dialog.setContentView(v);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_cancel:
                dismissDialog();
                break;
            case R.id.button_confirm:
                confirm();
                break;
        }
    }


    public interface IFFTValueListener{
        void onFFtValueSet(int maxFFTValue);
    }

    public void dismissDialog(){
        if(null!=dialog)
        dialog.dismiss();
    }


    private void confirm(){
        if(null==ifftValueListener) {
            dismissDialog();
            return;
        }else {
            int value;
            try {
                value = Integer.valueOf(editText.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showShort("请输入正确的数据");
                dismissDialog();
                return;
            }
            if (value < 32) {
                ToastUtil.showShort("最大值不能小于32");
                dismissDialog();
                return;
            }
            ifftValueListener.onFFtValueSet(value);
            dismissDialog();
        }
    }

    public void show(){
        if(null!=dialog){
            dialog.show();
        }
    }
}
