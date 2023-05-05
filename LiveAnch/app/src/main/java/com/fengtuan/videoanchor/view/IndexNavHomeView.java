package com.fengtuan.videoanchor.view;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.activity.MyProfitActivity;
import com.fengtuan.videoanchor.activity.RankListActivity;
import com.fengtuan.videoanchor.bean.LiveTimeReward;
import com.fengtuan.videoanchor.http.HttpCallback;
import com.fengtuan.videoanchor.http.HttpConsts;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.http.ParseUtils;
import com.fengtuan.videoanchor.util.ClickUtil;
import com.fengtuan.videoanchor.util.ToastUtil;

/**
 * 首页  开播信息
 */
public class IndexNavHomeView extends IndexNavBaseView implements View.OnClickListener{
    private Activity mActivity ;

    private TextView mLiveTimeTv ;
    private TextView mLiveRewardTv ;

    public IndexNavHomeView(Activity context) {
        super(context);

        mActivity = context ;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_index_nav_home ;
    }

    @Override
    protected void initView() {
        mLiveTimeTv = findViewById(R.id.view_index_home_live_time_tv) ;
        mLiveRewardTv = findViewById(R.id.view_index_home_live_reward_tv) ;

        View walletLay = findViewById(R.id.view_index_home_opt_wallet_lay) ;
        View rankLay = findViewById(R.id.view_index_home_opt_rank_lay) ;

        walletLay.setOnClickListener(this);
        rankLay.setOnClickListener(this);

    }

    @Override
    public void showNavView() {
        getLiveTimeReward() ;
    }

    @Override
    public void hiddenNavView() {

    }

    @Override
    public void destroyNavView() {
        HttpUtil.cancel(HttpConsts.GET_LIVE_TIME_REWARD) ;
    }


    /**
     * 获取直播时长和收益
     */
    private void getLiveTimeReward(){
        HttpUtil.getLiveTimeReward(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(info != null && info.length > 0){
                    LiveTimeReward timeReward = ParseUtils.parseJson(info[0],LiveTimeReward.class) ;
                    if(timeReward != null){
                        String liveTime = "00:00:00" ;
                        String liveReward = timeReward.getToday_income_total() ;

                        LiveTimeReward.LTRTimeBean timeBean = timeReward.getTime() ;
                        if(timeBean != null && timeBean.sec != null && timeBean.sec.size() > 0){
                            liveTime = timeBean.sec.get(0) ;
                            if("false".equals(liveTime)){
                                liveTime  = "00:00:00" ;
                            }
                        }

                        mLiveTimeTv.setText(liveTime);
                        mLiveRewardTv.setText("¥" + liveReward);
                    }
                }else{
                    ToastUtil.show("获取开播时长失败") ;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(!ClickUtil.canClick()){
            return;
        }

        int vId = view.getId() ;
        if(R.id.view_index_home_opt_wallet_lay == vId){

            MyProfitActivity.launch(mContext) ;

        }else if(R.id.view_index_home_opt_rank_lay == vId){

            RankListActivity.launch(mActivity) ;

        }
    }


}
