package com.aisiweather.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.aisiweather.android.adapter.ViewPagerAdapter;
import com.aisiweather.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aisi on 2017/8/21.
 */

public class GuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    private ViewPager viewPager;

    private ViewPagerAdapter viewPagerAdapter;

    private List<View> views;

    private ImageView[] dots;

    private int[] ids = {R.id.img1,R.id.img2,R.id.img3,R.id.img4};

    private Button btnStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        initViews();
        initDots();
    }

    private void initViews(){
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.guide_one,null));
        views.add(inflater.inflate(R.layout.guide_two,null));
        views.add(inflater.inflate(R.layout.guide_three,null));
        views.add(inflater.inflate(R.layout.guide_four,null));

        viewPagerAdapter = new ViewPagerAdapter(views,this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);

        btnStart = (Button) views.get(3).findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initDots(){
        dots = new ImageView[views.size()];
        for (int i=0;i<views.size();i++){
            dots[i] = (ImageView) findViewById(ids[i]);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i=0;i<ids.length;i++){
            if (i == position){
                dots[i].setImageResource(R.drawable.dot_selected);
            }else{
                dots[i].setImageResource(R.drawable.dot_unselected);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
