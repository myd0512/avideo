package com.fengtuan.videoanchor.view;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.activity.EditProfileActivity;
import com.fengtuan.videoanchor.activity.LiveHistoryActivity;
import com.fengtuan.videoanchor.activity.SettingActivity;
import com.fengtuan.videoanchor.bean.UserBean;
import com.fengtuan.videoanchor.glide.ImgLoader;
import com.fengtuan.videoanchor.http.HttpConsts;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.interfa.CommonCallback;
import com.fengtuan.videoanchor.util.ClickUtil;

/**
 * 首页  个人资料
 */
public class IndexNavMineView extends IndexNavBaseView implements View.OnClickListener{
    private Activity mActivity ;

    private ImageView mUserIv ;
    private TextView mUserNameTv ;

    public IndexNavMineView(Activity context) {
        super(context);

        mActivity = context ;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_index_nav_mine ;
    }

    @Override
    protected void initView() {
        mUserIv = findViewById(R.id.view_index_nav_mine_user_iv) ;
        mUserNameTv = findViewById(R.id.view_index_nav_mine_user_tv) ;

        mUserIv.setOnClickListener(this);

        View userLay = findViewById(R.id.view_index_mine_user_lay) ;
        View historyLay = findViewById(R.id.view_index_mine_history_lay) ;
        View settingLay = findViewById(R.id.view_index_mine_setting_lay) ;
        settingLay.setOnClickListener(this) ;
        historyLay.setOnClickListener(this) ;
        userLay.setOnClickListener(this) ;
    }

    @Override
    public void showNavView() {
        getUserInfo() ;
    }

    @Override
    public void hiddenNavView() {

    }

    @Override
    public void destroyNavView() {
        HttpUtil.cancel(HttpConsts.GET_BASE_INFO);
    }

    /**
     * 获取个人资料
     */
    private void getUserInfo(){
        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if(bean != null){
                    ImgLoader.displayAvatar(bean.getAvatar(),mUserIv) ;
                    mUserNameTv.setText(bean.getUserNiceName()) ;
                }else{
//                    ToastUtil.show("个人信息获取失败") ;
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

        if(R.id.view_index_mine_user_lay == vId || R.id.view_index_nav_mine_user_iv == vId){

            Intent toIt = new Intent(mActivity,EditProfileActivity.class) ;
            mActivity.startActivity(toIt) ;

        } else if(R.id.view_index_mine_history_lay == vId){

            LiveHistoryActivity.launch(mActivity) ;

        } else if(R.id.view_index_mine_setting_lay == vId){

            SettingActivity.launch(mActivity) ;

        }
    }
}
