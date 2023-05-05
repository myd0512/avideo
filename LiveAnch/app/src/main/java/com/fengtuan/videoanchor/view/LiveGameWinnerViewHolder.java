package com.fengtuan.videoanchor.view;


import android.animation.ValueAnimator;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.util.DpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 中奖用户展示
 */
public class LiveGameWinnerViewHolder extends AbsViewHolder {
    private ViewGroup mMainLay ;
    private TextView mOneTv ;

    private boolean hasMeasured = false ;
    private int mItemTvWidth ;
    private int mItemTvHeight ;
    private int mLimitX ;
    private int mLimitY ;
    private int mPerUnitDistanceBase ;//基础长度
    private int mPerUnitDistance ;//单位长度（是mPerUnitDistanceBase的N倍，为的是动画更细腻），设计的是3个阶段，每个阶段长度一样。

    private List<String> mInfoList = new ArrayList<>() ;
    private boolean mIsAdding = false ;

    private static final int ANIM_TIME = 3000 ;//进入 + 保持 + 隐藏  整个动画时间

    public LiveGameWinnerViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_game_winner ;
    }

    @Override
    public void init() {
        mMainLay = (ViewGroup) mContentView;

        mOneTv = (TextView) findViewById(R.id.view_live_game_winner_one_tv);

        addToParent() ;

        mLimitX = DpUtil.dp2px(60) ;
        mLimitY = DpUtil.dp2px(45) ;

        ViewTreeObserver vto = mOneTv.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!hasMeasured) {
                    mItemTvHeight = mOneTv.getMeasuredHeight();
                    mItemTvWidth = mOneTv.getMeasuredWidth();
                    mPerUnitDistanceBase = mItemTvWidth + mLimitX ;
                    mPerUnitDistance = 5 * mPerUnitDistanceBase ;

                    hasMeasured = true;
                }
                return true;
            }
        });

    }

    /**
     * 更新显示
     */
    private void updateTextShow(){
        if(mItemTvHeight == 0 || mItemTvWidth == 0){
            mItemTvWidth = mOneTv.getMeasuredWidth();
            mItemTvHeight = mOneTv.getMeasuredHeight();

            mPerUnitDistanceBase = mItemTvWidth + mLimitX ;
            mPerUnitDistance = 5 * mPerUnitDistanceBase ;
        }

        //创建新控件
        final TextView tv = new TextView(mContext) ;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,DpUtil.dp2px(25)) ;
        lp.rightMargin = -mPerUnitDistanceBase ;
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT) ;
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM) ;
        tv.setTag(mInfoList.get(0)) ;
        tv.setLayoutParams(lp);
        tv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_live_game_winner)) ;
        tv.setGravity(Gravity.CENTER);
        tv.setEms(16);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
        tv.setTextColor(mContext.getResources().getColor(R.color.white));
        tv.setText(mInfoList.get(0)) ;
        mMainLay.addView(tv) ;

        //3 的意思是整个动画过程分为3个阶段，入场、停顿、出场。
        ValueAnimator animator = ValueAnimator.ofInt(mPerUnitDistance * 3) ;
        animator.setDuration(ANIM_TIME) ;
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();

                if(value < mPerUnitDistance){//左移阶段

                    float rote = (float) value/mPerUnitDistance ;
                    moveFromX(tv,rote);

                }else if(value > mPerUnitDistance * 2 && value < mPerUnitDistance * 3){//上移阶段

                    int realValue = value - mPerUnitDistance*2 ;
                    //计算百分比，来调整高度
                    float rote = (float)realValue / mPerUnitDistance ;
                    moveFromY(tv,rote);

                    //上移了一半,就开始进行下一个
                    if(!mIsAdding && rote > 0.5F){
                        mIsAdding = true ;
                        //上移阶段就可以继续添加
                        if(mInfoList.size() > 0){
                            mInfoList.remove(0) ;
                        }

                        if(mInfoList.size() > 0){
                            updateTextShow() ;
                        }
                    }
                }else if(value == mPerUnitDistance * 3){//完成了
                    finishAnim(tv) ;
                }else{//保持不变

                }
            }
        });
        animator.start() ;
    }

    private void moveFromX(View tv,float rote){
        int transX = (-(int)((1F-rote) * mPerUnitDistanceBase)) + ((int)(rote * mLimitX));

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)tv.getLayoutParams() ;
        lp.rightMargin = transX ;
        tv.setLayoutParams(lp) ;
    }

    private void moveFromY(View tv,float rote){
        int transY = (int) (rote * mLimitY);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)tv.getLayoutParams() ;
        lp.bottomMargin = transY ;
        tv.setLayoutParams(lp) ;

        //同时设置透明度
        if(rote > 0.5F){
            tv.setAlpha(1 - rote + 0.5F) ;
        }
    }

    private void finishAnim(View tv){
        mMainLay.removeView(tv) ;
        mIsAdding = false ;
    }

    /**
     * 展示最新消息
     */
    public void showLatestWinner(List<String> infoList){
        boolean needShowNow = mInfoList.size() == 0 ;
        mInfoList.addAll(infoList) ;

        if(needShowNow){
            updateTextShow() ;
        }
    }

    public void relessView(){



    }

}
