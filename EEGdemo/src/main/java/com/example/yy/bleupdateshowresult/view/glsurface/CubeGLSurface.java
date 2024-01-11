package com.example.yy.bleupdateshowresult.view.glsurface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.example.yy.bleupdateshowresult.R;


/**
 * Created by YY on 2017/1/16.
 */

public class CubeGLSurface extends GLSurfaceView {
    private CubeRender renderer;

    public CubeGLSurface(Context context) {
        super(context);
    }

    public CubeGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
//        renderer = new Render_01();
        Bitmap bitmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        renderer = new CubeRender(bitmp);
        setRenderer(renderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
//        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }


    public void setData(float roll, float pitch, float yaw){
        renderer.setData(roll, pitch, yaw);
//        requestRender();
    }

}
