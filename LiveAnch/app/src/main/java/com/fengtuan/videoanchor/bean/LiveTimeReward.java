package com.fengtuan.videoanchor.bean;

import com.fengtuan.videoanchor.util.MyStringUtils;

import java.util.List;

public class LiveTimeReward {
    private LTRTimeBean time ;
    private String today_income_total ;

    public LTRTimeBean getTime() {
        return time;
    }

    public void setTime(LTRTimeBean time) {
        this.time = time;
    }

    public String getToday_income_total() {
        return MyStringUtils.convertMoney(today_income_total);
    }

    public void setToday_income_total(String today_income_total) {
        this.today_income_total = today_income_total;
    }

    public static class LTRTimeBean{
        public List<String> sec ;
    }

}
