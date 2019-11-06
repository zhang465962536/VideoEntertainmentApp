package com.example.videoapp.activity.BmobUser;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.videoapp.R;
import com.example.videoapp.Utils.URL;
import com.example.videoapp.Utils.Utils;
import com.example.videoapp.domain.BmobUser.MyUser;

import butterknife.Bind;
import butterknife.ButterKnife;
/*import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;*/
/*
* 注册界面逻辑
* */
public class RegisteredActivity extends Activity implements View.OnClickListener {

    @Bind(R.id.et_user)
    EditText etUser;
    @Bind(R.id.et_age)
    EditText etAge;
    @Bind(R.id.et_desc)
    EditText etDesc;
    @Bind(R.id.rb_boy)
    RadioButton rbBoy;
    @Bind(R.id.rb_girl)
    RadioButton rbGirl;
    @Bind(R.id.mRadioGroup)
    RadioGroup mRadioGroup;
    @Bind(R.id.et_pass)
    EditText etPass;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.et_email)
    EditText etEmail;
    @Bind(R.id.btn_Registered)
    Button btnRegistered;

    //性别
    private boolean isGender = true; //默认为true 为男生

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        ButterKnife.bind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_Registered:
                //点击注册的时候获取到输入框的值
                String name = etUser.getText().toString().trim();;
                String age = etAge.getText().toString().trim();
                String desc = etDesc.getText().toString().trim();
                String pass = etPass.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                //判断输入框的值是否为空
                //判断是否为空
                if (!TextUtils.isEmpty(name) & !TextUtils.isEmpty(age) &
                        !TextUtils.isEmpty(pass) &
                        !TextUtils.isEmpty(password) &
                        !TextUtils.isEmpty(email)) {
                    //判断两次输入的密码是否一致
                    if (pass.equals(password)) {
                        //判断性别
                        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if (checkedId == R.id.rb_boy) {
                                    isGender = true;
                                } else if (checkedId == R.id.rb_girl) {
                                    isGender = false;
                                }
                            }
                        });

                        //判断简介是否为空
                        if (TextUtils.isEmpty(desc)) {  //如果为空 提交的时候 默认赋值一句话
                            desc = getString(R.string.text_nothing);
                        }

                        //注册信息
                    /*   MyUser user = new MyUser();
                       user.setUsername(name);
                        user.setPassword(password);
                        user.setEmail(email);
                        user.setAge(Integer.parseInt(age));
                        user.setSex(isGender);
                        user.setDesc(desc);*/

                        //提交注册信息
                      /* user.signUp(new SaveListener<MyUser>() {
                            @Override
                            public void done(MyUser myUser, BmobException e) {
                                //请求成功
                                if(e==null){   //注册成功
                                    Utils.toast(getString( R.string.text_registered_successful));
                                    finish();
                                }else{  //注册失败
                                    Utils.toast(getString( R.string.text_registered_failure));
                                }
                            }
                        });*/

                    }else {
                        Utils.toast(getString(R.string.text_two_input_not_consistent));
                    }
                }else {
                    Utils.toast( getString(R.string.text_tost_empty));
                }

                break;
        }
    }
}
