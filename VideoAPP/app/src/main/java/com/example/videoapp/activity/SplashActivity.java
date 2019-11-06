package com.example.videoapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.example.videoapp.R;
/*
* 闪屏页面*/
public class SplashActivity extends Activity {

    private Handler  mHandler= new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startHomeActivity();
            }
        },2000);
    }

    private boolean isStartMain = false;
    private void startHomeActivity() {
        if(!isStartMain){
            isStartMain = true;
            Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startHomeActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //设置activity 启动模式为 singleTask 移除消息
        mHandler.removeCallbacksAndMessages(null);
    }
}
