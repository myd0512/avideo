package com.fengtuan.videoanchor.http;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class ParseUtils {

    public static <T> T parseJson(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json,clazz) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null ;
    }

    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        try {
            return JSON.parseArray(json,clazz) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>() ;
    }

}
