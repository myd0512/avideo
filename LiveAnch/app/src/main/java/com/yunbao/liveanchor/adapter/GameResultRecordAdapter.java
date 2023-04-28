package com.yunbao.liveanchor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.liveanchor.R;
import com.yunbao.liveanchor.bean.AwardBean;
import com.yunbao.liveanchor.bean.GameResultRecord;
import com.yunbao.liveanchor.util.DpUtil;
import com.yunbao.liveanchor.util.ScreenDimenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏开奖记录
 */
public class GameResultRecordAdapter extends RecyclerView.Adapter<GameResultRecordAdapter.Vh>  {
    private Context mContext;
    private List<GameResultRecord> mList;
    private String mGameId ;
    private String mGameName ;

    public GameResultRecordAdapter(Context context, List<GameResultRecord> list, String type, String gameName) {
        this.mContext = context;
        this.mList = list;
        this.mGameId = type;
        this.mGameName = gameName;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(LayoutInflater.from(mContext).inflate(R.layout.adapter_game_result_record, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        holder.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mPeriodTv;
        RecyclerView mResultRv ;

        int resultRvWidth ;

        public Vh(View itemView) {
            super(itemView);
            mName =  itemView.findViewById(R.id.adapter_game_result_name_tv);
            mPeriodTv =  itemView.findViewById(R.id.adapter_game_result_period_tv);
            mResultRv =  itemView.findViewById(R.id.adapter_game_result_rv);

            mResultRv.setNestedScrollingEnabled(false);
            mResultRv.setFocusable(false);

            //比例 2：3：4
            resultRvWidth = (int) ((ScreenDimenUtil.getInstance().getScreenWdith() - (DpUtil.dp2px(10) * 2)) * 4 / 9F);
        }

        void setData(GameResultRecord bean) {
            mName.setText(mGameName) ;
            mPeriodTv.setText("第" + bean.getId() + "期") ;

            String resultStr = bean.getResult() ;
            String[] result = resultStr.split(",") ;

            int type = 1 ;
            List<AwardBean> list = new ArrayList<>();

            if("1".equals(mGameId)){//快三
                type = 1 ;

                int imgeUrl = 0;
                for (String str : result) {
                    if (str.equals("1")) {
                        imgeUrl = R.mipmap.icon_game_dice_one;
                    } else if (str.equals("2")) {
                        imgeUrl = R.mipmap.icon_game_dice_two;
                    } else if (str.equals("3")) {
                        imgeUrl = R.mipmap.icon_game_dice_three;
                    } else if (str.equals("4")) {
                        imgeUrl = R.mipmap.icon_game_dice_four;
                    } else if (str.equals("5")) {
                        imgeUrl = R.mipmap.icon_game_dice_five;
                    } else if (str.equals("6")) {
                        imgeUrl = R.mipmap.icon_game_dice_six;
                    }
                    list.add(new AwardBean(imgeUrl));
                }
            }else if("2".equals(mGameId) || "4".equals(mGameId) || "6".equals(mGameId)){//11选5  时时彩  快乐十分
                type = 2 ;

                for (String str : result) {
                    list.add(new AwardBean(str));
                }
            }else if("3".equals(mGameId)){//赛车
                type = 4 ;

                for (String str : result){
                    list.add(new AwardBean(str));
                }
            }else if("5".equals(mGameId)){//六合彩
                type = 3 ;

                for (int i = 0; i < result.length; i++) {
                    if (i == result.length - 1) {
                        list.add(new AwardBean("",R.mipmap.ic_game_point_add_white));
                    }

                    String number = result[i] ;
                    list.add(new AwardBean(number,AwardBean.getItemBackgroundByName(number))) ;
                }
            }else if("7".equals(mGameId)){//时时彩
                type = 1 ;

                int imgeUrl = 0;
                for (String str : result) {
                    if (str.equals("1")) {
                        imgeUrl = R.mipmap.cqxync01;
                    } else if (str.equals("2")) {
                        imgeUrl = R.mipmap.cqxync02;
                    } else if (str.equals("3")) {
                        imgeUrl = R.mipmap.cqxync03;
                    } else if (str.equals("4")) {
                        imgeUrl = R.mipmap.cqxync04;
                    } else if (str.equals("5")) {
                        imgeUrl = R.mipmap.cqxync05;
                    } else if (str.equals("6")) {
                        imgeUrl = R.mipmap.cqxync06;
                    } else if(str.equals("7")){
                        imgeUrl = R.mipmap.cqxync07;
                    }else if (str.equals("8")) {
                        imgeUrl = R.mipmap.cqxync08;
                    } else if (str.equals("9")) {
                        imgeUrl = R.mipmap.cqxync09;
                    } else if (str.equals("10")) {
                        imgeUrl = R.mipmap.cqxync10;
                    } else if (str.equals("11")) {
                        imgeUrl = R.mipmap.cqxync11;
                    } else if (str.equals("12")) {
                        imgeUrl = R.mipmap.cqxync12;
                    } else if(str.equals("13")){
                        imgeUrl = R.mipmap.cqxync13;
                    }else if (str.equals("14")) {
                        imgeUrl = R.mipmap.cqxync14;
                    } else if (str.equals("15")) {
                        imgeUrl = R.mipmap.cqxync15;
                    } else if (str.equals("16")) {
                        imgeUrl = R.mipmap.cqxync16;
                    } else if (str.equals("17")) {
                        imgeUrl = R.mipmap.cqxync17;
                    } else if (str.equals("18")) {
                        imgeUrl = R.mipmap.cqxync18;
                    } else if(str.equals("19")){
                        imgeUrl = R.mipmap.cqxync19;
                    }else if (str.equals("20")){
                        imgeUrl = R.mipmap.cqxync20;
                    }
                    list.add(new AwardBean(imgeUrl));
                }
            }

            mResultRv.setHasFixedSize(true);
            mResultRv.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false));
            LastAwardAdapter adapter = new LastAwardAdapter(mContext, list, type,resultRvWidth);
            mResultRv.setAdapter(adapter);
        }
    }
}
