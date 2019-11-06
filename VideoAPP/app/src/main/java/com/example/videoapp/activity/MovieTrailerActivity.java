package com.example.videoapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.videoapp.R;
import com.example.videoapp.activity.live.LiveActivity;
import com.example.videoapp.base.BasePager;
import com.example.videoapp.base.MyFragment;
import com.example.videoapp.pager.netVideoPager;

import java.util.ArrayList;
/*
* 电影预告页面 逻辑*/
public class MovieTrailerActivity extends FragmentActivity implements View.OnClickListener {

    private ArrayList<BasePager> mBasePager;
    ImageView iv_title_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置没有标题
        setContentView(R.layout.activity_movie_trailer);

        iv_title_back = findViewById(R.id.iv_title_back);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("电影预告");
        mBasePager = new ArrayList<>();
        mBasePager.add(new netVideoPager(this));
        iv_title_back.setOnClickListener(this);
        setFragment();
    }

    @Override
    public void onClick(View v) {
        if(v == iv_title_back){
            finish();
        }
    }

    private void setFragment() {
        // 获取FragmentManager
        FragmentManager manager = getSupportFragmentManager();
        // 开启事务
        FragmentTransaction ft = manager.beginTransaction();
        // 替换
        ft.replace(R.id.fl_movietrailer, new MyFragment().newInstance(getBasePager()));
        // 提交
        ft.commit();
    }

    private BasePager getBasePager() {
        BasePager basePager = mBasePager.get(0);
        if(basePager != null && !basePager.isInitData){
            basePager.isInitData = true;
            basePager.initDta();
        }
        return basePager;
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, MovieTrailerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }
}
