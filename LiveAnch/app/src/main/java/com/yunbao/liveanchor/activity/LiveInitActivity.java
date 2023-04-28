package com.yunbao.liveanchor.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.speech.utils.LogUtil;
import com.yunbao.liveanchor.AdapterChooseGame;
import com.yunbao.liveanchor.Constants;
import com.yunbao.liveanchor.R;
import com.yunbao.liveanchor.bean.GameTypeBean;
import com.yunbao.liveanchor.bean.LiveRoomTypeBean;
import com.yunbao.liveanchor.fragment.LiveRoomTypeDialogFragment;
import com.yunbao.liveanchor.fragment.LiveTimeDialogFragment;
import com.yunbao.liveanchor.glide.ImgLoader;
import com.yunbao.liveanchor.http.HttpCallback;
import com.yunbao.liveanchor.http.HttpConsts;
import com.yunbao.liveanchor.http.HttpUtil;
import com.yunbao.liveanchor.interfa.ActivityResultCallback;
import com.yunbao.liveanchor.interfa.CommonCallback;
import com.yunbao.liveanchor.interfa.ImageResultCallback;
import com.yunbao.liveanchor.interfa.LifeCycleAdapter;
import com.yunbao.liveanchor.interfa.LifeCycleListener;
import com.yunbao.liveanchor.interfa.OnRvClickListener;
import com.yunbao.liveanchor.util.ClickUtil;
import com.yunbao.liveanchor.util.DialogUitl;
import com.yunbao.liveanchor.util.L;
import com.yunbao.liveanchor.util.MyStringUtils;
import com.yunbao.liveanchor.util.ProcessImageUtil;
import com.yunbao.liveanchor.util.ProcessResultUtil;
import com.yunbao.liveanchor.util.ScreenDimenUtil;
import com.yunbao.liveanchor.util.SpUtil;
import com.yunbao.liveanchor.util.StringUtil;
import com.yunbao.liveanchor.util.ToastUtil;
import com.yunbao.liveanchor.util.WordUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 准备直播，填写信息
 */
public class LiveInitActivity extends AbsActivity implements View.OnClickListener {
    private ImageView mGameIv ;
    private TextView mGameNameTv ;
    private ImageView mThumbIv ;
    private TextView mCoverText;
    private EditText mTitleEv ;
    private TextView mRoomTypeTv ;
    private TextView mCategoryTv ;


    //游戏
    private List<GameTypeBean> mGameTypeList = new ArrayList<>() ;
    private Dialog mChooseGameDialog ;

    private int mCheckedGamePosition = -1;


    //封面
    private ProcessImageUtil mImageUtil;
    private File mAvatarFile;

    //房间类型
    private int mLiveType = 0 ;//房间类型
    private int mLiveTypeVal;//房间密码，门票收费金额
    private int mLiveTimeCoin = 0 ;//计时收费金额
    private ActivityResultCallback mActivityResultCallback;
    private CommonCallback<LiveRoomTypeBean> mLiveTypeCallback;

    //直播频道
    private int mLiveClassID;//直播频道id
    private String mLiveClassName ;//频道name


    protected LifeCycleListener mLifeCycleListener;

    private ProcessResultUtil mProcessResultUtil;

    public static void launch(Context context){
        Intent toIt = new Intent(context,LiveInitActivity.class) ;
        context.startActivity(toIt) ;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_init ;
    }

    @Override
    protected void main() {
        super.main();

        mProcessResultUtil = new ProcessResultUtil(this);

        mGameIv = findViewById(R.id.act_live_init_game_iv) ;
        mGameNameTv = findViewById(R.id.act_live_init_game_tv) ;
        mThumbIv = findViewById(R.id.act_live_init_thumb_iv) ;
        mCoverText = findViewById(R.id.act_live_init_thumb_tv) ;
        mTitleEv = findViewById(R.id.act_live_init_title_tv) ;
        mRoomTypeTv = findViewById(R.id.act_live_init_room_type_tv) ;
        mCategoryTv = findViewById(R.id.act_live_init_category_tv) ;
        View startIv = findViewById(R.id.act_live_init_start_tv) ;

        mGameIv.setOnClickListener(this);
        mThumbIv.setOnClickListener(this);
        mRoomTypeTv.setOnClickListener(this);
        mCategoryTv.setOnClickListener(this);
        startIv.setOnClickListener(this);

        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(file, mThumbIv);
                    if (mAvatarFile == null) {
                        mCoverText.setText(WordUtil.getString(R.string.live_cover_2));
                        mCoverText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_live_cover));
                    }
                    mAvatarFile = file;
                }
            }

            @Override
            public void onFailure() {
            }
        });

        mActivityResultCallback = new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                mLiveClassID = intent.getIntExtra(Constants.CLASS_ID, 0);
                mLiveClassName = intent.getStringExtra(Constants.CLASS_NAME) ;
                mCategoryTv.setText(mLiveClassName);
            }
        };
        mLiveTypeCallback = new CommonCallback<LiveRoomTypeBean>() {
            @Override
            public void callback(LiveRoomTypeBean bean) {
                changeRoomType(bean) ;
            }
        };
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onDestroy() {
                HttpUtil.cancel(HttpConsts.CREATE_ROOM);
            }
        };

        addLifeCycleListener(mLifeCycleListener) ;

        initDefaultView() ;

        getGameList() ;
    }

    /**
     * 根据上次保存的内容，填充到相应控件中
     */
    private void initDefaultView(){
        String filePath = SpUtil.getInstance().getStringValue(SpUtil.LIVE_THUMB) ;
        if(!TextUtils.isEmpty(filePath)){
            File file = new File(filePath) ;
            if(file.exists()){
                mAvatarFile = file ;
                ImgLoader.display(mAvatarFile,mThumbIv) ;
            }
        }

        String title = SpUtil.getInstance().getStringValue(SpUtil.LIVE_TITLE) ;
        mTitleEv.setText(MyStringUtils.convertNull(title));

        String categoryId = SpUtil.getInstance().getStringValue(SpUtil.LIVE_CATEGORY_ID) ;
        String categoryName = SpUtil.getInstance().getStringValue(SpUtil.LIVE_CATEGORY_NAME) ;
        if(!TextUtils.isEmpty(categoryId)&& !TextUtils.isEmpty(categoryName)){
            mLiveClassID = Integer.valueOf(categoryId) ;
            mLiveClassName = categoryName ;

            mCategoryTv.setText(categoryName) ;
        }
    }

    @Override
    public void onClick(View view) {
        if(!ClickUtil.canClick()){
            return;
        }

        int vId = view.getId() ;

        if(R.id.act_live_init_game_iv == vId){//选择游戏类型

            showChooseGameDialog() ;

        } else if(R.id.act_live_init_thumb_iv == vId){//选择封面

            setAvatar() ;

        } else if(R.id.act_live_init_room_type_tv == vId){//选择房间类型

            chooseLiveRoomType() ;

        } else if(R.id.act_live_init_category_tv == vId){//选择直播分类

            chooseLiveClass() ;

        } else if(R.id.act_live_init_start_tv == vId){//开始直播

            startLive() ;

        }
    }


    /**
     * 点击开始直播按钮
     */
    private void startLive() {
        if(mCheckedGamePosition < 0){
            ToastUtil.show("请选择游戏");
            return;
        }
        if(mAvatarFile == null || !mAvatarFile.exists()){
            ToastUtil.show(R.string.live_choose_live_thumb);
            return;
        }
        String title = mTitleEv.getText().toString().trim() ;
        if(TextUtils.isEmpty(title)){
            ToastUtil.show("请输入直播标题");
            return;
        }
        if (mLiveClassID == 0) {
            ToastUtil.show("请选择直播分类");
            return;
        }

        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO}, new Runnable() {
            @Override
            public void run() {
                createRoom();
            }
        });
    }

    /**
     * 请求创建直播间接口，开始直播
     */
    private void createRoom() {
        final String gameId = mGameTypeList.get(mCheckedGamePosition).getId() ;
        final String gameName = mGameTypeList.get(mCheckedGamePosition).getName() ;

        final String title = mTitleEv.getText().toString().trim();
        HttpUtil.createRoom(title, mLiveClassID, mLiveType, mLiveTypeVal,gameId, mAvatarFile, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {

                    SpUtil.getInstance().setStringValue(SpUtil.LIVE_THUMB,mAvatarFile.getAbsolutePath()) ;
                    SpUtil.getInstance().setStringValue(SpUtil.LIVE_TITLE,title) ;
                    SpUtil.getInstance().setStringValue(SpUtil.LIVE_CATEGORY_ID,String.valueOf(mLiveClassID)) ;
                    SpUtil.getInstance().setStringValue(SpUtil.LIVE_CATEGORY_NAME,mLiveClassName) ;

                    L.e("开播", "createRoom------->" + info[0]);
                    LiveAnchorActivity.launch(mContext,gameId,gameName,info[0], mLiveType, mLiveTypeVal);
                    finish() ;
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext) ;
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }
        });
    }

    /**
     * 选择直播频道
     */
    private void chooseLiveClass() {
        Intent intent = new Intent(mContext, LiveChooseClassActivity.class);
        intent.putExtra(Constants.CLASS_ID, mLiveClassID);
        mImageUtil.startActivityForResult(intent, mActivityResultCallback);
    }

    /**
     * 选择直播类型
     */
    private void chooseLiveRoomType() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CHECKED_ID, mLiveType);
        LiveRoomTypeDialogFragment fragment = new LiveRoomTypeDialogFragment();
        fragment.setArguments(bundle);
        fragment.setCallback(mLiveTypeCallback);
        fragment.show(getSupportFragmentManager(), "LiveRoomTypeDialogFragment");
    }

    private void changeRoomType(LiveRoomTypeBean bean){
        switch (bean.getId()) {
            case Constants.LIVE_TYPE_NORMAL:
                onLiveTypeNormal(bean);
                break;
            case Constants.LIVE_TYPE_PWD:
                onLiveTypePwd(bean);
                break;
            case Constants.LIVE_TYPE_PAY:
                onLiveTypePay(bean);
                break;
            case Constants.LIVE_TYPE_TIME:
                onLiveTypeTime(bean);
                break;
        }
    }

    /**
     * 普通房间
     */
    private void onLiveTypeNormal(LiveRoomTypeBean bean) {
        mLiveType = bean.getId();
        mRoomTypeTv.setText(bean.getName());
        mLiveTypeVal = 0;
        mLiveTimeCoin = 0;
    }

    /**
     * 密码房间
     */
    private void onLiveTypePwd(final LiveRoomTypeBean bean) {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_pwd), DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, 8, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_pwd_empty);
                } else {
                    mLiveType = bean.getId();
                    mRoomTypeTv.setText(bean.getName());
                    if (StringUtil.isInt(content)) {
                        mLiveTypeVal = Integer.parseInt(content);
                    }
                    mLiveTimeCoin = 0;
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 付费房间
     */
    private void onLiveTypePay(final LiveRoomTypeBean bean) {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_fee), DialogUitl.INPUT_TYPE_NUMBER, 8, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_fee_empty);
                } else {
                    mLiveType = bean.getId();
                    mRoomTypeTv.setText(bean.getName());
                    if (StringUtil.isInt(content)) {
                        mLiveTypeVal = Integer.parseInt(content);
                    }
                    mLiveTimeCoin = 0;
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 计时房间
     */
    private void onLiveTypeTime(final LiveRoomTypeBean bean) {
        //2020年7月1日 改为每10分钟收费多少钱，由主播设置
        DialogUitl.showSimpleInputDialog(mContext, "设置收费金额(每10分钟)","请输入价格/10分钟"
                , DialogUitl.INPUT_TYPE_NUMBER, 8, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_fee_empty);
                } else {
                    mLiveType = bean.getId();
                    mRoomTypeTv.setText(bean.getName());
                    if (StringUtil.isInt(content)) {
                        mLiveTypeVal = Integer.parseInt(content);
                        mLiveTimeCoin = mLiveTypeVal ;
                    }
                    dialog.dismiss();
                }
            }
        });



//        LiveTimeDialogFragment fragment = new LiveTimeDialogFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt(Constants.CHECKED_COIN, mLiveTimeCoin);
//        fragment.setArguments(bundle);
//        fragment.setCommonCallback(new CommonCallback<Integer>() {
//            @Override
//            public void callback(Integer coin) {
//                mLiveType = bean.getId();
//                mRoomTypeTv.setText(bean.getName());
//                mLiveTypeVal = coin;
//                mLiveTimeCoin = coin;
//            }
//        });
//        fragment.show(getSupportFragmentManager(), "LiveTimeDialogFragment");
    }

    /**
     * 选择游戏类型Dialog
     */
    private void showChooseGameDialog(){
        if(null == mChooseGameDialog){
            mChooseGameDialog = new Dialog(mContext,R.style.custom_dialog) ;
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_choose_game,null) ;
            mChooseGameDialog.setContentView(contentView);
            mChooseGameDialog.setCancelable(true) ;

            Window dialogWindow = mChooseGameDialog.getWindow() ;
            dialogWindow.setGravity(Gravity.BOTTOM) ;
            dialogWindow.setWindowAnimations(R.style.main_menu_animStyle) ;
            //获得窗体的属性
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = ScreenDimenUtil.getInstance().getScreenWdith() ;
            dialogWindow.setAttributes(lp);

            RecyclerView gameRv = contentView.findViewById(R.id.dialog_choose_game_rv) ;
            gameRv.setFocusable(false);
            gameRv.setNestedScrollingEnabled(false);
            gameRv.setLayoutManager(new GridLayoutManager(mContext,4)) ;

            AdapterChooseGame adapter = new AdapterChooseGame(mContext, mGameTypeList, new OnRvClickListener() {
                @Override
                public void onClick(int type, int position) {
                    if(position >= 0  && position < mGameTypeList.size()){
                        mChooseGameDialog.dismiss();

                        mCheckedGamePosition = position ;
                        updateGameDisplay() ;
                    }
                }
            }) ;
            gameRv.setAdapter(adapter) ;
        }

        mChooseGameDialog.show() ;
    }

    /**
     * 填充游戏
     */
    private void updateGameDisplay(){
        GameTypeBean info = mGameTypeList.get(mCheckedGamePosition) ;

        int gameIcon = HttpConsts.getGameIconByType(info.getId()) ;
        mGameIv.setImageDrawable(mContext.getResources().getDrawable(gameIcon)) ;
        mGameNameTv.setText(info.getName());
    }

    /**
     * 获取游戏类型
     */
    private void getGameList(){
        HttpUtil.getGameList(new CommonCallback<List<GameTypeBean>>() {
            @Override
            public void callback(List<GameTypeBean> bean) {
                if(bean != null && bean.size() > 0){
                    mGameTypeList.clear();
                    for(GameTypeBean info : bean){
                        if(info.isShow()){
                            mGameTypeList.add(info) ;
                        }
//                        mGameTypeList.add(info) ;
                    }
                }
                if(mGameTypeList.size() > 0){
                    mCheckedGamePosition = 0 ;

                    updateGameDisplay() ;
                }
            }
        });
    }

    /**
     * 设置头像
     */
    private void setAvatar() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
    }
}
