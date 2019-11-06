package com.example.videoapp.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyFragment extends Fragment {

    private static BasePager basePage;

    public MyFragment(){

    }

    public static final MyFragment newInstance(BasePager page) {
        MyFragment fragment = new MyFragment();
        Bundle bundle = new Bundle();
        basePage = page;
        fragment.setArguments(bundle);
        return fragment ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (basePage != null){
            return basePage.rootView;
        }
        return null;
    }
}
