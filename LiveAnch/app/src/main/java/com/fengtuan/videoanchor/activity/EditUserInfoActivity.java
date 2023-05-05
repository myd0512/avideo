package com.fengtuan.videoanchor.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengtuan.videoanchor.AppConfig;
import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.bean.UserBean;
import com.fengtuan.videoanchor.http.HttpCallback;
import com.fengtuan.videoanchor.http.HttpConsts;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.util.MyStringUtils;
import com.fengtuan.videoanchor.util.ToastUtil;

/**
 * Created by cxf on 2018/9/29.
 * 编辑信息
 */

public class EditUserInfoActivity extends AbsActivity implements View.OnClickListener {
    private EditText mEditText;

    public static final int EDIT_TYPE_NAME = 0 ;//昵称
    public static final int EDIT_TYPE_LOCATION = 1 ;//地区
    public static final int EDIT_TYPE_CARD = 2 ;//名片

    private int mEditType ;

    private UserBean mUserBean ;

    private String mFiledName ;
    private String mHint ;

    public static void launch(Activity activity,int type,int requestCode){
        Intent toIt = new Intent(activity,EditUserInfoActivity.class) ;
        toIt.putExtra("editItem",type) ;
        activity.startActivityForResult(toIt,requestCode) ;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_user_info;
    }

    @Override
    protected void main() {
        mEditType = getIntent().getIntExtra("editItem",EDIT_TYPE_NAME) ;
        mUserBean = AppConfig.getInstance().getUserBean() ;
        String title = null ;
        String def = null ;

        if(EDIT_TYPE_NAME == mEditType){
            title = "设置昵称" ;
            mHint = "请输入昵称" ;
            mFiledName = "user_nicename" ;

            if(mUserBean != null){
                def = mUserBean.getUserNiceName() ;
            }
        }else if(EDIT_TYPE_LOCATION == mEditType){
            title = "设置地区" ;
            mHint = "请输入地区" ;
            mFiledName = "location" ;

            if(mUserBean != null){
                def = mUserBean.getLocation() ;
            }
        }else if(EDIT_TYPE_CARD == mEditType){
            title = "设置名片" ;
            mHint = "请输入名片" ;
            mFiledName = "signature" ;

            if(mUserBean != null){
                def = mUserBean.getSignature() ;
            }
        }

        setTitle(MyStringUtils.convertNull(title)) ;

        mEditText = findViewById(R.id.edit);
        mEditText.setHint(MyStringUtils.convertNull(mHint)) ;
        if(EDIT_TYPE_NAME == mEditType){
            mEditText.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(8)
            });
        }

        findViewById(R.id.btn_save).setOnClickListener(this);

        if (!TextUtils.isEmpty(def)) {
            if (EDIT_TYPE_NAME == mEditType && def.length() > 8) {
                def = def.substring(0, 8);
            }
            mEditText.setText(def);
            mEditText.setSelection(def.length());
        }
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }

        if(R.id.btn_save == v.getId()){
            submitChange() ;
        }
    }

    private void submitChange(){
        final String content = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show(MyStringUtils.convertNull(mHint)) ;
            return;
        }

        HttpUtil.updateFields("{\""+MyStringUtils.convertNull(mFiledName) +"\":\"" + content + "\"}", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        ToastUtil.show(obj.getString("msg"));
                        UserBean u = AppConfig.getInstance().getUserBean();
                        if (u != null) {
                            u.setUserNiceName(content);
                        }
                        Intent intent = getIntent();
                        intent.putExtra("itemName", content);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConsts.UPDATE_FIELDS);
        super.onDestroy();
    }
}
