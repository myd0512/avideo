package com.fengtuan.videoanchor;

import com.orhanobut.logger.Logger;

public class log {
    private static boolean isPrintLog = BuildConfig.DEBUG;
    public static void e(String msg){
        if (isPrintLog)
            Logger.e(msg);
    }
}
