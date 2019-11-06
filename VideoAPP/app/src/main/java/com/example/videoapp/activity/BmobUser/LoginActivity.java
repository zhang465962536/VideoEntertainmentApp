package com.example.videoapp.activity.BmobUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videoapp.R;
import com.example.videoapp.Utils.SPUtils;
import com.example.videoapp.Utils.URL;
import com.example.videoapp.Utils.Utils;
import com.example.videoapp.domain.BmobUser.MyUser;

import butterknife.Bind;
import butterknife.ButterKnife;
/*import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;*/

/*
登录界面逻辑
* */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.keep_password)
    CheckBox keepPassword;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.btn_registered)
    Button btnRegistered;
    @Bind(R.id.tv_forget)
    TextView tvForget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        btnLogin.setOnClickListener(this);
        btnRegistered.setOnClickListener(this);

        //设置选中的状态
        boolean isCheck = SPUtils.getBoolean(this, "keeppass", false);
        keepPassword.setChecked(isCheck);
        if(isCheck){
            //设置密码
            etName.setText(SPUtils.getString(this,"name",""));
            etPassword.setText(SPUtils.getString(this,"password",""));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                //【1】获取输入框的值
                String name = etName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                //【2】判断是否为空
                if(!TextUtils.isEmpty(name) & !TextUtils.isEmpty(password)){
                    //登录
                   /* MyUser user = new MyUser();
                   user.setUsername(name);
                    user.setPassword(password);*/

                    /*user.login(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser myUser, BmobException e) {
                                //判断结果
                            if(e == null){

                            }else {

                            }
                        }
                    });*/

                    SPUtils.putString(this,"name",name);
                startActivity(new Intent(this,PersonalCenterActivity.class));
                finish();
                }else {
                    Utils.toast(getString(R.string.text_tost_empty));
                }
                break;

            case R.id.btn_registered:
                startActivity(new Intent(this,RegisteredActivity.class));
                break;
        }
    }

    //假设已经输入用户名和密码 但是没有点击登录 而是直接退出

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //保存记住密码选择状态
        SPUtils.putBoolean(this,"keeppass",keepPassword.isChecked());
        //是否记住密码
        if(keepPassword.isChecked()){
            //保存 用户名 和 密码
            SPUtils.putString(this,"name",etName.getText().toString().trim());
            SPUtils.putString(this,"password",etPassword.getText().toString().trim());
        }else {
            //如果不记住密码 就把输入框账号密码给清除掉
            SPUtils.deleShare(this,"name");
            SPUtils.deleShare(this,"password");
        }
    }
}
