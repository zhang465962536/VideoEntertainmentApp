package com.example.videoapp.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.videoapp.R;
import com.example.videoapp.Utils.Utils;
import com.example.videoapp.activity.AudioPlayer;
import com.example.videoapp.adapter.AudioPagerAdapter;
import com.example.videoapp.base.BasePager;
import com.example.videoapp.domain.MediaItem;

import java.util.ArrayList;
/**音频列表界面 */
public class audioPager extends BasePager {

    private ListView lv_video_pager;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;

    private ArrayList<MediaItem> mediaItems;
    private Utils utils;



    public audioPager(Context context) {
        super(context);
        utils = new Utils();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.audio_pager,null);
        lv_video_pager = (ListView) view.findViewById(R.id.lv_video_pager);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        //设置点击事件
        lv_video_pager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaItem mediaItem = mediaItems.get(position);

                //传音频位置
                Intent intent = new Intent(mContext, AudioPlayer.class);
                intent.putExtra("position",position);  //播放列表中的某个音频
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initDta() {
        super.initDta();
        System.out.println("本地音乐数据初始化了。。。");
        getData();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //主线程
            if(mediaItems != null && mediaItems.size() >0){
                tv_nomedia.setVisibility(View.GONE);
                pb_loading.setVisibility(View.GONE);

                //设置适配器
                lv_video_pager.setAdapter(new AudioPagerAdapter(mContext,mediaItems));
            }else{
                tv_nomedia.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
            }

        }
    };

   /* class VideoPagerAdapter extends BaseAdapter {  抽成一个类

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
                convertView = View.inflate(mContext,R.layout.item_audio_pager,null);
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

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
    }*/

    private void getData() {


        new Thread(){
            @Override
            public void run() {
                super.run();

                mediaItems = new ArrayList<MediaItem>();
                ContentResolver contentResolver = mContext.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objects = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//歌曲在sk卡显示的名称
                        MediaStore.Audio.Media.DURATION,//歌曲的长度
                        MediaStore.Audio.Media.SIZE,//歌曲的大小
                        MediaStore.Audio.Media.DATA,//歌曲的绝对位置
                        MediaStore.Audio.Media. ARTIST, //歌曲的作者
                        MediaStore.Audio.Media.ALBUM,//歌曲的专辑名
                        MediaStore.Audio.Media.TITLE //歌曲的名称
                };
                Cursor cursor =  contentResolver.query(uri, objects, null, null, null);
                if(cursor != null){
                    while (cursor.moveToNext()){

                        MediaItem mediaItem = new MediaItem();
                        String name = cursor.getString(0);
                        mediaItem.setName(name);


                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);

                        //把视频添加到列表中
                        mediaItems.add(mediaItem);
                    }


                    cursor.close();
                }


                handler.sendEmptyMessage(0);

            }
        }.start();
    }
}
