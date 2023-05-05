package com.fengtuan.videoanchor.http;

public abstract class NormalObjCallBack<T> extends HttpCallback {
    private Class<T> clz ;

    public NormalObjCallBack(Class<T> clz) {
        this.clz = clz;
    }

    @Override
    public void onSuccess(int code, String msg, String[] info) {
        if(info != null && info.length > 0){
            T t = ParseUtils.parseJson(info[0],clz) ;
            onResult(msg,t) ;
        }else{
            onResult(msg,null) ;
        }
    }

    @Override
    public void onError() {
        super.onError();

        onResult("请求失败",null) ;
    }

    public abstract void onResult(String message,T info) ;
}
