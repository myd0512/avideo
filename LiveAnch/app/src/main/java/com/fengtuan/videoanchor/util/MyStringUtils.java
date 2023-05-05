package com.fengtuan.videoanchor.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyStringUtils {


    /**
     * 判断空
     * @param temp content
     * @return content.trim()
     */
    public static String convertNull(String temp){
        if(TextUtils.isEmpty(temp)){
            return "" ;
        }

        return temp.trim() ;
    }

    /**
     * 转换2位小数
     * @param money money
     * @return money
     */
    public static String convertMoney(String money){
        if(TextUtils.isEmpty(money)){
            return "0" ;
        }

        BigDecimal bigDecimal = new BigDecimal(money) ;
        return bigDecimal.setScale(2, RoundingMode.HALF_UP).toPlainString() ;
    }

    /**
     * 转换时间戳
     * @return MM-dd HH:mm
     */
    public static String convertGiftDateTime(String time){
        if(TextUtils.isEmpty(time)){
            return "" ;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm") ;
            return dateFormat.format(new Date(Long.valueOf(time) * 1000)) ;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return time ;
    }

}
