package com.yinjiee.ausers.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.yinjiee.ausers.AppConfig;
import com.yinjiee.ausers.Constants;
import com.yinjiee.ausers.R;
import com.yinjiee.ausers.utils.AndroidBug5497Workaround;
import com.yinjiee.ausers.utils.ScreenDimenUtil;
import com.yinjiee.ausers.utils.ToastUtil;
import com.yinjiee.ausers.views.customWebView.x5webview.X5WebChromeClient;
import com.yinjiee.ausers.views.customWebView.x5webview.X5WebView;
import com.yinjiee.ausers.views.customWebView.x5webview.X5WebViewClient;
import com.yinjiee.ausers.views.customWebView.x5webview.X5WebViewJSInterface;

import java.util.Objects;

/**
 * 加载网页
 */
public class WebViewServiceActivity extends AbsActivity implements X5WebViewJSInterface.OnWebViewJsInterface,
        X5WebChromeClient.OnChromeClientInterface, X5WebViewClient.OnWebViewClientInterface{
    /**
     * 当前的webView
     */
    private X5WebView mX5WebView = null;
    /**
     * 页面顶部的进度条
     */
    private ProgressBar mTopProgressbar = null;
    /**
     * 当前url
     */
    private String mCurUrl;
    /**
     * webview父容器
     */
    private RelativeLayout relative;

    private boolean mIsCustomer = false ;//是否是客服


    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview_service;
    }

    @Override
    protected void main() {
        //解决webview键盘遮挡输入框问题
        AndroidBug5497Workaround.assistActivity(this);
//        setContentView(R.layout.activity_webviewpager);

        //设置硬件加速
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        //初始化数据
        initData();
        //初始化view
        initView();
    }
    /**
     * 初始化数据
     */
    private void initData() {
        mCurUrl = getIntent().getStringExtra(Constants.URL);
        mIsCustomer = getIntent().getBooleanExtra("isCustomer",mIsCustomer) ;

    }
    /**
     * 初始化view
     */
    private void initView() {
        View statusView = findViewById(R.id.view_main_my_status_view) ;
        statusView.getLayoutParams().height = ScreenDimenUtil.getInstance().getStatusBarHeight() ;
        mTopProgressbar = findViewById(R.id.progressBar1);
        mTopProgressbar.setPadding(0, -10, 0, 0);
        if (Objects.equals(mTopProgressbar,"setAlpha")) {
            mTopProgressbar.setAlpha(0.6f);
        }
//        mX5WebView = findViewById(R.id.webView1);
        relative = findViewById(R.id.relative);
        //动态添加
        mX5WebView=new X5WebView(getApplicationContext());
        relative.addView(mX5WebView);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mX5WebView.getLayoutParams();
        params.height= RelativeLayout.LayoutParams.MATCH_PARENT;
        params.width= RelativeLayout.LayoutParams.MATCH_PARENT;
        params.addRule(RelativeLayout.ABOVE, R.id.bottom_view);
        mX5WebView.setLayoutParams(params);


//        mX5WebView.setDownloadListener(new MyWebViewDownLoadListener());

        if(mCurUrl.contains("html")){
            setWebview(mCurUrl);
        }else {
            mX5WebView.loadWebUrl(mCurUrl);
        }
        mX5WebView.setCanBackPreviousPage(true, this);

        mX5WebView.setJsListener(this);
        mX5WebView.setChromeClientListener(this);
        mX5WebView.setWebViewClientListener(this);

    }
    public static void forward(Context context, String url) {
        url += "&uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
        Intent intent = new Intent(context, WebViewServiceActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }
    //此方法使用在游戏
    public static void start(Context context, String url,boolean isCustomer) {
//        Log.e("返回数据===>url==",url);
        Intent intent = new Intent(context, WebViewServiceActivity.class);
        intent.putExtra(Constants.URL,url);
        intent.putExtra("isCustomer",isCustomer) ;
        context.startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        if (mX5WebView != null) {

            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = mX5WebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mX5WebView);
            }
            mX5WebView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mX5WebView.getSettings().setJavaScriptEnabled(false);
            mX5WebView.clearHistory();
            mX5WebView.clearView();
            mX5WebView.removeAllViews();
            mX5WebView.destroy();
            mX5WebView=null;

            relative.removeAllViews();
        }
        super.onDestroy();
    }

    /**
     * 复制到剪贴板
     */
    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(getString(R.string.copy_success));
    }

    @Override
    public void refreshWebView() {
        if (isFinishing()) {
            return;
        }
        mX5WebView.loadUrl(mCurUrl);
    }

    @Override
    public void setWebReadingInfo(String webmsg) {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void onProgressChanged(int newProgress) {

    }

    @Override
    public void onReceivedTitle(String title) {

    }

    @Override
    public void onPageStarted(String curUrl) {
        this.mCurUrl = curUrl;
    }

    @Override
    public void onPageFinished() {

    }

    @Override
    public void onReceivedError() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                &&keyCode == KeyEvent.KEYCODE_BACK) {
            if (mX5WebView.canGoBack()) {
                mCurUrl = "";
                mX5WebView.goBack();
                return true;
            }else {
                //弹窗
                exitActivity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    //先清除悬浮在推出
    private void  exitActivity(){
        finish();
    }
    public void gotoFinish(View view){
        finish();
    }
    private void setWebview(String content){
//        mX5WebView.loadDataWithBaseURL(null, getHtmlData(content),"text/html","UTF-8",null);
        mX5WebView.loadDataWithBaseURL(null, content,"text/html","UTF-8",null);
    }
    private String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto;}</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }
}
