package com.example.videoapp.activity.BmobUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.videoapp.R;
import com.example.videoapp.Utils.LogUtil;
import com.example.videoapp.Utils.SPUtils;
import com.example.videoapp.Utils.Utils;
import com.example.videoapp.domain.BmobUser.MyUser;
import com.example.videoapp.view.CustomDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
/*import cn.bmob.v3.BmobUser;  //todo
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;*/
import de.hdodenhof.circleimageview.CircleImageView;

/*
* 个人中心界面 逻辑
* */
public class PersonalCenterActivity extends Activity implements View.OnClickListener {

    @Bind(R.id.btn_edit_user)
    Button btnEditUser;
    @Bind(R.id.et_username)
    EditText etUsername;
    @Bind(R.id.et_sex)
    EditText etSex;
    @Bind(R.id.et_age)
    EditText etAge;
    @Bind(R.id.et_desc)
    EditText etDesc;
    @Bind(R.id.btn_update_ok)
    Button btnUpdateOk;
    @Bind(R.id.btn_change_password)
    Button btn_change_password;
    @Bind(R.id.btn_exit_user)
    Button btnExitUser;
    @Bind(R.id.profile_image)  //圆形头像
    CircleImageView profileImage;

    //自定义提示框
    private CustomDialog mDialog;
    private Button btn_camera;
    private Button btn_picture;
    private Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        ButterKnife.bind(this);

        initview();
        btnExitUser.setOnClickListener(this);
        btnUpdateOk.setOnClickListener(this);
        btn_change_password.setOnClickListener(this);
        btnEditUser.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_picture.setOnClickListener(this);
        profileImage.setOnClickListener(this);

        //判断用户是否已经登录
        isLogin();

    }

    private void isLogin() {
        //查看本地是否有用户的登录信息
        String name = SPUtils.getString(this, "name", "");
        if(TextUtils.isEmpty(name)){
            //本地没有保存过用户信息  给出提示登录框
           showLoginDialog();

        }else {
            //已经登录过 则直接加载用户信息 并且显示

        }
    }

    private void showLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您还没有登录哦！么么~")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            UIUtils.toast("进入登录页面",false);
                      startActivity(new Intent(PersonalCenterActivity.this,LoginActivity.class));
                      finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void initview() {
        //输入框默认是不可点击/不可输入的
        setEnable(false);

        //将本地缓存的User对象设置带个人中心信息  //todo
       /* MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        etUsername.setText(userInfo.getUsername());
        etAge.setText(userInfo.getAge() + "");
        etSex.setText(userInfo.isSex() ? "男" : "女");
        etDesc.setText(userInfo.getDesc());*/

        //【1】获取imgString
        String imgString = SPUtils.getString(this, "image_title", "");
        if (!imgString.equals("")) {
            //imgString 不为空 说明有图片
            //【2】利用Base64将图片的String转换为字节数组输入流
            byte[] byteArray = Base64.decode(imgString, Base64.DEFAULT);
            ByteArrayInputStream byStream = new ByteArrayInputStream(byteArray);
            //【3】生成bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(byStream);
            profileImage.setImageBitmap(bitmap);
        }

        mDialog = new CustomDialog(this, 0, 0, R.layout.dialog_photo, R.style.pop_anim_style, Gravity.CENTER, 0);
        //提示框以外 点击无效
        mDialog.setCancelable(false);
        btn_camera = mDialog.findViewById(R.id.btn_camera);
        btn_picture = mDialog.findViewById(R.id.btn_picture);
        btn_cancel = mDialog.findViewById(R.id.btn_cancel);
    }

    private void setEnable(boolean is) {
        etUsername.setEnabled(is);
        etAge.setEnabled(is);
        etDesc.setEnabled(is);
        etSex.setEnabled(is);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //退出登录
            case R.id.btn_exit_user:
                //清除缓存用户对象 //todo
      /*          MyUser.logOut();
                //现在的User对象为 null
                BmobUser currentUser = MyUser.getCurrentUser();*/
                SPUtils.deleShare(this,"name");
                finish();
                break;

            //编辑资料
            case R.id.btn_edit_user:
                setEnable(true);  //4个编辑框获取焦点
                btnUpdateOk.setVisibility(View.VISIBLE);
                break;

            //更新按钮
            case R.id.btn_update_ok:
                updateInformation();
                break;

            //修改头像
            case R.id.profile_image:
                mDialog.show();
                break;

                //修改密码
            case R.id.btn_change_password:

                break;

            //拍照获取图片
            case R.id.btn_camera:
                toCamera();
                break;

            //图库获取图片
            case R.id.btn_picture:
                toPicture();
                break;

            //取消更好头像操作
            case R.id.btn_cancel:
                mDialog.dismiss();
                break;


        }
    }

    public static final String PHOTO_IMAGE_FILE_NAME = "fileImg.jpg";
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int IMAGE_REQUEST_CODE = 101;
    public static final int RESULT_REQUEST_CODE = 102;
    private File tempFile = null;

    //拍照获取图片
    private void toCamera() {
       /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断内存卡是否可用，可用的话就进行储存
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME)));
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
        mDialog.dismiss();*/

        //打开系统拍照程序，选择拍照图片
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
        mDialog.dismiss();

    }

    //图库获取图片
    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
        mDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != this.RESULT_CANCELED) {
            switch (requestCode) {
                //相册数据
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                //相机数据
                case CAMERA_REQUEST_CODE:
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME);
                    startPhotoZoom(Uri.fromFile(tempFile));
                    break;
                case RESULT_REQUEST_CODE:
                    //有可能点击舍弃
                    if (data != null) {
                        //拿到图片设置
                        setImageToView(data);
                        //既然已经设置了图片，我们原先的图片就应该删除
                        if (tempFile != null) {
                            tempFile.delete();
                        }
                    }
                    break;
            }
        }
    }

    //设置图片
    private void setImageToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            Bitmap bitmap = bundle.getParcelable("data");
            profileImage.setImageBitmap(bitmap);
        }

    }

    //裁剪操作的方法
    private void startPhotoZoom(Uri uri) {
        if (uri == null) {
            LogUtil.e("uri == null");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", "true");
        //裁剪宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪图片的质量
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        //发送图片数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    //更新信息的操作过程
    private void updateInformation() {
        //【1】获取输入框的值
        String username = etUsername.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String sex = etSex.getText().toString().trim();

        //【2】判断输入框的是否为空
        if (!TextUtils.isEmpty(username) & !TextUtils.isEmpty(age) & !TextUtils.isEmpty(sex)) {
            //输入框不为空
            //更新属性
            MyUser user = new MyUser();
            /*user.setUsername(username);*/
            user.setAge(Integer.parseInt(age));

            //性别
            if (sex.equals("男")) {
                user.setSex(true);
            } else {
                user.setSex(false);
            }

            //简介
            if (!TextUtils.isEmpty(desc)) {
                user.setDesc(desc);
            } else {
                user.setDesc(getString(R.string.text_nothing));
            }

           /* BmobUser bmobUser = BmobUser.getCurrentUser();  //todo
            user.update(bmobUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {  //判断异常是否为空
                        //异常为空 说明修改成功
                        setEnable(false);
                        btnUpdateOk.setVisibility(View.GONE);
                        Utils.toast(getString(R.string.text_editor_success));
                    } else {
                        //修改失败
                        Utils.toast(getString(R.string.text_editor_failure));
                    }
                }
            });*/

            setEnable(false);   //todo
            btnUpdateOk.setVisibility(View.GONE);
            Utils.toast(getString(R.string.text_editor_success));
        } else {
            //3个输入框有个为空
            Utils.toast(getString(R.string.text_tost_empty));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //保存头像bitmap
        Utils.putImageToShare(this, profileImage);
    }

}
