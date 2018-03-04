package com.aisiweather.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.aisiweather.android.ui.CircleProgressbar;

/**
 * Created by aisi on 2018/3/4.
 */

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private CircleProgressbar mCircleProgressbar;

    private boolean isClick = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();

        //启动后延时3秒进入界面
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
//                boolean isFirstIn = sp.getBoolean("isFirstIn",true);
//                //第一次进入
//                if (isFirstIn){
//                    //改变状态
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putBoolean("isFirstIn",false);
//                    editor.commit();
//                    //进入引导界面
//                    Intent intent = new Intent(SplashActivity.this,GuideActivity.class);
//                    startActivity(intent);
//                    finish();
//                }else {
////                    //进入主界面
////                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
////                    startActivity(intent);
////                    finish();
//                }
//            }
//        },5000);
    }

    private void initView(){
        mCircleProgressbar = (CircleProgressbar) findViewById(R.id.tv_red_skip);
        mCircleProgressbar.setOutLineColor(Color.TRANSPARENT);
        mCircleProgressbar.setInCircleColor(Color.parseColor("#505559"));
        mCircleProgressbar.setProgressColor(Color.parseColor("#1BB079"));
        mCircleProgressbar.setProgressLineWidth(5);
        mCircleProgressbar.setProgressType(CircleProgressbar.ProgressType.COUNT);
        mCircleProgressbar.setTimeMillis(5000);
        mCircleProgressbar.reStart();

        mCircleProgressbar.setCountdownProgressListener(1,progressListener);

        mCircleProgressbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isClick = true;
                selectActivity();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //当前界面禁用返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private CircleProgressbar.OnCountdownProgressListener progressListener = new CircleProgressbar.OnCountdownProgressListener() {
        @Override
        public void onProgress(int what, int progress)
        {

            if(what==1 && progress==100 && !isClick)
            {
                selectActivity();
                Log.e(TAG, "onProgress: =="+progress );
            }

        }
    };

    //选择要跳转的Activity
    private void selectActivity(){
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        boolean isFirstIn = sp.getBoolean("isFirstIn",true);
        //第一次进入
        if (isFirstIn){
            //改变状态
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstIn",false);
            editor.commit();
            //进入引导界面
            Intent intent = new Intent(SplashActivity.this,GuideActivity.class);
            startActivity(intent);
            finish();
        }else {
            //进入主界面
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
