package com.fengtuan.videoanchor.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.activity.LiveActivity;
import com.fengtuan.videoanchor.activity.LiveAnchorActivity;
import com.fengtuan.videoanchor.http.HttpCallback;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.util.WordUtil;

/**
 * Created by cxf on 2018/10/9.
 * 主播直播间逻辑
 */

public class LiveAnchorViewHolder extends AbsLiveViewHolder {

    //聊天
    private View mChatParentLay ;
    private InputMethodManager imm;
    private EditText mInput;
    private CheckBox mCheckBox;
    private MyRadioButton mMyRadioButton;
    private String mHint1;
    private String mHint2;

    private ImageView mBtnVoiceIv;//音频
    private ImageView mBtnCameraIv;//视频

    private ImageView mBtnFunction;
    private View mBtnGameClose;//关闭游戏的按钮
    private View mBtnPk;//主播连麦pk按钮
    private Drawable mDrawable0;
    private Drawable mDrawable1;
    private Drawable mDrawableLinkMic0;//允许连麦
    private Drawable mDrawableLinkMic1;//禁止连麦
    private ImageView mLinkMicIcon;//是否允许连麦的标记
    private TextView mLinkMicTip;//是否允许连麦的提示

    private HttpCallback mChangeLinkMicCallback;
    private boolean mLinkMicEnable;

    private boolean mIsMute = false ;//静音

    public LiveAnchorViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_anchor;
    }

    @Override
    public void init() {
        super.init();
        mDrawable0 = ContextCompat.getDrawable(mContext, R.mipmap.icon_live_func_0);
        mDrawable1 = ContextCompat.getDrawable(mContext, R.mipmap.icon_live_func_1);
        mBtnFunction = (ImageView) findViewById(R.id.btn_function);
        mBtnFunction.setImageDrawable(mDrawable0);
        mBtnFunction.setOnClickListener(this);
        mBtnGameClose = findViewById(R.id.btn_close_game);
        mBtnGameClose.setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        mBtnPk = findViewById(R.id.btn_pk);
        mBtnPk.setOnClickListener(this);
        mDrawableLinkMic0 = ContextCompat.getDrawable(mContext, R.mipmap.icon_live_link_mic);
        mDrawableLinkMic1 = ContextCompat.getDrawable(mContext, R.mipmap.icon_live_link_mic_1);
        mLinkMicIcon = (ImageView) findViewById(R.id.link_mic_icon);
        mLinkMicTip = (TextView) findViewById(R.id.link_mic_tip);

        View chatIv = findViewById(R.id.btn_chat_iv);
        chatIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LiveActivity)mContext).openChatWindow() ;
            }
        });
        mBtnVoiceIv = (ImageView) findViewById(R.id.btn_voice_change_iv);
        mBtnCameraIv = (ImageView) findViewById(R.id.btn_camera_change_iv);
        View beautyIv = findViewById(R.id.btn_beauty_change_iv);
        View giftIv = findViewById(R.id.btn_gift_change_iv);
        giftIv.setOnClickListener(this);
        beautyIv.setOnClickListener(this);
        mBtnCameraIv.setOnClickListener(this);
        mBtnVoiceIv.setOnClickListener(this);

        //连麦
        View linkMicView = findViewById(R.id.btn_link_mic) ;
        linkMicView.setVisibility(View.GONE) ;
        linkMicView.setOnClickListener(this);

        initChat() ;
    }

    /**
     * 初始化聊天相关信息
     */
    private void initChat(){
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mChatParentLay = findViewById(R.id.view_live_chat_input_lay);
        mInput = (EditText) findViewById(R.id.input);

        mChatParentLay.setOnClickListener(this);

        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mMyRadioButton.doChecked(false);
                } else {
                    mMyRadioButton.doChecked(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCheckBox = (CheckBox) findViewById(R.id.danmu);
        mMyRadioButton = (MyRadioButton) findViewById(R.id.btn_send);
        mMyRadioButton.setOnClickListener(this);

        mHint2 = WordUtil.getString(R.string.live_say_something);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked) {
                    mInput.setHint(mHint1);
                } else {
                    mInput.setHint(mHint2);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_close:
                close();
                break;
            case R.id.btn_function:
                showFunctionDialog();
                break;
            case R.id.btn_close_game:
                closeGame();
                break;
            case R.id.btn_pk:
                applyLinkMicPk();
                break;
            case R.id.btn_link_mic:
                changeLinkMicEnable();
                break;
            case R.id.btn_voice_change_iv:
                ((LiveAnchorActivity)mContext).changeVoice() ;
                break;
            case R.id.btn_camera_change_iv://切换摄像头
                ((LiveAnchorActivity)mContext).toggleCamera() ;
                break;
            case R.id.btn_beauty_change_iv://美颜
                ((LiveAnchorActivity)mContext).beauty() ;
                break;
            case R.id.btn_gift_change_iv://礼物记录
                ((LiveAnchorActivity)mContext).openGiftRecord() ;
                break;
            case R.id.view_live_chat_input_lay:

//                hiddenChatInputLay() ;

                break;
        }
    }

    public void showChatInputLay(String danmuPrice){
        mHint1 = WordUtil.getString(R.string.live_open_alba) + danmuPrice + "/" + WordUtil.getString(R.string.live_tiao);
        mInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                //软键盘弹出
                imm.showSoftInput(mInput, InputMethodManager.SHOW_FORCED);
                mInput.requestFocus();
            }
        }, 200);

        mChatParentLay.setVisibility(View.VISIBLE) ;
    }

    public boolean hiddenChatInputLay(){
        if(mChatParentLay.getVisibility() != View.GONE){
            mChatParentLay.setVisibility(View.GONE);
            mInput.setText("") ;

            if(imm!=null){
                imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
            }

            return true ;
        }

        return false ;
    }

    private void sendMessage() {
        String content = mInput.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            if (mCheckBox.isChecked()) {
                ((LiveActivity) mContext).sendDanmuMessage(content);
            } else {
                ((LiveActivity) mContext).sendChatMessage(content);
            }
            mInput.setText("");
            if(imm!=null){
                imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
            }
        }
    }

    /**
     * 静音切换
     */
    public boolean changeAudioMute(){
        mIsMute = !mIsMute ;
        mBtnVoiceIv.setImageDrawable(mContext.getResources().getDrawable(mIsMute ? R.mipmap.stop_record : R.mipmap.stop_record_c)) ;
        return mIsMute ;
    }
    /**
     * 设置游戏按钮是否可见
     */
    public void setGameBtnVisible(boolean show) {
        if (mBtnGameClose != null) {
            if (show) {
                if (mBtnGameClose.getVisibility() != View.VISIBLE) {
                    mBtnGameClose.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnGameClose.getVisibility() == View.VISIBLE) {
                    mBtnGameClose.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 关闭游戏
     */
    private void closeGame() {
        ((LiveAnchorActivity) mContext).closeGame();
    }

    /**
     * 关闭直播
     */
    private void close() {
        ((LiveAnchorActivity) mContext).closeLive();
    }

    /**
     * 显示功能弹窗
     */
    private void showFunctionDialog() {
        if (mBtnFunction != null) {
            mBtnFunction.setImageDrawable(mDrawable1);
        }
        ((LiveAnchorActivity) mContext).showFunctionDialog();
    }

    /**
     * 设置功能按钮变暗
     */
    public void setBtnFunctionDark() {
        if (mBtnFunction != null) {
            mBtnFunction.setImageDrawable(mDrawable0);
        }
    }

    /**
     * 设置连麦pk按钮是否可见
     */
    public void setPkBtnVisible(boolean visible) {
        if (mBtnPk != null) {
            if (visible) {
                if (mBtnPk.getVisibility() != View.VISIBLE) {
                    mBtnPk.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnPk.getVisibility() == View.VISIBLE) {
                    mBtnPk.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 发起主播连麦pk
     */
    private void applyLinkMicPk() {
        ((LiveAnchorActivity) mContext).applyLinkMicPk();
    }

    public void setLinkMicEnable(boolean linkMicEnable) {
        mLinkMicEnable = linkMicEnable;
        showLinkMicEnable();
    }

    private void showLinkMicEnable() {
        if (mLinkMicEnable) {
            if (mLinkMicIcon != null) {
                mLinkMicIcon.setImageDrawable(mDrawableLinkMic1);
            }
            if (mLinkMicTip != null) {
                mLinkMicTip.setText(R.string.live_link_mic_5);
            }
        } else {
            if (mLinkMicIcon != null) {
                mLinkMicIcon.setImageDrawable(mDrawableLinkMic0);
            }
            if (mLinkMicTip != null) {
                mLinkMicTip.setText(R.string.live_link_mic_4);
            }
        }
    }


    private void changeLinkMicEnable() {
        if (mChangeLinkMicCallback == null) {
            mChangeLinkMicCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        showLinkMicEnable();
                    }
                }
            };
        }
        mLinkMicEnable = !mLinkMicEnable;
        HttpUtil.setLinkMicEnable(mLinkMicEnable, mChangeLinkMicCallback);
    }

}
