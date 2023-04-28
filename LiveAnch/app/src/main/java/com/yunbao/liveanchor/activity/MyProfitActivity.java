package com.yunbao.liveanchor.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.liveanchor.Constants;
import com.yunbao.liveanchor.HtmlConfig;
import com.yunbao.liveanchor.R;
import com.yunbao.liveanchor.http.HttpCallback;
import com.yunbao.liveanchor.http.HttpConsts;
import com.yunbao.liveanchor.http.HttpUtil;
import com.yunbao.liveanchor.util.IconUtil;
import com.yunbao.liveanchor.util.L;
import com.yunbao.liveanchor.util.MyStringUtils;
import com.yunbao.liveanchor.util.SpUtil;
import com.yunbao.liveanchor.util.StringUtil;
import com.yunbao.liveanchor.util.ToastUtil;

/**
 * Created by cxf on 2018/10/20.
 */

public class MyProfitActivity extends AbsActivity implements View.OnClickListener {

    private TextView mCan;//可提取映票数
    private TextView mMoney;
    private TextView mTip;//温馨提示
    private EditText mEdit;
    private int mRate;
    private long mMaxCanMoney;//可提取映票数
    private View mChooseTip;
    private View mAccountGroup;
    private ImageView mAccountIcon;
    private TextView mAccount;
    private String mAccountID;
    private View mBtnCash;

    public static void launch(Context context){
        Intent toIt = new Intent(context,MyProfitActivity.class) ;
        context.startActivity(toIt) ;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_profit;
    }

    @Override
    protected void main() {
        mCan = findViewById(R.id.can);
        mTip = findViewById(R.id.tip);
        mMoney = findViewById(R.id.money);
        mEdit = findViewById(R.id.edit);
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    long i = Long.parseLong(s.toString());
                    if (i > mMaxCanMoney) {
                        i = mMaxCanMoney;
                        s = String.valueOf(mMaxCanMoney);
                        mEdit.setText(s);
                        mEdit.setSelection(s.length());
                    }

                    mMoney.setText("¥" + MyStringUtils.convertMoney(String.valueOf(i * 0.7F)));
//                    mMoney.setText("¥" + MyStringUtils.convertMoney(String.valueOf(i)));

//                    if (mRate != 0) {
//                        mMoney.setText("¥" + (i / mRate));
//                    }
                    mBtnCash.setEnabled(true);
                } else {
                    mMoney.setText("¥");
                    mBtnCash.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBtnCash = findViewById(R.id.btn_cash);
        mBtnCash.setOnClickListener(this);
        findViewById(R.id.btn_choose_account).setOnClickListener(this);
        findViewById(R.id.btn_cash_record).setOnClickListener(this);
        mChooseTip = findViewById(R.id.choose_tip);
        mAccountGroup = findViewById(R.id.account_group);
        mAccountIcon = findViewById(R.id.account_icon);
        mAccount = findViewById(R.id.account);
        loadData();
    }


    private void loadData() {
        HttpUtil.getProfit(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    try {
                        JSONObject obj = JSON.parseObject(info[0]);
                        mTip.setText(obj.getString("tips"));
                        String votes = obj.getString("votes");
                        mCan.setText(votes);
                        if (votes.contains(".")) {
                            votes = votes.substring(0, votes.indexOf('.'));
                        }
                        mMaxCanMoney = Long.parseLong(votes);

//                        mRate = obj.getIntValue("cash_rate");

                    } catch (Exception e) {
                        L.e("提现接口错误------>" + e.getClass() + "------>" + e.getMessage());
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cash:
                cash();
                break;
            case R.id.btn_choose_account:
                chooseAccount();
                break;
            case R.id.btn_cash_record:
                cashRecord();
                break;
        }
    }

    /**
     * 提现记录
     */
    private void cashRecord() {
        String url = HtmlConfig.CASH_RECORD;
        WebViewActivity.forward(mContext, url);
    }

    /**
     * 提现
     */
    private void cash() {
        String votes = mEdit.getText().toString().trim();
        if (TextUtils.isEmpty(votes)) {
            ToastUtil.show("输入要提现的金额");
            return;
        }
        if (TextUtils.isEmpty(mAccountID)) {
            ToastUtil.show("请选择提现账户");
            return;
        }

        HttpUtil.doCash(StringUtil.converMoney2Point(String.valueOf(StringUtil.convertFloat(votes) * 0.7F)), mAccountID, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show(msg);
            }
        });
    }

    /**
     * 选择账户
     */
    private void chooseAccount() {
        Intent intent = new Intent(mContext, CashActivity.class);
        intent.putExtra(Constants.CASH_ACCOUNT_ID, mAccountID);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();

        getAccount();
    }

    private void getAccount() {
        String[] values = SpUtil.getInstance().getMultiStringValue(Constants.CASH_ACCOUNT_ID, Constants.CASH_ACCOUNT, Constants.CASH_ACCOUNT_TYPE);
        if (values != null && values.length == 3) {
            String accountId = values[0];
            String account = values[1];
            String type = values[2];
            if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(account) && !TextUtils.isEmpty(type)) {
                if (mChooseTip.getVisibility() == View.VISIBLE) {
                    mChooseTip.setVisibility(View.INVISIBLE);
                }
                if (mAccountGroup.getVisibility() != View.VISIBLE) {
                    mAccountGroup.setVisibility(View.VISIBLE);
                }
                mAccountID = accountId;
                mAccountIcon.setImageResource(IconUtil.getCashTypeIcon(Integer.parseInt(type)));
                mAccount.setText(account);
            } else {
                if (mAccountGroup.getVisibility() == View.VISIBLE) {
                    mAccountGroup.setVisibility(View.INVISIBLE);
                }
                if (mChooseTip.getVisibility() != View.VISIBLE) {
                    mChooseTip.setVisibility(View.VISIBLE);
                }
                mAccountID = null;
            }
        } else {
            if (mAccountGroup.getVisibility() == View.VISIBLE) {
                mAccountGroup.setVisibility(View.INVISIBLE);
            }
            if (mChooseTip.getVisibility() != View.VISIBLE) {
                mChooseTip.setVisibility(View.VISIBLE);
            }
            mAccountID = null;
        }
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConsts.DO_CASH);
        HttpUtil.cancel(HttpConsts.GET_PROFIT);
        super.onDestroy();
    }
}
