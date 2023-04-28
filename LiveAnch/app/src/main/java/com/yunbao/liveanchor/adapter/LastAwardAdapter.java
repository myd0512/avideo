package com.yunbao.liveanchor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.yunbao.liveanchor.R;
import com.yunbao.liveanchor.bean.AwardBean;
import com.yunbao.liveanchor.util.BitmapUtil;
import com.yunbao.liveanchor.util.DpUtil;

import java.util.List;

public class LastAwardAdapter extends RecyclerView.Adapter<LastAwardAdapter.Vh>  {

    private List<AwardBean> mList;
    private LayoutInflater mInflater;
    private ScaleAnimation mAnimation;
    private Context mContext;
    private int type;

    private int mParentWidth = 0 ;//自适应宽，如果超过parent宽，就自动缩小控件大小
    private int mCornorSize ;//默认是15dp
    private boolean mNeedAutoFit = false ;//是否需要调整item宽度

    public LastAwardAdapter(Context context, List<AwardBean> list, int type,int parentWidth){
        this(context,null,list,type) ;

        mParentWidth = parentWidth ;
    }

    public LastAwardAdapter(Context context, List<AwardBean> list, int type){
        this(context,null,list,type) ;
    }

    public LastAwardAdapter(Context context, LayoutInflater inflater, List<AwardBean> list, int type) {
        this.mContext = context;
        this.mInflater = inflater;
        this.mList = list;
        this.type = type;
        mAnimation = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimation.setDuration(400);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(-1);

        mCornorSize = DpUtil.dp2px(15) ;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(null == mInflater){
            mInflater = LayoutInflater.from(mContext) ;
        }
        return new Vh(mInflater.inflate(R.layout.item_last_award, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) { }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void release() {
        if (mList != null) {
            mList.clear();
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mName;

        public Vh(View itemView) {
            super(itemView);

            mName =  itemView.findViewById(R.id.last_ward_num);

            if(mParentWidth > 0){
                int itemSize = mList.size() ;
                int totalWidth = itemSize * (mCornorSize + DpUtil.dp2px(3)) ;
                if(totalWidth > mParentWidth){//总宽度超过父布局了，需要调整宽度
                    mCornorSize = (mParentWidth - (DpUtil.dp2px(3) * itemSize)) / itemSize ;
                    mNeedAutoFit = true ;
                }
            }

            if(mNeedAutoFit){
                mName.getLayoutParams().width = mCornorSize ;
                mName.getLayoutParams().height = mCornorSize ;
            }
        }

        void setData(final AwardBean bean, Object payload) {
            if (payload == null) {
                if (type == 1) {
                    mName.setBackgroundResource(bean.getImageUrl());
                }else if(type == 2){
                    mName.setText(bean.getName());
                }else if (type == 3){//六合彩
                    mName.setText(bean.getName());
                    mName.setBackgroundResource(bean.getImageUrl());
                }else if(type == 4){
                    mName.setText(bean.getName());

                    GradientDrawable bgDr = new GradientDrawable() ;
                    bgDr.setShape(GradientDrawable.OVAL);
                    bgDr.setCornerRadius(mCornorSize);
                    bgDr.setColor(Color.parseColor(BitmapUtil.getRandColorCode())) ;
                    mName.setBackgroundDrawable(bgDr) ;

//
//                    switch (getAdapterPosition()){
//                        case 0:
//                            mName.setBackgroundResource(R.drawable.bg_yfsc_point_0);
//                            break;
//                        case 1:
//                            mName.setBackgroundResource(R.drawable.bg_yfsc_point_1);
//                            break;
//                        case 2:
//                            mName.setBackgroundResource(R.drawable.bg_yfsc_point_2);
//                            break;
//                        case 3:
//                            mName.setBackgroundResource(R.drawable.bg_yfsc_point_3);
//                            break;
//                        case 4:
//                            mName.setBackgroundResource(R.drawable.bg_yfsc_point_4);
//                            break;
//                        case 5:
//                            mName.setBackgroundResource(R.drawable.bg_yfsc_point_5);
//                            break;
//                        case 6:
//                            mName.setBackgroundResource(R.drawable.bg_yfsc_point_6);
//                            break;
//                        case 7:
//                            mName.setBackgroundResource(R.drawable.bg_yfsc_point_7);
//                            break;
//                        case 8:
//                            mName.setBackgroundResource(R.drawable.bg_yfsc_point_8);
//                            break;
//                        case 9:
//                            mName.setBackgroundResource(R.drawable.bg_yfsc_point_9);
//                            break;
//                    }
                }
            }
        }
    }
}
