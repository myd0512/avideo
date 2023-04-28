package com.yunbao.liveanchor.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yunbao.liveanchor.Constants;
import com.yunbao.liveanchor.R;
import com.yunbao.liveanchor.util.ClickUtil;
import com.yunbao.liveanchor.util.SpUtil;
import com.yunbao.liveanchor.util.ToastUtil;
import com.yunbao.liveanchor.view.IndexNavBaseView;
import com.yunbao.liveanchor.view.IndexNavHomeView;
import com.yunbao.liveanchor.view.IndexNavMineView;

public class MainActivity extends AbsActivity implements View.OnClickListener{
    private ImageView mNavHomeIv ;
    private ImageView mNavMineIv ;

    private static final int NAV_TYPE_HOME = 0 ;
    private static final int NAV_TYPE_MINE = 1 ;
    private int mNavType = NAV_TYPE_HOME ;

    private IndexNavBaseView mCurrentView ;

    private IndexNavHomeView mHomeView ;
    private IndexNavMineView mMineView ;

    private boolean mIsFirst = true ;

    private boolean mCanExit = false ;
    private static final int MSG_WHAT_EXIT = 10 ;
    private static final int WAIT_TIME = 1500 ;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(MSG_WHAT_EXIT == msg.what){
                mCanExit = false ;
            }
        }
    } ;

    public static void launch(Context context){
        launch(context,false) ;
    }

    public static void launch(Context context, boolean showInvite){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.SHOW_INVITE, showInvite);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void main(Bundle savedInstanceState) {
        super.main(savedInstanceState);

        //重置version相关信息
        SpUtil.getInstance().setBooleanValue(Constants.VERSION_HAS_NEW,false) ;

        View navHomeLay = findViewById(R.id.act_main_bot_nav_home_lay) ;
        mNavHomeIv = findViewById(R.id.act_main_bot_nav_home_iv) ;
        View navMineLay = findViewById(R.id.act_main_bot_nav_mine_lay) ;
        mNavMineIv = findViewById(R.id.act_main_bot_nav_mine_iv) ;
        navMineLay.setOnClickListener(this);
        navHomeLay.setOnClickListener(this);

        View startIv = findViewById(R.id.act_main_bot_nav_start_iv) ;
        startIv.setOnClickListener(this);

        FrameLayout parentLay = findViewById(R.id.act_main_content_lay) ;

        mHomeView = new IndexNavHomeView(mActivity) ;
        mMineView = new IndexNavMineView(mActivity) ;

        parentLay.addView(mHomeView) ;
        parentLay.addView(mMineView) ;

        changeNavView(mNavType) ;
    }

    @Override
    public void onClick(View view) {
        if(!ClickUtil.canClick()){
            return;
        }

        int vId = view.getId() ;

        if(R.id.act_main_bot_nav_home_lay == vId){

            changeNavType(NAV_TYPE_HOME) ;

        }else if(R.id.act_main_bot_nav_mine_lay == vId){

            changeNavType(NAV_TYPE_MINE) ;

        }else if(R.id.act_main_bot_nav_start_iv == vId){//开播

            LiveInitActivity.launch(mContext) ;

        }

    }

    /**
     * 切换显示
     * @param type type
     */
    private void changeNavType(int type){
        if(mNavType == type){
            return;
        }

        switch (mNavType){
            case NAV_TYPE_HOME:
                mNavHomeIv.setImageDrawable(getResources().getDrawable(R.mipmap.tab_home)) ;
                break;
            case NAV_TYPE_MINE:
                mNavMineIv.setImageDrawable(getResources().getDrawable(R.mipmap.tab_mine)) ;
                break;
        }
        switch (type){
            case NAV_TYPE_HOME:
                mNavHomeIv.setImageDrawable(getResources().getDrawable(R.mipmap.tab_home_s)) ;
                break;
            case NAV_TYPE_MINE:
                mNavMineIv.setImageDrawable(getResources().getDrawable(R.mipmap.tab_mine_s)) ;
                break;
        }

        changeNavView(type) ;
    }

    /**
     * 切换view显示隐藏
     * @param type type
     */
    private void changeNavView(int type){
        if(mCurrentView != null){
            mCurrentView.setVisibility(View.GONE) ;
            mCurrentView.hiddenNavView() ;
        }

        if(NAV_TYPE_HOME == type){
            mCurrentView = mHomeView ;
        }else{
            mCurrentView = mMineView ;
        }

        mCurrentView.showNavView() ;
        mCurrentView.setVisibility(View.VISIBLE) ;

        mNavType = type ;

        if(mIsFirst){
            mIsFirst = false ;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mIsFirst){
            if(mCurrentView != null){
                mCurrentView.showNavView() ;
            }
        }
    }

    @Override
    public void onBackPressed() {

        if(!mCanExit){
            mCanExit = true ;
            ToastUtil.show("再按一次返回键退出");
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_EXIT,WAIT_TIME) ;
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mCurrentView != null){
            mCurrentView.destroyNavView();
        }

        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null) ;
        }
    }
}
