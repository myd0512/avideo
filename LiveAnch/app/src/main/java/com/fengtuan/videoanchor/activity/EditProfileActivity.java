package com.fengtuan.videoanchor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengtuan.videoanchor.AppConfig;
import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.bean.UserBean;
import com.fengtuan.videoanchor.glide.ImgLoader;
import com.fengtuan.videoanchor.http.HttpCallback;
import com.fengtuan.videoanchor.http.HttpConsts;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.interfa.CommonCallback;
import com.fengtuan.videoanchor.interfa.ImageResultCallback;
import com.fengtuan.videoanchor.util.DialogUitl;
import com.fengtuan.videoanchor.util.ProcessImageUtil;
import com.fengtuan.videoanchor.util.ToastUtil;

import java.io.File;

/**
 * Created by cxf on 2018/9/29.
 * 我的 编辑资料
 */

public class EditProfileActivity extends AbsActivity {

    private ImageView mAvatar;
    private TextView mName;
    private TextView mNumber;
    private TextView mSign;
    private TextView mBirthday;
    private TextView mSex;
    private TextView mLocationTv;
    private ProcessImageUtil mImageUtil;
    private UserBean mUserBean;


    private static final int REQUEST_CODE_NAME = 1000 ;
    private static final int REQUEST_CODE_LOCATION = 1001 ;
    private static final int REQUEST_CODE_CARD = 1002 ;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void main() {
        setTitle("个人信息");
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mNumber = findViewById(R.id.number);
        mSign = findViewById(R.id.sign);
        mBirthday = findViewById(R.id.birthday);
        mSex = findViewById(R.id.sex);
        mLocationTv = findViewById(R.id.location);

        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {
            }
            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(file, mAvatar);
                    HttpUtil.updateAvatar(file, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                ToastUtil.show(R.string.edit_profile_update_avatar_success);
                                UserBean bean = AppConfig.getInstance().getUserBean();
                                if (bean != null) {
                                    JSONObject obj = JSON.parseObject(info[0]);
                                    bean.setAvatar(obj.getString("avatar"));
                                    bean.setAvatarThumb(obj.getString("avatarThumb"));
                                }
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure() {
            }
        });

        mUserBean = AppConfig.getInstance().getUserBean();
        if (mUserBean != null) {
            showData(mUserBean);
        } else {
            HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                @Override
                public void callback(UserBean u) {
                    mUserBean = u;
                    showData(u);
                }
            });
        }
    }

    public void editProfileClick(View v) {
        if (!canClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_avatar:
                editAvatar();
                break;
            case R.id.btn_name:
                forwardName();
                break;
            case R.id.btn_sign:
                forwardSign();
                break;
            case R.id.btn_birthday:
                editBirthday();
                break;
            case R.id.btn_sex:
                forwardSex();
                break;
            case R.id.btn_location:
                forwardLocation();
                break;
        }
    }

    private void editAvatar() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{
                R.string.camera, R.string.alumb}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    mImageUtil.getImageByCamera();
                } else {
                    mImageUtil.getImageByAlumb();
                }
            }
        });
    }

    private void forwardName() {
        EditUserInfoActivity.launch(mActivity,EditUserInfoActivity.EDIT_TYPE_NAME,REQUEST_CODE_NAME) ;
    }

    private void forwardSign() {
        EditUserInfoActivity.launch(mActivity,EditUserInfoActivity.EDIT_TYPE_CARD,REQUEST_CODE_CARD) ;
    }

    private void editBirthday() {
        if (mUserBean == null) {
            return;
        }

        DialogUitl.showDatePickerDialog(mContext, new DialogUitl.DataPickerCallback() {
            @Override
            public void onConfirmClick(final String date) {
                HttpUtil.updateFields("{\"birthday\":\"" + date + "\"}", new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (info.length > 0) {
                                ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                                mUserBean.setBirthday(date);
                                mBirthday.setText(date);
                            }
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }
        });
    }

    private void forwardSex() {
        if (mUserBean == null) {
            return;
        }

        DialogUitl.showStringArrayDialog(mContext, new Integer[]{
                R.string.sex_male, R.string.sex_female, R.string.sex_secrecy}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.sex_male) {//男
                    updateSex(1) ;
                }else if (tag == R.string.sex_female) {//女
                    updateSex(2) ;
                } else {//保密
                    updateSex(0) ;
                }
            }
        });
    }

    private void updateSex(final int sexValue){
        HttpUtil.updateFields("{\"sex\":\"" + sexValue + "\"}", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    ToastUtil.show(obj.getString("msg"));

                    UserBean u = AppConfig.getInstance().getUserBean();
                    if (u != null) {
                        u.setSex(sexValue);
                    }
                    mSex.setText(sexValue == 1 ? R.string.sex_male : (sexValue == 0 ? R.string.sex_secrecy : R.string.sex_female));
                }
            }
        });
    }

    private void forwardLocation(){
        if (mUserBean == null) {
            return;
        }

        EditUserInfoActivity.launch(mActivity,EditUserInfoActivity.EDIT_TYPE_LOCATION,REQUEST_CODE_LOCATION) ;
    }

    @Override
    protected void onDestroy() {
        if (mImageUtil != null) {
            mImageUtil.release();
        }
        HttpUtil.cancel(HttpConsts.UPDATE_AVATAR);
        HttpUtil.cancel(HttpConsts.UPDATE_FIELDS);
        super.onDestroy();
    }

    private void showData(UserBean u) {
        if(null == u){
            DialogUitl.showSimpleDialog(mContext, "用户信息获取失败，请返回重试"
                    , false, new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    finish() ;
                }
            });
            return;
        }

        ImgLoader.displayAvatar(u.getAvatar(), mAvatar);
        mName.setText(u.getUserNiceName());
        mNumber.setText(u.getId());
        mSign.setText(u.getSignature());
        mBirthday.setText(u.getBirthday());
        mLocationTv.setText(u.getLocation());
        mSex.setText(u.getSex() == 1 ? R.string.sex_male : (u.getSex() == 0 ? R.string.sex_secrecy : R.string.sex_female)) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(RESULT_OK == resultCode && data != null){
            String content = data.getStringExtra("itemName") ;

            if(!TextUtils.isEmpty(content)){
                if(REQUEST_CODE_NAME == requestCode){
                    mUserBean.setUserNiceName(content);
                    mName.setText(content);
                } else if(REQUEST_CODE_LOCATION == requestCode){
                    mUserBean.setLocation(content);
                    mLocationTv.setText(content);
                } else if(REQUEST_CODE_CARD == requestCode){
                    mUserBean.setSignature(content);
                    mSign.setText(content);
                }
            }
        }
    }
}
