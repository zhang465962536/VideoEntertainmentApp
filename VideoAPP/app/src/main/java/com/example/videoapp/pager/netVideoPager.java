package com.example.videoapp.pager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.videoapp.R;
import com.example.videoapp.Utils.LogUtil;
import com.example.videoapp.Utils.SPUtils;
import com.example.videoapp.Utils.URL;
import com.example.videoapp.activity.SystemVideoPlayer;
import com.example.videoapp.base.BasePager;
import com.example.videoapp.domain.MediaItem;
import com.example.videoapp.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*  电影预告 界面*/
public class netVideoPager extends BasePager {

    private XListView  lv_video_pager;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;
    private MyNetVideoAdapter adapter;
    private ArrayList<MediaItem> mMediaItems;
    private MyNetVideoAdapter mMyNetVideoAdapter;

    public netVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.net_video_pager, null);

        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        lv_video_pager = (XListView) view.findViewById(R.id.lv_video_pager);

        //设置listview 可以拉动刷新
        lv_video_pager.setPullLoadEnable(true);

        //设置点击事件
        lv_video_pager.setOnItemClickListener(new MyOnItemClick());

        //设置listview 拉动监听
        lv_video_pager.setXListViewListener(new MyIXListViewListener());


        return view;
    }

    class MyOnItemClick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaItem = mMediaItems.get(position);

            //通过隐式意图 通过匹配合适的Activity
//                Intent intent = new Intent();
//                intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//                context.startActivity(intent);


   /*            Intent intent = new Intent(mContext, SystemVideoPlayer.class);
                intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
                mContext.startActivity(intent);*/

            //传递视频列表
            Intent intent = new Intent(mContext, SystemVideoPlayer.class);
            // intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*"); 相当于文件夹
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mMediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position - 1);
            mContext.startActivity(intent);
        }
    }


    /**
     * 获取系统时间
     *
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    private void onload(){
        lv_video_pager.stopRefresh();
        lv_video_pager.stopLoadMore();
        lv_video_pager.setRefreshTime(getSystemTime());
    }

    class MyIXListViewListener implements XListView.IXListViewListener{

        @Override
        public void onRefresh() {
            //刷新  也就是 重新请求网络资源
            getDataFromNet();
            onload();
        }

        @Override
        public void onLoadMore() {
            getMoreDataFromNet();
        }
    }


    @Override
    public void initDta() {
        super.initDta();

        String saveJson = SPUtils.getString(mContext,URL.NET_VIDEO_URL,"");
        if(!TextUtils.isEmpty(saveJson)){
            //如果 没有缓存 就请求网络
            getDataFromNet();
        }


    }

    //联网请求  执行在子线程
    private void getDataFromNet() {

                RequestParams params = new RequestParams(URL.NET_VIDEO_URL);
                x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.e("联网请求成功" + result);
                        //缓存资源
                        SPUtils.putString(mContext,URL.NET_VIDEO_URL,result);
                        //解析JSon数据
                processData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网请求失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("取消联网请求");
            }

            @Override
            public void onFinished() {
                LogUtil.e("联网请求完成");
            }
        });
    }

    private void processData(String json) {
        //解析Json方法 手动解析
        parsoJson(json);
        if (mMediaItems != null && mMediaItems.size() > 0) {
            tv_nomedia.setVisibility(View.GONE);
            //设施适配器
            mMyNetVideoAdapter = new MyNetVideoAdapter();
            lv_video_pager.setAdapter(mMyNetVideoAdapter);
        } else {
            tv_nomedia.setVisibility(View.VISIBLE);
        }

        pb_loading.setVisibility(View.GONE);


    }

    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(URL.NET_VIDEO_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网请求成功==" + result);
                SPUtils.putString(mContext, URL.NET_VIDEO_URL, result);
                parseMoreData(result);
                adapter.notifyDataSetChanged();
                onload();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网请求失败==");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==");
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }

    private void parseMoreData(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.optJSONArray("trailers");
//            object.getJSONArray("trailers");//不好-
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                if (jsonObject != null) {

                    MediaItem mediaItem = new MediaItem();
                    mMediaItems.add(mediaItem);//添加到集合中--可以

                    String coverImg = jsonObject.getString("coverImg");
                    mediaItem.setImageUrl(coverImg);

                    String url = jsonObject.optString("url");
                    mediaItem.setData(url);

                    String movieName = jsonObject.optString("movieName");
                    mediaItem.setName(movieName);

                    String videoTitle = jsonObject.optString("videoTitle");
                    mediaItem.setDesc(videoTitle);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    class MyNetVideoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMediaItems.size();
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
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_netvideo_pager, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_netvideo_screenshot = convertView.findViewById(R.id.iv_netvideo_screenshot);
                viewHolder.tv_videoLength = convertView.findViewById(R.id.tv_videoLength);
                viewHolder.tv_videoTitle = convertView.findViewById(R.id.tv_videoTitle);
                viewHolder.iv_center_collect_play = convertView.findViewById(R.id.iv_center_collect_play);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据位置得到对应的数据
            MediaItem mediaItem = mMediaItems.get(position);
            viewHolder.tv_videoTitle.setText(mediaItem.getDesc());

            //请求图片 xutils3 加载
            //x.image().bind(,mediaItem.getImageUrl());
            //使用Glide 加载存在的图片
            // Glide.with(mContext).load(mediaItem.getImageUrl()).into(viewHolder.iv_netvideo_screenshot);
            //使用  Glide 显示视频缩略图  //并且设置缓存
            Glide.with(mContext).load(mediaItem.getData())  //加载的视频地址
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  //给图片设置缓存
                    .placeholder(R.drawable.video_default)  //加载中的图片
                    .error(R.drawable.video_default)  //加载失败的图片
                    .into(viewHolder.iv_netvideo_screenshot);

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_netvideo_screenshot;
        TextView tv_videoTitle;
        ImageView iv_center_collect_play;
        TextView tv_videoLength;
    }

    //解析json
    private void parsoJson(String json) {

        try {
            mMediaItems = new ArrayList<>();
            JSONObject jo = new JSONObject(json);
            //如果 trailers 没有找到 并不会崩溃  get getJSONArray 会出现崩溃
            JSONArray ja = jo.optJSONArray("trailers");
            //jo.getJSONArray("trailers");两个语句相同

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo1 = (JSONObject) ja.get(i);

                if (jo1 != null) {

                    MediaItem mediaItem = new MediaItem();

                    String coverImg = jo1.getString("coverImg");
                    mediaItem.setImageUrl(coverImg);

                    String hightUrl = jo1.optString("url");
                    mediaItem.setData(hightUrl);

                    int videoLength = jo1.optInt("videoLength");
                    mediaItem.setVideoLength(videoLength);

                    String videoName = jo1.optString("movieName");
                    mediaItem.setName(videoName);

                    String videoTitle = jo1.optString("videoTitle");
                    mediaItem.setDesc(videoTitle);

                    mMediaItems.add(mediaItem);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
