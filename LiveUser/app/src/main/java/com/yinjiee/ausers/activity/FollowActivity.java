package com.yinjiee.ausers.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yinjiee.ausers.AppConfig;
import com.yinjiee.ausers.Constants;
import com.yinjiee.ausers.R;
import com.yinjiee.ausers.adapter.RefreshAdapter;
import com.yinjiee.ausers.adapter.SearchAdapter;
import com.yinjiee.ausers.bean.SearchUserBean;
import com.yinjiee.ausers.custom.RefreshView;
import com.yinjiee.ausers.http.HttpCallback;
import com.yinjiee.ausers.http.HttpConsts;
import com.yinjiee.ausers.http.HttpUtil;
import com.yinjiee.ausers.interfaces.OnItemClickListener;
import com.yinjiee.ausers.utils.WordUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 * 我的关注 TA的关注
 */

public class FollowActivity extends AbsActivity {

    private RefreshView mRefreshView;
    private SearchAdapter mAdapter;
    private String mToUid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_list;
    }

    @Override
    protected void main() {
        mToUid = getIntent().getStringExtra(Constants.TO_UID);
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        if (mToUid.equals(AppConfig.getInstance().getUid())) {
            setTitle(WordUtil.getString(R.string.follow_my_follow));
            mRefreshView.setNoDataLayoutId(R.layout.view_no_data_follow);
        } else {
            setTitle(WordUtil.getString(R.string.follow_ta_follow));
            mRefreshView.setNoDataLayoutId(R.layout.view_no_data_follow_2);
        }
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new RefreshView.DataHelper<SearchUserBean>() {
            @Override
            public RefreshAdapter<SearchUserBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new SearchAdapter(mContext, Constants.FOLLOW_FROM_FOLLOW);
                    mAdapter.setOnItemClickListener(new OnItemClickListener<SearchUserBean>() {
                        @Override
                        public void onItemClick(SearchUserBean bean, int position) {
                            UserHomeActivity.forward(mContext, bean.getId());
                        }
                    });
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getFollowList(mToUid, p, callback);
            }

            @Override
            public List<SearchUserBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), SearchUserBean.class);
            }

            @Override
            public void onRefresh(List<SearchUserBean> list) {

            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRefreshView.initData();
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConsts.GET_FOLLOW_LIST);
        super.onDestroy();
    }
}
