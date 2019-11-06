package com.example.videoapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.videoapp.R;
import com.example.videoapp.Utils.Utils;
import com.example.videoapp.domain.MediaItem;
import com.example.videoapp.pager.audioPager;

import java.util.ArrayList;
/*
* 音乐列表界面 适配器*/
public class AudioPagerAdapter extends BaseAdapter {

    private ArrayList<MediaItem> mediaItems;
    private Context mContext;
    private Utils mUtils;
    public AudioPagerAdapter(Context context, ArrayList<MediaItem> list){
        mediaItems = list;
        mContext = context;
        mUtils = new Utils();
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;

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
        if(convertView ==null){
            convertView = View.inflate(mContext, R.layout.item_audio_pager,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MediaItem mediaItem = mediaItems.get(position);
        viewHolder.tv_name.setText(mediaItem.getName());
        viewHolder.iv_icon.setImageResource(R.drawable.music_default_bg);
        return convertView;
    }

}
