package com.fengtuan.videoanchor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.speech.utils.LogUtil;
import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.bean.ListBean;
import com.fengtuan.videoanchor.glide.ImgLoader;

import java.util.List;

/**
 * Created by cxf on 2018/9/27.
 */

public class MainRankListAdapter extends RefreshAdapter<ListBean> {

    public MainRankListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            int size = mList.size() ;
            if(size < 10){
                //第一个没在列表显示，所以需要去掉1
                size = 9 ;
            }
            return size;
        }
        return 0;
    }

    @Override
    public void clearData() {
        super.clearData();
    }

    @Override
    public void insertList(List<ListBean> list) {
        if (mRecyclerView != null && mList != null && list != null && list.size() > 0) {
            int p = mList.size() + 1;

            mList.addAll(list);
            notifyItemRangeInserted(p, list.size());
            mRecyclerView.scrollBy(0, mLoadMoreHeight);
        }
    }

    @Override
    public void refreshData(List<ListBean> list) {
        super.refreshData(list);
    }

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RankViewHolder(mInflater.inflate(R.layout.item_main_rank_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        if (holder instanceof RankViewHolder) {
            //第一个数据没在列表中显示，所以数据是从第二个开始的，position+1
            int realItemPosi = position + 1 ;
            ((RankViewHolder) holder).setData(mList.size() > realItemPosi ? mList.get(realItemPosi) : null, realItemPosi);
        }
    }

    class RankViewHolder extends RecyclerView.ViewHolder {
        TextView mIndexTv;
        ImageView mAvatarIv;
        TextView mNameTv;
        TextView mLevelTv;
        TextView mVotesTv;

        public RankViewHolder(View itemView) {
            super(itemView);

            mIndexTv = itemView.findViewById(R.id.item_main_rank_no_tv);
            mAvatarIv = itemView.findViewById(R.id.item_main_rank_head_iv);
            mNameTv = itemView.findViewById(R.id.item_main_rank_name_tv);
            mLevelTv = itemView.findViewById(R.id.item_main_rank_level_tv);
            mVotesTv = itemView.findViewById(R.id.item_main_rank_gift_tv);
        }

        void setData(ListBean bean, int position) {
            LogUtil.e("RankListAdapter","setData--position=" + position) ;

            mIndexTv.setText(String.valueOf(position + 1)) ;

            if(bean != null){
                ImgLoader.display(bean.getAvatarThumb(), mAvatarIv);
                mNameTv.setText(bean.getUserNiceName());
                mLevelTv.setText(String.valueOf(bean.getLevel()));
                mVotesTv.setText(bean.getTotalCoinFormat());
            }else{
                mAvatarIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_rank_head_def)) ;
                mNameTv.setText("虚位以待");
                mLevelTv.setText("0");
                mVotesTv.setText("0") ;
            }
        }
    }
}
