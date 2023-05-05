package com.fengtuan.videoanchor.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.adapter.MainRankListAdapter;
import com.fengtuan.videoanchor.adapter.RefreshAdapter;
import com.fengtuan.videoanchor.bean.ListBean;
import com.fengtuan.videoanchor.glide.ImgLoader;
import com.fengtuan.videoanchor.http.HttpCallback;
import com.fengtuan.videoanchor.http.HttpConsts;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.interfa.DataLoader;
import com.fengtuan.videoanchor.interfa.LifeCycleAdapter;
import com.fengtuan.videoanchor.util.ClickUtil;
import com.fengtuan.videoanchor.util.L;

import java.util.Arrays;
import java.util.List;

public class MainRankListViewHolder extends AbsViewHolder implements DataLoader, View.OnClickListener {
    public static final String DAY = "day";
    public static final String WEEK = "week";
    public static final String MONTH = "month";

    private TextView mNavDayTv ;
    private TextView mNavWeekTv ;
    private TextView mNavMonthTv ;

    private ImageView mTopHeadIv ;
    private TextView mTopNameTv ;
    private TextView mTopVotesTv ;
    private TextView mTopLevelTv ;

    protected RefreshView mRefreshView;

    private String mType ;
    protected MainRankListAdapter mAdapter;


    public MainRankListViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_rank_list;
    }

    @Override
    public void init() {
        mType = DAY ;

        mNavDayTv = (TextView) findViewById(R.id.view_main_rank_nav_day_tv);
        mNavWeekTv = (TextView) findViewById(R.id.view_main_rank_nav_week_tv);
        mNavMonthTv = (TextView) findViewById(R.id.view_main_rank_nav_month_tv);
        mNavMonthTv.setOnClickListener(this);
        mNavWeekTv.setOnClickListener(this);
        mNavDayTv.setOnClickListener(this);

        mTopHeadIv = (ImageView) findViewById(R.id.view_main_rank_top_user_iv);
        mTopNameTv = (TextView) findViewById(R.id.view_main_rank_top_name_tv);
        mTopVotesTv = (TextView) findViewById(R.id.view_main_rank_top_votes_tv);
        mTopLevelTv = (TextView) findViewById(R.id.view_main_rank_top_level_tv);

        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_list);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new RefreshView.DataHelper<ListBean>() {
            @Override
            public RefreshAdapter<ListBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainRankListAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.profitList(mType, p, callback);
            }

            @Override
            public List<ListBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), ListBean.class);
            }

            @Override
            public void onRefresh(List<ListBean> list) {

            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {

                if(mAdapter != null){
                    int size = mAdapter.getList().size() ;
                    if(size > 0){
                        ListBean topItem = mAdapter.getList().get(0) ;
                        initTopItemInfo(topItem) ;
                    }else{
                        initTopItemInfo(null) ;
                    }
                }

                if (dataCount < 50) {
                    mRefreshView.setLoadMoreEnable(false);
                } else {
                    mRefreshView.setLoadMoreEnable(true);
                }
            }
        });
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onDestroy() {
                L.e("main----MainListProfitViewHolder-------LifeCycle---->onDestroy");
                HttpUtil.cancel(HttpConsts.PROFIT_LIST);
                HttpUtil.cancel(HttpConsts.SET_ATTENTION);
            }
        };
    }

    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    public void releaseView(){
        HttpUtil.cancel(HttpConsts.PROFIT_LIST);
        HttpUtil.cancel(HttpConsts.SET_ATTENTION);
    }

    /**
     * 填充榜首信息
     * @param bean info
     */
    private void initTopItemInfo(ListBean bean){
        if(bean != null){
            ImgLoader.display(bean.getAvatarThumb(), mTopHeadIv);
            mTopNameTv.setText(bean.getUserNiceName());
            mTopVotesTv.setText("礼物:" + bean.getTotalCoinFormat());
            mTopLevelTv.setText(String.valueOf(bean.getLevel()));
        }else{
            mTopHeadIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_rank_head_def)) ;
            mTopNameTv.setText("虚位以待");
            mTopVotesTv.setText("礼物:0");
            mTopLevelTv.setText("0");
        }
    }

    @Override
    public void onClick(View view) {
        if(!ClickUtil.canClick()){
            return;
        }

        int vId = view.getId() ;
        if(R.id.view_main_rank_nav_day_tv == vId){//日榜
            changeNavType(DAY) ;
        } else if(R.id.view_main_rank_nav_week_tv == vId){//周榜
            changeNavType(WEEK) ;
        } else if(R.id.view_main_rank_nav_month_tv == vId){//月榜
            changeNavType(MONTH) ;
        }
    }

    /**
     * 切换榜单类型
     */
    private void changeNavType(String type){
        if(mType.equals(type)){
            return;
        }

        if(DAY.equals(mType)){
            mNavDayTv.setTextColor(mContext.getResources().getColor(R.color.color_rank_list_nav_normal)) ;
            mNavDayTv.setBackground(mContext.getResources().getDrawable(R.drawable.drawable_rank_list_nav_normal)) ;
        }else if(WEEK.equals(mType)){
            mNavWeekTv.setTextColor(mContext.getResources().getColor(R.color.color_rank_list_nav_normal)) ;
            mNavWeekTv.setBackground(mContext.getResources().getDrawable(R.drawable.drawable_rank_list_nav_normal)) ;
        }else if(MONTH.equals(mType)){
            mNavMonthTv.setTextColor(mContext.getResources().getColor(R.color.color_rank_list_nav_normal)) ;
            mNavMonthTv.setBackground(mContext.getResources().getDrawable(R.drawable.drawable_rank_list_nav_normal)) ;
        }

        if(DAY.equals(type)){
            mNavDayTv.setTextColor(mContext.getResources().getColor(R.color.color_rank_list_nav_checked)) ;
            mNavDayTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rank_left_checked)) ;
        }else if(WEEK.equals(type)){
            mNavWeekTv.setTextColor(mContext.getResources().getColor(R.color.color_rank_list_nav_checked)) ;
            mNavWeekTv.setBackground(mContext.getResources().getDrawable(R.drawable.drawable_rank_list_nav_mid)) ;
        }else if(MONTH.equals(type)){
            mNavMonthTv.setTextColor(mContext.getResources().getColor(R.color.color_rank_list_nav_checked)) ;
            mNavMonthTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rank_right_checked)) ;
        }
        mType = type ;

        mRefreshView.initData() ;
    }

}
