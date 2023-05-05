package com.fengtuan.videoanchor.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ValidatePhoneUtil {
    //判断手机号码的正则表达式
    private static final String MOBILE_NUM_REGEX = "^1\\d{10}$";


    /**
     * 验证一个号码是不是手机号
     * @param mobileNumber phoneNumber
     */
    public static boolean validateMobileNumber(String mobileNumber) {
        Pattern p = Pattern.compile(MOBILE_NUM_REGEX);
        Matcher m = p.matcher(mobileNumber);
        return m.matches();
    }


}
