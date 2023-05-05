package com.fengtuan.videoanchor.view;

import android.content.Context;
import android.view.ViewGroup;

public abstract class GameGiftBaseHolder extends AbsViewHolder {


    public GameGiftBaseHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public GameGiftBaseHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    public abstract void setVisible(boolean show) ;
    public abstract void destroyView() ;
}
