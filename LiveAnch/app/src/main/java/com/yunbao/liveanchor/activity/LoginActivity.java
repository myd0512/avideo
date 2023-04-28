package com.yunbao.liveanchor.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.liveanchor.AppConfig;
import com.yunbao.liveanchor.AppContext;
import com.yunbao.liveanchor.HtmlConfig;
import com.yunbao.liveanchor.R;
import com.yunbao.liveanchor.bean.UserBean;
import com.yunbao.liveanchor.http.HttpCallback;
import com.yunbao.liveanchor.http.HttpConsts;
import com.yunbao.liveanchor.http.HttpUtil;
import com.yunbao.liveanchor.interfa.CommonCallback;
import com.yunbao.liveanchor.util.ToastUtil;
import com.yunbao.liveanchor.util.ValidatePhoneUtil;
import com.yunbao.liveanchor.util.WordUtil;

/**
 * 登录
 */
public class LoginActivity extends AbsActivity implements View.OnClickListener{
    private EditText mEditPhone;
    private EditText mEditPwd;
    private View mBtnLogin;
    private boolean mFirstLogin;//是否是第一次登录
    private boolean mShowInvite;//显示邀请码弹窗


    public static void forward() {
        Intent intent = new Intent(AppContext.sInstance, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        AppContext.sInstance.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void main() {
        mEditPhone = findViewById(R.id.edit_phone);
        mEditPwd = findViewById(R.id.edit_pwd);
        mBtnLogin = findViewById(R.id.btn_login);
        View tipsTv = findViewById(R.id.btn_tip);
        mBtnLogin.setOnClickListener(this);
        tipsTv.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;

        switch (vId) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_tip:
                forwardTip();
                break;
        }
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
        HttpUtil.login(phoneNum, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
        });
    }

    //登录即代表同意服务和隐私条款
    private void forwardTip() {
        WebViewActivity.forward(mContext, HtmlConfig.LOGIN_PRIVCAY);
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
                MainActivity.launch(mContext, mShowInvite);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {

        HttpUtil.cancel(HttpConsts.LOGIN);
        HttpUtil.cancel(HttpConsts.GET_BASE_INFO);

        super.onDestroy();
    }

}