package com.fengtuan.videoanchor.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.activity.LiveAnchorActivity;
import com.fengtuan.videoanchor.bean.LiveBean;
import com.fengtuan.videoanchor.glide.ImgLoader;
import com.fengtuan.videoanchor.http.HttpCallback;
import com.fengtuan.videoanchor.http.HttpConsts;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.interfa.LifeCycleAdapter;
import com.fengtuan.videoanchor.util.StringUtil;

/**
 * Created by cxf on 2018/10/9.
 */

public class LiveEndViewHolder extends AbsViewHolder implements View.OnClickListener {

    private ImageView mAvatar1;
    private ImageView mAvatar2;
    private TextView mName;
    private TextView mDuration;//直播时长
    private TextView mVotes;//收获映票
    private TextView mWatchNum;//观看人数

    public LiveEndViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_end;
    }

    @Override
    public void init() {
        mAvatar1 = (ImageView) findViewById(R.id.avatar_1);
        mAvatar2 = (ImageView) findViewById(R.id.avatar_2);
        mName = (TextView) findViewById(R.id.name);
        mDuration = (TextView) findViewById(R.id.duration);
        mVotes = (TextView) findViewById(R.id.votes);
        mWatchNum = (TextView) findViewById(R.id.watch_num);
        findViewById(R.id.btn_back).setOnClickListener(this);
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onDestroy() {
                HttpUtil.cancel(HttpConsts.GET_LIVE_END_INFO);
            }
        };
//        TextView votesName = (TextView) findViewById(R.id.votes_name);
//        votesName.setText(WordUtil.getString(R.string.live_votes) + AppConfig.getInstance().getVotesName());
    }

    public void showData(LiveBean liveBean, String stream) {
        HttpUtil.getLiveEndInfo(stream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    try {
                        JSONObject obj = JSON.parseObject(info[0]);
                        mVotes.setText(StringUtil.converMoney2Point(obj.getString("votes")));
                        mDuration.setText(obj.getString("length"));
                        mWatchNum.setText(obj.getString("nums"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if (liveBean != null) {
            mName.setText(liveBean.getUserNiceName());
            ImgLoader.displayBlur(liveBean.getAvatar(), mAvatar1);
            ImgLoader.displayAvatar(liveBean.getAvatar(), mAvatar2);
        }

    }

    @Override
    public void onClick(View v) {
        if (mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).superBackPressed();
        }
    }

}
