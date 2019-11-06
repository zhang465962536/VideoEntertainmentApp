package com.example.videoapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.videoapp.IMediaService;
import com.example.videoapp.R;
import com.example.videoapp.activity.AudioPlayer;
import com.example.videoapp.domain.MediaItem;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
/* 播放音频的时候 音频播放器的 service */
public class MediaService extends Service {

    //当播放音乐音频成功的时候动作
    public static final String OPENAUDIO = "com.mobileplayer_OPENAUDIO";
    private IMediaService.Stub stub = new IMediaService.Stub() {
        //定义服务实例
        MediaService mService = MediaService.this;
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            mService.openAudio(position);
        }


        @Override
        public void start() throws RemoteException {
            mService.start();

        }

        @Override
        public void pause() throws RemoteException {
            mService.pause();

        }

        @Override
        public void next() throws RemoteException {
            mService.next();
        }

        @Override
        public void pre() throws RemoteException {
        mService.pre();
        }

        @Override
        public int getPlaymode() throws RemoteException {
            return mService.getPlayMode();
        }

        @Override
        public void setPlaymode(int playmode) throws RemoteException {
            mService.setPalyMode(playmode);
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return mService.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return mService.getDuration();
        }

        @Override
        public String getName() throws RemoteException {
            return mService.getName();
        }

        @Override
        public String getArtist() throws RemoteException {
            return mService.getArtist();
        }

        @Override
        public void seekTo(int seekto) throws RemoteException {
            mService.seekTo(seekto);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mService.isPlaying();
        }

        @Override
        public void notifyChange(String action) throws RemoteException {
            mService.notifyChanage(action);
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return mService.getAudioPath();
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mService.getAudioSessionId();
        }
    };




    //音频列表
    private ArrayList<MediaItem> mediaItems;
    //当前列表播放的位置
    private int position;
    //一首歌曲的数据内容
    private MediaItem mediaItem;
    //播放器
    private MediaPlayer mediaplayer;


     //顺序播放-默认的播放
    public static final int REPEAT_ORDER = 1;

     //单曲循环
    public static final int REPEAT_SINGLE = 2;

    // 全部循环
    public static final int REPEAT_ALL = 3;

    //设置默认播放模式
    private int playmode = REPEAT_ORDER;

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //服务一创建 第一次实例化之后 以后都不需要实例化了
        getData();

    }

    private void getData() {


        new Thread(){
            @Override
            public void run() {
                super.run();

                mediaItems = new ArrayList<MediaItem>();
                ContentResolver contentResolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objects = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//歌曲在sk卡显示的名称
                        MediaStore.Audio.Media.DURATION,//歌曲的长度
                        MediaStore.Audio.Media.SIZE,//歌曲的大小
                        MediaStore.Audio.Media.DATA,//歌曲的绝对位置
                        MediaStore.Audio.Media.ARTIST, //歌曲的作者
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


                    cursor.close();;
                }



            }
        }.start();
    }

    /**
     * 根据位置打开音乐
     * @param position
     */
    private void openAudio(int position){

        this.position = position;

        if(mediaItems != null && mediaItems.size()> 0){

            mediaItem = mediaItems.get(position);

            //把上一次 或者 正在播放的 给释放出来
            if(mediaplayer != null){
                mediaplayer.reset();
                mediaplayer.release();
                mediaplayer = null;
            }

            try {
                //重新创建播放器
                mediaplayer = new MediaPlayer();
                //设置准备好的监听
                mediaplayer.setOnPreparedListener(new MyOnPrearedListener());
                //设置错误监听
                mediaplayer.setOnErrorListener(new MyOnErrorListener());
                //设置 播放完后的监听
                mediaplayer.setOnCompletionListener(new MyOnCompletionListener());
                //设置播放地址
                mediaplayer.setDataSource(mediaItem.getData());
                //本地资源和网络资源都可以准备异步
                mediaplayer.prepareAsync();

                if (playmode == MediaService.REPEAT_SINGLE) {
                    mediaplayer.setLooping(true);
                } else {
                    mediaplayer.setLooping(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            //数据还没有加载好
            Toast.makeText(MediaService.this,"数据还没有加载好",Toast.LENGTH_SHORT).show();
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return false;
        }
    }

    class MyOnPrearedListener implements MediaPlayer.OnPreparedListener{

        @Override
        public void onPrepared(MediaPlayer mp) {
            start();

            notifyChanage(OPENAUDIO);
        }
    }

    //根据不同的动作发广播
    private void notifyChanage(String action) {
       Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
        //【3】EventBus发消息
        //EventBus.getDefault().post(new MediaItem());
    }

    //通知服务管理

    private NotificationManager manager;

    //播放音乐
    private void start(){
        mediaplayer.start();
        mediaplayer.start();
        //弹出通知-点击的时候进入音乐播放器页面
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, AudioPlayer.class);
        intent.putExtra("Notification",true);//从状态栏进入音乐播放页面
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("321音乐")
                .setContentText("正在播放:"+getName())
                .setContentIntent(pi)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;//点击不消失
        manager.notify(1,notification);
    }

    //暂停音乐
    private void pause(){
        mediaplayer.pause();
        //通知消失掉
        manager.cancel(1);
    }

    //下一首
    private void next(){

        setNextPosition();
        openNextPosition();
    }

    private void openNextPosition() {
        mediaplayer.setLooping(false);

        if (playmode == MediaService.REPEAT_ORDER) {

            if (position < mediaItems.size()) {
                openAudio(position);
            }else{
                position = mediaItems.size()-1;
            }

        } else if (playmode == MediaService.REPEAT_SINGLE) {

            if (position < mediaItems.size()) {
                openAudio(position);


            }

        } else if (playmode == MediaService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if (position < mediaItems.size()) {
                openAudio(position);
            }else{
                position = mediaItems.size()-1;
            }
        }

    }

    private void setNextPosition() {
        int playmode = getPlayMode();

        if (playmode == MediaService.REPEAT_ORDER) {
            position++;

        } else if (playmode == MediaService.REPEAT_SINGLE) {
            position++;
        } else if (playmode == MediaService.REPEAT_ALL) {
            position++;
            if (position > mediaItems.size() - 1) {
                position = 0;
            }
        } else {
            position++;
        }

    }

    //上一首
    private void pre() {
        setPrePosition();
        openPrePosition();
    }

    private void openPrePosition() {
        int playmode = getPlayMode();

        if (playmode == MediaService.REPEAT_ORDER) {

            if (position >= 0) {
                openAudio(position);
            }else{
                position = 0;
            }

        } else if (playmode == MediaService.REPEAT_SINGLE) {
            if (position >= 0) {
                openAudio(position);

            }

        } else if (playmode == MediaService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if (position >= 0) {
                openAudio(position);
            }else{
                position = 0;
            }
        }

    }

    private void setPrePosition() {
        int playmode = getPlayMode();

        if (playmode == MediaService.REPEAT_ORDER) {
            position--;

        } else if (playmode == MediaService.REPEAT_SINGLE) {
            position--;
        } else if (playmode == MediaService.REPEAT_ALL) {
            position--;
            if (position < 0) {
                position = mediaItems.size() - 1;
            }
        } else {
            position--;
        }

    }

    //得到播放模式
    private int getPlayMode(){

        return playmode;
    }

    //设置播放模式
    private void  setPalyMode(int palyMode){
        this.playmode = palyMode;
        if (playmode == MediaService.REPEAT_SINGLE) {
            mediaplayer.setLooping(true);
        } else {
            mediaplayer.setLooping(false);
        }

    }

    //得到当前进度
    private int getCurrentPosition(){

        return  mediaplayer.getCurrentPosition();
    }

    //得到当前的总时长
    private int getDuration(){

        return mediaplayer.getDuration();
    }

    //得到歌曲名称
    private String getName(){

        return mediaItem.getName();
    }

    //得到演唱者名称
    private String getArtist(){

        return mediaItem.getArtist();
    }

    //音频的拖动
    private void seekTo(int seekTo){
        mediaplayer.seekTo(seekTo);

    }

    //是否正在播放音乐
    private boolean isPlaying() {
        return mediaplayer.isPlaying();
    }

    //获取音频的绝对路径
    private String getAudioPath() {

        return mediaItem.getData();
    }

    private int getAudioSessionId() {
        return mediaplayer.getAudioSessionId();
    }

}
