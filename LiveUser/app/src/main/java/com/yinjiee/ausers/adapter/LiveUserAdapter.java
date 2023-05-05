package com.yinjiee.ausers.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yinjiee.ausers.Constants;
import com.yinjiee.ausers.R;
import com.yinjiee.ausers.bean.LiveUserGiftBean;
import com.yinjiee.ausers.bean.UserBean;
import com.yinjiee.ausers.glide.ImgLoader;
import com.yinjiee.ausers.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/10.
 */

public class LiveUserAdapter extends RecyclerView.Adapter<LiveUserAdapter.Vh> {

    private List<LiveUserGiftBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<UserBean> mOnItemClickListener;

    public LiveUserAdapter(Context context) {
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<UserBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        //暂定最多显示5个
        int size = mList.size() ;
        if(size > 5){
            size = 5 ;
        }
        return size ;
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView mAvatar;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveUserGiftBean userBean, int position, Object payload) {
            itemView.setTag(position);
            if (payload == null) {
                ImgLoader.displayAvatar(userBean.getAvatar(), mAvatar);
            }
        }

    }

    public void refreshList(List<LiveUserGiftBean> list) {
        if (mList != null && list != null && list.size() > 0) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    private int findItemPosition(String uid) {
        if (!TextUtils.isEmpty(uid)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                if (uid.equals(mList.get(i).getId())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void removeItem(String uid) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        int position = findItemPosition(uid);
        if (position >= 0) {
            mList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mList.size(), Constants.PAYLOAD);
        }
    }

    public void insertItem(LiveUserGiftBean userBean) {
        if (userBean == null) {
            return;
        }
        int position = findItemPosition(userBean.getId());
        if (position >= 0) {
            return;
        }
        mList.add(userBean);
        notifyDataSetChanged();
    }

    public void insertList(List<LiveUserGiftBean> list) {
        if (mList != null && list != null && list.size() > 0) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 守护信息发生变化
     */
    public void onGuardChanged(String uid, int guardType) {
        if (!TextUtils.isEmpty(uid)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                LiveUserGiftBean bean = mList.get(i);
                if (uid.equals(bean.getId())) {
                    if (bean.getGuardType() != guardType) {
                        bean.setGuardType(guardType);
                        notifyItemChanged(i, Constants.PAYLOAD);
                    }
                    break;
                }
            }
        }
    }

    public void clear(){
        if(mList!=null){
            mList.clear();
        }
        notifyDataSetChanged();
    }
}
