package com.example.videoapp.activity.Game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.videoapp.R;
import com.example.videoapp.view.GobangBoardView;

/*
* 五子棋界面逻辑*/
public class GobangActivity extends AppCompatActivity implements View.OnClickListener {
    private GobangBoardView gobangboardview;
    private Button btn_restart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gobang);


        gobangboardview = findViewById(R.id.gobangboardview);
        btn_restart = findViewById(R.id.btn_restart);
        btn_restart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case   R.id.btn_restart:
                gobangboardview.restart();
                break;
        }
    }
}
