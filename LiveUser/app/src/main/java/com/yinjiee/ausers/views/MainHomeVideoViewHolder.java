package com.yinjiee.ausers.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.yinjiee.ausers.Constants;
import com.yinjiee.ausers.R;
import com.yinjiee.ausers.activity.VideoPlayActivity;
import com.yinjiee.ausers.adapter.MainHomeVideoAdapter;
import com.yinjiee.ausers.adapter.RefreshAdapter;
import com.yinjiee.ausers.bean.VideoBean;
import com.yinjiee.ausers.custom.ItemDecoration;
import com.yinjiee.ausers.custom.RefreshView;
import com.yinjiee.ausers.event.VideoDeleteEvent;
import com.yinjiee.ausers.event.VideoScrollPageEvent;
import com.yinjiee.ausers.http.HttpCallback;
import com.yinjiee.ausers.http.HttpUtil;
import com.yinjiee.ausers.interfaces.LifeCycleAdapter;
import com.yinjiee.ausers.interfaces.OnItemClickListener;
import com.yinjiee.ausers.interfaces.VideoScrollDataHelper;
import com.yinjiee.ausers.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 首页视频
 */

public class MainHomeVideoViewHolder extends AbsMainChildTopViewHolder implements OnItemClickListener<VideoBean> {

    private MainHomeVideoAdapter mAdapter;
    private VideoScrollDataHelper mVideoScrollDataHelper;

    public MainHomeVideoViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_video;
    }

    @Override
    public void init() {
        super.init();
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_live_video);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new RefreshView.DataHelper<VideoBean>() {
            @Override
            public RefreshAdapter<VideoBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeVideoAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainHomeVideoViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getHomeVideoList(p, callback);
            }

            @Override
            public List<VideoBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), VideoBean.class);
            }

            @Override
            public void onRefresh(List<VideoBean> list) {
                VideoStorge.getInstance().put(Constants.VIDEO_HOME, list);
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
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onCreate() {
                EventBus.getDefault().register(MainHomeVideoViewHolder.this);
            }

            @Override
            public void onDestroy() {
                EventBus.getDefault().unregister(MainHomeVideoViewHolder.this);
            }
        };
        mVideoScrollDataHelper = new VideoScrollDataHelper() {

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getHomeVideoList(p, callback);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoScrollPageEvent(VideoScrollPageEvent e) {
        if (Constants.VIDEO_HOME.equals(e.getKey()) && mRefreshView != null) {
            mRefreshView.setPage(e.getPage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteEvent(VideoDeleteEvent e) {
        if (mAdapter != null) {
            mAdapter.deleteVideo(e.getVideoId());
            if (mAdapter.getItemCount() == 0 && mRefreshView != null) {
                mRefreshView.showNoData();
            }
        }
    }

    @Override
    public void onItemClick(VideoBean bean, int position) {
        int page = 1;
        if (mRefreshView != null) {
            page = mRefreshView.getPage();
        }
        VideoStorge.getInstance().putDataHelper(Constants.VIDEO_HOME, mVideoScrollDataHelper);
        VideoPlayActivity.forward(mContext, position, Constants.VIDEO_HOME, page);
    }
}
