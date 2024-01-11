package com.example.yy.bleupdateshowresult.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


/**
 * Created by User on 2016/12/1.
  * improved by uqgzhu1  on 2022
 */

public class LineViewNew extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = "Show" + LineViewNew.class.getSimpleName();

    public static final int Y_TABLE_INTERVAL = 64;
    public static final int X_TABLE_INTERVAL = 64;

    public static final int MIN_X = 128;
    public static final int MIN_Y = 128;


    public static final int LINE_LINE_WIDTH = 1;
    public static final int TABLE_LEIN_WIDTH = 1;
    public static final int TEXT_SIZE = 9;

    public static final int LINE_COLOR = Color.parseColor("#E5160701");
    public static final int TABLE_COLOR = Color.LTGRAY;
    public static final int TEXT_COLOR = Color.DKGRAY;

    public static final int LEFT_MARGIN = 25;
    public static final int TOP_MARGIN = 15;
    public static final int BOTTOM_MARGIN = 15;
    public static final int RIGHT_MARGIN = 15;

    public static final int BUFFER_LENGTH = 256;
    public static final int ONE_DRAW_LENGTH = 64;
    public static final int X_LINES = 17;
    public static final int Y_LINES = 9;

    public static final int MAX_X = 2048 + ONE_DRAW_LENGTH;
    public static final int MAX_Y = 2048;


    private float density;
    private Paint linePaint;
    private Paint tablePaint;
    private Paint textPaint;

    private float tableLineWidth;
    private float lineLineWitdh;
    private float textSize;

    private int maxX;
    private int maxY;
    private int currentBufferIndex;

    private float yIntervalTable;
    private float xIntervalTable;

    private float xIntervalLine;
    private float yRatio;

    private float width;
    private float height;

    private float leftMargin;
    private float topMargin;
    private float rightMargin;
    private float bottomMargin;

    private byte[] buffer1;
    private byte[] buffer2;
    private int[] tempData;
    private LinkedList<Integer> drawDataList;
    private DrawThread drawThread;
    private Rect textRect;


    public LineViewNew(Context context) {
        this(context, null);
    }

    public LineViewNew(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineViewNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
        getHolder().addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initPaint();
        initOthers(holder);
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


    private void initPaint() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        linePaint.setColor(LINE_COLOR);
        tablePaint.setColor(TABLE_COLOR);
        textPaint.setColor(TEXT_COLOR);

        linePaint.setStyle(Paint.Style.STROKE);
        tablePaint.setStyle(Paint.Style.STROKE);
//        textPaint.setStyle(Paint.Style.STROKE);

        linePaint.setStrokeWidth(lineLineWitdh);
        tablePaint.setStrokeWidth(tableLineWidth);
        textPaint.setTextSize(textSize);
    }

    private void initData(Context context) {
        textRect = new Rect();
        maxX = 512;
        maxY = 1024;
        density = context.getResources().getDisplayMetrics().density;

        leftMargin = density * LEFT_MARGIN;
        topMargin = density * TOP_MARGIN;
        rightMargin = density * RIGHT_MARGIN;
        bottomMargin = density * BOTTOM_MARGIN;
        lineLineWitdh = density * LINE_LINE_WIDTH;
        tableLineWidth = density * TABLE_LEIN_WIDTH;
        textSize = density * TEXT_SIZE;

        buffer1 = new byte[BUFFER_LENGTH];
        buffer2 = new byte[BUFFER_LENGTH];
        drawDataList = new LinkedList<>();
        tempData = new int[BUFFER_LENGTH / 2];
    }

    private void initData1() {
        yIntervalTable = (height - topMargin - bottomMargin) / ((float) X_LINES - 1);
        xIntervalTable = (width - leftMargin - rightMargin) / ((float) Y_LINES - 1);

        setXIntervalLine();
        setyRatio();
    }

    private void setXIntervalLine() {
        xIntervalLine = (width - leftMargin - rightMargin) / ((float) maxX - 1);
    }

    private void setyRatio() {
        yRatio = (height - topMargin - bottomMargin) / 2f / maxY;
    }

    private void initOthers(SurfaceHolder holder) {
        drawThread = new DrawThread(holder);
        drawThread.start();
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
            int count = 0;
            while (needRunning && !isInterrupted()) {
                if (!isPaused) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    try {
                        cutData();
                        if(count++>2000) {
                            pauseThread();
                            writeSaveData(drawDataList);
                            count=0;
                            //break;
                        }

                        canvas.drawColor(Color.WHITE);
                        drawTable(canvas);
                        drawLine(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

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

    private void drawTable(Canvas canvas) {
        for (int i = 0; i < Y_LINES; i++) {
            canvas.drawLine(i * xIntervalTable + leftMargin, topMargin, i * xIntervalTable + leftMargin, height - bottomMargin, tablePaint);

            String str = String.valueOf(i * (maxX / (Y_LINES - 1)));
            textPaint.getTextBounds(str, 0, str.length(), textRect);
            canvas.drawText(str, i * xIntervalTable + leftMargin - textRect.width() / 2, height - bottomMargin + textRect.height() * 1.2f, textPaint);
        }

        for (int i = 0; i < X_LINES; i++) {
            canvas.drawLine(leftMargin, i * yIntervalTable + topMargin, width - rightMargin, i * yIntervalTable + topMargin, tablePaint);

            String str = String.valueOf(maxY - i * (maxY / (X_LINES - 1) * 2));
            textPaint.getTextBounds(str, 0, str.length(), textRect);
            canvas.drawText(str, leftMargin - 2 * density - textRect.width(), i * yIntervalTable + topMargin + textRect.height() / 2, textPaint);
        }
    }

    private void drawLine(Canvas canvas) {
//        Log.e(TAG, "drawLine: 花了一次--------------------》");
        float centerY = (height - topMargin - bottomMargin) / 2 + topMargin;
        for (int i = 0; i < drawDataList.size() - 1; i++) {
            canvas.drawLine(leftMargin + i * xIntervalLine, centerY - drawDataList.get(i) * yRatio,
                    leftMargin + (i + 1) * xIntervalLine, centerY - drawDataList.get(i + 1) * yRatio, linePaint);
        }
    }


    private List<Integer> tempDrawData = new ArrayList<>();
    public synchronized void addData(List<Integer> list){
//        Log.e(TAG, "addData------------------------->"+list.size());
        synchronized (tempDrawData) {
                tempDrawData.addAll(list);
        }
        drawThread.resumeThread();
    }

    private void cutData(){
        synchronized (tempDrawData) {
            for (int i = 0; i < tempDrawData.size(); i++) {
                drawDataList.addLast(tempDrawData.get(i));
            }
            tempDrawData.clear();
//            Log.e(TAG, "tempDrawData.clear------------------------->" + drawDataList.size());
        }
        while (drawDataList.size() > maxX) {
            drawDataList.removeFirst();
        }
    }



    public void addMaxX() {
        if (maxX < MAX_X) {
            maxX += X_TABLE_INTERVAL;
            setXIntervalLine();
        }
    }

    public void minusMaxX() {
        if (maxX > MIN_X) {
            maxX -= X_TABLE_INTERVAL;
            setXIntervalLine();
        }
    }

    public void addMaxY() {
        if (maxY < MAX_Y) {
            maxY += Y_TABLE_INTERVAL;
            setyRatio();
        }
    }

    public void minusMaxY() {
        if (maxY > MIN_Y) {
            maxY -= Y_TABLE_INTERVAL;
            setyRatio();
        }
    }

    public void setLineColor(int color){
        linePaint.setColor(color);
    }

    private void writeSaveData(List<Integer> dataList) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File dir = new File(path + "/My App Files");
            dir.mkdirs();

            String fileName = "EEG" +timeStamp + ".txt";
            File file = new File(dir, fileName);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < dataList.size(); i++) {
                bw.write(dataList.get(i));
                bw.write(" ");
            }
            bw.close();
//            Toast.makeText(this.getContext(), "File is saved to\n" + dir,
//                    Toast.LENGTH_SHORT).show();
        } catch(Exception ignore) {

        }
    }
}
