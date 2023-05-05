package com.yinjiee.ausers.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.baidu.speech.utils.LogUtil;
import com.lzy.okgo.model.Response;
import com.yinjiee.ausers.Constants;
import com.yinjiee.ausers.R;
import com.yinjiee.ausers.TzRecordBean;
import com.yinjiee.ausers.adapter.LiveTzRecordAdapter;
import com.yinjiee.ausers.custom.RefreshLayout;
import com.yinjiee.ausers.dialog.LiveGameTzRecordFragment;
import com.yinjiee.ausers.http.HttpCallback;
import com.yinjiee.ausers.http.HttpConsts;
import com.yinjiee.ausers.http.HttpUtil;
import com.yinjiee.ausers.http.JsonBean;
import com.yinjiee.ausers.utils.StringUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 开奖历史
 */
public class GameRecordListActivity extends AbsActivity  implements RefreshLayout.OnRefreshListener {

    private String mGameId ;

    private RefreshLayout mRefreshLay ;
    private RecyclerView recyclerView;

    private int mCurPage = 1;
    private LiveTzRecordAdapter adapter;
    private List<TzRecordBean> list;

    public static void launch(Context context,String gameId){
        Intent toIt = new Intent(context,GameRecordListActivity.class) ;
        toIt.putExtra(Constants.LIVE_GAME_ID,gameId) ;
        context.startActivity(toIt) ;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_record_list ;
    }

    @Override
    protected void main() {
        super.main();

        mGameId = getIntent().getStringExtra(Constants.LIVE_GAME_ID) ;

        mRefreshLay = findViewById(R.id.act_game_record_refresh_lay);
        recyclerView = findViewById(R.id.act_game_record_rv);

        mRefreshLay.setScorllView(recyclerView);
        mRefreshLay.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        setTitle(HttpConsts.getGameNameByType(mGameId) + "开奖记录");

        getData(mCurPage) ;
    }

    private void getData(final int page) {
        HttpUtil.getAwardRecord(mGameId, String.valueOf(page), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                LogUtil.e(LiveGameTzRecordFragment.class.getSimpleName(), Arrays.toString(info));

                if (code == 0) {
                    try {
                        list = JSON.parseArray(Arrays.toString(info), TzRecordBean.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (page == 1) {
                        if (list != null && list.size() > 0) {
                            adapter = new LiveTzRecordAdapter(mContext, getLayoutInflater(), list, StringUtil.convertInt(mGameId)) ;
                            recyclerView.setAdapter(adapter);
                        }
                    } else {
                        if (list != null && list.size() > 0) {
                            adapter.addList(list);
                        }
                    }

                    if(list != null && list.size() > 0){
                        mCurPage++;
                    }
                }

                mRefreshLay.completeRefresh();
                mRefreshLay.completeLoadMore();
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);

                mRefreshLay.completeRefresh();
                mRefreshLay.completeLoadMore();
            }
        });
    }

    @Override
    public void onRefresh() {
        mCurPage = 1;

        if(list != null && list.size() > 0){
            list.clear();
            adapter.clear();
        }

        getData(mCurPage);
    }

    @Override
    public void onLoadMore() {
        getData(mCurPage);
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConsts.GAME_LOG);
        super.onDestroy();
    }
}
