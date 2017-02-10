package com.wkq.scrollviewenlargebanner;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;


import com.youth.banner.Banner;

/**
 * ================================================
 * 作者：WKQ
 * 时间:  2017/2/9 16:06
 * 用途: 为了实现  viewpage 下拉放大的需求自定义的Scrollview
 * 备注:
 * <p>
 * ================================================
 */

public class FlowScrollView extends ScrollView implements View.OnTouchListener {
    float mDownPosX;
    float mDownPosY;

boolean isPlay=false;
    // 记录首次按下位置
    private float mFirstPosition = 0;
    // 是否正在放大
    private Boolean mScaling = false;
    private Banner dropZoomView;
    private int dropZoomViewWidth;
    private int dropZoomViewHeight;
    private float xDistance, yDistance, xLast, yLast;


    public FlowScrollView(Context context) {
        super(context);
    }

    public FlowScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //处理与viewpager冲突问题(左右滑动分发下去)
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                L.e("xLast距离:" + xLast);
                L.e("yLast距离:" + yLast);
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                L.e("x距离:" + xDistance);
                L.e("y距离:" + yDistance);
                if (xDistance > yDistance) {
                    return false;
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    //回弹动画 (使用了属性动画)
    public void replyImage() {
        final float distance = dropZoomView.getMeasuredWidth() - dropZoomViewWidth;

        // 设置动画
        ValueAnimator anim = ObjectAnimator.ofFloat(0.0F, 1.0F).setDuration((long) (distance * 0.7));

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                setZoom(distance - ((distance) * cVal));
                isPlay=true;
            }
        });
        anim.start();


    }

    //缩放

    public void setZoom(float s) {
        if (dropZoomViewHeight <= 0 || dropZoomViewWidth <= 0) {
            return;
        }
        //处理banner的轮播问题
        if(isPlay){
            dropZoomView.isAutoPlay(true);
            dropZoomView.startAutoPlay();
            isPlay=false;
        }else{
            dropZoomView.stopAutoPlay();

        }

        ViewGroup.LayoutParams lp = dropZoomView.getLayoutParams();
        lp.width = (int) (dropZoomViewWidth );
        lp.height = (int) (dropZoomViewHeight * ((dropZoomViewWidth + s) / dropZoomViewWidth));
        dropZoomView.setLayoutParams(lp);


    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }
//初始化用到的数据
    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);
        setOnTouchListener(this);
        ViewGroup view = (ViewGroup) getChildAt(0);
        dropZoomView = (Banner) view.getChildAt(0);


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        L.e("滑动:onTouch");
        if (dropZoomViewWidth <= 0 || dropZoomViewHeight <= 0) {
            dropZoomViewWidth = dropZoomView.getMeasuredWidth();
            dropZoomViewHeight = dropZoomView.getMeasuredHeight();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstPosition = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mScaling) {
                    if (getScrollY() == 0) {
                        mFirstPosition = event.getY();// 滚动到顶部时记录位置，否则正常返回
                    } else {
                        break;
                    }
                }

                // 处理放大
                mScaling = true;
                int distance = (int) ((event.getY() - mFirstPosition) * 0.6); // 滚动距离乘以一个系数
                L.e("距离:" + distance);
                if (distance < 0) { // 当前位置比记录位置要小，正常返回
                    break;
                } else {
                    setZoom(1 + distance);
                    return true; // 返回true表示已经完成触摸事件，不再处理
                }


            case MotionEvent.ACTION_UP:
                mScaling = false;
                replyImage();

                break;


        }
        return false;
    }
}
