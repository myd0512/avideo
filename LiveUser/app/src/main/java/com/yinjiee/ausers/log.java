package com.yinjiee.ausers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

public class log {
    private static boolean isPrintLog = BuildConfig.DEBUG;
    public static void e(@NonNull String message) {
        if (isPrintLog)
            Logger.e(message);
    }
    public static void e(@NonNull String message, @Nullable Object... args) {
        if (isPrintLog)
            Logger.e(null, message, args);
    }
}
