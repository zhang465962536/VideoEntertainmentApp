package com.example.videoapp;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;

import com.example.videoapp.Utils.URL;
import com.google.gson.Gson;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.xutils.x;


import java.io.File;

/*import cn.bmob.v3.Bmob;*/
import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    //在整个应用执行过程中，需要提供的变量
   public static Context context;//需要使用的上下文对象
    public static Handler handler;//需要使用的handler
    public static Thread mainThread;//提供主线程对象
    public static int mainThreadId;//提供主线程对象的id

    private static Gson mGson;
    private static OkHttpClient mOkHttpClient;

    // 在application的onCreate中初始化
    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5d631b63");
        //initUniversalImageLoader();

        context = this.getApplicationContext();
        handler = new Handler();
        mainThread = Thread.currentThread();//实例化当前Application的线程即为主线程
        mainThreadId = android.os.Process.myTid();//获取当前线程的id

        mGson = new Gson();
        mOkHttpClient = new OkHttpClient();

        //初始化 Bmob
      /* Bmob.initialize(this, URL.BMOB_KEY)*/;
    }

    public static Gson getGson(){
        return mGson;
    }

    public static OkHttpClient getHttpClient(){
        return mOkHttpClient;
    }

    public static Context getContext(){
        return context;
    }

    //获取资源 string color drawable
    public static Resources getResource(){
        return context.getResources();
    }

    //判断网络是否可用
    public static boolean isNetWorkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /*private void initUniversalImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(new ColorDrawable(Color.parseColor("#f0f0f0")))
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
//                .displayer(new FadeInBitmapDisplayer(1000)) // 设置图片渐显的时间
//                .delayBeforeLoading(300)  // 下载前的延迟时间
                .build();

        int memClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        int memCacheSize = 1024 * 1024 * memClass / 8;

        File cacheDir = new File(Environment.getExternalStorageDirectory().getPath() + "/jiecao/cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(3) // default  线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2) // default 设置当前线程的优先级
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(new UsingFreqLimitedMemoryCache(memCacheSize)) // You can pass your own memory cache implementation/
                .memoryCacheSize(memCacheSize) // 内存缓存的最大值
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .defaultDisplayImageOptions(options)
//                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }*/
}
