package com.aisiweather.android;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aisiweather.android.util.DataCleanManager;
import com.aisiweather.android.util.DiskManager;
import com.bumptech.glide.Glide;
import com.aisiweather.android.gson.Forecast;
import com.aisiweather.android.gson.Weather;
import com.aisiweather.android.service.AutoUpdateService;
import com.aisiweather.android.util.HttpUtil;
import com.aisiweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private String mWeatherId;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefresh;

    public DrawerLayout drawerLayout;

    private Button navButton;

    public static boolean aisiweather = true;

    private NavigationView navigationView;

    private String suggestion = "暂无数据";

    private TextView navTem,navCity;

    private static final String address = "http://guolin.tech/api/bing_pic";

    private static final int SUCCESS = 1;
    private static final int FAILED = 0;

    private static final String TAG = "WeatherActivity";

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    Toast.makeText(WeatherActivity.this,"下载成功",Toast.LENGTH_SHORT).show();
                    break;
                case FAILED:
                    Toast.makeText(WeatherActivity.this,"下载失败",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        // 初始化各控件
        initView();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }


        String bingPic = prefs.getString("bing_pic",null);
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
    }

    private void initView(){
        //天气主界面布局,侧拉布局,背景图
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);

        //左上角按钮,城市标题,系统时间
        navButton = (Button) findViewById(R.id.nav_button);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);

        //温度,天气情况,预报,空气质量,舒适度,洗车指数,运动建议
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.sqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);

        //下拉刷新
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        //左侧导航界面
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_select_city);
        //导航栏右下角的城市和温度
        navTem = (TextView) findViewById(R.id.nav_text_tem);
        navCity = (TextView) findViewById(R.id.nav_text_city);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    //选择城市
                    case R.id.nav_select_city:
                        replaceFragment(new ChooseAreaFragment());
                        break;
                    //清空缓存
                    case R.id.nav_clear_cache:
                        showClearCacheDialog();
                        break;
                    //保存背景
                    case R.id.nav_download_background:
                        if (ContextCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            //请求权限
                            ActivityCompat.requestPermissions(WeatherActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        }else {
                            requestImg();
                        }
                        break;
                    //设置
                    case R.id.nav_setting:
                        Intent intent = new Intent(WeatherActivity.this,SettingActivity.class);
                        intent.putExtra("notification_text",suggestion);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_activity_enter_scale,R.anim.anim_activity_exit_scale);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //请求天气数据
                requestWeather(mWeatherId);
                //获取最新图片
                loadBingPic();
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        //当drawerLayout关闭时,移除fragment
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                FragmentManager manager = getSupportFragmentManager();
                if (manager.findFragmentById(R.id.nav_view) != null){
                    manager.beginTransaction().remove(manager.findFragmentById(R.id.nav_view)).commit();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }


    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=ee8ef847c5284288a5c44e3ad974025b";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        //停止刷新
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        //停止刷新
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }


    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);

        getExtraInfo();

        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
        startService(intent);
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final  String bingPic = response.body().string();
                //缓存图片
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    //将菜单替换成城市选择的fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.nav_view,fragment);
        //将当前的fragment添加到返回栈
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //额外获取数据(生活建议,城市,温度)
    private void getExtraInfo(){
        suggestion = sportText.getText().toString();
        navTem.setText(degreeText.getText().toString());
        navCity.setText(titleCity.getText().toString());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //如果重新启动,关闭drawerLayout
        drawerLayout.closeDrawers();
    }

    //显示清空缓存窗体
    private void showClearCacheDialog(){
        String cacheSize="";
        try {
            cacheSize = DataCleanManager.getTotalCacheSize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(WeatherActivity.this);
        dialog.setTitle("清空缓存");
        dialog.setMessage("当前缓存大小为"+cacheSize+",确认清空?");
        //能不能返回键取消
        dialog.setCancelable(true);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataCleanManager.clearAllCache(getApplicationContext());
                Toast.makeText(WeatherActivity.this,"清空成功",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton("取消",null);
        dialog.show();
    }

    private void sendRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //解析图片地址
                HttpUtil.sendOkHttpRequest(address, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //请求失败
                        Toast.makeText(WeatherActivity.this,"请求数据失败",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = Message.obtain();
                        String fileName = null;
                        String bingPic = response.body().string();
                        fileName = bingPic.substring(bingPic.lastIndexOf("/")+1,bingPic.length());
                        Log.d("-------------->","filename  "+fileName+"  bingPic  "+bingPic);
                        if (fileName != null){
                            if (DiskManager.saveToDisk(fileName,HttpUtil.setHttpURLConnectionRequest(bingPic))){
                                message.what = SUCCESS;
                                Log.d(TAG,"下载成功");
                            }else {
                                message.what = FAILED;
                                Log.d(TAG,"下载失败");
                            }
                            handler.sendMessage(message);
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 获取当前网络状态
     */
    private boolean isNetworkConnected(){
        //获取网络状态服务
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    private boolean isWifiConnected(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        //判断wifi是否可用
        return wifiManager.isWifiEnabled();
    }

    //设置网络
    private void showSetNetworkDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置网络");
        builder.setMessage("网络错误,请检查网络状态");
        builder.setPositiveButton("设置网络", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                //参数:系统设置界面的包名,类名
                intent.setClassName("com.android.settings","com.android.settings.MainSettings");
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消",null);
        builder.create().show();
    }

    /**
     * 不是wifi网络提醒
     */
    private void notWifiWarning(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(WeatherActivity.this);
        dialog.setTitle("注意");
        dialog.setMessage("当前网络为数据流量,确认下载吗?");
        //能不能返回键取消
        dialog.setCancelable(true);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendRequest();
            }
        });
        dialog.setNegativeButton("取消",null);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                requestImg();
            }else{
                Toast.makeText(WeatherActivity.this,"权限请求被拒绝",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //下载图片
    private void requestImg(){

        //判断当前网络状态
        if (isNetworkConnected()){
            //wifi
            if (isWifiConnected()){
                sendRequest();
            }else{
                //数据流量
                notWifiWarning();
            }
        }else {
            //请求网络
            showSetNetworkDialog();
        }


    }
}
