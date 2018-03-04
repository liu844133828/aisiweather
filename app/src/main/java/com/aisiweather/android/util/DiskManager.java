package com.aisiweather.android.util;

import android.os.Environment;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by aisi on 2018/3/2.
 */

public class DiskManager {
    //保存到磁盘
    public static boolean saveToDisk(String fileName, byte[] results){
        boolean flag = false;
        File file = Environment.getExternalStorageDirectory();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            FileOutputStream outputStream = null;

            try {
                outputStream = new FileOutputStream(new File(file, fileName));
                outputStream.write(results,0,results.length);
                flag = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if (outputStream != null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return flag;
    }
}
