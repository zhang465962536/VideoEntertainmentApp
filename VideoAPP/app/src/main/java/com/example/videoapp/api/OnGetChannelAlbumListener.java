package com.example.videoapp.api;

import com.example.videoapp.domain.Album;
import com.example.videoapp.domain.AlbumlList;
import com.example.videoapp.domain.ErrorInfo;

public interface OnGetChannelAlbumListener {

    void OnGetChannelAlbumSuccess(AlbumlList list);
    void OnGetChannelAlbumFailed(ErrorInfo info);
}
