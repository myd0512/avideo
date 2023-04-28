package com.yunbao.liveanchor.presenter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yunbao.liveanchor.view.LiveGameWinnerViewHolder;

import java.util.List;

/**
 * 中奖结果
 */
public class LiveGameWinnerPresenter {
    private Context mContext ;
    private LiveGameWinnerViewHolder mWinnerHolder ;

    public LiveGameWinnerPresenter(Context mContext, ViewGroup parentView) {
        this.mContext = mContext;
        mWinnerHolder = new LiveGameWinnerViewHolder(mContext,parentView) ;
    }

    public void showWinnerList(List<String> winnerList){
        if(mWinnerHolder != null){
            mWinnerHolder.showLatestWinner(winnerList) ;
        }
    }

    public void relessView(){
        if(mWinnerHolder != null){
            mWinnerHolder.relessView() ;
        }
    }
}
