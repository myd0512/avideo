package com.yinjiee.ausers.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yinjiee.ausers.AppConfig;
import com.yinjiee.ausers.Constants;
import com.yinjiee.ausers.R;
import com.yinjiee.ausers.adapter.CoinAdapter;
import com.yinjiee.ausers.adapter.VIPAdapter;
import com.yinjiee.ausers.bean.CoinBean;
import com.yinjiee.ausers.bean.PayChannel;
import com.yinjiee.ausers.bean.UserBean;
import com.yinjiee.ausers.bean.VIPBean;
import com.yinjiee.ausers.event.CoinChangeEvent;
import com.yinjiee.ausers.http.HttpCallback;
import com.yinjiee.ausers.http.HttpClient;
import com.yinjiee.ausers.http.HttpConsts;
import com.yinjiee.ausers.http.HttpUtil;
import com.yinjiee.ausers.interfaces.OnItemClickListener;
import com.yinjiee.ausers.log;
import com.yinjiee.ausers.utils.DialogUitl;
import com.yinjiee.ausers.utils.DpUtil;
import com.yinjiee.ausers.utils.StringUtil;
import com.yinjiee.ausers.utils.ToastUtil;
import com.yinjiee.ausers.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.List;

public class VIPActivity extends AbsActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vip;
    }
    public static void forward(Context context) {
        context.startActivity(new Intent(context, VIPActivity.class));
    }
    RecyclerView mRecyclerView;
    VIPAdapter mAdapter;
    List<PayChannel> paylist;
    VIPBean mCheckedCoinBean;
    TextView text1;
    TextView text2;
    private View mTop;
    @Override
    protected void main() {
        setTitle("成为VIP");
        mTop = findViewById(R.id.top);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new VIPAdapter(mContext, DpUtil.dp2px(80));
        mAdapter.setContactView(mTop);
        mAdapter.setOnItemClickListener(new OnItemClickListener<VIPBean>() {
            @Override
            public void onItemClick(VIPBean bean, int position) {
//                h5Pay(bean);
                onItemClick2(bean,position);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        checkPayResult();
    }

    /**
     * 检查支付结果
     */
    private void checkPayResult() {
        HttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    log.e(obj.toString());
                    String vip_txt = obj.getString("vip_txt");
                    String endtime = obj.getString("endtime");
                    if (!endtime.equals("")){
                        text1.setText(vip_txt);
                        text2.setText("到期时间"+endtime);
                    }

                    List<VIPBean> list = JSON.parseArray(obj.getString("vip"), VIPBean.class);
                    if (mAdapter != null) {
                        mAdapter.setList(list);
                    }
                    paylist = JSON.parseArray(obj.getString("paylist"), PayChannel.class);
                }
            }
        });
    }
    void onItemClick2(VIPBean bean, int position) {
        mCheckedCoinBean = bean;
        if (paylist == null || paylist.size() == 0) {
            ToastUtil.show(Constants.PAY_ALL_NOT_ENABLE);
            return;
        }
        String[] strs = new String[paylist.size()];
        for(int i = 0;i<paylist.size();i++){
            strs[i] = paylist.get(i).name;
        }
        DialogUitl.showStringArrayDialog(mContext, strs, new DialogUitl.StringArrayDialogCallback2() {
            @Override
            public void onItemClick(String text, int index) {
                h5Pay(mCheckedCoinBean,paylist.get(index).id);
            }
        });
    }

    private void h5Pay(VIPBean vipBean,String id){
        HttpClient.getInstance().get("Charge.getH5Order", HttpConsts.GET_ALI_ORDER)
                .params("uid", AppConfig.getInstance().getUid())
                .params("money", vipBean.money)
                .params("changeid", vipBean.id)
                .params("changetype","2")
                .params("coin", vipBean.coin)
                .params("id", id)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
//                        checkPayResult();
                        JSONObject obj = JSON.parseObject(info[0]);
                        String url = obj.getString("url");
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                    @Override
                    public boolean showLoadingDialog() {
                        return true;
                    }

                    @Override
                    public Dialog createLoadingDialog() {
                        return DialogUitl.loadingDialog(mContext);
                    }
                });
    }
}
