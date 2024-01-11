package com.example.yy.bleupdateshowresult.activity;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eegsmart.esalgosdkb.bluetooth.EEGBluetoothService;
import com.eegsmart.esalgosdkb.entity.MainDeviceEntity;
import com.eegsmart.esalgosdkb.util.Utils;
import com.example.yy.bleupdateshowresult.R;
import com.example.yy.bleupdateshowresult.adapter.SingleAdapter;
import com.example.yy.bleupdateshowresult.util.Constant;
import com.example.yy.bleupdateshowresult.util.FileUtil;
import com.example.yy.bleupdateshowresult.view.FileChooseDialog;
import com.example.yy.bleupdateshowresult.view.StateDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by YY on 2017/1/12.
  * improved by uqgzhu1  on 2021
 */

public class UpdateActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener, FileChooseDialog.IFileChoose, EEGBluetoothService.IScanListener,
        EEGBluetoothService.IFirmwareListener {

//    public static final String TAG = "CMApp" + UpdateActivity.class.getSimpleName();
//
//    public static final int SEARCH_TIME = 1000;
//    public static final int CONNECT_TIME_OUT = 10000;
    
    
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.button_search)
    Button button_search;
    @BindView(R.id.button_fileChoose)
    Button button_fileChoose;
    @BindView(R.id.tvFile)
    TextView tvFile;

    private FileChooseDialog fileChooseDialog;
    private StateDialog stateDialog;
    private List<MainDeviceEntity> deviceList;
    private SingleAdapter singleAdapter;
    private byte[] firmwareData;

    private EEGBluetoothService eegBluetoothService;
    private Handler handler;
    private Timer checkUpdateTimer;//用于检查升级过程中是否有升级停止的异常情况
    private boolean isUpdating;     //用于检查升级过程中是否有升级停止的异常情况
    private ServiceConnection sc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_layout);
        initView();
        initData();
        initService();
    }

    private void initView() {
        stateDialog = new StateDialog(this);
        setTitlebarTitle("固件升级");
    }

    private void initData() {
        deviceList = new ArrayList<>();
        singleAdapter = new SingleAdapter(getApplicationContext(), deviceList);
        listView.setAdapter(singleAdapter);

        handler = new Handler();
    }

    private void initService() {
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                eegBluetoothService = ((EEGBluetoothService.EEGBluetoothBinder) binder).getService();
                eegBluetoothService.addScanListener(UpdateActivity.this);
                eegBluetoothService.addConnectStateListener(iConnectStateListener);
                eegBluetoothService.addUpdateFirmwareListener(UpdateActivity.this);
                startSearch();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e(TAG, "onServiceDisconnected: ");
            }
        };
        Intent intent = new Intent(UpdateActivity.this, EEGBluetoothService.class);
        bindService(intent, sc, BIND_AUTO_CREATE);
    }


    private void startSearch() {
        deviceList.clear();
        button_search.setText("正在查找....");
        singleAdapter.notifyDataSetChanged();
        eegBluetoothService.startScan(Constant.SEARCH_TIME);
    }

    @OnItemClick({R.id.listView})
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(null!=firmwareData) {
            stateDialog.show();
            stateDialog.setVisibility(true, false);
            stateDialog.setContent("正在连接....");
            eegBluetoothService.connectDevice(deviceList.get(position).device, Constant.CONNECT_TIME_OUT);
        }else{
            showToast("没有设置选择升级文件，请先选择升级文件");
        }
    }


    @Override
    public void onFileChoosed(final File file) {
        tvFile.setText(file.getName());
        Log.e(TAG, "onFileChoosed========>" + file.getName());
        stateDialog.setContent("正在读取文件...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                firmwareData = FileUtil.getFileData(file);
                Log.e(TAG, "run: 文件读取完毕========》文件长度：" + (firmwareData.length / 1024));
                eegBluetoothService.setUpdateDataBuffer(firmwareData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stateDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    @OnClick({R.id.button_search, R.id.button_fileChoose})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_search:
                startSearch();
                break;
            case R.id.button_fileChoose:
                showFileChooseDialog();
                break;
        }
    }

    private void showFileChooseDialog() {
        if (null == fileChooseDialog) {
            fileChooseDialog = new FileChooseDialog(UpdateActivity.this, UpdateActivity.this);
        }
        fileChooseDialog.show();
    }

    private EEGBluetoothService.IConnectStateListener iConnectStateListener = new EEGBluetoothService.IConnectStateListener() {
        @Override
        public void onConnectStateChanged(boolean isConnected) {
            super.onConnectStateChanged(isConnected);
            Log.e(TAG, "onConnectStateChanged====================>" + isConnected);
            if (isConnected) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "handler=======>准备升级，当前线程为:"+Thread.currentThread());
                        eegBluetoothService.startData(EEGBluetoothService.UPDATE_TYPE);
                        stateDialog.setContent("正在升级...");
                        startCheckTimer();
                        stateDialog.setProgress(0);
                        stateDialog.setVisibility(true, true);
                    }
                }, 200);
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stateDialog.setVisibility(true, false);
                        stateDialog.setContent("连接失败， 也许重启设备蓝牙可以连上...");
                        stateDialog.showConfirmButton(true);
                    }
                });

            }
        }
    };

    @Override
    public void onUpdateCompleted(final boolean isSuccessed) {
        Log.e(TAG, "onConnectStateChanged====================>" + isSuccessed);
        cancelChechTimer();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                if(isSuccessed){
                    result = "升级成功";
                }else{
                    result = "升级失败";
                }
                stateDialog.setContent(result);
                stateDialog.showConfirmButton(true);

            }
        });

    }

    @Override
    public void onUpdateProgress(int total, int current) {
        Log.e(TAG, "onConnectStateChanged====================>" + ((float) current / total));
        final int progress =  current*100/total;
        isUpdating = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stateDialog.setProgress(progress);
            }
        });
    }

    @Override
    public void onDeviceScaned(final BluetoothDevice device, final int rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!Utils.isDeviceListContains(deviceList, device) && device.getName() != null){
                    Utils.insertIntoList(deviceList, device, rssi);
                    singleAdapter.notifyDataSetChanged();
                }
            }
        });
    }



    @Override
    public void onScanCompleted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button_search.setText("查找设备");
            }
        });
    }


    private void showToast(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
    
    private void startCheckTimer(){
        if(null!=checkUpdateTimer){
            try{
                checkUpdateTimer.cancel();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        checkUpdateTimer = new Timer();
        checkUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!isUpdating) {
                            stateDialog.showConfirmButton(true);
                            stateDialog.setVisibility(true, false);
                            stateDialog.setContent("升级失败");
                            eegBluetoothService.close();
                            cancelChechTimer();
                        }else{
                            isUpdating = false;
                        }
                    }
                });
            }
        }, 2000);
    }

    private void cancelChechTimer(){
        if(null!=checkUpdateTimer){
            try{
                checkUpdateTimer.cancel();
            }catch(Exception e){
                e.printStackTrace();
            }
            checkUpdateTimer = null;
        }
        isUpdating = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        eegBluetoothService.removeConnectStateListener(iConnectStateListener);
        eegBluetoothService.removeScanListener(this);
        eegBluetoothService.removeUpdateFirmwareListener(this);
        if(null!=sc){
            try {
                unbindService(sc);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
}
