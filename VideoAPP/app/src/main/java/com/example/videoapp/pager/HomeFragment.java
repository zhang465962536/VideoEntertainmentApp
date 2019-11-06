package com.example.videoapp.pager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.videoapp.R;
import com.example.videoapp.Utils.Utils;
import com.example.videoapp.activity.DetailListActivity;
import com.example.videoapp.activity.MovieTrailerActivity;
import com.example.videoapp.activity.live.LiveActivity;
import com.example.videoapp.adapter.HomePicAdapter;
import com.example.videoapp.base.BasePager;
import com.example.videoapp.domain.Channel;
import com.hejunlin.superindicatorlibray.CircleIndicator;
import com.hejunlin.superindicatorlibray.LoopViewPager;

import butterknife.Bind;
/* 首页 fragment 界面*/
public class HomeFragment extends BasePager {
    @Bind(R.id.looperviewpager)
    LoopViewPager looperviewpager;
    @Bind(R.id.indicator)
    CircleIndicator indicator;
    @Bind(R.id.gv_channel)
    GridView gvChannel;
    @Bind(R.id.activity_main)
    LinearLayout activityMain;

    private static final String TAG = HomeFragment.class.getSimpleName();
    private GridView mGridView;

    public HomeFragment(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_home, null);
        LoopViewPager looperviewpager = view.findViewById(R.id.looperviewpager);
        CircleIndicator indicator = view.findViewById(R.id.indicator);
        looperviewpager.setAdapter(new HomePicAdapter(mContext));
        looperviewpager.setLooperPic(true);//5秒自动轮询轮播
        indicator.setViewPager(looperviewpager); //indicator和viewPager绑定


        mGridView = view.findViewById(R.id.gv_channel);
        mGridView.setAdapter(new ChannelAdapter());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, ">> onItemClick " + position);
                switch (position) {
                    case 1: //电影预告
                        Utils.toast("电影预告");
                        MovieTrailerActivity.launch(mContext);
                        break;
                    case 6:
                        //跳转直播
                        Utils.toast("直播");
                        LiveActivity.launch(mContext);
                        break;
                    case 7:
                        //跳转收藏
                        Utils.toast("收藏");
                        //FavoriteActivity.launch(getActivity());
                        break;
                    case 8:
                        //跳转历史记录
                        Utils.toast("历史记录");
                        //HistoryActivity.launch(getActivity());
                        break;
                    default:
                        //跳转对应频道
                        Utils.toast("频道" + (position + 1));
                        DetailListActivity.launchDetailListActivity(mContext, position + 1);
                        break;
                }
            }
        });



        return view;
    }


    class ChannelAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return Channel.MAX_COUNT;
        }

        @Override
        public Channel getItem(int position) {
            return new Channel(position + 1,mContext);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Channel channel = getItem(position);
            ViewHolder holder = null;
            if(convertView == null){
                convertView = View.inflate(mContext,R.layout.home_grid_item,null);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.tv_home_item_text);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_home_item_img);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(channel.getChannelName());
            int id = channel.getChannelId();
            int imgResId = -1;
            switch (id) {
                case Channel.SHOW:
                    imgResId = R.drawable.ic_show;
                    break;
                case Channel.MOVIE:
                    imgResId = R.drawable.ic_movie;
                    break;
                case Channel.COMIC:
                    imgResId = R.drawable.ic_comic;
                    break;
                case Channel.DOCUMENTRY:
                    imgResId = R.drawable.ic_movie;
                    break;
                case Channel.MUSIC:
                    imgResId = R.drawable.ic_music;
                    break;
                case Channel.VARIETY:
                    imgResId = R.drawable.ic_variety;
                    break;
                case Channel.LIVE:
                    imgResId = R.drawable.ic_live;
                    break;
                case Channel.FAVORITE:
                    imgResId = R.drawable.ic_bookmark;
                    break;
                case Channel.HISTORY:
                    imgResId = R.drawable.ic_history;
                    break;
            }
            holder.imageView.setImageDrawable(mContext.getResources().getDrawable(imgResId));
            return convertView;
        }
    }

    class ViewHolder{
        TextView textView;
        ImageView imageView;
    }
}
