package com.yunbao.liveanchor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.liveanchor.R;
import com.yunbao.liveanchor.bean.GiftRecord;

import java.util.List;

/**
 * 礼物记录
 */
public class GiftRecordAdapter extends RecyclerView.Adapter<GiftRecordAdapter.Vh>  {
    private Context mContext;
    private List<GiftRecord> mList;

    public GiftRecordAdapter(Context context, List<GiftRecord> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(LayoutInflater.from(mContext).inflate(R.layout.adapter_gift_record_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        holder.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView mTimeTv;
        TextView mUserNameTv;
        TextView mGiftNameTv;
        TextView mCountTv;
        TextView mMoneyTv;

        public Vh(View itemView) {
            super(itemView);
            mTimeTv =  itemView.findViewById(R.id.adapter_gift_record_time_tv);
            mUserNameTv =  itemView.findViewById(R.id.adapter_gift_record_user_tv);
            mGiftNameTv =  itemView.findViewById(R.id.adapter_gift_record_name_tv);
            mCountTv =  itemView.findViewById(R.id.adapter_gift_record_count_tv);
            mMoneyTv =  itemView.findViewById(R.id.adapter_gift_record_money_tv);
        }

        void setData(GiftRecord bean) {
            String time = bean.getAddtime() ;
            String userName = bean.getUser_nicename() ;
            String giftName = bean.getGift_name() ;
            String count = bean.getGiftcount() ;
            String money = bean.getTotalcoin() ;

            mTimeTv.setText(time);
            mUserNameTv.setText(userName);
            mGiftNameTv.setText(giftName);
            mCountTv.setText(count);
            mMoneyTv.setText(money) ;
        }
    }
}
