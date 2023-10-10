package com.fengtuan.videoanchor.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fengtuan.videoanchor.AppConfig;
import com.fengtuan.videoanchor.AppContext;
import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.bean.ConfigBean;
import com.fengtuan.videoanchor.bean.UserBean;
import com.fengtuan.videoanchor.http.HttpCallback;
import com.fengtuan.videoanchor.http.HttpClient;
import com.fengtuan.videoanchor.http.HttpConsts;
import com.fengtuan.videoanchor.http.HttpUtil;
import com.fengtuan.videoanchor.interfa.CommonCallback;
import com.fengtuan.videoanchor.util.SpUtil;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 启动页
 */
public class LauncherActivity extends AppCompatActivity {

    private Handler mHandler;
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //下面的代码是为了防止一个bug:
        // 收到极光通知后，点击通知，如果没有启动app,则启动app。然后切后台，再次点击桌面图标，app会重新启动，而不是回到前台。
        Intent intent = getIntent();
        if (!isTaskRoot()
                && intent != null
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
        setStatusBar();
        setContentView(R.layout.activity_launcher);
        mContext = this;

//        mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getConfig();
//            }
//        }, 800);
        downLoadFile();
    }

    private void downLoadFile(){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(AppConfig.txtUrl).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                runOnUiThread(()->ToastUtil.show("网络文件报错"));
                Log.e("TAG12", "onResponse: 网络文件报错");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.e("TAG12", "onResponse: "+ response.body().string());
                String rsakey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu+3e8ck+yzUOErNKZSuW\n" +
                        "R99tEtQ37VWW0ns3w5FXKH2UwDLdKjQq9ZYp4L/TKkNmPpNN+pGaJMqZyTYhrh/bJ4QWGez9cv40Jgcnp+fwJz3WzEjRL/yCZQFIMp4KGH8ddRxD94DnyHqFB8DG51PkpGuPogqrqfCJ0aze7x9QN/KRwljhoN92+Gdv4JvRd8QH3z+Kp01du19aza/qhA4MbhAOcUH6tvJvo9cGNi0iizxOMMyWB2Ju8EaWcEW3Z/b7CarBzSnzk4xcqi4NwtBW9yHUbyn04MhMPrjJp1j3H9IbiJSOK4WZv9yT5hKqpuZlmhZCr1N1N+ZlghalbHpEKQIDAQAB";
                String rs = decryptWithRSAPublicKey(rsakey,response.body().string());
//                Log.e("TAG12", "onResponse: "+ rs);
                String[] urls = rs.split(",");
                urlList = new ArrayList<String>(Arrays.asList(urls));
                testUrl();
            }
        });
    }

    private ArrayList<String> urlList;

    void testUrl(){
        if(urlList.size() > 0){
            String url = urlList.remove(0);
            AppConfig.getInstance().resetUrl(url);
            HttpClient.getInstance().resetUrl();
            HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
                @Override
                public void callback(ConfigBean bean) {
                    if (bean != null) {
//                        Log.e("TAG12", "resetUrl success: "+ url);
                        HttpClient.getInstance().resetUrl();
                        AppContext.sInstance.initBeautySdk(bean.getBeautyKey());
                        checkUidAndToken();
                    }else{
//                        Log.e("TAG12", "resetUrl faile: "+ url);
                        testUrl();
                    }
                }
            });
        }

    }

    String decryptWithRSAPublicKey(String rsaPubKey, String encryptedText) {
        try {
            // 将 RSA 公钥转换为字节数组
            byte[] publicKeyBytes = Base64.decode(rsaPubKey, Base64.DEFAULT);

            // 构建 RSA 公钥对象
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // 创建 RSA 解密器实例
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);

            // 解密密文
            byte[] encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // 将解密后的字节数组转换为字符串
            String decryptedText = new String(decryptedBytes, "UTF-8");
            return decryptedText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 获取Config信息
     */
    private void getConfig() {
        HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                if (bean != null) {
                    AppContext.sInstance.initBeautySdk(bean.getBeautyKey());
                    checkUidAndToken();
                }
            }
        });
    }

    /**
     * 检查uid和token是否存在
     */
    private void checkUidAndToken() {
        String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(new String[]{SpUtil.UID, SpUtil.TOKEN});
        final String uid = uidAndToken[0];
        final String token = uidAndToken[1];
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
            HttpUtil.ifToken(uid, token, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        AppConfig.getInstance().setLoginInfo(uid, token, false);
                        getBaseUserInfo();
                    }
                }
            });
        } else {
            LoginActivity.forward();
        }
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if (bean != null) {
                    forwardMainActivity();
                }
            }
        });
    }

    /**
     * 跳转到首页
     */
    private void forwardMainActivity() {
        MainActivity.launch(mContext);
        finish();
    }


    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConsts.IF_TOKEN);
        HttpUtil.cancel(HttpConsts.GET_BASE_INFO);
        HttpUtil.cancel(HttpConsts.GET_CONFIG);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    /**
     * 设置透明状态栏
     */
    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
        }
    }
}
