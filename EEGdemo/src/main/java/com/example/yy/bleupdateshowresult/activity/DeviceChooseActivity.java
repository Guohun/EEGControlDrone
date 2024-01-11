package com.example.yy.bleupdateshowresult.activity;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.eegsmart.esalgosdkb.bluetooth.EEGBluetoothService;
import com.eegsmart.esalgosdkb.entity.MainDeviceEntity;
import com.eegsmart.esalgosdkb.serial.Serial;
import com.eegsmart.esalgosdkb.util.Utils;
import com.example.yy.bleupdateshowresult.R;
import com.example.yy.bleupdateshowresult.adapter.SerialAdapter;
import com.example.yy.bleupdateshowresult.adapter.SingleAdapter;
import com.example.yy.bleupdateshowresult.util.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnItemClick;

/**
 * Created by YY on 2017/1/13.
  * improved by uqgzhu1  on 2021
 */

public class DeviceChooseActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, EEGBluetoothService.IScanListener {

    @BindView(R.id.listView)
    ListView listView;

    private BaseAdapter singleAdapter;
    private List<MainDeviceEntity> deviceEntityList = new ArrayList<>();
    private List<String> listSerial = new ArrayList<>();
    public static EEGBluetoothService eegBluetoothService;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_choose_layout);

        initView();
        initData();
//        initService();
    }

    private void initView(){
        setRightClick(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startSearch();
            }
        }, getString(R.string.refresh));
    }

    private int code = Constant.TYPE_BLE;
    private void initData(){
        code = getIntent().getIntExtra("code", code);
        Log.i(TAG, "code " + code);

        switch (code){
            case Constant.TYPE_BLE:
                setTitlebarTitle(getString(R.string.show_connect));
                singleAdapter = new SingleAdapter(getApplicationContext(), deviceEntityList);
                listView.setAdapter(singleAdapter);

                initService();
            break;
            case Constant.TYPE_SERIAL:
                setTitlebarTitle(getString(R.string.connect_serial));
                singleAdapter = new SerialAdapter(getApplicationContext(), listSerial);
                listView.setAdapter(singleAdapter);

                initSerial();
                break;
        }
    }

    // 初始化蓝牙服务
    private void initService(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.i(TAG, "onServiceConnected");
                eegBluetoothService = ((EEGBluetoothService.EEGBluetoothBinder) binder).getService();
                eegBluetoothService.addScanListener(DeviceChooseActivity.this);
                startSearch();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e(TAG, "onServiceDisconnected: ");
            }
        };
        Intent intent = new Intent(DeviceChooseActivity.this, EEGBluetoothService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    // 初始化串口
    public static Serial serial;
    private void initSerial(){
        serial = new Serial(getApplicationContext());
        eegBluetoothService = serial.getEegBluetoothService();
        startSearch();
    }

    private void startSearch() {
        setRightText(getString(R.string.refreshing));

        switch (code) {
            case Constant.TYPE_BLE:
                deviceEntityList.clear();
                eegBluetoothService.startScan(Constant.SEARCH_TIME);
                break;
            case Constant.TYPE_SERIAL:
                listSerial.clear();
                String[] paths = serial.getAllDevicesPath();
                for (String path : paths) {
                    Log.i(TAG, "串口地址 " + path);
                    listSerial.add(path);
                }
                break;
        }
        singleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        startSearch();
    }

    @OnItemClick({R.id.listView})
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(DeviceChooseActivity.this, ShowConnectActivity.class);
        intent.putExtra("code", code);
        Log.i(TAG, "code " + code);

        switch (code) {
            case Constant.TYPE_BLE:
                BluetoothDevice device = deviceEntityList.get(position).device;
                intent.putExtra(ShowConnectActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(ShowConnectActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                intent.putExtra(ShowConnectActivity.EXTRAS_DEVICE, device);
                break;
            case Constant.TYPE_SERIAL:
                String path = listSerial.get(position);
                intent.putExtra(ShowConnectActivity.EXTRAS_DEVICE_NAME, path);
                break;
        }

        startActivity(intent);
    }

    @Override
    public void onDeviceScaned(final BluetoothDevice device, final int rssi) {
        if(device.getName() == null || !device.getName().toLowerCase().startsWith("umind"))
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!Utils.isDeviceListContains(deviceEntityList, device)){
                    Utils.insertIntoList(deviceEntityList, device, rssi);
                    singleAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onScanCompleted() {
        setRightText(getString(R.string.refresh));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        eegBluetoothService.removeScanListener(this);
        unbindService(serviceConnection);
    }
}
