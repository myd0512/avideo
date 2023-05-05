package com.fengtuan.videoanchor.beauty;

import com.fengtuan.beauty.bean.FilterBean;

/**
 * Created by cxf on 2018/10/8.
 * 基础美颜回调
 */

public interface DefaultEffectListener extends EffectListener {

    void onFilterChanged(FilterBean filterBean);

    void onMeiBaiChanged(int progress);

    void onMoPiChanged(int progress);

    void onHongRunChanged(int progress);

}
