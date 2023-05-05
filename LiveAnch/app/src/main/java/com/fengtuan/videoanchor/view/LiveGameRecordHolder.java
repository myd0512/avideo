package com.fengtuan.videoanchor.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.adapter.GameResultRecordAdapter;
import com.fengtuan.videoanchor.bean.GameResultRecord;
import com.fengtuan.videoanchor.http.HttpCallback;
import com.fengtuan.videoanchor.http.HttpConsts;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.http.ParseUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 直播游戏开奖记录
 */
public class LiveGameRecordHolder extends GameGiftBaseHolder{
    private SwipeRefreshLayout mRefreshLay ;
    private RecyclerView mRv ;

    private TextView mGamePeriodTv ;

    private View mLoadingPb ;
    private View mNullTv ;

    private String mGameId ;
    private String mGameName ;
    private String mGamePeriod ;
    private boolean mIsFirstLoad = true ;

    private List<GameResultRecord> mRecordList ;
    private GameResultRecordAdapter mAdapter ;
    private LinearLayoutManager mLayoutManager ;

    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;

    public LiveGameRecordHolder(Context context, ViewGroup parentView,String gameId,String gameName,String gamePeriod) {
        super(context, parentView,gameId,gameName,gamePeriod);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.live_game_record_list;
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);

        if(args != null && args.length == 3){
            mGameId = (String) args[0];
            mGameName = (String) args[1];
            mGamePeriod = (String) args[2];
        }
    }

    @Override
    public void init() {
        mRefreshLay = (SwipeRefreshLayout) findViewById(R.id.live_game_recode_list_refresh);
        mRv = (RecyclerView) findViewById(R.id.live_game_recode_list_rv);

        TextView gameNameTv = (TextView) findViewById(R.id.live_game_recode_name_tv);
        mGamePeriodTv = (TextView) findViewById(R.id.live_game_recode_period_tv);
        gameNameTv.setText(mGameName);
        if(!TextUtils.isEmpty(mGamePeriod)){
            mGamePeriodTv.setText("第" + mGamePeriod + "期") ;
        }

        mLoadingPb = findViewById(R.id.live_game_recode_pb);
        mNullTv = findViewById(R.id.live_game_recode_null_tv);

        mRefreshLay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mRecordList.size() > 8 && !mRv.canScrollVertically(1)){
                    getRecordList() ;
                }
            }
        });

        mLayoutManager = new LinearLayoutManager(mContext) ;
        mRv.setLayoutManager(mLayoutManager);

        mRecordList = new ArrayList<>() ;
        mAdapter = new GameResultRecordAdapter(mContext,mRecordList,mGameId,mGameName) ;
        mRv.setAdapter(mAdapter) ;

        mCurPage = 1 ;
        getRecordList() ;
    }

    public void setGamePeriod(String oldPeriod,String latestPeriod,String[] resultPoint){
        mGamePeriod = latestPeriod ;
        if(mGamePeriodTv != null){
            mGamePeriodTv.setText("第" + latestPeriod + "期") ;
        }

        if(!mIsLoading && mRecordList.size() > 0){
            if(!TextUtils.isEmpty(oldPeriod) && resultPoint != null && resultPoint.length > 0){
                GameResultRecord firstItem = new GameResultRecord() ;
                firstItem.setId(oldPeriod) ;
                StringBuffer pointBuf = new StringBuffer() ;
                for(String point : resultPoint){
                    pointBuf.append(point).append(",") ;
                }
                String pointStr ;
                if(pointBuf.length() > 0){
                    pointStr = pointBuf.substring(0,pointBuf.length() - 1) ;
                }else{
                    pointStr = "" ;
                }

                firstItem.setResult(pointStr) ;

                mRecordList.add(0,firstItem) ;
                mAdapter.notifyItemInserted(0) ;

                if(mLayoutManager.findFirstVisibleItemPosition() < 2){//基本在最上面，那么就滚动一下
                    mRv.smoothScrollToPosition(0) ;
                }
            }
        }
    }

    @Override
    public void setVisible(boolean visible){
        if(visible && !mIsLoading && mRecordList.size() == 0){
            refreshList() ;
        }
    }

    @Override
    public void destroyView() {
        HttpUtil.cancel(HttpConsts.GAME_LOG);
    }

    private void refreshList(){
        mCurPage = 1 ;
        getRecordList() ;
    }

    /**
     * 获取列表
     */
    private void getRecordList(){
        mIsLoading = true ;

        HttpUtil.getAwardRecord(mGameId, String.valueOf(mCurPage), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(info != null && info.length > 0){
                    Log.e("getRecordList","info=" + info[0]) ;

                    List<GameResultRecord> list = ParseUtils.parseArray(Arrays.toString(info),GameResultRecord.class) ;

                    if(1 == mCurPage){
                        mRecordList.clear();
                    }
                    if(list.size() > 0){
                        mCurPage ++ ;
                        mRecordList.addAll(list) ;
                    }
                    mAdapter.notifyDataSetChanged() ;
                }

                finishRequest() ;
            }

            @Override
            public void onError() {
                super.onError();

                finishRequest() ;
            }
        }) ;
    }


    private void finishRequest(){
        mIsLoading = false ;
        mRefreshLay.setRefreshing(false) ;

        if(mIsFirstLoad){
            mIsFirstLoad = false ;
            mLoadingPb.setVisibility(View.GONE);
        }

        mNullTv.setVisibility(mRecordList.size() == 0 ? View.VISIBLE : View.GONE) ;
    }



}
