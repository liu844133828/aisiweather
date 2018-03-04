package com.aisiweather.android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.aisiweather.android.OnSwitchListener;
import com.aisiweather.android.R;

/**
 * Created by aisi on 2018/3/2.
 * 自定义控件SwitchButton
 */

public class SwitchButton extends RelativeLayout {

    private static String TAG = "SwitchButton";

    //SwitchButton打开
    public static final int OPEN = 1;
    //SwitchButton关闭
    public static final int CLOSE = 0;

    //SwitchButton默认状态
    private int defaultStatus;
    //SwitchButton当前状态
    private static int currentStatus;

    //状态变化接口
    private OnSwitchListener onSwitchListener;

    //自定义属性数组
    private TypedArray typedArray;

    //SwitchButton底部布局
    private RelativeLayout relativeLayout;
    //SwitchButton开关按钮
    private View view;

    public SwitchButton(Context context) {
        super(context);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.switch_button_layout, this);
        //获取自定义属性
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        //初始化视图和控件
        initView();
        //初始化按钮的状态
        init();
    }

    private void initView(){
        relativeLayout = (RelativeLayout) findViewById(R.id.switch_layout);
        view = findViewById(R.id.view_switch);
    }

    private void init(){
        defaultStatus = typedArray.getInt(R.styleable.SwitchButton_defaultStatus, 1);

        changStatus(defaultStatus);
    }

    //改变控件状态
    private void changStatus(int status){
        defaultStatus = status;
        if (defaultStatus == 0) {
            //关闭状态
            relativeLayout.setBackgroundResource(R.drawable.bg_switch_bottom_close);
            view.setBackgroundResource(R.drawable.bg_switch_top_close);
            //设置圆形部分的位置(关闭)
            setViewLocation(view, RelativeLayout.ALIGN_PARENT_LEFT);
            currentStatus = CLOSE;
        } else if (defaultStatus == 1) {
            //打开状态
            relativeLayout.setBackgroundResource(R.drawable.bg_switch_bottom_open);
            view.setBackgroundResource(R.drawable.bg_switch_top_open);
            //设置圆形部分的位置(开启)
            setViewLocation(view, RelativeLayout.ALIGN_PARENT_RIGHT);
            currentStatus = OPEN;
        }
    }

    //设置默认状态
    public void setDefaultStatus(int status){
        changStatus(status);
        Log.d(TAG,"status "+status);

    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int Status) {
        currentStatus = Status;
    }

    private void setViewLocation(View view, int location){
        //获取view的布局参数
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (location == RelativeLayout.ALIGN_PARENT_LEFT) {
            //移除align_parent_right属性
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
        } else if (location == RelativeLayout.ALIGN_PARENT_RIGHT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //移除align_parent_left属性
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }
        }
        layoutParams.addRule(location);
        view.setLayoutParams(layoutParams);
    }

    //监听触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //按下时,改变SwitchButton的状态
                changeStatus();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 改变SwitchButton的状态
     */
    private void changeStatus(){
        if (currentStatus == OPEN) {
            closeButton();
        } else if (currentStatus == CLOSE) {
            openButton();
        }
        //监听状态的改变
        if (onSwitchListener != null) {
            onSwitchListener.onSwitchChange(currentStatus);
        }
    }

    /**
     * 关闭按钮
     */
    private void closeButton() {
        relativeLayout.setBackgroundResource(R.drawable.bg_switch_bottom_close);
        view.setBackgroundResource(R.drawable.bg_switch_top_close);
        setViewLocation(view, RelativeLayout.ALIGN_PARENT_LEFT);
        currentStatus = CLOSE;
    }

    /**
     * 打开按钮
     */
    private void openButton() {
        relativeLayout.setBackgroundResource(R.drawable.bg_switch_bottom_open);
        view.setBackgroundResource(R.drawable.bg_switch_top_open);
        setViewLocation(view, RelativeLayout.ALIGN_PARENT_RIGHT);
        currentStatus = OPEN;
    }

    //开关按钮的监听事件
    public void setOnSwitchListener(OnSwitchListener onSwitchListener) {
        this.onSwitchListener = onSwitchListener;
    }

    //使SwitchButton的长度是宽度的3倍
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec * 3, heightMeasureSpec);
    }

}
