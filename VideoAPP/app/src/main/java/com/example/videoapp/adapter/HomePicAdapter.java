package com.example.videoapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.videoapp.R;

import butterknife.Bind;
/*
* 首页fragment 界面适配器*/
public class HomePicAdapter extends PagerAdapter {

    /*@Bind(R.id.iv_img)
    ImageView ivImg;
    @Bind(R.id.tv_dec)
    TextView tvDec;*/
    @Bind(R.id.activity_guide)
    FrameLayout activityGuide;
    private Context mContext;


    private int[] mDes = new int[] {
            R.string.a_name,
            R.string.b_name,
            R.string.c_name,
            R.string.d_name,
            R.string.e_name,
    };

    private int[] mImg = new int[] {
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e,
    };

    public HomePicAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(mContext, R.layout.home_pic_item, null);
        TextView tvDec = view.findViewById(R.id.tv_dec);
        ImageView ivImg = view.findViewById(R.id.iv_img);
        tvDec.setText(mDes[position]);
        ivImg.setImageResource(mImg[position]);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
