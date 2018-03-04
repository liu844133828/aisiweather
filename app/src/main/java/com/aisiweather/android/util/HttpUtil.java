package com.aisiweather.android.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by aisi on 2017/7/3.
 */

public class HttpUtil {
    //通过okHttp发送网络请求
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    //通过HttpURLConnection请求网络
    public static byte[] setHttpURLConnectionRequest(String address){
        URL url = null;
        InputStream in = null;
        ByteArrayOutputStream outputStream = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            in = connection.getInputStream();

            outputStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int count = -1;
            while((count = in.read(data,0,data.length)) != -1){
                outputStream.write(data, 0, count);
            }
            connection.disconnect();
            data = null;
        } catch (IOException e) {
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
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return outputStream.toByteArray();
    }
}
