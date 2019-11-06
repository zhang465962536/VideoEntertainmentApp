package com.example.videoapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.videoapp.R;
import com.example.videoapp.activity.BmobUser.LoginActivity;
import com.example.videoapp.base.BasePager;
import com.example.videoapp.base.MyFragment;
import com.example.videoapp.pager.HomeFragment;
import com.example.videoapp.pager.NewsFragment;
import com.example.videoapp.pager.audioPager;
import com.example.videoapp.pager.hotPager;
import com.example.videoapp.pager.netVideoPager;
import com.example.videoapp.pager.videoPager;

import java.util.ArrayList;
/*主页activity 存放4个 fragment */
public class HomeActivity extends FragmentActivity {

    private RadioGroup rg_home;
    private ArrayList<BasePager> mBasePager;
    private int position;
    private ImageView iv_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置没有标题
        setContentView(R.layout.activity_home);

        rg_home = findViewById(R.id.rg_home);
        iv_user = findViewById(R.id.iv_user);


        mBasePager = new ArrayList<>();
        mBasePager.add(new HomeFragment(this));
        mBasePager.add(new NewsFragment(this));
        mBasePager.add(new videoPager(this));
        mBasePager.add(new audioPager(this));
        rg_home.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_home.check(R.id.rb_netvideo);

        // mBasePager.add(new hotPager(this));
        //mBasePager.add(new netVideoPager(this));


    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_netvideo:
                    position = 0;
                    break;

                case R.id.rb_nethot:
                    position = 1;
                    break;

                case R.id.rb_localvideo:
                    position = 2;
                    break;

                case R.id.rb_localmusic:
                    position = 3;
                    break;

            }
            setFragment();
        }
    }

    private void setFragment() {
        // 获取FragmentManager
        FragmentManager manager = getSupportFragmentManager();
        // 开启事务
        FragmentTransaction ft = manager.beginTransaction();
        // 替换
        ft.replace(R.id.fl_home, new MyFragment().newInstance(getBasePager()));
        // 提交
        ft.commit();
    }

    private BasePager getBasePager() {
        BasePager basePager = mBasePager.get(position);
        if (basePager != null && !basePager.isInitData) {
            basePager.isInitData = true;
            basePager.initDta();
        }
        return basePager;
    }

    private boolean isExit = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    //点击退出软件功能
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (position != 0) {
                position = 0;
                rg_home.check(R.id.rb_localvideo);
                return true;
            } else if (!isExit) {
                isExit = true;
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                //如果两秒之内 按退后退键 就可以退出 如果超出两秒 重新计时
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);

    }
}
