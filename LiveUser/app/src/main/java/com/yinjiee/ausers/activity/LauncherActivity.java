package com.yinjiee.ausers.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.yinjiee.ausers.AppConfig;
import com.yinjiee.ausers.AppContext;
import com.yinjiee.ausers.R;
import com.yinjiee.ausers.bean.ConfigBean;
import com.yinjiee.ausers.bean.UserBean;
import com.yinjiee.ausers.http.HttpCallback;
import com.yinjiee.ausers.http.HttpConsts;
import com.yinjiee.ausers.http.HttpUtil;
import com.yinjiee.ausers.interfaces.CommonCallback;
import com.yinjiee.ausers.utils.SpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by cxf on 2018/9/17.
 */

public class LauncherActivity extends AppCompatActivity {

    private Handler mHandler;
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //下面的代码是为了防止一个bug:
        // 收到极光通知后，点击通知，如果没有启动app,则启动app。然后切后台，再次点击桌面图标，app会重新启动，而不是回到前台。
        Intent intent = getIntent();
        if (!isTaskRoot()
                && intent != null
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }

        String icode = readInviteCode();
        if (icode.length() > 0){
//            new AlertDialog.Builder(this).setTitle("邀请码").setMessage(icode).setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    getConfig();
//                }
//            }).create().show();
            SpUtil.getInstance().setStringValue(SpUtil.INVCODE,icode);
        }


        setStatusBar();
        setContentView(R.layout.activity_launcher);
        mContext = this;

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getConfig();
            }
        }, 800);

    }

    private String readInviteCode(){

            InputStream is = null;
            BufferedReader reader = null;
            String result = "";

            StringBuilder sb = new StringBuilder();
            String line = "";
            try {
                String ccfpath = "/META-INF/share.json";
                is = this.getClass().getResourceAsStream(ccfpath);
                reader = new BufferedReader(new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result0 = sb.toString();
                if (result0.length() > 0) {
                    JSONObject obj = JSONObject.parseObject(result0);
                    result = obj.getString("sharecode");
                }
            } catch (Exception e) {
                result = "";
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                }
            }

            return result;
    }

    /**
     * 获取Config信息
     */
    private void getConfig() {
        HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                if (bean != null) {
                    AppContext.sInstance.initBeautySdk(bean.getBeautyKey());
                    checkUidAndToken();
                }
            }
        });
    }

    /**
     * 检查uid和token是否存在
     */
    private void checkUidAndToken() {
        String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(new String[]{SpUtil.UID, SpUtil.TOKEN});
        final String uid = uidAndToken[0];
        final String token = uidAndToken[1];
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
            HttpUtil.ifToken(uid, token, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        AppConfig.getInstance().setLoginInfo(uid, token, false);
                        getBaseUserInfo();
                    }
                }
            });
        } else {
//            LoginActivity.forward();
            ActivityLoginNew.forward();
        }
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if (bean != null) {
                    forwardMainActivity();
                }
            }
        });
    }

    /**
     * 跳转到首页
     */
    private void forwardMainActivity() {
        MainActivity.forward(mContext);
        finish();
    }


    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConsts.IF_TOKEN);
        HttpUtil.cancel(HttpConsts.GET_BASE_INFO);
        HttpUtil.cancel(HttpConsts.GET_CONFIG);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    /**
     * 设置透明状态栏
     */
    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
        }
    }
}
