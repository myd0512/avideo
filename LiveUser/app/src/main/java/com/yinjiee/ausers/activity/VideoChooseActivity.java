package com.yinjiee.ausers.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yinjiee.ausers.Constants;
import com.yinjiee.ausers.R;
import com.yinjiee.ausers.adapter.VideoChooseAdapter;
import com.yinjiee.ausers.bean.VideoChooseBean;
import com.yinjiee.ausers.custom.ItemDecoration;
import com.yinjiee.ausers.interfaces.CommonCallback;
import com.yinjiee.ausers.interfaces.OnItemClickListener;
import com.yinjiee.ausers.utils.VideoLocalUtil;
import com.yinjiee.ausers.utils.WordUtil;

import java.util.List;

/**
 * Created by cxf on 2018/12/10.
 * 选择本地视频
 */

public class VideoChooseActivity extends AbsActivity implements OnItemClickListener<VideoChooseBean> {

    private long mMaxDuration;
    private RecyclerView mRecyclerView;
    private View mNoData;
    private VideoLocalUtil mVideoLocalUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_choose;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.video_local));
        mMaxDuration = getIntent().getLongExtra(Constants.VIDEO_DURATION, 15000);
        mNoData = findViewById(R.id.no_data);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 1, 1);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mVideoLocalUtil = new VideoLocalUtil();
        mVideoLocalUtil.getLocalVideoList(new CommonCallback<List<VideoChooseBean>>() {
            @Override
            public void callback(List<VideoChooseBean> videoList) {
                if (videoList == null || videoList.size() == 0) {
                    if (mNoData != null && mNoData.getVisibility() != View.VISIBLE) {
                        mNoData.setVisibility(View.VISIBLE);
                    }
                    return;
                }
                if (mRecyclerView != null) {
                    VideoChooseAdapter adapter = new VideoChooseAdapter(mContext, videoList);
                    adapter.setOnItemClickListener(VideoChooseActivity.this);
                    mRecyclerView.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onItemClick(VideoChooseBean bean, int position) {
//        if (bean.getDuration() > mMaxDuration + 1000) {
//            ToastUtil.show(R.string.video_duration_error);
//            return;
//        }
        Intent intent = new Intent();
        intent.putExtra(Constants.VIDEO_PATH, bean.getVideoPath());
        intent.putExtra(Constants.VIDEO_DURATION, bean.getDuration());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mVideoLocalUtil != null) {
            mVideoLocalUtil.release();
        }
        mVideoLocalUtil = null;
        mRecyclerView = null;
        mNoData = null;
        super.onDestroy();
    }
}
