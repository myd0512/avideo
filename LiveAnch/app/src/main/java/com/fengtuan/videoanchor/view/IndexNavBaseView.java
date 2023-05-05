package com.fengtuan.videoanchor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 首页
 */
public abstract class IndexNavBaseView extends RelativeLayout {
    protected Context mContext ;

    public IndexNavBaseView(Context context) {
        super(context);

        init(context) ;
    }

    public IndexNavBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context) ;
    }

    public IndexNavBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context) ;
    }

    private void init(Context context){
        mContext = context ;
        inflate(mContext,getLayoutId(),this) ;
        setVisibility(GONE);
        initView() ;
    }


    protected abstract int getLayoutId() ;
    protected abstract void initView() ;
    public abstract void showNavView() ;
    public abstract void hiddenNavView() ;
    public abstract void destroyNavView() ;

}
