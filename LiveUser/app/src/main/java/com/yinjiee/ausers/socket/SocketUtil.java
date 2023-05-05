package com.yinjiee.ausers.socket;

import android.text.TextUtils;

import com.baidu.speech.utils.LogUtil;
import com.yinjiee.ausers.AppContext;
import com.yinjiee.ausers.event.GameTimeEvent;
import com.yinjiee.ausers.http.HttpConsts;
import com.yinjiee.ausers.utils.GsonUtil;
import com.yinjiee.ausers.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.net.URI;

public class SocketUtil {
    public static void startGameResultSocket(final String mGameId ){
        final String url = HttpConsts.getGameResultUrlByType(mGameId) ;
        LogUtil.e("TestGameSocket","startGameResultSocket-url=" + url) ;
        if(!TextUtils.isEmpty(url)){
            //启动socked
            JWebSocketClient  mGameResultClient = new JWebSocketClient(AppContext.sInstance, URI.create(url),JWebSocketClient.GAME_SOCKET_TYPE_RESULT){
                @Override
                public void onMessage(String message) {
                    LogUtil.e("TestGameSocket","onMessage-message=" + message) ;

                    SocketGameReceiveBean resultBean = GsonUtil.fromJson(message, SocketGameReceiveBean.class);

                    if(resultBean != null){
                        String dianshu = resultBean.getDianshu_result();
                        final String[] strArr = dianshu.split(",");
                        final String period = resultBean.getOldGamePeroid(mGameId) ;
                        final String newGamePeriod = resultBean.getNewGamePeroid(mGameId) ;
                        final int time = StringUtil.convertInt(resultBean.getTime()) ;
                        GameTimeEvent event=new GameTimeEvent();
                        event.mGameId=mGameId;
                        event.time=time- HttpConsts.GAME_STOP_IN_TIME;
                        EventBus.getDefault().post(event);
                        //关闭
                        if(this != null){
                            if(this.isOpen()){
                                this.close();
                            }
                        }
                    }
                }
            } ;

            LogUtil.e("TestGameSocket","connectGameSocket") ;
            mGameResultClient.connect() ;
        }
    }
}
