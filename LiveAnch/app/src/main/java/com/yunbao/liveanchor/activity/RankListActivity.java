package com.yunbao.liveanchor.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.yunbao.liveanchor.R;
import com.yunbao.liveanchor.view.MainRankListViewHolder;

/**
 * 排行榜
 */
public class RankListActivity extends AbsActivity {

    private MainRankListViewHolder mRankView ;

    public static void launch(Context context){
        Intent toIt = new Intent(context,RankListActivity.class) ;
        context.startActivity(toIt );
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rank_list;
    }

    @Override
    protected void main() {
        super.main();

        setTitle("排行榜");

        FrameLayout mContentLay = findViewById(R.id.act_rank_content_lay) ;
        mRankView = new MainRankListViewHolder(mContext,mContentLay) ;
        mRankView.addToParent() ;
        mRankView.loadData() ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mRankView != null){
            mRankView.releaseView() ;
        }
    }
}
