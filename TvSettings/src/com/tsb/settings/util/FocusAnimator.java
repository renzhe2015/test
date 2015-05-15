package com.tsb.settings.util;

//import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

/**
 * Created by Administrator on 14-7-16.
 */
public class FocusAnimator {
    /**
     *焦点框飞动、移动、变大
     * @param frame   焦点框
     * @param width   移动目标的宽
     * @param height  移动目标的高
     * @param paramFloat1 移动目标绝对位置的left,通过xx.getLocationOnScreen获取
     * @param paramFloat2 移动目标绝对位置的top,通过xx.getLocationOnScreen获取
     * */
    public void flyFoucsFrame(ImageView frame, int width, int height, float paramFloat1, float paramFloat2) {
        if(frame == null)
            return;

        frame.setVisibility(View.VISIBLE);
        int mWidth = frame.getWidth();
        int mHeight = frame.getHeight();
        if (mWidth == 0 || mHeight == 0) {
            mWidth = 1;
            mHeight = 1;
        }

        ViewPropertyAnimator localViewPropertyAnimator = frame.animate();
        localViewPropertyAnimator.setDuration(150L);
        localViewPropertyAnimator.scaleX((float) (width * 1) / (float) mWidth);
        localViewPropertyAnimator.scaleY((float) (height * 1) / (float) mHeight);
        localViewPropertyAnimator.x(paramFloat1 + (width - mWidth)/2);
        localViewPropertyAnimator.y(paramFloat2 + (height - mHeight)/2);
        //Log.v("wsl######", " width:"+width + " height" + height + " paramFloat1:" + paramFloat1 + " paramFloat2:" + paramFloat2 +" x:"+ (paramFloat1 + (width - mWidth)/2) + " y:" + (paramFloat2 + (height - mHeight)/2));
        localViewPropertyAnimator.start();
    }
}
