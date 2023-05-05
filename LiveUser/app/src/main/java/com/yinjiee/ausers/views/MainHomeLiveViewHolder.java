package com.yinjiee.ausers.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.yinjiee.ausers.AppConfig;
import com.yinjiee.ausers.Constants;
import com.yinjiee.ausers.R;
import com.yinjiee.ausers.adapter.AdapterHomeGameList;
import com.yinjiee.ausers.adapter.MainHomeHotAdapter;
import com.yinjiee.ausers.adapter.MainHomeLiveClassAdapter;
import com.yinjiee.ausers.adapter.RefreshAdapter;
import com.yinjiee.ausers.bean.ConfigBean;
import com.yinjiee.ausers.bean.GameTypeBean;
import com.yinjiee.ausers.bean.LiveBean;
import com.yinjiee.ausers.bean.LiveClassBean;
import com.yinjiee.ausers.bean.RollPicBean;
import com.yinjiee.ausers.custom.ItemDecoration;
import com.yinjiee.ausers.custom.RefreshView;
import com.yinjiee.ausers.http.HttpCallback;
import com.yinjiee.ausers.http.HttpUtil;
import com.yinjiee.ausers.interfaces.OnItemClickListener;
import com.yinjiee.ausers.utils.LiveStorge;
import com.yinjiee.ausers.utils.ToastUtil;
import com.yinjiee.ausers.utils.WordUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页 直播
 */

public class MainHomeLiveViewHolder extends AbsMainChildTopViewHolder implements OnItemClickListener<LiveBean> {

    private RecyclerView mClassRecyclerView;
    private MainHomeLiveClassAdapter mHomeLiveClassAdapter;
    private MainHomeHotAdapter mAdapter;
    private SlideshowView mSlideshowView;
    private boolean isFirst = true;

    public MainHomeLiveViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_live;
    }

    @Override
    public void init() {
        super.init();
        mClassRecyclerView = (RecyclerView) findViewById(R.id.classRecyclerView);
        mClassRecyclerView.setHasFixedSize(true);
        mClassRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        mClassRecyclerView.setVisibility(View.GONE);

        ConfigBean configBean = AppConfig.getInstance().getConfig();
        if (configBean != null) {
            List<LiveClassBean> list = configBean.getLiveClass();
            if (list == null || list.size() == 0) {
                mClassRecyclerView.setVisibility(View.GONE);
            } else {
                List<LiveClassBean> targetList = new ArrayList<>();
                if (list.size() <= 6) {
                    targetList.addAll(list);
                } else {
                    targetList.addAll(list.subList(0, 5));
                    LiveClassBean bean = new LiveClassBean();
                    bean.setAll(true);
                    bean.setName(WordUtil.getString(R.string.all));
                    targetList.add(bean);
                }
                mHomeLiveClassAdapter = new MainHomeLiveClassAdapter(mContext, targetList, false);
                mClassRecyclerView.setAdapter(mHomeLiveClassAdapter);
            }
        } else {
            mClassRecyclerView.setVisibility(View.GONE);
        }

        RecyclerView gameRv = (RecyclerView) findViewById(R.id.view_main_home_game_rv);
        gameRv.setFocusable(false);
        gameRv.setNestedScrollingEnabled(false);

        AdapterHomeGameList gameListAdapter = new AdapterHomeGameList(mContext, new AdapterHomeGameList.OnGameOptClickListener() {
            @Override
            public void onGameClick(GameTypeBean bean) {
                ToastUtil.show("暂未开放");
            }
        }) ;
        gameRv.setLayoutManager(new GridLayoutManager(mContext,4));
        gameRv.setAdapter(gameListAdapter) ;

        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_live);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 5);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new RefreshView.DataHelper<LiveBean>() {
            @Override
            public RefreshAdapter<LiveBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeHotAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainHomeLiveViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getHot(p, callback);
            }

            @Override
            public List<LiveBean> processData(String[] info) {
                try {
                    if (isFirst) {
                        isFirst = false;
                        List<RollPicBean> images = JSON.parseArray(JSON.parseObject(info[0]).getString("slide"), RollPicBean.class);
                        if (images.size() > 0) {
                            mSlideshowView.setVisibility(View.VISIBLE);
                            mSlideshowView.addDataToUI(images);
                        } else {
                            mSlideshowView.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace() ;
                }
                return JSON.parseArray(JSON.parseObject(info[0]).getString("list"), LiveBean.class);
            }

            @Override
            public void onRefresh(List<LiveBean> list) {
                LiveStorge.getInstance().put(Constants.LIVE_HOME, list);
            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {
                if (dataCount < 10) {
                    mRefreshView.setLoadMoreEnable(false);
                } else {
                    mRefreshView.setLoadMoreEnable(true);
                }
            }
        });
        mSlideshowView = (SlideshowView) findViewById(R.id.slideshowView);
    }


    public void setLiveClassItemClickListener(OnItemClickListener<LiveClassBean> liveClassItemClickListener) {
        if (mHomeLiveClassAdapter != null) {
            mHomeLiveClassAdapter.setOnItemClickListener(liveClassItemClickListener);
        }
    }

    @Override
    public void onItemClick(LiveBean bean, int position) {
        watchLive(bean, Constants.LIVE_HOME, position);
    }

    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }
}
