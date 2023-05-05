package com.yinjiee.ausers.mob;

/**
 * Created by cxf on 2018/9/21.
 */

public interface MobCallback {
    void onSuccess(Object data);

    void onError();

    void onCancel();

    void onFinish();
}
