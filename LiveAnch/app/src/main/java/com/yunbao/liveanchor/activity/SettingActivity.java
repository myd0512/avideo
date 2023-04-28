package com.yunbao.liveanchor.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.yunbao.liveanchor.AppConfig;
import com.yunbao.liveanchor.Constants;
import com.yunbao.liveanchor.R;
import com.yunbao.liveanchor.bean.ConfigBean;
import com.yunbao.liveanchor.interfa.CommonCallback;
import com.yunbao.liveanchor.util.ClickUtil;
import com.yunbao.liveanchor.util.DialogUitl;
import com.yunbao.liveanchor.util.SpUtil;
import com.yunbao.liveanchor.util.ToastUtil;
import com.yunbao.liveanchor.util.VersionUtil;

/**
 * 设置
 */
public class SettingActivity extends AbsActivity implements View.OnClickListener{
    private View mVersionTipsTv ;

    public static void launch(Context context){
        Intent toIt = new Intent(context,SettingActivity.class) ;
        context.startActivity(toIt) ;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void main() {
        super.main();

        setTitle("设置");

        mVersionTipsTv = findViewById(R.id.act_setting_version_tips_tv) ;
        TextView versionTv = findViewById(R.id.act_setting_version_tv) ;
        versionTv.setText("当前版本：V" + VersionUtil.getVersion()) ;

        View passLay = findViewById(R.id.act_setting_pass_lay) ;
        View versionLay = findViewById(R.id.act_setting_version_lay) ;
        View exitLay = findViewById(R.id.act_setting_exit_lay) ;
        exitLay.setOnClickListener(this);
        versionLay.setOnClickListener(this);
        passLay.setOnClickListener(this);

        updateVersionTips();
    }

    @Override
    public void onClick(View view) {
         if(!ClickUtil.canClick()){
             return;
         }

         int vId = view.getId() ;
         if(R.id.act_setting_pass_lay == vId){

             startActivity(new Intent(mContext, ModifyPwdActivity.class));

         } else if(R.id.act_setting_version_lay == vId){

             checkVersion() ;

         } else if(R.id.act_setting_exit_lay == vId){

             DialogUitl.showSimpleDialog(mContext, "退出当前账号？", new DialogUitl.SimpleCallback() {
                 @Override
                 public void onConfirmClick(Dialog dialog, String content) {
                     dialog.dismiss();
                     logout() ;
                 }
             });

         }
    }


    /**
     * 退出登录
     */
    private void logout() {
        AppConfig.getInstance().clearLoginInfo();
        LoginActivity.forward();
    }

    /**
     * 检查更新
     */
    private void checkVersion() {
        AppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (VersionUtil.isLatest(configBean.getVersion())) {
                        SpUtil.getInstance().setBooleanValue(Constants.VERSION_HAS_NEW,false) ;
                        ToastUtil.show(R.string.version_latest);
                    } else {
                        SpUtil.getInstance().setBooleanValue(Constants.VERSION_HAS_NEW,true) ;
                        VersionUtil.showDialog(mContext, configBean.getUpdateDes(), configBean.getDownloadApkUrl());
                    }

                    updateVersionTips() ;
                }
            }
        });
    }

    private void updateVersionTips(){
        boolean hasNew = SpUtil.getInstance().getBooleanValue(Constants.VERSION_HAS_NEW) ;

        mVersionTipsTv.setVisibility(hasNew ? View.VISIBLE : View.GONE) ;
    }

}
