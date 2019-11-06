package com.example.videoapp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videoapp.IMediaService;
import com.example.videoapp.R;
import com.example.videoapp.Utils.LyricUtils;
import com.example.videoapp.Utils.Utils;
import com.example.videoapp.domain.Lyric;
import com.example.videoapp.domain.MediaItem;
import com.example.videoapp.service.MediaService;
import com.example.videoapp.view.BaseVisualizerView;
import com.example.videoapp.view.LyricShowView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
/*
* 音乐播放器逻辑*/
public class AudioPlayer extends Activity implements View.OnClickListener {



    private ImageView iv_icon;
    private TextView tvName;
    private TextView tvArtist;
    private TextView tvTimeStart;
    private SeekBar seekbarAudio;
    private TextView tvTimeEnd;
    private ImageView btnAudioPlaymode;
    private ImageView btnAudioPre;
    private ImageView btnAudioStartPause;
    private ImageView btnAudioNext;
    private ImageView btnLyrc;
    private RelativeLayout rl_top;
    private LinearLayout ll_bottom;
    private LyricShowView lyric_showview;
    private ImageView iv_audio_back;

    //音频的播放位置
    private int position;
    //MediaService的代理类
    private IMediaService mIMediaService;

    private MyReceiver mReceiver;

    private Utils mUtils;

    //进度更新
    private static final int PROGRESS = 0;

    //显示歌词 让歌词缓慢向上滚动
    private static final int SHOW_LYRIC = 1;

    /**
     * 判断put传值 从哪里来
     * ture 从状态栏 点击进入
     * flase 从音乐列表点击进入
     */
    private boolean notification;

    private ServiceConnection con = new ServiceConnection() {
        //当Activity和Service 连接成功的时候 回调这个方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMediaService = IMediaService.Stub.asInterface(service);


            try {
                if (!notification) {
                    //如果是从列表点击进入 音乐播放界面 才需要重新打开音乐
                    //操作服务
                    mIMediaService.openAudio(position);
                } else {
                    //如果从状态栏点击进入  重新获取数据 要服务发广播
                    mIMediaService.notifyChange(MediaService.OPENAUDIO);
                }


            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        //当Activity和Service 断开连接的时候 回调这个方法
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIMediaService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        findViews();

        getData();

        bindAndStartService();
    }

    private void initData() {

        mUtils = new Utils();
        //注册广播
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //监听打开音乐成功的动作
        intentFilter.addAction(MediaService.OPENAUDIO);
        registerReceiver(mReceiver, intentFilter);

        //使用EventBus 开源框架 注册广播
        //【1】注册EventBus
        //EventBus.getDefault().register(AudioPlayer.this);
    }

    //【2】在该类中使用  onEventMainThread
    public void onEventMainThread(MediaItem mediaItem){
        //获取音频的 名称 和演唱者的信息   在主线程运行
        //获取当前线程名称 判断是否是主线程
        //Thread.currentThread().getName();
        setViewData();

        try {
            seekbarAudio.setMax(mIMediaService.getDuration());

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //发送消息 实时更新进度时间
        mHandler.sendEmptyMessage(PROGRESS);

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //显示歌词 缓慢向上移动
                case SHOW_LYRIC:

                    try {
                        //得到当前的进度
                        int currentPosition = mIMediaService.getCurrentPosition();

                        lyric_showview.setShowNextLyric(currentPosition);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    removeMessages(SHOW_LYRIC);
                    sendEmptyMessage(SHOW_LYRIC);
                    break;

                case PROGRESS:
                    //更新时间

                    try {
                        //得到当前的进度
                        int currentPosition = mIMediaService.getCurrentPosition();
                        int duration = mIMediaService.getDuration();

                        tvTimeStart.setText(mUtils.stringForTime(currentPosition) + "");
                        tvTimeEnd.setText(mUtils.stringForTime(duration) + "");

                        //更新进度条
                        seekbarAudio.setProgress(currentPosition);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    mHandler.removeMessages(PROGRESS);
                    mHandler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };


    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取音频的 名称 和演唱者的信息   在主线程运行
            //获取当前线程名称 判断是否是主线程
            //Thread.currentThread().getName();
            setViewData();

            try {
                seekbarAudio.setMax(mIMediaService.getDuration());

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            //发送消息 实时更新进度时间
            mHandler.sendEmptyMessage(PROGRESS);
            showLyric();
           // setupVisualizerFxAndUi();

        }
    }

    private void showLyric() {
        LyricUtils lyricUtils = new LyricUtils();
        try {
            String path =  mIMediaService.getAudioPath();//mnt/sdcard/audio/beijing.mp3
            path = path.substring(0,path.indexOf("."));////mnt/sdcard/audio/beijing;

            File file = new File(path+".lrc");//////mnt/sdcard/audio/beijing.lrc
            if(!file.exists()){
                file = new File(path+".txt");//////mnt/sdcard/audio/beijing.txt
            }

            lyricUtils.readLyricFile(file);//传文件进入解析歌词工具类

            //把解析好的歌词传入显示歌词的控件上
            lyric_showview.setLyrics(lyricUtils.getLyrics());


        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(lyricUtils.isExistLyric()){
            mHandler.sendEmptyMessage(SHOW_LYRIC);
        }
    }

    //设置设置歌曲名称 和演唱者 和时长
    private void setViewData() {
        try {
            tvName.setText(mIMediaService.getName());
            tvArtist.setText(mIMediaService.getArtist());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, MediaService.class);
        //在清单文件 Service 定义的Action
        intent.setAction("com.mobileplayer_OPENAUDIO");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
        //避免Service被重新创建
        startService(intent);
    }

    private void getData() {
        notification = getIntent().getBooleanExtra("Notification", false);
        if (!notification)
            //从列表点击的 才需要取值
            position = getIntent().getIntExtra("position", 0);


    }

    private void findViews() {
        setContentView(R.layout.activity_audioplayer);
        iv_icon = findViewById(R.id.iv_icon);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvTimeStart = (TextView) findViewById(R.id.tv_time_start);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        tvTimeEnd = (TextView) findViewById(R.id.tv_time_end);
        btnAudioPlaymode = (ImageView) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (ImageView) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (ImageView) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (ImageView) findViewById(R.id.btn_audio_next);
        btnLyrc = (ImageView) findViewById(R.id.btn_lyrc);
        rl_top = findViewById(R.id.rl_top);
        ll_bottom = findViewById(R.id.ll_bottom);
        lyric_showview = findViewById(R.id.lyric_showview);
        iv_audio_back = findViewById(R.id.iv_audio_back);

        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnLyrc.setOnClickListener(this);


        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }



    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    mIMediaService.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_audio_start_pause:

                try {

                    if (mIMediaService.isPlaying()) {
                        //如果正在播放音乐 点击后变为暂停
                        mIMediaService.pause();
                        btnAudioStartPause.setBackgroundResource(R.drawable.iv_audio_pause);
                    } else {
                        //如果现在是暂停状态 切换到播放状态
                        mIMediaService.start();

                        btnAudioStartPause.setBackgroundResource(R.drawable.iv_audio_play);
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_audio_playmode:
                changePlaymode();
                break;

            case R.id.btn_audio_next:
                if (mIMediaService != null) {

                    try {
                        mIMediaService.next();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            case R.id.btn_audio_pre:
                if (mIMediaService != null) {

                    try {
                        mIMediaService.pre();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.iv_audio_back:
                Intent intent = new Intent(AudioPlayer.this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void changePlaymode() {
        try {
            int playmode = mIMediaService.getPlaymode();

            if (playmode == MediaService.REPEAT_ORDER) {
                playmode = MediaService.REPEAT_SINGLE;
            } else if (playmode == MediaService.REPEAT_SINGLE) {
                playmode = MediaService.REPEAT_ALL;
            } else if (playmode == MediaService.REPEAT_ALL) {
                playmode = MediaService.REPEAT_ORDER;
            } else {
                playmode = MediaService.REPEAT_ORDER;
            }
            //保持到Service的实例中
            mIMediaService.setPlaymode(playmode);

            showPlaymode();


        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlaymode() {
        try {
            int playmode = mIMediaService.getPlaymode();//从服务里面

            if (playmode == MediaService.REPEAT_ORDER) {
                Toast.makeText(AudioPlayer.this, "顺序播放", Toast.LENGTH_SHORT).show();
                btnAudioPlaymode.setBackgroundResource(R.drawable.iv_sequential_loop);
            } else if (playmode == MediaService.REPEAT_SINGLE) {
                Toast.makeText(AudioPlayer.this, "单曲播放", Toast.LENGTH_SHORT).show();
                btnAudioPlaymode.setBackgroundResource(R.drawable.iv_single_circulation);
            } else if (playmode == MediaService.REPEAT_ALL) {
                Toast.makeText(AudioPlayer.this, "全部播放", Toast.LENGTH_SHORT).show();
                btnAudioPlaymode.setBackgroundResource(R.drawable.iv_loop_playback);
            } else {
                Toast.makeText(AudioPlayer.this, "顺序播放", Toast.LENGTH_SHORT).show();
                btnAudioPlaymode.setBackgroundResource(R.drawable.iv_sequential_loop);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (con != null) {
            unbindService(con);
            con = null;
        }

        if (mReceiver != null) {
            //取消注册广播
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
