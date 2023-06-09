package com.yinjiee.ausers.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.yinjiee.ausers.R;
import com.yinjiee.ausers.adapter.MainListAdapter;
import com.yinjiee.ausers.adapter.RefreshAdapter;
import com.yinjiee.ausers.bean.ListBean;
import com.yinjiee.ausers.custom.RefreshView;
import com.yinjiee.ausers.http.HttpCallback;
import com.yinjiee.ausers.http.HttpConsts;
import com.yinjiee.ausers.http.HttpUtil;
import com.yinjiee.ausers.interfaces.LifeCycleAdapter;
import com.yinjiee.ausers.utils.L;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/27.
 * 首页 排行 贡献榜
 */

public class MainListContributeViewHolder extends AbsMainListViewHolder {

    public MainListContributeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_list_page;
    }

    @Override
    public void init() {
       // super.init();
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_list);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new RefreshView.DataHelper<ListBean>() {
            @Override
            public RefreshAdapter<ListBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainListAdapter(mContext,MainListAdapter.TYPE_CONTRIBUTE);
                    mAdapter.setOnItemClickListener(MainListContributeViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.consumeList(mType, p, callback);
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
                L.e("main----MainListContributeViewHolder-------LifeCycle---->onDestroy");
                HttpUtil.cancel(HttpConsts.CONSUME_LIST);
                HttpUtil.cancel(HttpConsts.SET_ATTENTION);
            }
        };
    }

    @Override
    public void loadData() {
        if (!isFirstLoadData()) {
            return;
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

}
