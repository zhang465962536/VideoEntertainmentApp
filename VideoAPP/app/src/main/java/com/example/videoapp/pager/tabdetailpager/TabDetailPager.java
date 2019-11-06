package com.example.videoapp.pager.tabdetailpager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.videoapp.MyApplication;
import com.example.videoapp.R;
import com.example.videoapp.Utils.CacheUtils;
import com.example.videoapp.Utils.LogUtil;
import com.example.videoapp.Utils.Utils;
import com.example.videoapp.activity.NewsDetailActivity;
import com.example.videoapp.api.NewsAPI;
import com.example.videoapp.base.MenuDetaiBasePager;
import com.example.videoapp.domain.NewsData;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/* 新闻列表 分类 界面*/
public class TabDetailPager extends MenuDetaiBasePager {

    public static final String READ_ARRAY_UNIQUEKEY = "read_array_uniquekey";
    private TextView mTextView;
    private int mPosition;
    private String url;
    //新闻列表数据的集合
    private  List<NewsData.ResultBean.DataBean> news;
    private newsListAdapter newsListAdapter;
    private ListView news_listview;

    //下拉刷新 加载更多开源库
    private PullToRefreshListView mPullRefreshListView;

    private String[] newsTitle = {"头条","社会","国内","国际","娱乐","体育","军事","科技","财经","时尚"};

    private String[] urltype = {
            NewsAPI.TOP,NewsAPI.SHEHUI,NewsAPI.GUONEI,NewsAPI.GUOJI,NewsAPI.YULE,
            NewsAPI.TIYU, NewsAPI.JUNSHI,NewsAPI.KEJI,NewsAPI.CAIJING,NewsAPI.SHISHANG};

    public TabDetailPager(Context context,int position) {
        super(context);
        mPosition = position;
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tabdetail_pager, null);
        //news_listview = view.findViewById(R.id.news_listview); //todo 要恢复


        mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);

        //添加声音事件监听  给下拉刷新 上拉刷新 添加声音
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(context);
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
        mPullRefreshListView.setOnPullEventListener(soundListener);

        news_listview = mPullRefreshListView.getRefreshableView();
        //监听下拉刷新
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            //下拉刷新数据
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getDataFromNet();
            }

            //上拉加载更多
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getDataFromNet();
            }
        });

        //设置listview的 item 的点击监听  //todo  要恢复
        news_listview.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 点击新闻条目 使得 item变灰
           NewsData.ResultBean.DataBean newsData = news.get(position);

            //【1】取出保存 uniquekeyArray 的 集合 (唯一指定一条新闻)
            String uniquekeyArray = CacheUtils.getString(context, READ_ARRAY_UNIQUEKEY);
            //【2】判断 uniquekey 是否存在  如果不存在就做变灰处理(保存uniquekey 并且刷新适配器)
            if(!uniquekeyArray.contains(newsData.getUniquekey())){
                //SP中不包含 uniquekey 存入SP中
                CacheUtils.putString(context,READ_ARRAY_UNIQUEKEY,uniquekeyArray + newsData.getUniquekey()+",");

                //刷新适配器
                newsListAdapter.notifyDataSetChanged();// getcount ---> getview
            }

            //跳转到新闻详细页面
            Intent intent = new Intent(context, NewsDetailActivity.class);
            intent.putExtra("url",newsData.getUrl());
            context.startActivity(intent);

        }
    }

    @Override
    public void initData() {
        super.initData();
        url = urltype[mPosition];
        //把之前缓存得数据取出来
        String savaJson = CacheUtils.getString(context,url);
        if(!TextUtils.isEmpty(savaJson)){
            //解析JSON数据和处理显示数据
            processData(savaJson);
        }
        //联网请求
        getDataFromNet();
    }

    //解析JSON数据和处理显示数据
    private void processData(String json) {
        NewsData newsData =  parseJson(json);
        String title = newsData.getResult().getData().get(0).getTitle();
        LogUtil.e(newsTitle[mPosition] +  "  ++++++++++ 解析成功  ++++++"+ title);

        //准备listView对应的集合数据
        news = newsData.getResult().getData();

        //设置ListView 的适配器
        newsListAdapter = new newsListAdapter();
        news_listview.setAdapter(newsListAdapter);

    }

    class newsListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = View.inflate(context,R.layout.news_list_item,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_news_icon = convertView.findViewById(R.id.iv_news_icon);
                viewHolder.tv_news_title = convertView.findViewById(R.id.tv_news_title);
                viewHolder.tv_news_time = convertView.findViewById(R.id.tv_news_time);
                viewHolder.tv_news_author = convertView.findViewById(R.id.tv_news_author);

                convertView.setTag(viewHolder);

            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据位置得到数据
            NewsData.ResultBean.DataBean newsData = news.get(position);
            String imageUrl = newsData.getThumbnail_pic_s();
            if(imageUrl != null){
                //请求图片
                x.image().bind(viewHolder.iv_news_icon,imageUrl);
            }
            //设置标题
            viewHolder.tv_news_title.setText(newsData.getTitle());
            //设置时间
            viewHolder.tv_news_time.setText(newsData.getDate());
            //设置来源
            viewHolder.tv_news_author.setText(newsData.getAuthor_name());

            String uniquekeyArry = CacheUtils.getString(context, READ_ARRAY_UNIQUEKEY);
            if(uniquekeyArry.contains(newsData.getUniquekey())){
                //如果包含 设置字体颜色为灰色
                viewHolder.tv_news_title.setTextColor(Color.GRAY);
            }else {
                //如果不包含  设置字体颜色为黑色
                viewHolder.tv_news_title.setTextColor(Color.BLACK);
            }

            return convertView;
        }
    }

    static class ViewHolder{
        ImageView iv_news_icon;
        TextView tv_news_title;
        TextView tv_news_time;
        TextView tv_news_author;

    }

    //使用GSON解析JSON数据
    private NewsData parseJson(String json) {
        return MyApplication.getGson().fromJson(json,NewsData.class);
    }

    //联网请求
    private void getDataFromNet() {
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //缓存数据  将json进行缓存
                CacheUtils.putString(context,url,result);
                LogUtil.e(newsTitle[mPosition] + " 请求数据成功" + result);
                //解析数据和处理显示数据
                processData(result);

                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(newsTitle[mPosition] + " 请求数据失败" + ex.getMessage());
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e(newsTitle[mPosition] + " 页面数据请求 onCancelled " + cex.getMessage() );
            }

            @Override
            public void onFinished() {
                LogUtil.e(newsTitle[mPosition] + " 页面数据请求 onFinished ");
            }
        });
    }


}
