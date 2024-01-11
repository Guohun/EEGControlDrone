package com.example.yy.bleupdateshowresult.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yy.bleupdateshowresult.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 2016/6/14.
 */
public class BaseActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();

    ImageView imageView_back;
    TextView textView_title;
    TextView textView_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        imageView_back = (ImageView)findViewById(R.id.imageView_back);
        textView_title = (TextView)findViewById(R.id.textView_title);
        textView_right = (TextView)findViewById(R.id.textView_right);
        Log.e(TAG, "initView: (null==imageView_back)="+(null==imageView_back));
        if(null!=imageView_back){
            imageView_back.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Log.e(TAG, "onClick: ");
                    finish();
                }
            });
        }
    }

    protected void setTitlebarTitle(String title){
        if(null!=textView_title){
            textView_title.setText(title);
        }
    }

    protected void setRightClick(View.OnClickListener onClickListener, String rightText){
        if(null!=textView_right){
            textView_right.setVisibility(View.VISIBLE);
            textView_right.setOnClickListener(onClickListener);
            textView_right.setText(rightText);
        }
    }

    protected void setRightText(String str){
        if(null!=textView_right){
            textView_right.setText(str);
        }
    }
}
