package com.example.videoapp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.videoapp.R;
import com.example.videoapp.Utils.LogUtil;
import com.example.videoapp.Utils.Utils;
import com.example.videoapp.domain.MediaItem;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/*自定义视频播放器  使用videoview 实现*/
public class SystemVideoPlayer extends Activity implements View.OnClickListener {


    private VideoView mVideoView;
    private Uri mUri;
    private LinearLayout llTop;
    private ImageView ivBattery;
    private TextView tvBatteryNumber;
    private TextView tvTime;
    private ImageButton btnVideoExit;
    private TextView tvName;
    private LinearLayout llBottom;
    private ImageView btnStop;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private ImageButton btnVideoNext;
    private LinearLayout llSpeaker;
    private SeekBar seekbarVoice;
    private ImageView ivSpeak;
    private TextView tvVoiceNumber;
    private TextView tvBrightnessNumber;
    private LinearLayout llBrightness;
    private SeekBar seebarBrightness;
    private RelativeLayout rl_loading;
    private LinearLayout ll_buffer;
    private TextView tv_loading_netspeed;
    private TextView tv_buffer_netspeed;


    private Utils mUtils;
    private BatteryReceiver mBatteryReceiver;
    private int electricQuantity;
    private ArrayList<MediaItem> mediaItems;
    private int position;
    //[1]定义一个手势识别器
    private GestureDetector mGestureDetector;
    //隐藏控制面板的时间
    private static final int HINDE_TIME = 3000;
    //获取声音管理者
    private AudioManager am;
    //当前音量 系统分为 0 - 15
    private int currentVolume;
    //最大音量
    private int maxVolume;
    //是否是静音
    private boolean isMute = false;
    //屏幕的宽
    private int screenWidth;
    //屏幕的高
    private int screenHeight;
    //上一次声音的位置
    private int lastVolume;
    //当前屏幕亮度
    private int currentBrightness;
    //是否是网络资源
    private boolean isNetUri = false;

    //进度更新
    private static final int PROGRESS = 0;
    //音量面板
    private static final int HIDE_VolumeModule = 1;
    //隐藏控制面板
    private static final int HIDE_MEIDACONTROLLER = 2;
    //改变音量
    private static final int UPDATE_VOLUME = 3;
    //隐藏亮度面板
    private static final int HIDE_BRIGHTNESS = 4;
    //更新亮度值
    private static final int UPDATE_BRIGHTNESS = 5;
    //显示网速
    private static final int SHOW_NETSPEED = 6;

    //上一秒的位置
    private int prePosition = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    //不断得到当前播放进度
                    int currentPosition = mVideoView.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);
                    tvCurrentTime.setText(mUtils.stringForTime(currentPosition));

                    //实时更新系统时间
                    tvTime.setText(getSystemTime());

                    //更新电池电量
                    tvBatteryNumber.setText(electricQuantity + "");

                    //设置缓冲效果
                    if (isNetUri) {
                        //如果是网络视频 有缓存条  0 - 100
                        int buffer = mVideoView.getBufferPercentage();
                        int totalBuffer = seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;

                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        //如果是本地视频 缓存条设置为0
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    //监听卡顿现象的第二种办法

                    //当前的进度 - 上一秒的进度  判断是否卡顿 网络部好
                    int buffer = currentPosition - prePosition;
                    //必须是播放状态 才能判断是否有卡顿现象
                    if(mVideoView.isPlaying()){
                        if(buffer < 500){
                            //如果buffer 相差500ms 说明卡顿
                            ll_buffer.setVisibility(View.VISIBLE);
                        }else {
                            // 不出现卡顿状态 隐藏缓存区
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }

                    //更新上一秒的位置
                    prePosition = currentPosition;


                    //每一秒 更新进度条一次
                    mHandler.removeMessages(PROGRESS);
                    mHandler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;

                case HIDE_MEIDACONTROLLER:
                    hideMediaController();
                    break;

                case HIDE_VolumeModule:

                    hideVolumeModule();

                    break;

                case UPDATE_VOLUME:
                    int updateVolume = (int) (currentVolume * (100.0 / 60.0));
                    if (updateVolume < 0) {
                        updateVolume = 0;
                    } else if (updateVolume > 100) {
                        updateVolume = 100;
                    }
                    tvVoiceNumber.setText(updateVolume + "%");
                    break;

                case HIDE_BRIGHTNESS:
                    hideBrightnessModuele();
                    break;

                case UPDATE_BRIGHTNESS:
                    tvBrightnessNumber.setText(currentBrightness + "%");
                    break;

                case SHOW_NETSPEED:
                    String netSpeed = mUtils.getNetSpeed(SystemVideoPlayer.this);
                    tv_buffer_netspeed.setText("缓存中...."+netSpeed);
                    tv_loading_netspeed.setText("正在玩命加载中..."+netSpeed);

                    mHandler.sendEmptyMessageDelayed(SHOW_NETSPEED,1000);
                    break;
            }
        }
    };


    /**
     * 获取系统时间
     *
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("onCreate------");

        initData();


        findViews();

        getData();
        setData();
        setListenr();


        //视频控制面板
        // mVideoView.setMediaController(new MediaController(this));
    }

    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            mVideoView.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            //判断是网络视频 还是 本地视频
            isNetUri = mUtils.isNetUri(mediaItem.getData());
        } else if (mUri != null) {
            mVideoView.setVideoURI(mUri);
            tvName.setText(mUri.toString());
            //判断是网络视频 还是 本地视频
            isNetUri = mUtils.isNetUri(mUri.toString());
        }

        setButtonState();

        //在看视频的时候 不会锁屏
        mVideoView.setKeepScreenOn(true);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    private void getData() {
        //得到一个地址 来自于文件夹浏览器  浏览器 相册
        mUri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        //列表中的位置
        position = getIntent().getIntExtra("position", 0);
    }

    private void setListenr() {
        //当底层解码器准备好的时候 回调这个方法
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                //监听 进度条拖动  如果拖动成功就执行这个回调 可以统计用户拖动多少次
                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        Toast.makeText(SystemVideoPlayer.this,"拖动完成",Toast.LENGTH_SHORT).show();
                    }
                });

                //【1】得到视频的总时长 并且和 seekBar.setMax关联
                int duration = mp.getDuration();
                seekbarVideo.setMax(duration);
                //设置总时长
                tvDuration.setText(mUtils.stringForTime(duration));

                //【2】发送给handler 在主线程更新UI
                mHandler.sendEmptyMessage(PROGRESS);

                //开始播放
                mVideoView.start();

                //刚开始进入 默认隐藏控制面板
                hideMediaController();

                //一开始隐藏好加载页面
                rl_loading.setVisibility(View.GONE);
            }
        });

        //当播放出错的时候 回调这个方法
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                //Toast.makeText(getApplicationContext(), "视频播放失败", Toast.LENGTH_SHORT).show();
                //播放出错的情况
                //【1】播放不支持的视频格式 ----》 跳转到万能播放器继续播放
                startVitamioPlayer();

                //【2】播放网络视频的过程中 网络中断 ————》需要重新播放

                //【3】视频文件中间 部分有缺损 --》 把下载模块解决掉

                return true;  //返回true  就自己初始错误监听 如果返回false 就默认显示一个视频出错的 对话框
            }
        });

        //当播放完视频 之后 回调这个方法
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Toast.makeText(getApplicationContext(), "播放完成", Toast.LENGTH_SHORT).show();
                //finish();
                setButtonState();
            }
        });

        //给seekBar 设置拖动监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChanageListener());
        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChanageListener());
        seebarBrightness.setOnSeekBarChangeListener(new BrightnessOnSeekBarChanageListener());

        //设置监听卡   这是一种办法 另种办法 在mHanlder种
       // mVideoView.setOnInfoListener(new MyOnInfoListener());
    }

    /*
        跳转到万能播放器
     */
    private void startVitamioPlayer() {
    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://开始卡顿了，拖动卡顿了
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;

                case MediaPlayer.MEDIA_INFO_BUFFERING_END://卡结束了，拖动卡结束
                    ll_buffer.setVisibility(View.GONE);
                    break;

            }
            return true;
        }
    }

    class VoiceOnSeekBarChanageListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 当进度更新的时候 回调这个方法
         *
         * @param seekBar
         * @param progress 当前的进度
         * @param fromUser 是否是由用户引起的
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                //
                updateVolumeProgress(progress);
            }
        }

        //当手触摸seekbar 的时候 回调这个方法
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(HIDE_VolumeModule);
        }

        //当手指离开seekbar 的时候 回调这个方法
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(HIDE_VolumeModule, HINDE_TIME);
        }
    }

    //根据音量传入的值 修改音量  主要是设置静音
    private void updateVolume(int volume) {
        if (isMute) {
            //setStreamVolume 第二个参数 可以使系统的音量界面是否显示  0 就是不显示  1 就是显示
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
            // currentVolume = 0;
            ivSpeak.setBackgroundResource(R.drawable.iv_video_speaker_null);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            seekbarVoice.setProgress(volume);
            currentVolume = volume;
            ivSpeak.setBackgroundResource(R.drawable.iv_video_speaker);
        }
        mHandler.sendEmptyMessage(UPDATE_VOLUME);

    }

    //可以滑动
    private void updateVolumeProgress(int volume) {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        seekbarVoice.setProgress(volume);

        currentVolume = volume;

        if (volume <= 0) {
            isMute = true;
            ivSpeak.setBackgroundResource(R.drawable.iv_video_speaker_null);
        } else {
            isMute = false;
            ivSpeak.setBackgroundResource(R.drawable.iv_video_speaker);
        }
        mHandler.sendEmptyMessage(UPDATE_VOLUME);
    }


    private void updateBrightnessProgress(int brightness) {
        seebarBrightness.setProgress(brightness);
        currentBrightness = brightness;

        mHandler.sendEmptyMessage(UPDATE_BRIGHTNESS);
    }


    class VideoOnSeekBarChanageListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当进度更新的时候 回调这个方法
         *
         * @param seekBar
         * @param progress 当前的进度
         * @param fromUser 是否是由用户引起的
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                //如果是由用户拖动
                mVideoView.seekTo(progress);
            }
        }

        /**
         * 当手触摸seekbar 的时候 回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(HIDE_VolumeModule);
        }

        /**
         * 当手指离开seekbar 的时候 回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(HIDE_VolumeModule, HINDE_TIME);
        }
    }

    class BrightnessOnSeekBarChanageListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当进度更新的时候 回调这个方法
         *
         * @param seekBar
         * @param progress 当前的进度
         * @param fromUser 是否是由用户引起的
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                //如果是由用户拖动
                changeAppBrightness(progress);
            }
        }

        /**
         * 当手触摸seekbar 的时候 回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(HIDE_BRIGHTNESS);
        }

        /**
         * 当手指离开seekbar 的时候 回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(HIDE_BRIGHTNESS, HINDE_TIME);
        }
    }


    private void initData() {

        //实例化AudioManager
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        //获取最大音量
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);


        //获取屏幕的宽高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        mUtils = new Utils();
        //注册电量监听广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mBatteryReceiver = new BatteryReceiver();
        registerReceiver(mBatteryReceiver, intentFilter);

        //[2]实例化手势识别器
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                //TODO 改变当前屏幕的背光亮度
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness = 0;
                getWindow().setAttributes(lp);
            }

            //双击回调的方法
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                startAndPause();
                return super.onDoubleTap(e);

            }

            //单击回调的方法
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                if (isShowMediaController) {
                    //如果是显示的 就让他隐藏
                    hideMediaController();
                    //mHandler.removeMessages(HIDE_MEIDACONTROLLER);

                } else {
                    //如果是隐藏的 就让它显示
                    showMediaController();
                    mHandler.sendEmptyMessageDelayed(HIDE_MEIDACONTROLLER, HINDE_TIME);
                }

                return super.onSingleTapConfirmed(e);

            }
        });

    }

    //设置当前屏幕的亮度
    public void changeAppBrightness(int brightness) {
        Window window = this.getWindow();
        seebarBrightness.setProgress(brightness);
        //currentBrightness = brightness;

        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        currentBrightness = (int) (lp.screenBrightness * 100);
        window.setAttributes(lp);
        mHandler.sendEmptyMessage(UPDATE_BRIGHTNESS);
    }

    //默认是隐藏控制面板的
    //true 显示  false 隐藏
    private boolean isShowMediaController = false;

    //隐藏控制面板
    private void hideMediaController() {
        llBottom.setVisibility(View.INVISIBLE);
        llTop.setVisibility(View.INVISIBLE);
        isShowMediaController = false;
    }

    //显示控制面板
    private void showMediaController() {
        llBottom.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }

    //默认是隐藏音量面板的
    private boolean isShowVolumeModule = false;

    //隐藏音量面板
    private void hideVolumeModule() {
        llSpeaker.setVisibility(View.INVISIBLE);
        isShowVolumeModule = false;
    }

    //显示音量模块
    private void showVolumeModule() {
        llSpeaker.setVisibility(View.VISIBLE);
        isShowVolumeModule = true;
    }

    //默认是隐藏亮度面板的
    private boolean isShowBrightnessModuele = false;

    //隐藏亮度面板
    private void hideBrightnessModuele() {
        llBrightness.setVisibility(View.INVISIBLE);
        isShowBrightnessModuele = false;
    }

    //显示亮度模块
    private void showBrightnessModuele() {
        llBrightness.setVisibility(View.VISIBLE);
        isShowBrightnessModuele = true;
    }


    private float startY;
    private float startX;
    private float touchRange;
    private int mVol; //记录当前的音量
    private int mBrightness = 0; //记录当前亮度

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //[3] 把事件给手势识别器 解析
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            //【1】按下的时候 记录新的初始值
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                //此时的最小值是  screenHeight 因为是默认横屏
                touchRange = Math.min(screenWidth, screenHeight);
                if (startX <= screenWidth / 2) {
                    //如果x起始点在屏幕左侧 操作音量
                    mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                }



                break;


            case MotionEvent.ACTION_MOVE:
                //【2】来到新的左边
                float endY = event.getY();
                float endX = event.getX();
                //【3】计算偏移量
                float distanceY = startY - endY;
                if (startX <= screenWidth / 2) {
                    //屏幕滑动的距离： 总距离 = 改变的声音 / 最大音量
                    //要改变的声音 = (滑动的距离 / 总距离)*最大音量
                    float changVolume = (distanceY / touchRange) * maxVolume;
                    //最终的声音= 原来的音量 + 改变的声音；
                    //下面公式相当于 0 < mVol + changVolume < volume <maxVolume
                    float volume = Math.min(Math.max(mVol + changVolume, 0), maxVolume);

                    if (changVolume != 0) {
                        updateVolumeProgress((int) volume);
                        showVolumeModule();
                        mHandler.removeMessages(HIDE_VolumeModule);
                        mHandler.sendEmptyMessage(UPDATE_VOLUME);
                    }
                } else {

                    float changeBrightness = (distanceY / touchRange) * 255;
                    float brightness = Math.min(Math.max(mBrightness + changeBrightness, 0), 255);
                    changeAppBrightness((int) brightness);
                    showBrightnessModuele();

                    mHandler.removeMessages(HIDE_BRIGHTNESS);
                    mHandler.sendEmptyMessage(UPDATE_VOLUME);
                    LogUtil.e(getScreenBrightness(SystemVideoPlayer.this) + "系统亮度");
                }


                break;

            case MotionEvent.ACTION_UP:
                if (startX <= touchRange / 2) {
                    mHandler.sendEmptyMessageDelayed(HIDE_VolumeModule, HINDE_TIME);
                } else {
                    mHandler.sendEmptyMessageDelayed(HIDE_BRIGHTNESS, HINDE_TIME);
                    mBrightness = currentBrightness;
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取电量值 0 - 100
            int level = intent.getIntExtra("level", 0);
            electricQuantity = level;
            //广播接收者在主线程运行的
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }


    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        mVideoView = findViewById(R.id.videoview);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvBatteryNumber = (TextView) findViewById(R.id.tv_battery_number);
        tvTime = (TextView) findViewById(R.id.tv_time);
        btnVideoExit = (ImageButton) findViewById(R.id.btn_video_exit);
        tvName = (TextView) findViewById(R.id.tv_name);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        btnStop = findViewById(R.id.btn_stop);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnVideoNext = (ImageButton) findViewById(R.id.btn_video_next);
        llSpeaker = findViewById(R.id.ll_speacker);
        seekbarVoice = findViewById(R.id.seekbar_voice);
        ivSpeak = findViewById(R.id.iv_video_speaker);
        tvVoiceNumber = findViewById(R.id.tv_voice_number);
        llBrightness = findViewById(R.id.ll_brightness);
        seebarBrightness = findViewById(R.id.seekbar_brightness);
        tvBrightnessNumber = findViewById(R.id.tv_brightness_number);
        rl_loading = findViewById(R.id.rl_loading);
        ll_buffer = findViewById(R.id.ll_buffer);
        tv_buffer_netspeed = findViewById(R.id.tv_buffer_netspeed);
        tv_loading_netspeed = findViewById(R.id.tv_loading_netspeed);

        btnVideoExit.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        ivSpeak.setOnClickListener(this);

        //设置声量 seekBar 的最大值
        seekbarVoice.setMax(maxVolume);
        //默认值是当前声量
        seekbarVoice.setProgress(currentVolume);
        //设置亮度 seekBar 的最大值
        seebarBrightness.setMax(255);
        //默认设置当前显示屏的亮度
        seebarBrightness.setProgress(getScreenBrightness(SystemVideoPlayer.this));
        tvBrightnessNumber.setText((int) (getScreenBrightness(SystemVideoPlayer.this) * (100.0 / 180.0)) + "%");

        mHandler.sendEmptyMessage(SHOW_NETSPEED);
    }

    //获取当前屏幕亮度
    public static int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    @Override
    public void onClick(View v) {
        if (v == btnVideoExit) {
            finish();
        } else if (v == btnStop) {
            startAndPause();
        } else if (v == btnVideoNext) {
            setPlayNext();
        } else if (v == ivSpeak) {
            isMute = !isMute;
            updateVolume(currentVolume);
            //点击音量按钮的时候 隐藏音量模块的时间 重新计算时间
            mHandler.removeMessages(HIDE_VolumeModule);
            mHandler.sendEmptyMessageDelayed(HIDE_VolumeModule, HINDE_TIME);
        }

        //点击按钮的时候 隐藏控制面板时间 重新计算时间
        mHandler.removeMessages(HIDE_MEIDACONTROLLER);
        mHandler.sendEmptyMessageDelayed(HIDE_MEIDACONTROLLER, HINDE_TIME);
    }

    private void startAndPause() {
        if (mVideoView.isPlaying()) {
            //如果正在播放 切换到暂停状态
            mVideoView.pause();
            btnStop.setBackgroundResource(R.drawable.btn_pause_pressed);
        } else {
            //如果是暂停状态 切换到播放
            mVideoView.start();
            btnStop.setBackgroundResource(R.drawable.btn_pause_normal);
        }
    }

    private void setPlayNext() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //如果播放列表不为空 就播放下一个
            position++;
            if (position < mediaItems.size()) {

                MediaItem mediaItem = mediaItems.get(position);
                //设置播放地址
                mVideoView.setVideoPath(mediaItem.getData());
                tvName.setText(mediaItem.getName());
                isNetUri = mUtils.isNetUri(mediaItem.getData());

                setButtonState();

                if (position == mediaItems.size() - 1) {
                    Toast.makeText(getApplicationContext(), "已经是最后一个视频了", Toast.LENGTH_SHORT).show();
                }
                rl_loading.setVisibility(View.INVISIBLE);
            }
        } else if (mUri != null) {
            //播放完 此文件夹 退出播放器
            finish();
        }
    }

    /*
    设置 下一部 视频 的状态
     */
    private void setButtonState() {
        //播放列表
        if (mediaItems != null && mediaItems.size() > 0) {
            if (position == 0) {
                //选择视频播放在第一个  可以有下一部视频
                btnVideoNext.setVisibility(View.VISIBLE);
            } else if (position == mediaItems.size() - 1) {
                //如果是最后一个视频了 没有下一部了 隐藏按键
                btnVideoNext.setVisibility(View.INVISIBLE);
            } else {
                //其他位置的 都可以点击
                btnVideoNext.setVisibility(View.VISIBLE);
            }

        } else if (mUri != null) {

            btnVideoNext.setVisibility(View.INVISIBLE);

        } else {
            Toast.makeText(getApplicationContext(), "没有播放地址", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //如果按下了音量减小键
            //制定音量边界
            if (currentVolume > 0) {
                currentVolume--;
            } else {
                currentVolume = 0;
            }

            updateVolumeProgress(currentVolume);
            showVolumeModule();
            mHandler.removeMessages(HIDE_VolumeModule);
            mHandler.sendEmptyMessageDelayed(HIDE_VolumeModule, HINDE_TIME);
            mHandler.sendEmptyMessage(UPDATE_VOLUME);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //如果按下了音量增大键
            //制定音量边界
            if (currentVolume < 100) {
                currentVolume++;
            } else {
                currentVolume = 100;
            }
            updateVolumeProgress(currentVolume);
            showVolumeModule();
            mHandler.removeMessages(HIDE_VolumeModule);
            mHandler.sendEmptyMessageDelayed(HIDE_VolumeModule, HINDE_TIME);
            mHandler.sendEmptyMessage(UPDATE_VOLUME);
            return true; //返回true 就不会显示 系统自带的音量界面
        }


        return super.onKeyDown(keyCode, event);

    }

    //完全覆盖 占完整个屏幕 Activity A -》 B 跳转 ->A
    //(A->B)A.onPause -> B.onCreate-> B.onStart-> B.onResume -> A.onStop->(B->A) B.onPause -> A.onRestart->A.onStart->A.onResume
    // --> B.onStop - > B.onDestory

    //跳转页面不完全覆盖
    //(A->B)A.onPause -> B.onCreate - > B.onStart -> B.onResume
    //(B->A)B.onPause -> A.onResume -> B.onstop - > B.onDestory

    //Actvity 横竖屏切换 先销毁 再创建
    // onPause--> onStop--> onDestory-->oncreate-->onstart-->onResume

    //Activity 创建到显示 执行方法 oncreate-->onstart-->onResume

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart------");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume------");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("onRestart------");
    }

    //Activity销毁过程 onPause--> onStop--> onDestory
    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause------");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop------");
    }

    @Override
    protected void onDestroy() {
        //解绑广播接收者
        if (mBatteryReceiver != null) {
            unregisterReceiver(mBatteryReceiver);
            mBatteryReceiver = null;
        }
        super.onDestroy();
        LogUtil.e("onDestroy------");
    }
}
