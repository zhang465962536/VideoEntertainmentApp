package com.example.videoapp.pager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.example.videoapp.R;
import com.example.videoapp.Utils.LogUtil;
import com.example.videoapp.base.BasePager;
import com.example.videoapp.base.MenuDetaiBasePager;
import com.example.videoapp.pager.tabdetailpager.TabDetailPager;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/* 新闻列表 界面*/
public class NewsFragment extends BasePager {

    @ViewInject(R.id.news_viewpager)
    private ViewPager news_viewpager;

    @ViewInject(R.id.tabPageIndicator)
    private TabPageIndicator mTabPageIndicator;


    private String[] newsTitle = {"头条","社会","国内","国际","娱乐","体育","军事","科技","财经","时尚"};


    //页签页面的集合  ----- 页面
    private ArrayList<TabDetailPager> mTabDetailPagers;

    public NewsFragment(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_news, null);
        x.view().inject(NewsFragment.this,view);

        return view;
    }

    @Override
    public void initDta() {
        super.initDta();

        LogUtil.e("新闻详情页数据已经被初始化了");

        //准备新闻详情页的数据
        mTabDetailPagers = new ArrayList<>();
        for(int i = 0; i < 10 ; i ++){
            mTabDetailPagers.add(new TabDetailPager(mContext,i));
        }

        //设置viewPager 的适配器
        news_viewpager.setAdapter(new MyNewsMenuDetailPagerAdapter());

        //viewPager 和 TabPageIndicator关联
        mTabPageIndicator.setViewPager(news_viewpager);
        //监听页面的变化 使用 TabPageIndicator 监听
    }



    class MyNewsMenuDetailPagerAdapter extends PagerAdapter {

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return newsTitle[position];
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TabDetailPager tabDetailPager = mTabDetailPagers.get(position);
            View rootView = tabDetailPager.rootView;
            System.out.println(rootView);
            tabDetailPager.initData(); //初始化数据
            container.addView(rootView);
            return rootView;
        }

        @Override
        public int getCount() {
            return mTabDetailPagers.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
           container.removeView((View) object);
        }
    }
}
