package com.fengtuan.videoanchor.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.adapter.LiveHistoryAdapter;
import com.fengtuan.videoanchor.adapter.RefreshAdapter;
import com.fengtuan.videoanchor.bean.LiveHistory;
import com.fengtuan.videoanchor.http.HttpCallback;
import com.fengtuan.videoanchor.http.HttpConsts;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.view.ItemDecoration;
import com.fengtuan.videoanchor.view.RefreshView;

import java.util.Arrays;
import java.util.List;

/**
 * 开播历史
 */
public class LiveHistoryActivity extends AbsActivity {
    private RefreshView mRefreshView;
    private LiveHistoryAdapter mAdapter;


    public static void launch(Context context){
        Intent toIt = new Intent(context,LiveHistoryActivity.class) ;
        context.startActivity(toIt) ;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_history;
    }

    @Override
    protected void main() {
        super.main();

        setTitle("开播历史");

        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_live_record);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 2, 2);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);

        mRefreshView.setDataHelper(new RefreshView.DataHelper<LiveHistory>() {
            @Override
            public RefreshAdapter<LiveHistory> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new LiveHistoryAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getSelfLiveRecord(p, callback);
            }

            @Override
            public List<LiveHistory> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), LiveHistory.class);
            }

            @Override
            public void onRefresh(List<LiveHistory> list) {

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
        HttpUtil.cancel(HttpConsts.GET_LIVE_RECORD);
        super.onDestroy();
    }


}
