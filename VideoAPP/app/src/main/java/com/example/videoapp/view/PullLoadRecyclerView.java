package com.example.videoapp.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.videoapp.R;

//自定义下拉刷新
public class PullLoadRecyclerView extends LinearLayout {

    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRefresh = false; // 是否正在刷新
    private boolean mIsLoadMore = false; // 是否要加载更多
    private RecyclerView mRecyclerView;
    private View mFootView;
    private OnPullLoadMoreListener mOnPullLoadMoreListener;
    private AnimationDrawable mAnimationDrawable;

    public PullLoadRecyclerView(Context context) {
        super(context);
        initView(mContext);
    }

    public PullLoadRecyclerView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        initView(mContext);
    }

    public PullLoadRecyclerView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(mContext);
    }

    private void initView(Context context) {
        mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.pull_loadmore_layout, null);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        //设置刷新时控件颜色渐变
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_blue_dark, android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayoutOnRefresh());

        //处理 RecyclerView
        mRecyclerView = view.findViewById(R.id.recyclerview);
        //给mRecyclerView 设置固定大小 不能变化
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置默认动画
        mRecyclerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //当刷新 或者 加载更多的时候   mRecyclerView 可以触摸
                return mIsLoadMore || mIsRefresh;

            }
        });

        mRecyclerView.setVerticalScrollBarEnabled(false);//隐藏滚动条mRecyclerView.
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScroll());  //添加滚动监听

        mFootView = view.findViewById(R.id.footer_view);
        ImageView imageView = mFootView.findViewById(R.id.iv_load_img);
        imageView.setBackgroundResource(R.drawable.loading);
        mAnimationDrawable = (AnimationDrawable) imageView.getBackground();

        TextView textView = mFootView.findViewById(R.id.tv_load_text);
        mFootView.setVisibility(GONE);
        //view 包含swipeRefreshLayout, RecyclerView, FootView
        this.addView(view);//
    }

    //设置 RecyclerView 的列数
    public void setGridLayout(int spanCount){
        GridLayoutManager manager = new GridLayoutManager(mContext,spanCount);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        if(adapter != null){
            mRecyclerView.setAdapter(adapter);
        }
    }

    class SwipeRefreshLayoutOnRefresh implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
            if(!mIsRefresh){
                mIsRefresh = true;
                refreshData(); //刷新数据
            }
        }
    }



    class RecyclerViewOnScroll extends RecyclerView.OnScrollListener{
        //已经滑动过的方法
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int firstItem = 0;
            int lastItem = 0;
            RecyclerView.LayoutManager manager = mRecyclerView.getLayoutManager();
            int totalCount = manager.getItemCount(); //获取列表 item个数
            if(manager instanceof GridLayoutManager){  //如果是网格布局
                GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
                //第一个能完全显示的item
                firstItem = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                //最后一个完全可见的item
                lastItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();

                if(firstItem == 0 || firstItem == RecyclerView.NO_POSITION){  //RecyclerView.NO_POSITION 表示-1  表示item不在当前列表里面范围内
                    lastItem = gridLayoutManager.findLastVisibleItemPosition();
                }
            }

            //触发 上拉加载更多 条件
            if(mSwipeRefreshLayout.isEnabled()){ // mSwipeRefreshLayout 可以往下拉的时候 触发上拉加载更多
                mSwipeRefreshLayout.setEnabled(true);
            }else { //不能拉拽
                mSwipeRefreshLayout.setEnabled(false);
            }

            //加载更多的条件
            // 1.加载更多状态值是false
            // 2.totalCount - 1 === lastItem  lastItem等于 list总item数量
            // 3.mSwipeRefreshLayout 控件 可以使用的时候
            // 4. 不是处于下拉刷新状态
            // 5. 偏移量dx > 0 或dy > 0
            if( !mIsLoadMore
                    && totalCount - 1 == lastItem
                    && mSwipeRefreshLayout.isEnabled()
                    && !mIsRefresh
                    && (dx > 0 || dy > 0))
            {
                //在加载更多时,禁止mSwipeRefreshLayout使用
                mSwipeRefreshLayout.setEnabled(false);
                mIsLoadMore = true;
                loadMoreData();
            }
        }
    }

    //加载更多
    private void loadMoreData() {
        if(mOnPullLoadMoreListener != null){
            mFootView.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        //刷新动画开始执行的时候 开始加载更多数据
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            mFootView.setVisibility(VISIBLE);
                            mAnimationDrawable.start();
                        }
                    }).start();
            //强制刷新界面
            invalidate();
            mOnPullLoadMoreListener.loadMore();
        }
    }

    //刷新完成后 修改状态
    public void setRefreshCompleted(){
        mIsRefresh = false;
        setRefreshing(false);
    }

    //设置 是否正在刷新 的状态
    private void setRefreshing(final boolean isRefreshing) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }

    //加载更多后完成 修改状态
    public void setLoadMoreCompleted() {
        mIsLoadMore = false;
        mIsRefresh = false;
        setRefreshing(false);
        mFootView.animate().translationY(mFootView.getHeight()).setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(300).start();
    }


    //刷新数据方法
    private void refreshData() {
        if(mOnPullLoadMoreListener != null){
            mOnPullLoadMoreListener.reRresh();
        }
    }

    //拉拽加载更多数据 监听器 接口
    public interface OnPullLoadMoreListener{
        void reRresh();
        void loadMore();
    }

    public void setOnPullLoadMoreListener(OnPullLoadMoreListener listener) {
        mOnPullLoadMoreListener = listener;
    }
}
