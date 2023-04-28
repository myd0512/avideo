package com.yunbao.liveanchor.view;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.yunbao.liveanchor.R;
import com.yunbao.liveanchor.activity.MyProfitActivity;
import com.yunbao.liveanchor.activity.RankListActivity;
import com.yunbao.liveanchor.bean.LiveTimeReward;
import com.yunbao.liveanchor.http.HttpCallback;
import com.yunbao.liveanchor.http.HttpConsts;
import com.yunbao.liveanchor.http.HttpUtil;
import com.yunbao.liveanchor.http.ParseUtils;
import com.yunbao.liveanchor.util.ClickUtil;
import com.yunbao.liveanchor.util.ToastUtil;

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
