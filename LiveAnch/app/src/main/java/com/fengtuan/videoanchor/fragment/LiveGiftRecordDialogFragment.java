package com.fengtuan.videoanchor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fengtuan.videoanchor.Constants;
import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.event.GameResultEvent;
import com.fengtuan.videoanchor.http.HttpConsts;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.util.ScreenDimenUtil;
import com.fengtuan.videoanchor.view.GameGiftBaseHolder;
import com.fengtuan.videoanchor.view.LiveGameRecordHolder;
import com.fengtuan.videoanchor.view.LiveGiftRecordHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播礼物、游戏开奖记录
 */

public class LiveGiftRecordDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private TextView mNavGameTv ;
    private TextView mNavGiftTv ;

    private ViewPager mVp ;

    private int mNavType = NAV_TYPE_GAME ;
    private static final int NAV_TYPE_GAME = 0 ;
    private static final int NAV_TYPE_GIFT = 1 ;

    private LiveGameRecordHolder mGameRecordHolder;
    private LiveGiftRecordHolder mGiftRecordHolder ;

    private String mGameId ;
    private String mGameName ;
    private String mGamePeriod ;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_gift_record;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        EventBus.getDefault().register(this);

        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) (ScreenDimenUtil.getInstance().getScreenHeight() / 2F);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mVp = mRootView.findViewById(R.id.dialog_live_gift_record_content_vp) ;

        View closeIv = mRootView.findViewById(R.id.dialog_live_gift_record_close_iv) ;
        mNavGameTv = mRootView.findViewById(R.id.dialog_live_gift_record_nav_game_tv) ;
        mNavGiftTv = mRootView.findViewById(R.id.dialog_live_gift_record_nav_gift_tv) ;
        mNavGameTv.setOnClickListener(this);
        mNavGiftTv.setOnClickListener(this);
        closeIv.setOnClickListener(this);

        Bundle bundle = getArguments() ;
        if(bundle != null){
            mGameId = bundle.getString(Constants.LIVE_GAME_ID) ;
            mGameName = bundle.getString(Constants.LIVE_GAME_NAME) ;
            mGamePeriod = bundle.getString(Constants.LIVE_GAME_PERIOD) ;
        }

        mGameRecordHolder = new LiveGameRecordHolder(mContext,null,mGameId,mGameName,mGamePeriod) ;
        mGiftRecordHolder = new LiveGiftRecordHolder(mContext,null) ;

        List<GameGiftBaseHolder> viewList = new ArrayList<>(2) ;
        viewList.add(mGameRecordHolder) ;
        viewList.add(mGiftRecordHolder) ;

        RecordPageAdapter pageAdapter = new RecordPageAdapter(viewList) ;
        mVp.setOffscreenPageLimit(3) ;
        mVp.setAdapter(pageAdapter) ;
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                changeNavType(0 == position ? NAV_TYPE_GAME : NAV_TYPE_GIFT) ;
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateGameResultEvent(GameResultEvent event){
        if(event != null){
            mGamePeriod = event.getNewPeriod() ;

            if(mGameRecordHolder != null){
                mGameRecordHolder.setGamePeriod(event.getPeriod(),mGamePeriod,event.getResultPoint()) ;
            }

        }
    }

    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);
        HttpUtil.cancel(HttpConsts.GIFT_LOG);

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;

        if(R.id.dialog_live_gift_record_close_iv == vId){
            dismiss() ;
        } else if(R.id.dialog_live_gift_record_nav_game_tv == vId){//游戏

            mVp.setCurrentItem(0);

        } else if(R.id.dialog_live_gift_record_nav_gift_tv == vId){//礼物

            mVp.setCurrentItem(1);

        }
    }

    /**
     * 切换选项卡
     * @param type type
     */
    private void changeNavType(int type){
        if(mNavType == type){
            return;
        }

        switch (mNavType){
            case NAV_TYPE_GAME:
                mNavGameTv.setTextColor(mContext.getResources().getColor(R.color.gift_record_nav_normal)) ;
                break;
            case NAV_TYPE_GIFT:
                mNavGiftTv.setTextColor(mContext.getResources().getColor(R.color.gift_record_nav_normal)) ;
                break;
            default:
                break;
        }
        switch (type){
            case NAV_TYPE_GAME:
                mNavGameTv.setTextColor(mContext.getResources().getColor(R.color.gift_record_nav_checked)) ;
                break;
            case NAV_TYPE_GIFT:
                mNavGiftTv.setTextColor(mContext.getResources().getColor(R.color.gift_record_nav_checked)) ;
                break;
            default:
                break;
        }

        changeNav(type) ;
    }

    /**
     * 切换选项卡
     * @param type type
     */
    private void changeNav(int type){
        mGameRecordHolder.setVisible(type == NAV_TYPE_GAME) ;
        mGiftRecordHolder.setVisible(type == NAV_TYPE_GIFT) ;

        mNavType = type ;
    }


    static class RecordPageAdapter extends PagerAdapter{
        private List<GameGiftBaseHolder> mViewList ;

        public RecordPageAdapter(List<GameGiftBaseHolder> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size() ;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = mViewList.get(position).getContentView() ;
            container.addView(view) ;
            return view ;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if(object instanceof GameGiftBaseHolder){
                ((GameGiftBaseHolder)object).destroyView() ;
                container.removeView(((GameGiftBaseHolder)object).getContentView());
            }
        }
    }

}
