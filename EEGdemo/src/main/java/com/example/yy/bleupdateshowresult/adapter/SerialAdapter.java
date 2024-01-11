package com.example.yy.bleupdateshowresult.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yy.bleupdateshowresult.R;

import java.util.List;


/**
 * Created by User on 2016/12/21.
 */

public class SerialAdapter extends BaseAdapter {

    private List<String> list;
    private LayoutInflater inflater;

    public SerialAdapter(Context context, List<String> list){
        inflater = LayoutInflater.from(context);
        this.list = list;
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
            convertView = inflater.inflate(R.layout.single_item_layout, parent, false);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.textView);
        TextView textView_rssid = (TextView)convertView.findViewById(R.id.textView_ssid);

        String path = list.get(position);
        textView.setText(path);

        return convertView;
    }
}
