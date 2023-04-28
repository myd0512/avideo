package com.yunbao.liveanchor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by cxf on 2018/7/19.
 */

public class MyRelativeLayout4 extends RelativeLayout {
    public MyRelativeLayout4(Context context) {
        super(context);
    }

    public MyRelativeLayout4(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout4(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * 0.6f), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}