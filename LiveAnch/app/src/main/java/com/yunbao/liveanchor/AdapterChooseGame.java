package com.yunbao.liveanchor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.liveanchor.bean.GameTypeBean;
import com.yunbao.liveanchor.http.HttpConsts;
import com.yunbao.liveanchor.interfa.OnRvClickListener;

import java.util.List;

public class AdapterChooseGame extends RecyclerView.Adapter<AdapterChooseGame.AcgHolder> {
    private Context mContext ;
    private List<GameTypeBean> mDataList ;
    private OnRvClickListener mClickListener ;

    public AdapterChooseGame(Context mContext, List<GameTypeBean> mDataList, OnRvClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public AcgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AcgHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_game_type,parent,false),mClickListener) ;
    }

    @Override
    public void onBindViewHolder(@NonNull AcgHolder holder, int position) {
        GameTypeBean info = mDataList.get(position) ;

        holder.mIv.setImageDrawable(mContext.getResources().getDrawable(HttpConsts.getGameIconByType(info.getId()))) ;
        holder.mNameTv.setText(info.getName()) ;
    }

    @Override
    public int getItemCount() {
        return mDataList.size() ;
    }

    static class AcgHolder extends RecyclerView.ViewHolder{
        private ImageView mIv ;
        private TextView mNameTv ;

        public AcgHolder(View itemView,final OnRvClickListener clickListener) {
            super(itemView);

            mIv = itemView.findViewById(R.id.adapter_game_type_iv) ;
            mNameTv = itemView.findViewById(R.id.adapter_game_type_tv) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onClick(0,getAdapterPosition()) ;
                    }
                }
            });
        }
    }
}
