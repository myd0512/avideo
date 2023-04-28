package com.yunbao.liveanchor.bean;

import com.yunbao.liveanchor.util.MyStringUtils;

public class LiveHistory {
    private String datestarttime ;
    private String length ;

    public String getDatestarttime() {
        return MyStringUtils.convertNull(datestarttime);
    }

    public void setDatestarttime(String datestarttime) {
        this.datestarttime = datestarttime;
    }

    public String getLength() {
        return MyStringUtils.convertNull(length);
    }

    public void setLength(String length) {
        this.length = length;
    }
}
