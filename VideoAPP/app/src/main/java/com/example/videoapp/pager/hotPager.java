package com.example.videoapp.pager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videoapp.R;
import com.example.videoapp.Utils.LogUtil;
import com.example.videoapp.Utils.SPUtils;
import com.example.videoapp.Utils.URL;
import com.example.videoapp.adapter.NetHotAdapter;
import com.example.videoapp.base.BasePager;
import com.example.videoapp.domain.NetHotBean;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class hotPager extends BasePager {


    public hotPager(Context context) {
        super(context);
    }

    private ListView lv_nethot;
    //数据集合
    private List<NetHotBean.ListBean> mList;

    @Override
    public View initView() {
        View view = View.inflate(mContext,R.layout.net_hot_pager,null);
        lv_nethot = view.findViewById(R.id.lv_nethot);
        return view;
    }

    @Override
    public void initDta() {
        super.initDta();
        System.out.println("热点数据开始初始化");
        String saveJson = SPUtils.getString(mContext,URL.NET_HOT_URL,URL.NET_HOT_URL);
       if(!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }
        getDataFromNet();
    }

    private void getDataFromNet() {

        RequestParams params = new RequestParams(URL.NET_HOT_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("onSuccess ====" + result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("onError ===" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled ===" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished ");
            }
        });
    }

    private void processData(String json) {
        NetHotBean netHotBean = parseJson(json);
        mList = netHotBean.getList();
        if(mList != null && mList.size() > 0){
            lv_nethot.setAdapter(new NetHotAdapter(mContext,mList));
        }else {
            Toast.makeText(mContext,"没有数据",Toast.LENGTH_SHORT).show();
        }

    }

    //解析数据
    private NetHotBean parseJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json,NetHotBean.class);
    }


}
