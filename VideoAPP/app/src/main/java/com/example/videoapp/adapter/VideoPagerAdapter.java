package com.example.videoapp.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.videoapp.R;
import com.example.videoapp.Utils.Utils;
import com.example.videoapp.domain.MediaItem;
import com.example.videoapp.pager.videoPager;

import java.util.ArrayList;
/*
* 本地视频列表 适配器*/
public class VideoPagerAdapter extends BaseAdapter {

    private ArrayList<MediaItem> mediaItems;
    private Context mContext;
    private Utils mUtils;

    public VideoPagerAdapter(Context context, ArrayList<MediaItem> list) {
        mediaItems = list;
        mContext = context;
        mUtils = new Utils();
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_video_pager, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MediaItem mediaItem = mediaItems.get(position);
        viewHolder.tv_name.setText(mediaItem.getName());
        viewHolder.tv_size.setText(Formatter.formatFileSize(mContext, mediaItem.getSize()));
        viewHolder.tv_duration.setText(mUtils.stringForTime((int) mediaItem.getDuration()));
        //使用  Glide 显示视图缩略图
        Glide.with(mContext).load(mediaItem.getData()).into(viewHolder.iv_icon);


        return convertView;
    }
}
