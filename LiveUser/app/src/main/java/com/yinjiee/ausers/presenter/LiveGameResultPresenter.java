package com.yinjiee.ausers.presenter;

import android.content.Context;
import android.widget.RelativeLayout;

import com.yinjiee.ausers.views.LiveGameResultViewHolder;

/**
 * 主播直播开奖结果
 */
public class LiveGameResultPresenter {
    private Context mContext ;
    private RelativeLayout mParentView ;
    private LiveGameResultViewHolder mResultHolder ;

    public LiveGameResultPresenter(Context context, RelativeLayout mParentView) {
        this.mContext = context;
        this.mParentView = mParentView;
        mResultHolder = new LiveGameResultViewHolder(mContext,mParentView) ;
    }


    public void showGameResult(String gameType,String[] result,String period){
        if(mResultHolder != null){
            mResultHolder.updateDisplay(gameType,result,period) ;
        }
    }

}
