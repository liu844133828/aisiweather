package com.aisiweather.android.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by aisi on 2017/8/21.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private List<View> views;

    private Context context;

    public ViewPagerAdapter(List<View> views, Context context) {
        this.views = views;
        this.context = context;
    }

    /**
     * 获得ViewPager的长度
     */
    @Override
    public int getCount() {
        return views.size();
    }

    /**
     *判断当前view是否为我们所需的对象
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    /**
     * 移除ViewPager中的view
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView(views.get(position));
    }

    /**
     *向ViewPager中添加view
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager)container).addView(views.get(position));
        return views.get(position);
    }
}
