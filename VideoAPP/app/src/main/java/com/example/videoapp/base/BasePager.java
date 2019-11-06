package com.example.videoapp.base;

import android.content.Context;
import android.view.View;

/**
 * 本地视频 本地音乐 首页 热点activity的基类
 */
public abstract class BasePager {

    //上下文 context
    public Context mContext;

    //标记是否初始化数据  屏蔽各个页面重复加载
    public boolean isInitData = false;

    //视图 view  由各个子页面实例化的结果
    public View rootView;

    public BasePager(Context context){
        mContext = context;
        rootView = initView();
        isInitData = false;
    }

    /**
     * 强制孩子实现该方法 实现特定的效果
     * @return
     */
    public abstract View initView();

    /**
     * 当孩子需要初始化数据的时候 重写该方法 用于请求数据 或者显示数据
     */
    public void initDta(){

    }
}
