package com.example.yy.bleupdateshowresult.util;

import android.content.Context;
import android.widget.Toast;

import com.example.yy.bleupdateshowresult.MyApplication;


/**
 * Created by User on 2016/3/18.
 */
public class ToastUtil {
    private static Context context = MyApplication.getInstance();
//    private static ToastUtil instance;

//    private ToastUtil(Context context){
//        this.context = context;
//    }
//
//    public static ToastUtil getInstance(Context context){
//        if(null==instance){
//            instance = new ToastUtil(context);
//        }
//        return instance;
//    }


    public static void showShort(String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(String str){
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    public static void showShort(int res){
        Toast.makeText(context, context.getResources().getString(res), Toast.LENGTH_SHORT).show();
    }

    public static void showLong(int res){
        Toast.makeText(context, context.getResources().getString(res), Toast.LENGTH_LONG).show();
    }
}
