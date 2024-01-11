package com.example.yy.bleupdateshowresult.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yy.bleupdateshowresult.BuildConfig;
import com.example.yy.bleupdateshowresult.R;
import com.example.yy.bleupdateshowresult.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQ_PER = 1;
    @BindView(R.id.button_showConnect)
    Button button_showConnect;
    @BindView(R.id.bSerial)
    Button bSerial;
    @BindView(R.id.version_tv)
    TextView mVersionTv;

    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final Map<String, String> mPermissionNoteMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVersionTv.setText(BuildConfig.VERSION_NAME);

        mPermissionNoteMap.put(Manifest.permission.ACCESS_FINE_LOCATION, "请允许应用获取位置，以发现蓝牙硬件设备");
        mPermissionNoteMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "请允许应用读写外部存储，用于存储数据");
        mPermissionNoteMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, "请允许应用读写外部存储，用于存储数据");
        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PER) {
            for (int i = 0; i < grantResults.length; i++) {
                if (PackageManager.PERMISSION_GRANTED != grantResults[i]) {
                    Toast.makeText(getApplicationContext(), mPermissionNoteMap.get(permissions[i]), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @OnClick({R.id.button_showConnect, R.id.bSerial, R.id.bUpdate})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_showConnect:
                goActivity(DeviceChooseActivity.class, Constant.TYPE_BLE);
                break;
            case R.id.bSerial:
                goActivity(DeviceChooseActivity.class, Constant.TYPE_SERIAL);
                break;
            case R.id.bUpdate:
                goActivity(UpdateActivity.class, Constant.TYPE_BLE);
                break;
        }
    }

    private void checkPermissions() {
        List<String> perList = new ArrayList<>();
        for (String p : PERMISSIONS) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, p)) {
                perList.add(p);
            }
        }
        if (!perList.isEmpty()) {
            String[] per = new String[perList.size()];
            for (int i = 0; i < per.length; i++) {
                per[i] = perList.get(i);
            }
            ActivityCompat.requestPermissions(this, per, REQ_PER);
        }
    }

    private void goActivity(Class clazz, int code) {
        Intent intent = new Intent(MainActivity.this, clazz);
        intent.putExtra("code", code);
        startActivityForResult(intent, code);
    }

}
