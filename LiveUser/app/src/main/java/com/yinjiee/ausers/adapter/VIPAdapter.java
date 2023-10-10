package com.yinjiee.ausers.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yinjiee.ausers.R;
import com.yinjiee.ausers.bean.VIPBean;
import com.yinjiee.ausers.interfaces.OnItemClickListener;
import com.yinjiee.ausers.utils.WordUtil;

import java.util.ArrayList;
import java.util.List;

public class VIPAdapter extends HeaderAdapter{

    private List<VIPBean> mList;
    private View.OnClickListener mOnClickListener;
    private String mGiveString;
    private OnItemClickListener<VIPBean> mOnItemClickListener;

    public VIPAdapter(Context context, int headHeight) {
        super(context, headHeight);
        mList = new ArrayList<>();
        mGiveString = WordUtil.getString(R.string.coin_give);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((VIPBean) tag, 0);
                }
            }
        };
    }

    public void setList(List<VIPBean> list) {
        if (list != null && list.size() > 0) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }


    public void setOnItemClickListener(OnItemClickListener<VIPBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateNormalViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VIPAdapter.Vh(mInflater.inflate(R.layout.item_vip, parent, false));
    }

    @Override
    protected void onBindNormalViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        ((VIPAdapter.Vh) vh).setData(mList.get(position));
    }

    @Override
    protected int getNormalItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mCoin;
        TextView mMoney;
        TextView mGive;

        public Vh(View itemView) {
            super(itemView);
            mCoin = itemView.findViewById(R.id.coin);
            mMoney = itemView.findViewById(R.id.money);
            mGive = itemView.findViewById(R.id.give);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(VIPBean bean) {
            itemView.setTag(bean);
            mCoin.setText(bean.desc);
            mMoney.setText(bean.money);
            mGive.setText("赠送 ￥"+bean.give);
//            mMoney.setText("￥" + bean.getMoney());
//            if (!"0".equals(bean.getGive())) {
//                if (mGive.getVisibility() != View.VISIBLE) {
//                    mGive.setVisibility(View.VISIBLE);
//                }
//                mGive.setText(mGiveString + bean.getGive() + "元");
//            } else {
//                if (mGive.getVisibility() == View.VISIBLE) {
//                    mGive.setVisibility(View.INVISIBLE);
//                }
//            }
        }
    }
}
