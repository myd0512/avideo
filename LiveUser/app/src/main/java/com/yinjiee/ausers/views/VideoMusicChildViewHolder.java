package com.yinjiee.ausers.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.yinjiee.ausers.R;
import com.yinjiee.ausers.adapter.MusicAdapter;
import com.yinjiee.ausers.custom.RefreshView;
import com.yinjiee.ausers.interfaces.VideoMusicActionListener;

/**
 * Created by cxf on 2018/12/8.
 */

public abstract class VideoMusicChildViewHolder extends AbsViewHolder {

    protected RefreshView mRefreshView;
    protected MusicAdapter mAdapter;
    protected VideoMusicActionListener mActionListener;


    public VideoMusicChildViewHolder(Context context, ViewGroup parentView, VideoMusicActionListener actionListener) {
        super(context, parentView, actionListener);
    }

    @Override
    protected void processArguments(Object... args) {
        mActionListener = (VideoMusicActionListener) args[0];
    }

    @Override
    public void init() {
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }


    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    public void show() {
        if (mContentView != null && mContentView.getVisibility() != View.VISIBLE) {
            mContentView.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        if (mContentView != null && mContentView.getVisibility() == View.VISIBLE) {
            mContentView.setVisibility(View.INVISIBLE);
        }
    }


    public void release() {
        mActionListener = null;
        if (mAdapter != null) {
            mAdapter.setActionListener(null);
        }
    }

    public void collectChanged(MusicAdapter adapter, int musicId, int collect) {
        if (mAdapter != null) {
            mAdapter.collectChanged(adapter, musicId, collect);
        }
    }

    public void collapse() {
        if (mAdapter != null) {
            mAdapter.collapse();
        }
    }
}
