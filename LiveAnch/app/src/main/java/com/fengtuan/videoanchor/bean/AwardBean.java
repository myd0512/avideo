package com.fengtuan.videoanchor.bean;

import android.text.TextUtils;

import com.fengtuan.videoanchor.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AwardBean {
    private String name;
    private int imageUrl;
    public AwardBean(){
    }

    public AwardBean(String name, int resourceId){
        this.name = name;
        this.imageUrl = resourceId;
    }
    public AwardBean(String name){
        this.name = name;
    }

    public AwardBean(int imageUrl){
        this.imageUrl = imageUrl;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }





    private static final List<String> redList = new ArrayList<>();
    private static final List<String> blueList = new ArrayList<>();
    private static final List<String> greenList = new ArrayList<>();

    /**
     * 六合彩用到了此方法
     * @return 根据号码，获取对应的背景
     */
    public static int getItemBackgroundByName(String number){
        if(TextUtils.isEmpty(number)){
            return R.drawable.bg_game_point_white ;
        }

        if(redList.size() == 0){
            redList.clear();
            blueList.clear();
            greenList.clear();

            //1 ||  2 ||  7 ||  8 ||  12 ||  13 ||  18 ||  19 ||  23 ||  24 ||  29 ||  30 ||  34 ||  35 ||  40 ||  45 ||  46
            String[] redStr = new String[]{"1","2","7","8","12","13","18","19","23","24","29","30","34","35","40","45","46"} ;
            //3 ||  4 ||  9 ||  10 ||  14 ||  15 ||  20 ||  25 ||  26 ||  31 ||  36 ||  37 ||  41 ||  42 ||  47 ||  48
            String[] blueStr = new String[]{"3","4","9","10","14","15","20","25","26","31","36","37","41","42","47","48"} ;
            //5 ||  6 ||  11 ||  16 ||  17 ||  21 ||  22 ||  27 ||  28 ||  32 ||  33 ||  38 ||  39 ||  43 ||  44 ||  49
            String[] greenStr = new String[]{"5","6","11","16","17","21","22","27","28","32","33","38","39","43","44","49"} ;

            redList.addAll(Arrays.asList(redStr)) ;
            blueList.addAll(Arrays.asList(blueStr)) ;
            greenList.addAll(Arrays.asList(greenStr)) ;
        }

        if(redList.contains(number)){
            return R.drawable.bg_game_point_red ;
        }
        if(blueList.contains(number)){
            return R.drawable.bg_game_point_blue ;
        }
        if(greenList.contains(number)){
            return R.drawable.bg_game_point_green ;
        }

        return R.drawable.bg_game_point_white ;
    }

}
