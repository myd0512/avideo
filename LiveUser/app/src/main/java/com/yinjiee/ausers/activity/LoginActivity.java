package com.yinjiee.ausers.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.yinjiee.ausers.AppConfig;
import com.yinjiee.ausers.AppContext;
import com.yinjiee.ausers.HtmlConfig;
import com.yinjiee.ausers.R;
import com.yinjiee.ausers.adapter.LoginTypeAdapter;
import com.yinjiee.ausers.bean.ConfigBean;
import com.yinjiee.ausers.bean.UserBean;
import com.yinjiee.ausers.event.RegSuccessEvent;
import com.yinjiee.ausers.http.HttpCallback;
import com.yinjiee.ausers.http.HttpConsts;
import com.yinjiee.ausers.http.HttpUtil;
import com.yinjiee.ausers.interfaces.CommonCallback;
import com.yinjiee.ausers.interfaces.OnItemClickListener;
import com.yinjiee.ausers.mob.LoginData;
import com.yinjiee.ausers.mob.MobBean;
import com.yinjiee.ausers.mob.MobCallback;
import com.yinjiee.ausers.mob.MobLoginUtil;
import com.yinjiee.ausers.utils.DialogUitl;
import com.yinjiee.ausers.utils.ToastUtil;
import com.yinjiee.ausers.utils.ValidatePhoneUtil;
import com.yinjiee.ausers.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by cxf on 2018/9/17.
 */
public class LoginActivity extends AbsActivity implements OnItemClickListener<MobBean> {

    private EditText mEditPhone;
    private EditText mEditPwd;
    private View mBtnLogin;
    private RecyclerView mRecyclerView;
    private MobLoginUtil mLoginUtil;
    private boolean mFirstLogin;//是否是第一次登录
    private boolean mShowInvite;//显示邀请码弹窗
    private String mLoginType = "phone";//登录方式

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void main() {
        mEditPhone = findViewById(R.id.edit_phone);
        mEditPwd = findViewById(R.id.edit_pwd);
        mBtnLogin = findViewById(R.id.btn_login);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEditPhone.getText().toString();
                String pwd = mEditPwd.getText().toString();
                mBtnLogin.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mEditPhone.addTextChangedListener(textWatcher);
        mEditPwd.addTextChangedListener(textWatcher);
        boolean otherLoginType = false;
        ConfigBean configBean = AppConfig.getInstance().getConfig();
        if (configBean != null) {
            List<MobBean> list = MobBean.getLoginTypeList(configBean.getLoginType());
            if (list != null && list.size() > 0) {
                mRecyclerView = findViewById(R.id.recyclerView);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                LoginTypeAdapter adapter = new LoginTypeAdapter(mContext, list);
                adapter.setOnItemClickListener(this);
                mRecyclerView.setAdapter(adapter);
                mLoginUtil = new MobLoginUtil();
                otherLoginType = true;
            }
        }
        if (!otherLoginType) {
            findViewById(R.id.other_login_tip).setVisibility(View.INVISIBLE);
        }
        EventBus.getDefault().register(this);
    }


    public static void forward() {
        Intent intent = new Intent(AppContext.sInstance, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        AppContext.sInstance.startActivity(intent);
    }


    public void loginClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                register();
                break;
            case R.id.btn_forget_pwd:
                forgetPwd();
                break;
            case R.id.btn_tip:
                forwardTip();
                break;
        }
    }

    //注册
    private void register() {
        startActivity(new Intent(mContext, RegisterActivity.class));
    }

    //忘记密码
    private void forgetPwd() {
        startActivity(new Intent(mContext, FindPwdActivity.class));
    }


    //手机号密码登录
    private void login() {
        String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.login_input_phone));
            mEditPhone.requestFocus();
            return;
        }
        if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.login_phone_error));
            mEditPhone.requestFocus();
            return;
        }
        String pwd = mEditPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            mEditPwd.setError(WordUtil.getString(R.string.login_input_pwd));
            mEditPwd.requestFocus();
            return;
        }
        mLoginType = "phone";
        HttpUtil.login(phoneNum, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
        });
    }

    //登录即代表同意服务和隐私条款
    private void forwardTip() {
        WebViewPagerActivity.forward(mContext, HtmlConfig.LOGIN_PRIVCAY);
    }

    //登录成功！
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            JSONObject obj = JSON.parseObject(info[0]);
            String uid = obj.getString("id");
            String token = obj.getString("token");
            mFirstLogin = obj.getIntValue("isreg") == 1;
            mShowInvite = obj.getIntValue("isagent") == 1;
            AppConfig.getInstance().setLoginInfo(uid, token, true);
            getBaseUserInfo();
            //友盟统计登录
            MobclickAgent.onProfileSignIn(mLoginType, uid);
        } else {
            ToastUtil.show(msg);
        }
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if (mFirstLogin) {
                    RecommendActivity.forward(mContext, mShowInvite);
                } else {
                    MainActivity.forward(mContext, mShowInvite);
                }
                finish();
            }
        });
    }

    /**
     * 三方登录
     */
    private void loginBuyThird(LoginData data) {
        mLoginType = data.getType();
        HttpUtil.loginByThird(data.getOpenID(), data.getNickName(), data.getAvatar(), data.getType(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
        });
    }

    @Override
    public void onItemClick(MobBean bean, int position) {
        if (mLoginUtil == null) {
            return;
        }
        final Dialog dialog = DialogUitl.loginAuthDialog(mContext);
        dialog.show();
        mLoginUtil.execute(bean.getType(), new MobCallback() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    loginBuyThird((LoginData) data);
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFinish() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegSuccessEvent(RegSuccessEvent e) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        HttpUtil.cancel(HttpConsts.LOGIN);
        HttpUtil.cancel(HttpConsts.GET_QQ_LOGIN_UNION_ID);
        HttpUtil.cancel(HttpConsts.LOGIN_BY_THIRD);
        HttpUtil.cancel(HttpConsts.GET_BASE_INFO);
        if (mLoginUtil != null) {
            mLoginUtil.release();
        }
        super.onDestroy();
    }
}
