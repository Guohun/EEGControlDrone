package com.example.yy.bleupdateshowresult.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.yy.bleupdateshowresult.R;
import com.example.yy.bleupdateshowresult.adapter.FileChooseAdapter;

import java.io.File;
import java.io.FileReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by YY on 2017/1/12.
  * improved by uqgzhu1  on 2021
 */

public class FileChooseDialog implements AdapterView.OnItemClickListener, View.OnClickListener{

    public static final String FIRMWARE_DIR = "EEGBin";

    private Dialog dialog;
    private ListView listView;
    private TextView textView_noData;
    private FileChooseAdapter fileChooseAdapter;
    private List<File> fileList;
    private IFileChoose iFileChoose;
    private File dir = new File(Environment.getExternalStorageDirectory() + File.separator + FIRMWARE_DIR);
    public FileChooseDialog(Activity context, IFileChoose iFileChoose){
        this.iFileChoose = iFileChoose;
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.file_choose_diallgo_layout, null);
        listView = (ListView)v.findViewById(R.id.listView);
        textView_noData = (TextView)v.findViewById(R.id.textView_noData);
        listView.setOnItemClickListener(this);
        textView_noData.setOnClickListener(this);
        textView_noData.setText(dir.getAbsolutePath());
        fileList = new ArrayList<>();
//        initFile();
        fileChooseAdapter = new FileChooseAdapter(context, fileList);
        listView.setAdapter(fileChooseAdapter);
        listView.setOnItemClickListener(this);
        dialog = new Dialog(context);
        dialog.setTitle("选择升级文件");
        dialog.setContentView(v);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }


    private void initFile(){
        fileList.clear();
        if(!dir.exists()){
            dir.mkdirs();
        }
        File[] files = dir.listFiles();
        for(int i=0;i<files.length;i++){
            if(files[i].isFile()){
                fileList.add(files[i]);
            };
        }

        if(fileList.size()<=0){
//            textView_noData.setVisibility(View.VISIBLE);
            textView_noData.setText(dir.getAbsolutePath()+"\n目录下没有找到文件，请确保有文件");
            listView.setVisibility(View.GONE);
        }else{
//            textView_noData.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
//            Collections.sort(fileList, Collator.getInstance(java.util.Locale.CHINA));//注意：是根据的
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(null!=iFileChoose){
            iFileChoose.onFileChoosed(fileList.get(position));
        }
        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        dialog.dismiss();
    }

    public static interface IFileChoose{
        void onFileChoosed(File file);
    }

    public void show(){
        dialog.show();
        initFile();
    }

    public void dismiss(){
        dialog.dismiss();
    }
}
