package com.example.yy.bleupdateshowresult.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.yy.bleupdateshowresult.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by User on 2016/8/24.
 */
public class MusicChooseAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<String> list;
    public MusicChooseAdapter(Context context, List<String> list){
        inflater = LayoutInflater.from(context);
        this.list = null!=list?list:new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return list.size();
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
            convertView = inflater.inflate(R.layout.simple_item, parent, false);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.textView_content);
        textView.setText(list.get(position));
        return convertView;
    }
}
