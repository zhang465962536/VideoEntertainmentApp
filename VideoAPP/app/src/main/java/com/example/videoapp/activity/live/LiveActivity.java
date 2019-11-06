package com.example.videoapp.activity.live;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.videoapp.R;
import com.example.videoapp.base.BaseActivity;
/*
* 直播频道列表 逻辑
* */
public class LiveActivity extends BaseActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live;
    }

    @Override
    protected void initView() {
        TextView tv_title = bindViewId(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.live_title));
        mRecyclerView = bindViewId(R.id.ry_live);
        GridLayoutManager manager = new GridLayoutManager(this, 1);   //mRecyclerView只有一列
       //垂直布局排列
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new MyDecoration(this));

        LiveItemAdapter adapter = new LiveItemAdapter(this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.scrollToPosition(0);//回到第一个位置
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitle() {
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, LiveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }
}
