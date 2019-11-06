package com.example.videoapp.api;

import android.content.Context;

import com.example.videoapp.domain.Channel;
import com.example.videoapp.domain.Site;

public class SiteApi {
//通过接口来回调api 是否成功获得数据
    public static void onGetChannelAlbums(Context context,int pageNo, int pageSize, int siteId, int channelId, OnGetChannelAlbumListener listener) {
        switch (siteId) {
            case Site.LETV:
                new LetvApi().onGetChannelAlbums(new Channel(channelId,context),pageNo,pageSize,listener);
                break;

            case Site.SOHU:
                new SoHuApi().onGetChannelAlbums(new Channel(channelId,context),pageNo,pageSize,listener);
                break;
        }
    }
}
