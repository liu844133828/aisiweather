package com.aisiweather.android.ui;

/**
 * Created by aisi on 2018/3/3.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.aisiweather.android.R;
import com.aisiweather.android.ResponseOnTouch;

import java.util.ArrayList;


public class CustomSeekBar extends View {

    private final String TAG = "CustomSeekBar";

    private int width;
    private int height;

    private int downX = 0;
    private int downY = 0;

    private int upX = 0;
    private int upY = 0;

    private int moveX = 0;
    private int moveY = 0;

    private float scale = 0;
    private int perWidth = 0;
    private Paint mPaint;
    private Paint mTextPaint;
    private Paint buttonPaint;
    private Canvas canvas;
    private Bitmap bitmap;
    //节点上的游标
    private Bitmap cursorSpot;
    //seekBar上spot原始节点,spot_on选择后的节点
    private Bitmap spot;
    private Bitmap spotOn;

    //点击的热区
    private int hotarea = 100;
    //当前所处的分段
    private int currentSection = 0;
    private ResponseOnTouch responseOnTouch;
    //第一个点的起始位置起始，图片的长宽是16，所以取一半的距离
    private int selfBitMapHeight = 8;
    //字与下方点的距离，因为字体字体是40px，再加上10的间隔
    private int textSpotMargin = 50;
    //进度条的绿色,进度条的灰色,字体的灰色
    private int[] colors = new int[]{0xFF1A9B1A,0xFF8C8B8C};
    private int textSize;
    private int circleRadius;
    private ArrayList<String> sectionTitle;


    public CustomSeekBar(Context context) {
        super(context);
    }
    public CustomSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        currentSection = 0;
        bitmap = Bitmap.createBitmap(900, 900, Bitmap.Config.ARGB_8888);

        canvas = new Canvas();
        canvas.setBitmap(bitmap);

        //节点设置图片资源
        cursorSpot = BitmapFactory.decodeResource(getResources(), R.drawable.seek_bar_spot_on);
        spot = BitmapFactory.decodeResource(getResources(),R.drawable.seek_bar_spot);
        spotOn = BitmapFactory.decodeResource(getResources(),R.drawable.seek_bar_spot_on);

        selfBitMapHeight = cursorSpot.getWidth() / 2;
        textSpotMargin = selfBitMapHeight + 20;

        //绘制节点
        circleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.DITHER_FLAG);
        //锯齿不显示
        mPaint.setAntiAlias(true);
        //设置空心线宽
        mPaint.setStrokeWidth(circleRadius);

        //绘制字体
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        mTextPaint = new Paint(Paint.DITHER_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(0xFF8C8B8C);


        buttonPaint = new Paint(Paint.DITHER_FLAG);
        buttonPaint.setAntiAlias(true);

        initData(sectionTitle);
    }
    /**
     * 实例化后调用，设置bar的段数和文字
     */
    public void initData(ArrayList<String> section){
        if(section != null){
            sectionTitle = section;
        }else {
            String[] str = new String[]{"1", "2", "3","4"};
            sectionTitle = new ArrayList<String>();
            for (int i = 0; i < str.length; i++) {
                sectionTitle.add(str[i]);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        width = widthSize;
        float scaleX = widthSize / 1080f;
        float scaleY = heightSize / 1920f;
        scale = Math.max(scaleX,scaleY);
        //控件的高度
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 62, getResources().getDisplayMetrics());
        setMeasuredDimension(width, height);
        //每一段线段的宽度 (线宽 - 节点 - 两端半个节点) / 线段数
        perWidth = (width - sectionTitle.size() * cursorSpot.getWidth() - cursorSpot.getWidth()) / (sectionTitle.size()-1);
        hotarea = perWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAlpha(0);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        canvas.drawBitmap(bitmap, 0, 0, null);
        mPaint.setAlpha(255);
        mPaint.setColor(colors[1]);

        //绘制整条线段
        canvas.drawLine(selfBitMapHeight, height * 2 / 3, width - cursorSpot.getWidth(), height * 2 / 3, mPaint);
        int section = 0;
        while(section < sectionTitle.size()){

            if(section < currentSection) {
                if (currentSection < sectionTitle.size()){
                    mPaint.setColor(colors[0]);
                    //划过的线条绘制 起点: 左边距+节点直径*节点个数+线宽*线的个数
                    canvas.drawLine(selfBitMapHeight+cursorSpot.getWidth()*(section+1)+perWidth*section,
                            height * 2 / 3,
                            selfBitMapHeight+cursorSpot.getWidth()*(section+1)+perWidth*(section+1),
                            height * 2 / 3, mPaint);
                    //划过的节点绘制
                    canvas.drawBitmap(spotOn, selfBitMapHeight + cursorSpot.getWidth()*(section)+perWidth*section,
                            height * 2 / 3 - selfBitMapHeight,mPaint);
                }
            }else{
                mPaint.setAlpha(255);
                //未划过的节点绘制
                canvas.drawBitmap(spot,  selfBitMapHeight + cursorSpot.getWidth()*(section+1)+perWidth*(section+1),
                            height * 2 / 3 - selfBitMapHeight, mPaint);
            }

            //绘制title
            if(section == sectionTitle.size()-1) {
                canvas.drawText(sectionTitle.get(section),
                        width - cursorSpot.getWidth()- selfBitMapHeight - textSize / 2,
                        height * 2 / 3 - textSpotMargin, mTextPaint);
            }else{
                canvas.drawText(sectionTitle.get(section),
                        selfBitMapHeight + section * perWidth + section * cursorSpot.getWidth(),
                        height * 2 / 3 - textSpotMargin, mTextPaint);
            }
            section++;
        }

        //绘制游标
        if(currentSection == sectionTitle.size()-1){
            canvas.drawBitmap(cursorSpot,
                    width - cursorSpot.getWidth() - selfBitMapHeight,
                    height * 2 / 3 - selfBitMapHeight, buttonPaint);
        }else {
            canvas.drawBitmap(cursorSpot, selfBitMapHeight + cursorSpot.getWidth()*(currentSection)+perWidth*currentSection,
                    height * 2 / 3 - selfBitMapHeight,mPaint);
        }
    }

    /**
     * 触摸事件处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cursorSpot = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_button_up);
                downX = (int) event.getX();
                downY = (int) event.getY();
                responseTouch(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
                cursorSpot = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_button_up);
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                responseTouch(moveX, moveY);
                break;
            case MotionEvent.ACTION_UP:
                cursorSpot = BitmapFactory.decodeResource(getResources(), R.drawable.seek_bar_spot_on);
                upX = (int) event.getX();
                upY = (int) event.getY();
                responseTouch(upX, upY);
                responseOnTouch.onTouchResponse(currentSection);
                break;
        }
        return true;
    }
    //根据触摸点得到SeekBar的进度
    private void responseTouch(int x, int y){
        if(x <= width - selfBitMapHeight - cursorSpot.getWidth() / 2) {
            currentSection = (x -selfBitMapHeight) / (perWidth+cursorSpot.getWidth());
        }else{
            currentSection = sectionTitle.size() - 1;
        }
        invalidate();
    }

    //设置监听
    public void setResponseOnTouch(ResponseOnTouch response){
        responseOnTouch = response;
    }

    //设置进度
    public void setProgress(int progress){
        currentSection = progress;
        invalidate();
    }
}
