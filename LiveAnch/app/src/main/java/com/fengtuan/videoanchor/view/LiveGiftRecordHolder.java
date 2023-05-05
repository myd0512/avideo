package com.fengtuan.videoanchor.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.adapter.GiftRecordAdapter;
import com.fengtuan.videoanchor.bean.GiftRecord;
import com.fengtuan.videoanchor.http.HttpCallback;
import com.fengtuan.videoanchor.http.HttpConsts;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.http.ParseUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 主播收到的礼物记录
 */
public class LiveGiftRecordHolder extends GameGiftBaseHolder{
    private SwipeRefreshLayout mRefreshLay ;
    private RecyclerView mRv ;

    private View mLoadingPb ;
    private View mNullTv ;

    private boolean mIsFirstLoad = true ;

    private List<GiftRecord> mRecordList ;
    private GiftRecordAdapter mAdapter ;

    private boolean mIsLoading = false ;

    public LiveGiftRecordHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.live_gift_record_list;
    }

    @Override
    public void init() {
        mRefreshLay = (SwipeRefreshLayout) findViewById(R.id.live_gift_recode_list_refresh);
        mRv = (RecyclerView) findViewById(R.id.live_gift_recode_list_rv);

        mLoadingPb = findViewById(R.id.live_gift_recode_pb);
        mNullTv = findViewById(R.id.live_gift_recode_null_tv);

        mRefreshLay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        mRv.setLayoutManager(new LinearLayoutManager(mContext));

        mRecordList = new ArrayList<>() ;
        mAdapter = new GiftRecordAdapter(mContext,mRecordList) ;
        mRv.setAdapter(mAdapter) ;

        addToParent() ;

        getRecordList() ;
    }

    @Override
    public void setVisible(boolean visible){
        if(visible && !mIsLoading && mRecordList.size() == 0){
            mRefreshLay.setRefreshing(true) ;
            getRecordList() ;
        }
    }

    @Override
    public void destroyView() {
        HttpUtil.cancel(HttpConsts.GIFT_LOG);
    }

    public void refreshList(){
        getRecordList() ;
    }

    /**
     * 获取列表
     */
    private void getRecordList(){
        mIsLoading = true ;

        HttpUtil.getGiftRecord(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(info != null && info.length > 0){
                    Log.e("getRecordList","info=" + info[0]) ;

                    List<GiftRecord> list = ParseUtils.parseArray(Arrays.toString(info),GiftRecord.class) ;

                    mRecordList.clear();
                    if(list.size() > 0){
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
        }); ;
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
