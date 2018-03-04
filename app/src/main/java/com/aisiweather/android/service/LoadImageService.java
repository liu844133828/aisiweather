package com.aisiweather.android.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.aisiweather.android.WeatherActivity;
import com.aisiweather.android.util.DiskManager;
import com.aisiweather.android.util.HttpUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by aisi on 2018/3/2.
 */

public class LoadImageService extends Service {

    private static final String TAG = "LoadImageService";

    private String imgUrl,fileUrl;

    private static final int SUCCESS = 1;
    private static final int FAILED = 0;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //关闭服务
            stopSelf();
            if (SUCCESS == msg.what){
                Toast.makeText(getApplicationContext(),"下载成功",Toast.LENGTH_SHORT).show();
            }
            if (FAILED == msg.what){
                Toast.makeText(getApplicationContext(),"下载失败",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        new Thread(new MyThread()).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MyThread implements Runnable{

        @Override
        public void run() {
            //下载背景图片
            String requestBingPic = "http://guolin.tech/api/bing_pic";
            HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {

                HttpURLConnection connection = null;
                InputStream in = null;
                ByteArrayOutputStream outputStream = null;

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    imgUrl = response.body().string();
                    fileUrl = imgUrl.substring(imgUrl.lastIndexOf("/")+1);

                    try {
                        if (imgUrl == null){
                            return;
                        }
                        URL url = new URL(imgUrl);
                        try {
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(8000);
                            connection.setReadTimeout(8000);

                            in = connection.getInputStream();

                            outputStream = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int len = -1;
                            while ((len = in.read(buffer)) != -1){
                                outputStream.write(buffer,0,len);
                            }


                            boolean flag = DiskManager.saveToDisk(fileUrl,outputStream.toByteArray());
                            if (flag){
                                Message message = Message.obtain();
                                message.what = SUCCESS;
                                handler.sendMessage(message);
                            }else{
                                Message message = Message.obtain();
                                message.what = FAILED;
                                handler.sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            if (in != null){
                                in.close();
                            }
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }finally {
                        if (in != null){
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (outputStream != null){
                            outputStream.close();
                        }
                        if (connection != null){
                            connection.disconnect();
                        }
                    }
                }
            });
        }
    }
}
