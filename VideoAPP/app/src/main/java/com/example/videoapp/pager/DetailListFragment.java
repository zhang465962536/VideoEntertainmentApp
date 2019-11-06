package com.example.videoapp.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.videoapp.R;
import com.example.videoapp.api.OnGetChannelAlbumListener;
import com.example.videoapp.api.SiteApi;
import com.example.videoapp.base.BaseFragment;
import com.example.videoapp.base.BasePager;
import com.example.videoapp.domain.Album;
import com.example.videoapp.domain.AlbumlList;
import com.example.videoapp.domain.Channel;
import com.example.videoapp.domain.ErrorInfo;
import com.example.videoapp.domain.Site;
import com.example.videoapp.view.PullLoadRecyclerView;

import java.util.Arrays;
import java.util.List;
/*网络视频列表界面*/
public class DetailListFragment extends BaseFragment {
    private static final String TAG = DetailListFragment.class.getSimpleName();
    private int mSiteId;
    private int mChannelId;
    private static final String CHANNEL_ID = "channelid";
    private static final String SITE_ID = "siteid";
    private PullLoadRecyclerView mRecyclerView;
    private TextView mEmptyView;
    private int mColumns;
    private DetailListAdapter mAdapter;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static final int REFRESH_DURATION = 1500;
    private static final int LOADMORE_DURATION = 3000;
    private int pageNo;
    private int pageSize = 30;

    public DetailListFragment() {
    }

    public static Fragment newInstance(int siteId, int channld) {
        DetailListFragment fragment = new DetailListFragment();
        //创建Bundle 将  siteId channld 传给fragment
        Bundle bundle = new Bundle();
        bundle.putInt(SITE_ID, siteId);
        bundle.putInt(CHANNEL_ID, channld);
        fragment.setArguments(bundle); //如:乐视,电视剧页面,fragment需要知道
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("详情页列表显示了");
        pageNo = 0;
        mAdapter = new DetailListAdapter();
        loadData(); //第一次加载数据
        if (mSiteId == Site.LETV) {
            //乐视tv频道 弄成两列
            mColumns = 2;
            mAdapter.setColumns(mColumns);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detailist;
    }

    @Override
    protected void initView() {
        mEmptyView = bindViewId(R.id.tv_empty);
        mEmptyView.setText(getActivity().getResources().getString(R.string.load_more_text));
        /*mRecyclerView = bindViewId(R.id.pullloadRecyclerView);
        mRecyclerView.setGridLayout(3);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());*/
    }

    class PullLoadMoreListener implements PullLoadRecyclerView.OnPullLoadMoreListener {

        @Override
        public void reRresh() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reRreshData();
                    mRecyclerView.setRefreshCompleted();
                }
            }, REFRESH_DURATION);
        }

        @Override
        public void loadMore() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                    mRecyclerView.setLoadMoreCompleted();
                }
            }, LOADMORE_DURATION);
        }
    }

    private void reRreshData() {
        //请求接口 加载新数据

    }

    private void loadData() {
        //请求接口 加载更多数据
        pageNo++;
        System.out.println("pageNo "  + pageNo);
        SiteApi.onGetChannelAlbums(getActivity(), pageNo, pageSize,mSiteId , mChannelId, new OnGetChannelAlbumListener() {


            @Override
            public void OnGetChannelAlbumSuccess(AlbumlList list) {
                System.out.println("我被执行了 卧槽");
                for (Album album : list) {
                    System.out.println("12456");
                    Log.e(TAG, ">> album " + album.toString());
                }
            }

            @Override
            public void OnGetChannelAlbumFailed(ErrorInfo info) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("我被执行了 卧槽");
                        mEmptyView.setText(getActivity().getResources().getString(R.string.data_failed_tip));
                    }
                });
            }
        });
    }


    class DetailListAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public void setColumns(int columns) {

        }
    }

    @Override
    protected void initData() {
        loadData();
        System.out.println("数据开始解析了");
    }
}
