package com.aisiweather.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aisiweather.android.service.AutoUpdateService;
import com.aisiweather.android.ui.CustomSeekBar;
import com.aisiweather.android.ui.SwitchButton;
import com.aisiweather.android.R;

import java.util.ArrayList;

/**
 * Created by aisi on 2018/3/2.
 */

public class SettingUpdateActivity extends AppCompatActivity {

    private static final String TAG = "SettingUpdateActivity";

    private SwitchButton switchButton;
    private LinearLayout seekBarLayout;
    private CustomSeekBar seekBar;
    private ArrayList<String> volumeSections = new ArrayList<String>();
    private ImageView imaBack;
    private TextView textUpdateSign;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_update);
        initView();
    }

    private void initView(){
        switchButton = (SwitchButton) findViewById(R.id.switch_button);
        seekBarLayout = (LinearLayout) findViewById(R.id.seek_bar_layout);
        textUpdateSign = (TextView) findViewById(R.id.update_sign);

        if (WeatherActivity.aisiweather){
            Log.d(TAG,"自动更新");
            switchButton.setDefaultStatus(1);
            seekBarLayout.setVisibility(View.VISIBLE);
        }else{
            Log.d(TAG,"不自动更新");
            switchButton.setDefaultStatus(0);
            seekBarLayout.setVisibility(View.GONE);
        }
        switchButton.setOnSwitchListener(new OnSwitchListener() {
            @Override
            public void onSwitchChange(int status) {
                switch (status){
                    //打开状态
                    case 1:
                        seekBarLayout.setVisibility(View.VISIBLE);
                        textUpdateSign.setVisibility(View.VISIBLE);
                        //开启后台更新天气的服务
                        WeatherActivity.aisiweather = true;
                        Intent intentOpen = new Intent(SettingUpdateActivity.this,AutoUpdateService.class);
                        startService(intentOpen);
                        Toast.makeText(SettingUpdateActivity.this,"后台更新天气服务已开启",Toast.LENGTH_SHORT).show();
                        break;
                    //关闭状态
                    case 0:
                        seekBarLayout.setVisibility(View.GONE);
                        textUpdateSign.setVisibility(View.GONE);
                        //关闭后台更新的服务
                        WeatherActivity.aisiweather = false;
                        Intent intentClose = new Intent(SettingUpdateActivity.this,AutoUpdateService.class);
                        stopService(intentClose);
                        Toast.makeText(SettingUpdateActivity.this,"后台更新天气服务已关闭",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });

        //seekBar监听事件
        seekBar = (CustomSeekBar) findViewById(R.id.seek_bar);

        volumeSections.add("1小时");
        volumeSections.add("2小时");
        volumeSections.add("3小时");
        volumeSections.add("4小时");
        volumeSections.add("5小时");
        seekBar.initData(volumeSections);
        seekBar.setProgress(AutoUpdateService.updateCount - 1);
        seekBar.setResponseOnTouch(new ResponseOnTouch() {
            @Override
            public void onTouchResponse(int volume) {
                //设置后台更新数据的频率(1次/volume小时)
                AutoUpdateService.updateCount =  volume+1;
            }
        });

        imaBack = (ImageView) findViewById(R.id.img_setting_back);
        imaBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
