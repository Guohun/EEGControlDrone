package com.example.yy.bleupdateshowresult.util;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Environment;

import com.example.yy.bleupdateshowresult.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by YY on 2017/1/10.
 */

public class FileUtil {

    public static byte[] getFirmwareData(){
        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File(sdCard, "Android"+File.separator+"dd.bin");
        byte[] buffer = new byte[(int)file.length()];
//        byte[] buffer = new byte[1024];
        int readCount=0;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            readCount = fis.read(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null!=fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer;
    }

    public static byte[] getFileData(File file){
        byte[] buffer = new byte[(int)file.length()];
        int readCount=0;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            readCount = fis.read(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null!=fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer;
    }

    public static void writeFile(String path, String text){
        File file = new File(path);
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            if(!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(text);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static AssetFileDescriptor getDescriptorFromAssets(String name){
        AssetManager am = MyApplication.getInstance().getAssets();
        AssetFileDescriptor afd = null;
        try {
            afd = am.openFd(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return afd;
    }
}
