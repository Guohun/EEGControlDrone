package com.example.yy.bleupdateshowresult.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.yy.bleupdateshowresult.R;

import java.io.File;
import java.util.List;

/**
 * Created by YY on 2017/1/12.
 */

public class FileChooseAdapter extends BaseAdapter {
    private List<File> fileList;
    private LayoutInflater inflater;
    public FileChooseAdapter(Context context, List<File> fileList){
        inflater = LayoutInflater.from(context);
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(null==convertView){
            convertView = inflater.inflate(R.layout.single_item_layout, parent, false);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.textView);
        textView.setText(fileList.get(position).getName());
        return convertView;
    }
}
