package com.fengtuan.videoanchor.interfa;

/**
 * Created by cxf on 2017/8/9.
 * RecyclerView的Adapter点击事件
 */

public interface OnItemClickListener<T> {
    void onItemClick(T bean, int position);
}
