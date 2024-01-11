package com.example.yy.bleupdateshowresult.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.eegsmart.esalgosdkb.esalgosupport.Complex;
import com.eegsmart.esalgosdkb.esalgosupport.FFT;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by User on 2016/12/1.
  * improved by uqgzhu1  on 2021
 */

public class BarViewNew extends SurfaceView implements SurfaceHolder.Callback{

    public static final String TAG = "Show"+BarViewNew.class.getSimpleName();

    public static final int MAX_Y = 1024;
    public static final int MIN_Y = 128;
    public static final int Y_INTERVAL = 64;

    public static final int X_LINES = 17;
    public static final int Y_LINES = 9;
    public static final int DATA_LENGTH = 256;
//    public static final int NEED_REFRESH_DATA_LENGTH = 64;
    public static final int FFT_Divider = 2<<8;

    public static final int LINE_COLOR = Color.parseColor("#E5160701");;
    public static final int TABLE_COLOR = Color.LTGRAY;
    public static final int TEXT_COLOR = Color.DKGRAY;

    public static final int LEFT_MARGIN = 25;
    public static final int TOP_MARGIN = 15;
    public static final int BOTTOM_MARGIN = 15;
    public static final int RIGHT_MARGIN = 15;

    public static final int BARS = 64;

    public static final int BAR_WIDTH= 3;
    public static final int TABLE_LEIN_WIDTH = 1;
    public static final int TEXT_SIZE = 9;

    private Paint tablePaint;
    private Paint textPaint;
    private Paint barPaint;

    private float yIntervalTable;
    private float xIntervalTable;
    private float barWidth;
    private float barInverval;


    private float yRatio;


    private int maxX;
    private int maxY;
    private int height;
    private int width;

    private float leftMargin;
    private float topMargin;
    private float rightMargin;
    private float bottomMargin;

    private float density;

    private Rect textRect;

    private DrawThread drawThread;

    private float[] barData;
    private LinkedList<Integer> listTempData;
    private LinkedList<Integer> listData;



    public BarViewNew(Context context) {
        this(context, null);
    }

    public BarViewNew(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarViewNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        initData(context);
        initPaint();
        getHolder().addCallback(this);
    }

    private void initData(Context context){
        barData = new float[BARS];
        listTempData = new LinkedList<>();
        listData = new LinkedList<>();
        textRect = new Rect();
        maxX = BARS;
        maxY = MIN_Y;
        density = context.getResources().getDisplayMetrics().density;

        leftMargin = density * LEFT_MARGIN;
        topMargin = density * TOP_MARGIN;
        rightMargin = density * RIGHT_MARGIN;
        bottomMargin = density * BOTTOM_MARGIN;

        barWidth = density*BAR_WIDTH;

    }

    private void initData1() {
        yIntervalTable = (height - topMargin - bottomMargin) / ((float) X_LINES - 1);
        xIntervalTable = (width - leftMargin - rightMargin) / ((float) Y_LINES - 1);
//        float barTotal = (width - leftMargin - rightMargin)/BARS;
//        barInverval = barTotal - barWidth;
        barInverval = ((width - leftMargin - rightMargin) - BARS*barWidth)/(BARS-1);
        resetyRatio();
    }

    private void resetyRatio(){
        yRatio = (height - topMargin - bottomMargin) / maxY;
    }

    private void initPaint(){
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        barPaint.setColor(LINE_COLOR);
        tablePaint.setColor(TABLE_COLOR);
        textPaint.setColor(TEXT_COLOR);

        barPaint.setStyle(Paint.Style.STROKE);
        tablePaint.setStyle(Paint.Style.STROKE);
//        textPaint.setStyle(Paint.Style.STROKE);

        barPaint.setStrokeWidth(barWidth);
        tablePaint.setStrokeWidth(density*TABLE_LEIN_WIDTH);
        textPaint.setTextSize(density * TEXT_SIZE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(holder);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        initData1();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.needRunning = false;
    }

    private void drawTable(Canvas canvas) {
        for (int i = 0; i < Y_LINES; i++) {
            canvas.drawLine(i * xIntervalTable + leftMargin, topMargin, i * xIntervalTable + leftMargin, height - bottomMargin, tablePaint);

            String str = String.valueOf(i * (maxX / (Y_LINES - 1)))+"HZ";
            textPaint.getTextBounds(str, 0, str.length(), textRect);
            canvas.drawText(str, i * xIntervalTable + leftMargin - textRect.width() / 2, height - bottomMargin + textRect.height() * 1.2f, textPaint);
        }

        for (int i = 0; i < X_LINES; i++) {
            canvas.drawLine(leftMargin, i * yIntervalTable + topMargin, width - rightMargin, i * yIntervalTable + topMargin, tablePaint);

            String str = String.valueOf(maxY - i * (maxY / (X_LINES - 1)));
            textPaint.getTextBounds(str, 0, str.length(), textRect);
            canvas.drawText(str, leftMargin - 2 * density - textRect.width(), i * yIntervalTable + topMargin + textRect.height() / 2, textPaint);
        }
    }


    private void drawBar(Canvas canvas){
        float barTotal = barWidth +barInverval;
        float xPosition;
//        Log.e(TAG, "bartotal:" + barTotal + "    barInverval：" + barInverval + "   数据为：---->" + Utils.floats2String(barData));
        for(int i=0;i<barData.length;i++){
//            xPosition = leftMargin + barWidth/2+barInverval+i*barTotal;
            xPosition = leftMargin + (barWidth+barInverval)*i+1*density;
            canvas.drawLine(xPosition, height-bottomMargin, xPosition, height-bottomMargin-barData[i]*yRatio, barPaint);
        }
    }




    private List<Integer> tempListData = new ArrayList<>();
    public void addData(List<Integer> list){
        synchronized (tempListData){
            tempListData.addAll(list);
        }
        drawThread.resumeThread();
    }
    private synchronized void addToDrawData(){
        synchronized (tempListData){
            for(int i=0;i<tempListData.size();i++){
                listData.add(tempListData.get(i));
            }
            tempListData.clear();
        }
    }
    private synchronized Complex[] dealData(LinkedList<Integer> listData){

        while (listData.size()>DATA_LENGTH){
            listData.removeFirst();
        }

        Complex[] complexs = makeComplexs(listData);
        return FFT.fft(complexs);
    }


    private Complex[] makeComplexs(LinkedList<Integer> listData){
        Complex[] complices = new Complex[listData.size()];
        for(int i=0;i<listData.size();i++){
            complices[i] = new Complex(listData.get(i), 0);
        }
        return complices;
    }

    class DrawThread extends Thread {
        SurfaceHolder surfaceHolder;
        boolean needRunning;
        boolean isPaused;

        public DrawThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            needRunning = true;
            isPaused = false;
        }

        @Override
        public void run() {
            while (needRunning && !isInterrupted()) {
                if (!isPaused) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    try {
                        canvas.drawColor(Color.WHITE);
                        drawTable(canvas);
                        addToDrawData();
                        if(listData.size()>=DATA_LENGTH) {
                            Complex[] complices = dealData(listData);
                            for(int i=0;i<barData.length;i++){
                                barData[i] = (float)complices[i].squareValue();
                            }
//                            Log.e(TAG, Utils.floats2String(barData, 0, barData.length));
                            drawBar(canvas);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        pauseThread();
                    }
                } else {
                    waitThread();
                }
            }
        }

        void pauseThread() {
            isPaused = true;
        }

        void resumeThread() {
            isPaused = false;
            synchronized (this) {
                notify();
            }
        }

        private void waitThread() {
            try {
                synchronized (this) {
                    wait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void minusMaxY(){
        if(maxY>MIN_Y){
            maxY -= Y_INTERVAL;
            resetyRatio();
        }

    }

    public void addMaxY(){
//        if(maxY<MAX_Y){
            maxY += Y_INTERVAL;
        resetyRatio();
//        }
    }

    public void setMaxY(int maxY){
        this.maxY = maxY;
        resetyRatio();
    }

    public void setBarColor(int color){
        barPaint.setColor(color);
    }
}
