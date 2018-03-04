package com.aisiweather.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aisiweather.android.R;

import com.aisiweather.android.gson.Weather;
import com.aisiweather.android.R;

import java.io.File;

/**
 * Created by aisi on 2018/3/2.
 */

public class SettingActivity extends AppCompatActivity {

    private ImageView imgBack;
    private TextView text_update;
    private TextView notificationText;
    private TextView textView1,textView2,textView3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView(){
        imgBack = (ImageView) findViewById(R.id.img_back);
        text_update = (TextView) findViewById(R.id.text_update);
        notificationText = (TextView) findViewById(R.id.text_notification);
        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        textView3 = (TextView) findViewById(R.id.text3);

        imgBack.setOnClickListener(new MyOnClickListener());
        text_update.setOnClickListener(new MyOnClickListener());
        notificationText.setOnClickListener(new MyOnClickListener());
        textView1.setOnClickListener(new MyOnClickListener());
        textView2.setOnClickListener(new MyOnClickListener());
        textView3.setOnClickListener(new MyOnClickListener());
    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_back:
                    finish();
                    break;
                case R.id.text_update:
                    Intent intent = new Intent(SettingActivity.this,SettingUpdateActivity.class);
                    startActivity(intent);
                    break;
                case R.id.text_notification:
                    NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
                    Intent intentNotification = new Intent(SettingActivity.this,WeatherActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(SettingActivity.this,0,intentNotification,0);
                    Notification notification = new NotificationCompat.Builder(SettingActivity.this)
                            .setContentTitle("最新天气数据提示")
                            .setContentText(getIntent().getStringExtra("notification_text"))
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.logo)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.logo))
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentIntent(pendingIntent)
                            . setAutoCancel(true).build();
                    manager.notify(1,notification);

                    break;
                case R.id.text1:
                    Toast.makeText(SettingActivity.this,"隐私",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.text2:
                    Toast.makeText(SettingActivity.this,"通用",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.text3:
                    Toast.makeText(SettingActivity.this,"艾斯天气\n版本1.0",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_activity_enter_scale,R.anim.anim_activity_exit_scale);
    }
}
