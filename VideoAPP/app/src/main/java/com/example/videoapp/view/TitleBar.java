package com.example.videoapp.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.videoapp.R;
import com.example.videoapp.activity.BmobUser.LoginActivity;
import com.example.videoapp.activity.BmobUser.PersonalCenterActivity;
import com.example.videoapp.activity.Game.GobangActivity;
import com.example.videoapp.activity.HomeActivity;
import com.example.videoapp.activity.SearchActivity;
/*自定义 头部标题条 */
public class TitleBar extends LinearLayout {

    private final Context context;
    private View search;
    private View game;
    private View iv_history;
    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        search = getChildAt(0);
        game = getChildAt(1);
        iv_history = getChildAt(2);
        MyOnClickListener myOnClickListener  = new MyOnClickListener();
        search.setOnClickListener(myOnClickListener);
        game.setOnClickListener(myOnClickListener);
        iv_history.setOnClickListener(myOnClickListener);
    }

    public  class MyOnClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_seache:
                    context.startActivity(new Intent(context, SearchActivity.class));
                    break;
                case R.id.rl_game:
                    context.startActivity(new Intent(context, GobangActivity.class));
                    break;
                case R.id.iv_user:
                    context.startActivity(new Intent(context, PersonalCenterActivity.class));
            }
        }
    }
}

