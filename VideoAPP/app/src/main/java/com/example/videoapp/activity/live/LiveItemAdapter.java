package com.example.videoapp.activity.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.videoapp.R;
import com.example.videoapp.activity.SystemVideoPlayer;
import com.example.videoapp.domain.MediaItem;

import java.util.ArrayList;
/*
直播item 适配器
* */
public class LiveItemAdapter extends RecyclerView.Adapter<LiveItemAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MediaItem> liveList;
    // 数据集
    private String[] mDataList = new String[]{
            "CCTV-1 综合", "CCTV-2 财经", "CCTV-3 综艺", "CCTV-4 中文国际(亚)", "CCTV-5 体育",
            "CCTV-6 电影", "CCTV-7 军事农业", "CCTV-8 电视剧", "CCTV-9 纪录", "CCTV-10 科教",
            "CCTV-11 戏曲", "CCTV-12 社会与法", "CCTV-13 新闻", "CCTV-14 少儿", "CCTV-15 音乐",
            "广东卫视", "北京卫视", "天津卫视", "香港卫视", "东方卫视",
    };

    private int[] mIconList = new int[]{
            R.drawable.cctv_1, R.drawable.cctv_2, R.drawable.cctv_3, R.drawable.cctv_4, R.drawable.cctv_5,
            R.drawable.cctv_6, R.drawable.cctv_7, R.drawable.cctv_8, R.drawable.cctv_9, R.drawable.cctv_10,
            R.drawable.cctv_11, R.drawable.cctv_12, R.drawable.cctv_13, R.drawable.cctv_14, R.drawable.cctv_15,
            R.drawable.guangdong_tv, R.drawable.beijing_tv, R.drawable.tianjing_tv, R.drawable.xianggang_tv, R.drawable.dongfang_tv,
    };

    private String[] mUrlList = new String[]{
            "http://cctvalih5ca.v.myalicdn.com/live/cctv1_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv2_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv3_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv4_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv5_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv6_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv7_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv8_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv9_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv10_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv11_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv12_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv13_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv14_2/index.m3u8",
            "http://cctvalih5ca.v.myalicdn.com/live/cctv15_2/index.m3u8",
            "http://cctvtxyh5ca.liveplay.myqcloud.com/wstv/guangdong_2/index.m3u8",
            "http://cctvtxyh5ca.liveplay.myqcloud.com/wstv/btv1_2/index.m3u8",
            "http://cctvtxyh5ca.liveplay.myqcloud.com/wstv/tianjin_2/index.m3u8",
            "http://zhibo.hkstv.tv/livestream/mutfysrq/playlist.m3u8",
            "http://cctvtxyh5ca.liveplay.myqcloud.com/wstv/dongfang_2/index.m3u8",
    };

   /* private String[] mUrlList = new String[]{
            "http://ivi.bupt.edu.cn/hls/cctv1.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv2.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv3.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv4.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv5.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv6.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv7.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv8.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv9.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv10.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv11.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv12.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv13.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv14.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv15.m3u8",
            "http://223.110.241.204:6610/gitv/live1/G_CCTV-1-CQ/G_CCTV-1-CQ/",
            "http://223.110.241.204:6610/gitv/live1/G_CCTV-1-CQ/G_CCTV-1-CQ/",
            "http://223.110.241.204:6610/gitv/live1/G_CCTV-1-CQ/G_CCTV-1-CQ/",
    };*/


    public LiveItemAdapter(Context context) {
        mContext = context;
    }

    // view 相关
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.live_item, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    // 数据相关
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        liveList = new ArrayList<>();

        for (int i = 0; i < mUrlList.length; i++) {
            MediaItem mediaItem = new MediaItem();
            liveList.add(mediaItem);
            mediaItem.setData(mUrlList[i]);
            mediaItem.setDesc(mDataList[i]);

        }
        holder.mIcon.setImageResource(mIconList[position]);
        holder.mTitle.setText(mDataList[position]);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PlayActivity.launch((Activity)mContext, mUrlList[position], mDataList[position]);
                //传视频列表
                Intent intent = new Intent(mContext, SystemVideoPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist", liveList);
                intent.putExtras(bundle);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIcon;
        public TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_live_icon);
            mTitle = (TextView) itemView.findViewById(R.id.tv_live_title);
        }
    }

}
