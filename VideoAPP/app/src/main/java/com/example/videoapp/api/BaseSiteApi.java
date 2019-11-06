package com.example.videoapp.api;

import com.example.videoapp.domain.Channel;

public abstract class BaseSiteApi {

    public abstract void onGetChannelAlbums(Channel channel,int pageNo,int pageSize,OnGetChannelAlbumListener listener);


}
