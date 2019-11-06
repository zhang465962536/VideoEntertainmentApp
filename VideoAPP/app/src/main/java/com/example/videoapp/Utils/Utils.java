package com.example.videoapp.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.TrafficStats;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.videoapp.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Formatter;
import java.util.Locale;
/* 工具类*/
public class Utils {
	public static Context getContext(){
		return MyApplication.context;
	}

	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

	public Utils() {
		// 转换成字符串的时间
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

	}

	/**
	 * 把毫秒转换成：1:20:30这里形式
	 * @param timeMs
	 * @return
	 */
	public String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;
		int seconds = totalSeconds % 60;

		int minutes = (totalSeconds / 60) % 60;

		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	//判断是否是网络资源
    public boolean isNetUri(String uri) {
		boolean result = false;
		if (uri != null) {

			if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")) {
				result = true;
			}

		}
		return false;
    }


    /**
     * 得到网络速度 思路 每隔一个时间就去获取这个时间段的网络数据大小 然后计算这个网速
     * @param context
     * @return
     *
     */
    public String getNetSpeed(Context context) {

        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        //网速
        String speedStr = String.valueOf(speed) + " kb/s";

        return speedStr;
    }

	public static void toast(String str){
		Toast.makeText(Utils.getContext(),str,Toast.LENGTH_SHORT).show();
	}

	//将图片传入SP中保存
	public static void putImageToShare(Context context,ImageView imageView){
		BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
		Bitmap bitmap = drawable.getBitmap();
		//【1】将Bitmap压缩成字节数组输出流
		ByteArrayOutputStream byStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,80,byStream);
		//【2】利用Base64将字节数组输出流转换成String
		byte[] byteArray = byStream.toByteArray();
		String imgString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
		//【3】将String保存到SP中
		SPUtils.putString(context,"image_title",imgString);
	}

	//将图片从SP取出 显示到ImageView控件上
	public static void getImageFromShare(Context context,ImageView imageView){
		//【1】获取imgString
		String imgString = SPUtils.getString(context, "image_title", "");
		if(!imgString.equals("")){
			//imgString 不为空 说明有图片
			//【2】利用Base64将图片的String转换为字节数组输入流
			byte[] byteArray = Base64.decode(imgString, Base64.DEFAULT);
			ByteArrayInputStream byStream = new ByteArrayInputStream(byteArray);
			//【3】生成bitmap
			Bitmap bitmap = BitmapFactory.decodeStream(byStream);
			imageView.setImageBitmap(bitmap);
		}
	}

}
