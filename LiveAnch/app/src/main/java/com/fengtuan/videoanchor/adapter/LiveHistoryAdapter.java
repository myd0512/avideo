package com.fengtuan.videoanchor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fengtuan.videoanchor.R;
import com.fengtuan.videoanchor.bean.LiveHistory;

import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 */

public class LiveHistoryAdapter extends RefreshAdapter<LiveHistory> {
    public LiveHistoryAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.adapter_live_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        ((Vh) vh).setData(mList.get(position));
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView mDateTv;
        TextView mTimeTv;

        public Vh(View itemView) {
            super(itemView);
            mDateTv = itemView.findViewById(R.id.adapter_live_history_date_tv);
            mTimeTv = itemView.findViewById(R.id.adapter_live_history_time_tv);
        }

        void setData(LiveHistory bean) {
            if(bean != null){
                mDateTv.setText(bean.getDatestarttime());
                mTimeTv.setText(bean.getLength()) ;
            }
        }

    }
}
