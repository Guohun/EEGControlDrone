package com.example.yy.bleupdateshowresult.view.glsurface;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by YY on 2017/1/16.
 */

public class CubeRender implements GLSurfaceView.Renderer{
    private float a = 0.5f;
    private float roll;
    private float pitch;
    private float yaw;
    private float[] rotateRatio;
    private float[] vertices=
            {
                    -a,a,a,
                    -a,-a,a,
                    a,-a,a,
                    a,a,a,		//前面

                    a,a,a,
                    a,-a,a,
                    a,-a,-a,
                    a,a,-a,		//右面

                    a,a,-a,
                    a,-a,-a,
                    -a,-a,-a,
                    -a,a,-a,		//后面

                    -a,a,-a,
                    -a,-a,-a,
                    -a,-a,a,
                    -a,a,a,		//左面

                    -a,a,-a,
                    -a,a,a,
                    a,a,a,
                    a,a,-a,		//上面

                    -a,-a,a,
                    -a,-a,-a,
                    a,-a,-a,
                    a,-a,a,		//下面
            };
    //顶点颜色,R,G,B,A
    private float[] colors={
            0.2f,0f,0.7f,1f,
            0f,0.4f,0.3f,1f,
            0.8f,0.1f,0.1f,1f,
            1f,1f,1f,1f,		//前面

            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,		//后面

            0f,0f,1f,1f,
            0f,0f,1f,1f,
            0f,0f,1f,1f,
            0f,0f,1f,1f,		//左面

            0.3f,0.5f,1f,1f,
            0.3f,0.5f,1f,1f,
            0.3f,0.5f,1f,1f,
            0.3f,0.5f,1f,1f,	//上面

            0f,0.2f,0.3f,1f,
            0f,0.4f,0.3f,1f,
            0f,0.3f,0.3f,1f,
            0f,0.4f,0.3f,1f,	//下面

    };

    private short[] indices={
            0,1,2,
            0,2,3,
            4,5,6,
            4,6,7,
            8,9,10,
            8,10,11,
            12,13,14,
            12,14,15,
            16,17,18,
            16,18,19,
            20,21,22,
            20,22,23,
    };

    private FloatBuffer mVertexBuffer,mColorBuffer;
    private ShortBuffer mIndixBuffer;
    private float rx=45.0f;

    public CubeRender(Bitmap bitmap){
        rotateRatio = new float[3];
        ByteBuffer vbb=ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer=vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length*2);
        ibb.order(ByteOrder.nativeOrder());
        mIndixBuffer=ibb.asShortBuffer();
        mIndixBuffer.put(indices);
        mIndixBuffer.position(0);

        ByteBuffer cbb=ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer=cbb.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClearColor(0f, 0f, 0f, 0.5f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3,GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -5f);
//        gl.glRotatef(-45f, 0f, a, 0f);
//        gl.glRotatef(rx, a, a, 0f);
        gl.glRotatef(roll, 1, 0, 0);
        gl.glRotatef(pitch, 0, 1, 0);
        gl.glRotatef(yaw, 0, 0, 1);

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, mIndixBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
        rx++;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float)width/(float)height, 0.1f, 100.f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //启用深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // 所做深度测试的类型
        gl.glDepthFunc(GL10.GL_DITHER);
        //黑色背景
        gl.glClearColor(a, 0f, 0f, a);
        //启用阴影平滑
        gl.glShadeModel(GL10.GL_SMOOTH);
        //清除深度缓存
        gl.glClearDepthf(1.0f);
    }

    public void setData(float roll, float pitch, float yaw){
        this.roll += roll;
        this.pitch += pitch;
        this.yaw += yaw;
    }
}
