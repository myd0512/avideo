package com.fengtuan.videoanchor.bean;

import com.fengtuan.videoanchor.util.MyStringUtils;

/**
 * 礼物记录
 */
public class GiftRecord {
    private String giftcount ;
    private String gift_name ;
    private String addtime ;
    private String user_nicename ;
    private String totalcoin ;

    public String getGiftcount() {
        return giftcount;
    }

    public void setGiftcount(String giftcount) {
        this.giftcount = giftcount;
    }

    public String getGift_name() {
        return gift_name;
    }

    public void setGift_name(String gift_name) {
        this.gift_name = gift_name;
    }

    public String getAddtime() {
        return MyStringUtils.convertGiftDateTime(addtime);
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getTotalcoin() {
        return MyStringUtils.convertMoney(totalcoin);
    }

    public void setTotalcoin(String totalcoin) {
        this.totalcoin = totalcoin;
    }
}
