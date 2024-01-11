package com.example.yy.bleupdateshowresult;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import com.eegsmart.esalgosdkb.ESAlgoSDKB;

/**
 * Created by User on 2016/6/14.
 */
public class MyApplication extends Application {
    private static MyApplication instance;//= new MyApplication();
    public BluetoothDevice currentBluetoothDevice = null;     //当前正在连接的蓝牙设备

    private static BluetoothDevice device = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ESAlgoSDKB.init();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public void setBluetoothDevice(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothDevice getBluetoothDevice() {
        return device;
    }
}
