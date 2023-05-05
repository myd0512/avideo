package com.fengtuan.videoanchor.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.speech.utils.LogUtil;
import com.ksy.statlibrary.util.NetworkUtil;
import com.opensource.svgaplayer.SVGAImageView;
import com.fengtuan.videoanchor.AppConfig;
import com.fengtuan.videoanchor.Constants;
import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.bean.LiveBean;
import com.fengtuan.videoanchor.bean.LiveGuardInfo;
import com.fengtuan.videoanchor.bean.UserBean;
import com.fengtuan.videoanchor.beauty.BeautyViewHolder;
import com.fengtuan.videoanchor.beauty.DefaultBeautyViewHolder;
import com.fengtuan.videoanchor.beauty.LiveBeautyViewHolder;
import com.fengtuan.videoanchor.event.GameWindowEvent;
import com.fengtuan.videoanchor.event.LoginInvalidEvent;
import com.fengtuan.videoanchor.fragment.LiveGiftRecordDialogFragment;
import com.fengtuan.videoanchor.http.HttpCallback;
import com.fengtuan.videoanchor.http.HttpConsts;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.interfa.LiveFunctionClickListener;
import com.fengtuan.videoanchor.interfa.LivePushListener;
import com.fengtuan.videoanchor.music.LiveMusicDialogFragment;
import com.fengtuan.videoanchor.presenter.LiveLinkMicAnchorPresenter;
import com.fengtuan.videoanchor.presenter.LiveLinkMicPkPresenter;
import com.fengtuan.videoanchor.presenter.LiveLinkMicPresenter;
import com.fengtuan.videoanchor.socket.HostUidResultBean;
import com.fengtuan.videoanchor.socket.JWebSocketClient;
import com.fengtuan.videoanchor.socket.SocketClient;
import com.fengtuan.videoanchor.socket.SocketGameReceiveBean;
import com.fengtuan.videoanchor.socket.UserUidResultBean;
import com.fengtuan.videoanchor.util.DialogUitl;
import com.fengtuan.videoanchor.util.GsonUtil;
import com.fengtuan.videoanchor.util.L;
import com.fengtuan.videoanchor.util.StringUtil;
import com.fengtuan.videoanchor.util.ToastUtil;
import com.fengtuan.videoanchor.util.WordUtil;
import com.fengtuan.videoanchor.view.AbsLivePushViewHolder;
import com.fengtuan.videoanchor.view.LiveAnchorViewHolder;
import com.fengtuan.videoanchor.view.LiveEndViewHolder;
import com.fengtuan.videoanchor.view.LiveKsyPushViewHolder;
import com.fengtuan.videoanchor.view.LiveMusicViewHolder;
import com.fengtuan.videoanchor.view.LiveRoomViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONTokener;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/7.
 * 主播直播间
 */
public class LiveAnchorActivity extends LiveActivity implements LiveFunctionClickListener {

    private ViewGroup mRoot;
    private ViewGroup mContainerWrap;
    private AbsLivePushViewHolder mLivePushViewHolder;
    private BeautyViewHolder mLiveBeautyViewHolder;
    private LiveAnchorViewHolder mLiveAnchorViewHolder;
    private LiveMusicViewHolder mLiveMusicViewHolder;
    private boolean mStartPreview;//是否开始预览
    private boolean mStartLive;//是否开始了直播
    private List<Integer> mGameList;//游戏开关

    //开奖结果
    private JWebSocketClient mGameResultClient ;


    public static void launch(Context context,String gameId,String gameName,String data, int liveType, int liveTypeVal){
        Intent toIt = new Intent(context,LiveAnchorActivity.class) ;
        toIt.putExtra("gameId",gameId) ;
        toIt.putExtra("gameName",gameName) ;
        toIt.putExtra("roomInfo",data) ;
        toIt.putExtra("liveType",liveType) ;
        toIt.putExtra("typeValue",liveTypeVal) ;
        context.startActivity(toIt) ;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_anchor;
    }

    @Override
    protected void main() {
        super.main();
        mRoot = findViewById(R.id.root);
        mSocketUserType = Constants.SOCKET_USER_TYPE_ANCHOR;
        UserBean u = AppConfig.getInstance().getUserBean();
        mLiveUid = u.getId();
        mLiveBean = new LiveBean();
        mLiveBean.setUid(mLiveUid);
        mLiveBean.setUserNiceName(u.getUserNiceName());
        mLiveBean.setAvatar(u.getAvatar());
        mLiveBean.setAvatarThumb(u.getAvatarThumb());
        mLiveBean.setLevelAnchor(u.getLevelAnchor());
        mLiveBean.setGoodNum(u.getGoodName());
        mLiveBean.setCity(u.getCity());
        //添加推流预览控件
        mLivePushViewHolder = new LiveKsyPushViewHolder(mContext, (ViewGroup) findViewById(R.id.preview_container));
        mLivePushViewHolder.setLivePushListener(new LivePushListener() {
            @Override
            public void onPreviewStart() {
                //开始预览回调
                mStartPreview = true;
            }

            @Override
            public void onPushStart() {
                //开始推流回调
                HttpUtil.changeLive(mStream);
            }

            @Override
            public void onPushFailed() {
                //推流失败回调
                ToastUtil.show(R.string.live_push_failed);
            }
        });
        mLivePushViewHolder.addToParent();
        addLifeCycleListener(mLivePushViewHolder.getLifeCycleListener());
        mContainerWrap = findViewById(R.id.container_wrap);
        mContainer = findViewById(R.id.container);

        mLiveLinkMicPresenter = new LiveLinkMicPresenter(mContext, mLivePushViewHolder, true, mContainer);
        mLiveLinkMicPresenter.setLiveUid(mLiveUid);
        mLiveLinkMicAnchorPresenter = new LiveLinkMicAnchorPresenter(mContext, mLivePushViewHolder, true, mContainer);
        mLiveLinkMicPkPresenter = new LiveLinkMicPkPresenter(mContext, mLivePushViewHolder, true, mContainer);

        Intent reIntent = getIntent() ;
        if(reIntent != null){
            mGameId = reIntent.getStringExtra("gameId") ;
            mGameName = reIntent.getStringExtra("gameName") ;

            startLiveSuccess(reIntent.getStringExtra("roomInfo")
                    ,reIntent.getIntExtra("liveType",0)
                    ,reIntent.getIntExtra("typeValue",0)) ;
        }
    }

    public boolean isStartPreview() {
        return mStartPreview;
    }

    /**
     * 主播直播间功能按钮点击事件
     *
     * @param functionID
     */
    @Override
    public void onClick(int functionID) {
        switch (functionID) {
            case Constants.LIVE_FUNC_BEAUTY://美颜
                beauty();
                break;
            case Constants.LIVE_FUNC_CAMERA://切换镜头
                toggleCamera();
                break;
            case Constants.LIVE_FUNC_FLASH://切换闪光灯
                toggleFlash();
                break;
            case Constants.LIVE_FUNC_MUSIC://伴奏
                openMusicWindow();
                break;
            case Constants.LIVE_FUNC_SHARE://分享
                openShareWindow();
                break;
            case Constants.LIVE_FUNC_GAME://游戏
                openGameWindow();
                break;
            case Constants.LIVE_FUNC_RED_PACK://红包
//                openRedPackSendWindow();
                break;
            case Constants.LIVE_FUNC_LINK_MIC://连麦
                openLinkMicAnchorWindow();
                break;
        }
    }

    //打开相机前执行
    public void beforeCamera() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.setOpenCamera(true);
        }
    }

    /**
     * 切换镜头
     */
    public void toggleCamera() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.toggleCamera();
        }
    }

    /**
     * 静音
     */
    public void changeVoice(){
        if (mLivePushViewHolder != null && mLiveAnchorViewHolder != null) {
            mLivePushViewHolder.changeAudioMute(mLiveAnchorViewHolder.changeAudioMute());
        }
    }

    /**
     * 切换闪光灯
     */
    public void toggleFlash() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.toggleFlash();
        }
    }

    /**
     * 设置美颜
     */
    public void beauty() {
        if (mLiveBeautyViewHolder == null) {
            if (AppConfig.TI_BEAUTY_ENABLE) {
                mLiveBeautyViewHolder = new LiveBeautyViewHolder(mContext, mContainer);
            } else {
                mLiveBeautyViewHolder = new DefaultBeautyViewHolder(mContext, mContainer);
            }
            mLiveBeautyViewHolder.setVisibleListener(new BeautyViewHolder.VisibleListener() {
                @Override
                public void onVisibleChanged(boolean visible) {



                }
            });
            if (mLivePushViewHolder != null) {
                mLiveBeautyViewHolder.setEffectListener(mLivePushViewHolder.getEffectListener());
            }
        }
        mLiveBeautyViewHolder.show();
    }

    /**
     * 打开音乐窗口
     */
    private void openMusicWindow() {
        LiveMusicDialogFragment fragment = new LiveMusicDialogFragment();
        fragment.setActionListener(new LiveMusicDialogFragment.ActionListener() {
            @Override
            public void onChoose(String musicId) {
                if (mLivePushViewHolder != null) {
                    if (mLiveMusicViewHolder == null) {
                        mLiveMusicViewHolder = new LiveMusicViewHolder(mContext, mContainer, mLivePushViewHolder);
                        addLifeCycleListener(mLiveMusicViewHolder.getLifeCycleListener());
                        mLiveMusicViewHolder.addToParent();
                        mLiveMusicViewHolder.setCloseCallback(new Runnable() {
                            @Override
                            public void run() {
                                if (mLiveMusicViewHolder != null) {
                                    mLiveMusicViewHolder.release();
                                }
                                mLiveMusicViewHolder = null;
                            }
                        });
                    }
                    mLiveMusicViewHolder.play(musicId);
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "LiveMusicDialogFragment");
    }

    /**
     * 打开功能弹窗
     */
    public void showFunctionDialog() {
//        LiveFunctionDialogFragment fragment = new LiveFunctionDialogFragment();
//        Bundle bundle = new Bundle();
//        boolean hasGame = false;
//        if (AppConfig.GAME_ENABLE && mGameList != null) {
//            hasGame = mGameList.size() > 0;
//        }
//        bundle.putBoolean(Constants.HAS_GAME, hasGame);
//        fragment.setArguments(bundle);
//        fragment.setFunctionClickListener(this);
//        fragment.show(getSupportFragmentManager(), "LiveFunctionDialogFragment");
    }

    /**
     * 打开主播连麦窗口
     */
    private void openLinkMicAnchorWindow() {
//        if (mLiveLinkMicAnchorPresenter != null && !mLiveLinkMicAnchorPresenter.canOpenLinkMicAnchor()) {
//            return;
//        }
//        LiveLinkMicListDialogFragment fragment = new LiveLinkMicListDialogFragment();
//        fragment.show(getSupportFragmentManager(), "LiveLinkMicListDialogFragment");
    }

    /**
     * 打开选择游戏窗口
     */
    private void openGameWindow() {

    }

    /**
     * 关闭游戏
     */
    public void closeGame() {

    }

    /**
     * 打开礼物记录窗口
     */
    public void openGiftRecord(){
        LiveGiftRecordDialogFragment fragment = new LiveGiftRecordDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_GAME_ID, mGameId);
        bundle.putString(Constants.LIVE_GAME_NAME, mGameName);
        bundle.putString(Constants.LIVE_GAME_PERIOD, mGamePeriod);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGiftRecordDialogFragment");
    }

    /**
     * 开播成功
     *
     * @param data createRoom返回的数据
     */
    private void startLiveSuccess(String data, int liveType, int liveTypeVal) {
        mLiveType = liveType;
        mLiveTypeVal = liveTypeVal;
        //处理createRoom返回的数据
        JSONObject obj = JSON.parseObject(data);
        mStream = obj.getString("stream");
        mDanmuPrice = obj.getString("barrage_fee");
        mShutTime = obj.getString("shut_time");
        String playUrl = obj.getString("pull");
        mLiveBean.setPull(playUrl);
        if (mLiveRoomViewHolder == null) {
            mLiveRoomViewHolder = new LiveRoomViewHolder(this, mContainer
                    , (GifImageView) findViewById(R.id.gift_gif), (SVGAImageView) findViewById(R.id.gift_svga));
            mLiveRoomViewHolder.addToParent();
            addLifeCycleListener(mLiveRoomViewHolder.getLifeCycleListener());

            mLiveRoomViewHolder.setOnCloseListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeLive() ;
                }
            });
            mLiveRoomViewHolder.setLiveInfo(mLiveUid, mStream, obj.getIntValue("userlist_time") * 1000);
            mLiveRoomViewHolder.setVotes(obj.getString("votestotal"));
            UserBean u = AppConfig.getInstance().getUserBean();
            if (u != null) {
                mLiveRoomViewHolder.setRoomNum(u.getLiangNameTip());
                mLiveRoomViewHolder.setName(u.getUserNiceName());
                mLiveRoomViewHolder.setAvatar(u.getAvatar());
                mLiveRoomViewHolder.setAnchorLevel(u.getLevelAnchor());
            }
        }
        if (mLiveAnchorViewHolder == null) {
            mLiveAnchorViewHolder = new LiveAnchorViewHolder(mContext, mContainer);
            mLiveAnchorViewHolder.addToParent();
            mLiveAnchorViewHolder.setUnReadCount(((LiveActivity) mContext).getImUnReadCount());

            mLiveAnchorViewHolder.setLinkMicEnable(true) ;
        }
        mLiveBottomViewHolder = mLiveAnchorViewHolder;

        //连接socket
        if (mSocketClient == null) {
            mSocketClient = new SocketClient(obj.getString("chatserver"), this);
            if (mLiveLinkMicPresenter != null) {
                mLiveLinkMicPresenter.setSocketClient(mSocketClient);
            }
            if (mLiveLinkMicAnchorPresenter != null) {
                mLiveLinkMicAnchorPresenter.setSocketClient(mSocketClient);
                mLiveLinkMicAnchorPresenter.setPlayUrl(playUrl);
            }
            if (mLiveLinkMicPkPresenter != null) {
                mLiveLinkMicPkPresenter.setSocketClient(mSocketClient);
                mLiveLinkMicPkPresenter.setLiveUid(mLiveUid);
            }
        }
        mSocketClient.connect(mLiveUid, mStream);

        //开始推流
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.startPush(obj.getString("push"));
        }
        //开始显示直播时长
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.startAnchorLiveTime();
        }
        mStartLive = true;
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.startRefreshUserList();
        }

        //守护相关
        mLiveGuardInfo = new LiveGuardInfo();
        int guardNum = obj.getIntValue("guard_nums");
        mLiveGuardInfo.setGuardNum(guardNum);
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setGuardNum(guardNum);
        }
        Log.e(LiveAnchorActivity.class.getSimpleName(),obj.toJSONString());
//        //游戏相关
//        if (AppConfig.GAME_ENABLE) {
//            mGameList = JSON.parseArray(obj.getString("game_switch"), Integer.class);
//            GameParam param = new GameParam();
//            param.setContext(mContext);
//            param.setParentView(mContainerWrap);
//            param.setTopView(mContainer);
//            param.setInnerContainer(mLiveRoomViewHolder.getInnerContainer());
//            param.setSocketClient(mSocketClient);
//            param.setLiveUid(mLiveUid);
//            param.setStream(mStream);
//            param.setAnchor(true);
//            param.setCoinName(mCoinName);
//            param.setObj(obj);
//            mGamePresenter = new GamePresenter(param);
//            mGamePresenter.setGameList(mGameList);
//        }

        //开启游戏结果socket
        startGameResultSocket() ;
    }

    /**
     * 关闭直播
     */
    public void closeLive() {
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.live_end_live), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                endLive();
            }
        });
    }

    /**
     * 结束直播
     */
    public void endLive() {
        //断开socket
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }

        if(!NetworkUtil.isNetworkConnected(mContext)){//没网络直接退出
            release();
            mStartLive = false;
            superBackPressed() ;
            return;
        }

        //请求关播的接口
        HttpUtil.stopLive(mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mLiveEndViewHolder == null) {
                        mLiveEndViewHolder = new LiveEndViewHolder(mContext, mRoot);
                        addLifeCycleListener(mLiveEndViewHolder.getLifeCycleListener());
                        mLiveEndViewHolder.addToParent();
                        mLiveEndViewHolder.showData(mLiveBean, mStream);
                    }
                    release();
                    mStartLive = false;
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext, WordUtil.getString(R.string.live_end_ing));
            }
        });
    }

//    public void openChatWindow(){
//        super.openChatWindow() ;
//
//        if(mLiveAnchorViewHolder != null){
//            mLiveAnchorViewHolder.showChatInputLay(mDanmuPrice) ;
//        }
//    }

    @Override
    public void onBackPressed() {
        if (mLiveBeautyViewHolder != null && mLiveBeautyViewHolder.isShowed()) {
            mLiveBeautyViewHolder.hide();
            return;
        }
        if (mStartLive) {
            if (!canBackPressed()) {
                return;
            }
            closeLive();
        } else {
            if (mLivePushViewHolder != null) {
                mLivePushViewHolder.release();
            }
            if (mLiveLinkMicPresenter != null) {
                mLiveLinkMicPresenter.release();
            }
            mLivePushViewHolder = null;
            mLiveLinkMicPresenter = null;
            superBackPressed();
        }
    }

    public void superBackPressed() {
        super.onBackPressed();
    }

    public void release() {
        HttpUtil.cancel(HttpConsts.CHANGE_LIVE);
        HttpUtil.cancel(HttpConsts.STOP_LIVE);
        HttpUtil.cancel(HttpConsts.LIVE_PK_CHECK_LIVE);
        HttpUtil.cancel(HttpConsts.SET_LINK_MIC_ENABLE);

        if (mLiveMusicViewHolder != null) {
            mLiveMusicViewHolder.release();
        }
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.release();
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.release();
        }
        if (mLiveBeautyViewHolder != null) {
            mLiveBeautyViewHolder.release();
        }
//        if (mGamePresenter != null) {
//            mGamePresenter.release();
//        }
        mLiveMusicViewHolder = null;
        mLivePushViewHolder = null;
        mLiveLinkMicPresenter = null;
        mLiveBeautyViewHolder = null;
//        mGamePresenter = null;
        super.release();
    }


    @Override
    protected void onPause() {
        if (mLiveMusicViewHolder != null) {
            mLiveMusicViewHolder.pause();
        }
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.pause();
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.anchorPause();
        }
        super.onPause();
        sendSystemMessage(WordUtil.getString(R.string.live_anchor_leave),0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.resume();
        }
        if (mLiveMusicViewHolder != null) {
            mLiveMusicViewHolder.resume();
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.anchorResume();
        }
        sendSystemMessage(WordUtil.getString(R.string.live_anchor_come_back),0);
    }

    /**
     * 超管关闭直播间
     */
    @Override
    public void onSuperCloseLive() {
        endLive();
        DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.live_illegal));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginInvalidEvent(LoginInvalidEvent e) {
        release();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameWindowEvent(GameWindowEvent e) {
        if (mLiveAnchorViewHolder != null) {
            mLiveAnchorViewHolder.setGameBtnVisible(e.isOpen());
        }
    }

    public void setBtnFunctionDark() {
        if (mLiveAnchorViewHolder != null) {
            mLiveAnchorViewHolder.setBtnFunctionDark();
        }
    }

    /**
     * 主播与主播连麦  主播收到其他主播发过来的连麦申请
     */
    @Override
    public void onLinkMicAnchorApply(UserBean u, String stream) {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorApply(u, stream);
        }
    }

    /**
     * 主播与主播连麦  对方主播拒绝连麦的回调
     */
    @Override
    public void onLinkMicAnchorRefuse() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorRefuse();
        }
    }

    /**
     * 主播与主播连麦  对方主播无响应的回调
     */
    @Override
    public void onLinkMicAnchorNotResponse() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicNotResponse();
        }
    }

    /**
     * 主播与主播连麦  对方主播正在忙的回调
     */
    @Override
    public void onLinkMicAnchorBusy() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorBusy();
        }
    }

    /**
     * 发起主播连麦申请
     *
     * @param pkUid  对方主播的uid
     * @param stream 对方主播的stream
     */
    public void linkMicAnchorApply(final String pkUid, String stream) {
        HttpUtil.livePkCheckLive(pkUid, stream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mLiveLinkMicAnchorPresenter != null) {
                        mLiveLinkMicAnchorPresenter.applyLinkMicAnchor(pkUid, mStream);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 设置连麦pk按钮是否可见
     */
    public void setPkBtnVisible(boolean visible) {
        if (mLiveAnchorViewHolder != null) {
            if (visible) {
                if (mLiveLinkMicAnchorPresenter.isLinkMic()) {
                    mLiveAnchorViewHolder.setPkBtnVisible(true);
                }
            } else {
                mLiveAnchorViewHolder.setPkBtnVisible(false);
            }
        }
    }

    /**
     * 发起主播连麦pk
     */
    public void applyLinkMicPk() {
        String pkUid = null;
        if (mLiveLinkMicAnchorPresenter != null) {
            pkUid = mLiveLinkMicAnchorPresenter.getPkUid();
        }
        if (!TextUtils.isEmpty(pkUid) && mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.applyLinkMicPk(pkUid, mStream);
        }
    }

    /**
     * 主播与主播PK  主播收到对方主播发过来的PK申请的回调
     *
     * @param u      对方主播的信息
     * @param stream 对方主播的stream
     */
    @Override
    public void onLinkMicPkApply(UserBean u, String stream) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkApply(u, stream);
        }
    }

    /**
     * 主播与主播PK  对方主播拒绝pk的回调
     */
    @Override
    public void onLinkMicPkRefuse() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkRefuse();
        }
    }

    /**
     * 主播与主播PK   对方主播正在忙的回调
     */
    @Override
    public void onLinkMicPkBusy() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkBusy();
        }
    }

    /**
     * 主播与主播PK   对方主播无响应的回调
     */
    @Override
    public void onLinkMicPkNotResponse() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkNotResponse();
        }
    }



    /**
     * 开启查询结果socked
     */
    private void startGameResultSocket(){
        final String url = HttpConsts.getGameResultUrlByType(mGameId) ;
        Log.e("TestGameSocket","startGameResultSocket-url=" + url) ;

        if(!TextUtils.isEmpty(url)){
            //启动socked
            mGameResultClient = new JWebSocketClient(mContext, URI.create(url),JWebSocketClient.GAME_SOCKET_TYPE_RESULT){
                @Override
                public void onMessage(String message) {
                    super.onMessage(message) ;
                    Log.e("TestGameSocket","onMessage-message=" + message) ;

                    if(mStartLive){
                        double currentRaward = 0D ;

                        if(!TextUtils.isEmpty(message)){
                            try {
                                JSONObject object = JSONObject.parseObject(message) ;
                                if(object != null){
                                    JSONArray userList = object.getJSONArray("related_user_uid_result") ;
                                    if(userList != null && userList.size() > 0){
                                        String firstItemString = JSONObject.toJSONString(userList.get(0)) ;
                                        LogUtil.e("JWebSocketClient","获取中奖结果,userList---firstItemString=" + firstItemString) ;

                                        String userUidListString = null ;

                                        Object nextObj = new JSONTokener(firstItemString).nextValue() ;
                                        if(nextObj instanceof org.json.JSONObject){

                                            userUidListString = firstItemString ;

                                            LogUtil.e("JWebSocketClient","获取中奖结果,result instanceof JSONObject") ;

                                        }else if(nextObj instanceof org.json.JSONArray){//没有中奖结果的时候，返回的是这种，暂不处理

                                            LogUtil.e("JWebSocketClient","获取中奖结果,result instanceof JSONArray") ;

                                        }

                                        if(userUidListString != null){
                                            SocketGameReceiveBean crb = JSON.parseObject(message, SocketGameReceiveBean.class);
                                            UserUidResultBean uurb = JSON.parseObject(userUidListString,UserUidResultBean.class) ;

                                            if (uurb != null && crb != null) {
                                                //当主动连接socket的时候，会接收到一个结果，里面没有old_xxxx_game_id字段
                                                // ，而推送的结果是有的，所以可以根据这个来区分是主动获取的还是系统推的
                                                String gameName;
                                                if (crb.getOld_yfks_game_id() != null) {
                                                    gameName = HttpConsts.GAME_NAME_YFKS;
                                                } else if (crb.getOld_yf115_game_id() != null) {
                                                    gameName = HttpConsts.GAME_NAME_YF115;
                                                } else if (crb.getOld_yfsc_game_id() != null) {
                                                    gameName = HttpConsts.GAME_NAME_YFSC;
                                                } else if (crb.getOld_yfssc_game_id() != null) {
                                                    gameName = HttpConsts.GAME_NAME_YFSSC;
                                                } else if (crb.getOld_yflhc_game_id() != null) {
                                                    gameName = HttpConsts.GAME_NAME_YFLHC;
                                                } else if (crb.getOld_yfklsf_game_id() != null) {
                                                    gameName = HttpConsts.GAME_NAME_YFKLSF;
                                                } else if (crb.getOld_yfxync_game_id() != null) {
                                                    gameName = HttpConsts.GAME_NAME_YFXYNC;
                                                } else {
                                                    gameName = "";
                                                }

                                                List<String> strList = uurb.getList_nicename();
                                                final List<String> messageList = new ArrayList<>() ;
                                                for (String str : strList) {
                                                    messageList.add("恭喜" + str + "在" + gameName + "中奖了");
                                                }

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mLiveRoomViewHolder.setGameWinnerInfo(messageList) ;
                                                    }
                                                });

                                                LogUtil.e("JWebSocketClient","获取中奖结果，gameName=" + gameName + ",strList=" + strList) ;

                                                //拼接好相关中奖信息，发送给公屏
                                                for (String str : messageList) {
                                                    ((LiveActivity) mContext).sendSystemMessage(str, 1);
                                                }
                                            }
                                        }
//奖金
                                        //"related_host_uid_result": [{
                                        //		"list_id": [
                                        //			[34127, 34133, 34134],
                                        //			[34127, 34133, 34134]
                                        //		],
                                        //		"list_nicename": [
                                        //			["\u53ef\u4e50\u4ed4\u4ed4", "\u624b\u673a\u7528\u62375602", "\u624b\u673a\u7528\u62375344"],
                                        //			["\u53ef\u4e50\u4ed4\u4ed4", "\u624b\u673a\u7528\u62375602", "\u624b\u673a\u7528\u62375344"]
                                        //		],
                                        //		"host_jiangjin": [0.001379, 0.001379]
                                        //	}]
                                        //先判断list_id里面有没有当前主播，如果有，就从host_jiangjin的对应位置获取奖金，并累加上。
                                        JSONArray hostList = object.getJSONArray("related_host_uid_result") ;
                                        if(hostList != null && hostList.size() > 0){
                                            String firstHostString = JSONObject.toJSONString(hostList.get(0)) ;
                                            Object nextHostObj = new JSONTokener(firstHostString).nextValue() ;
                                            if(nextHostObj instanceof org.json.JSONObject){
                                                HostUidResultBean hostResultBean = JSON.parseObject(firstHostString,HostUidResultBean.class) ;

                                                if(hostResultBean != null
                                                        && hostResultBean.getList_id() != null
                                                        && hostResultBean.getList_id().size() > 0){
                                                    List<Double> hostRewardList = hostResultBean.getHost_jiangjin() ;
                                                    List<List<Integer>> hostUserIdList = hostResultBean.getList_id() ;
                                                    if(hostRewardList != null && hostRewardList.size() == hostUserIdList.size()){
                                                        int anchorId = StringUtil.convertInt(mLiveUid) ;
                                                        for(int z = 0 ; z < hostUserIdList.size() ; z ++){
                                                            List<Integer> hostUserIds = hostUserIdList.get(z) ;
                                                            if(hostUserIds != null && hostUserIds.contains(anchorId)){
                                                                currentRaward += hostRewardList.get(z) ;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        final double realReward = currentRaward ;
                        final SocketGameReceiveBean resultBean = GsonUtil.fromJson(message, SocketGameReceiveBean.class);
                        if(resultBean != null){
                            String dianshu = resultBean.getDianshu_result();
                            final String[] strArr = dianshu.split(",");
                            final String period = resultBean.getOldGamePeroid(mGameId) ;
                            mGamePeriod = resultBean.getNewGamePeroid(mGameId) ;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int time = 0 ;
                                    try {
                                        time = Integer.valueOf(resultBean.getTime()) ;
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }

                                    if(mLiveRoomViewHolder != null){
                                        mLiveRoomViewHolder.setGameInfo(realReward,mGameId,time,strArr,period,mGamePeriod) ;
                                    }
                                }
                            });
                        }
                    }
                }
            } ;
            Log.e("TestGameSocket","connectGameSocket") ;
            mGameResultClient.connect() ;
        }
    }

    @Override
    protected void onDestroy() {
        L.e("LiveAnchorActivity-------onDestroy------->");
        if(mGameResultClient != null){
            if(mGameResultClient.isOpen()){
                mGameResultClient.close();
            }
            mGameResultClient = null ;
        }

        if(mLiveRoomViewHolder != null){
            mLiveRoomViewHolder.clearData() ;
        }

        super.onDestroy();
    }

}
