package com.example.videoapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.videoapp.R;
import com.example.videoapp.base.BaseActivity;
import com.example.videoapp.domain.Channel;
import com.example.videoapp.domain.Site;
import com.example.videoapp.pager.DetailListFragment;

import java.util.HashMap;

public class DetailListActivity extends BaseActivity {

    private static final String CHANNEL_ID = "channid";
    private int mChannId;
    private ViewPager mViewPager;
    String [] mSiteNames = new String[] {"搜狐视频","乐视视频"};
    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail_list;
    }

    @Override
    protected void initView() {
        final Intent intent = getIntent();
        if(intent != null){
            mChannId = intent.getIntExtra(CHANNEL_ID,0);
        }

        Channel channel = new Channel(mChannId, this);
        String TitleName = channel.getChannelName();

        TextView tv_title = bindViewId(R.id.tv_title);
        tv_title.setText(TitleName);

        mViewPager = bindViewId(R.id.pager);
        mViewPager.setAdapter(new SitePagerAdapter(getSupportFragmentManager(),this,mChannId));

        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DetailListActivity.this, HomeActivity.class);
                startActivity(intent1);
                finish();
            }
        });

    }


    class SitePagerAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private int mChannelID;
        private HashMap<Integer,DetailListFragment> mPagerMap;

        public SitePagerAdapter(FragmentManager fm, Context context, int channelid) {
            super(fm);
            mContext = context;
            mChannelID = channelid;
            mPagerMap = new HashMap<>();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object obj =  super.instantiateItem(container, position);
            if (obj instanceof DetailListFragment) {
                mPagerMap.put(position, (DetailListFragment) obj);
            }
            return obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPagerMap.remove(position);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = DetailListFragment.newInstance(position + 1, mChannelID);
            return fragment;
        }

        @Override
        public int getCount() {
            return Site.MAX_SITE;
        }

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitle() {

    }

    public static void launchDetailListActivity(Context context, int channelId) {
        Intent intent = new Intent(context, DetailListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(CHANNEL_ID, channelId);
        context.startActivity(intent);
    }
}
