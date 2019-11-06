package com.example.videoapp.domain;

import android.util.Log;

import java.util.ArrayList;

public class AlbumlList extends ArrayList<Album> {

    private static final String TAG = AlbumlList.class.getSimpleName();

    public void debug () {
        for (Album a : this) {
            Log.d(TAG, ">> albumlist " + a.toString());
        }
    }
}
