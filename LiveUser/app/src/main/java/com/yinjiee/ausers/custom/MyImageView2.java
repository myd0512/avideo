package com.yinjiee.ausers.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Created by cxf on 2018/11/21.
 */

public class MyImageView2 extends ImageView {

    private boolean mAnimating;
    private RotateAnimation mRotateAnimation;


    public MyImageView2(Context context) {
        this(context, null);
    }

    public MyImageView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyImageView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRotateAnimation = new RotateAnimation(-30f, 30f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.2f);
        mRotateAnimation.setDuration(300);
        mRotateAnimation.setRepeatCount(-1);
        mRotateAnimation.setRepeatMode(Animation.REVERSE);
    }

    public void startAnim() {
        if (!mAnimating) {
            mAnimating = true;
            startAnimation(mRotateAnimation);
        }
    }

    public void stopAnim() {
        if(mAnimating){
            mAnimating = false;
            clearAnimation();
        }
    }

}
