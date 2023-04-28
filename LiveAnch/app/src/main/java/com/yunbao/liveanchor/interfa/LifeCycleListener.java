package com.yunbao.liveanchor.interfa;

public interface LifeCycleListener {

    void onCreate();

    void onStart();

    void onReStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
