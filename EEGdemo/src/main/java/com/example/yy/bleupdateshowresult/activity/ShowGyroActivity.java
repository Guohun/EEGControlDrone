package com.example.yy.bleupdateshowresult.activity;


import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.eegsmart.esalgosdkb.bluetooth.EEGBluetoothService;
import com.eegsmart.esalgosdkb.util.Utils;
import com.example.yy.bleupdateshowresult.R;
import com.example.yy.bleupdateshowresult.view.glsurface.CubeGLSurface;

import butterknife.BindView;

/**
 * Created by YY on 2017/1/16.
  * improved by uqgzhu1  on 2021
 */

public class ShowGyroActivity extends BaseActivity {

    public static final int RAW_GYRO_DATA_LENGTH = 6;

    @BindView(R.id.textView_pitch)
    TextView textView_pitch;
    @BindView(R.id.textView_yaw)
    TextView textView_yaw;
    @BindView(R.id.textView_roll)
    TextView textView_roll;

    @BindView(R.id.cubeGLSurface)
    CubeGLSurface cubeGLSurface;

    private float[] lastGyroData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.show_gyro_layout);
        initTitleBar();
        initData();
    }

    private EEGBluetoothService eegBluetoothService = DeviceChooseActivity.eegBluetoothService;
    private EEGBluetoothService.IConnectStateListener iConnectStateListener = new EEGBluetoothService.IConnectStateListener() {

        @Override
        public void onGyro(int[] gyro, float[] attitude) {
            dealGyro(attitude);
        }
    };
    private  void initTitleBar(){
        setTitlebarTitle(getString(R.string.gyro_data));
    }
    private void initData(){
        lastGyroData = new float[3];
        eegBluetoothService.addConnectStateListener(iConnectStateListener);
    }

    public void dealGyro(final float[] data){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMDPData(data);
                float pitch = data[0]-lastGyroData[0];
                float roll = data[1]-lastGyroData[1];
                float yaw = data[2]-lastGyroData[2];
                cubeGLSurface.setData(pitch, roll, yaw);
                Utils.copyArray(lastGyroData, data);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eegBluetoothService.removeConnectStateListener(iConnectStateListener);
    }


    private void showMDPData(float[] data){
        textView_pitch.setText(getString(R.string.pitch)+ " " + data[0]);
        textView_roll.setText(getString(R.string.roll)+ " " +data[1]);
        textView_yaw.setText(getString(R.string.yaw)+ " " + data[2]);
    }
}
