package com.yinjiee.ausers.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yinjiee.ausers.R;
import com.yinjiee.ausers.bean.Niuren2;
import com.yinjiee.ausers.bean.NiurenList;
import com.yinjiee.ausers.glide.ImgLoader;
import com.yinjiee.ausers.interfaces.OnRvClickListener;
import com.yinjiee.ausers.utils.StringUtil;

import java.util.List;

import cn.iwgang.countdownview.CountdownView;

public class AdapterNiurenList extends RecyclerView.Adapter<AdapterNiurenList.AnlHolder> {
    private Context mContext ;
    private List<Niuren2> mDateList ;
    private OnRvClickListener mClickListener ;

    public AdapterNiurenList(Context mContext, List<Niuren2> mDateList, OnRvClickListener mClickListener) {
        this.mContext = mContext;
        this.mDateList = mDateList;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public AnlHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AnlHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_niuren_list,parent,false),mClickListener) ;
    }

    @Override
    public void onBindViewHolder(@NonNull AnlHolder holder, final int position) {
        Niuren2 info = mDateList.get(position) ;

        if(info != null){
            String userHead = info.avatar;
            String userName = info.nickname ;
            String tottingRote = info.totalmoney ;
//            String winRote = info.getProbabilityWinRote() ;
//            String gameName = info.getGame_name() ;
//            String gameType = info.getTypeName() ;
//            String gamePeriod = info.getPeriods() ;

            ImgLoader.display(userHead,holder.mUserIv,R.mipmap.icon_avatar_placeholder) ;

            holder.mUserNameTv.setText(TextUtils.isEmpty(userName) ? "一剑飘红" : userName) ;
            holder.mPlayCountTv.setText(tottingRote);
//            holder.mPlayRateTv.setText(winRote);
//            holder.mGameNameTv.setText(StringUtil.convertNull(gameName));
//            holder.mGameTypeTv.setText(StringUtil.convertNull(gameType));
//            holder.mGamePeriodTv.setText(StringUtil.convertNull(gamePeriod));

//            long limitTime = info.getEndTime() ;
//            boolean canTz = limitTime > 0 ;

//            holder.mSubmitTv.setVisibility(canTz ? View.VISIBLE : View.INVISIBLE);
//            if(canTz){
//                holder.mGameStateTv.setVisibility(View.GONE);
//                holder.rl_count_down.setVisibility(View.VISIBLE);
////                holder.cv_countdownView.start(limitTime-System.currentTimeMillis());
////                holder.cv_countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
////                    @Override
////                    public void onEnd(CountdownView cv) {
////                        //刷新界面
//////                        mDateList.get(position).setEndTime(0);
////                        notifyDataSetChanged();
////                    }
////                });
//            }else {
//                holder.mGameStateTv.setVisibility(View.VISIBLE);
//                holder.rl_count_down.setVisibility(View.GONE);
//                holder.mGameStateTv.setText("已结束") ;
//            }
        }
    }

    @Override
    public int getItemCount() {
        return mDateList.size();
    }
    public List<Niuren2>getmDateList(){return mDateList;}
    static class AnlHolder extends RecyclerView.ViewHolder{
        private ImageView mUserIv ;
        private TextView mUserNameTv ;
        private TextView mPlayCountTv ;
        private TextView mPlayRateTv ;
        private TextView mGameStateTv ;
        private TextView mGameNameTv ;
        private TextView mGameTypeTv ;
        private TextView mGamePeriodTv ;
        private View mSubmitTv ;
        RelativeLayout rl_count_down;
        CountdownView cv_countdownView;

        public AnlHolder(View itemView, final OnRvClickListener clickListener) {
            super(itemView);

            mUserIv = itemView.findViewById(R.id.adapter_niuren_list_user_iv) ;
            mUserNameTv = itemView.findViewById(R.id.adapter_niuren_list_user_tv) ;
            mPlayCountTv = itemView.findViewById(R.id.adapter_niuren_list_count_tv) ;
            mPlayRateTv = itemView.findViewById(R.id.adapter_niuren_list_rate_tv) ;
            mGameStateTv = itemView.findViewById(R.id.adapter_niuren_list_state_tv) ;
            mGameNameTv = itemView.findViewById(R.id.adapter_niuren_list_game_name_tv) ;
            mGameTypeTv = itemView.findViewById(R.id.adapter_niuren_list_game_type_tv) ;
            mGamePeriodTv = itemView.findViewById(R.id.adapter_niuren_list_game_period_tv) ;
            mSubmitTv = itemView.findViewById(R.id.adapter_niuren_list_submit_tv) ;
            cv_countdownView = itemView.findViewById(R.id.cv_countdownView) ;
            rl_count_down = itemView.findViewById(R.id.rl_count_down) ;
            mSubmitTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onClick(1,getAdapterPosition()) ;
                    }
                }
            });
        }
    }




}
