package com.example.videoapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.example.videoapp.domain.Lyric;

import java.util.ArrayList;

public class LyricShowView extends android.support.v7.widget.AppCompatTextView {


    /**
     * 当前控件的宽和高
     */
    private int width;
    private int height;
    /**
     * 当前播放进度
     */
    private float currentPosition;

    /**
     * 设置歌词列表
     * @param lyrics
     */
    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    private ArrayList<Lyric> lyrics;
    private Paint paint;
    private Paint whitePaint;
    /**
     * 歌词下标的索引
     */
    private int index;
    private float textHeight = 20;
    /**
     * 高亮时间
     */
    private float sleepTime;
    /**
     * 时间戳
     */
    private float timePoint;

    public LyricShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setTextSize(16);
        //设置文字居中
        paint.setTextAlign(Paint.Align.CENTER);

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setAntiAlias(true);
        whitePaint.setTextSize(16);
        //设置文字居中
        whitePaint.setTextAlign(Paint.Align.CENTER);

        //添加假歌词
//        lyrics = new ArrayList<>();
//        Lyric lyric = new Lyric();
//        for (int i = 0; i < 1001; i++) {
//            lyric.setContent(i + "aaaaaaaaa" + i);
//            lyric.setSleepTime(2000 + i);
//            lyric.setTimePoint(2000 * i);
//            lyrics.add(lyric);//添加到集合中
//            lyric = new Lyric();
//        }


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyrics != null && lyrics.size() > 0&&index< lyrics.size()) {

            //平移动画
            float dy = 0;
            if(sleepTime ==0){
                dy = 0;
            }else{
                //花的时间:休眠时间 = 移动距离：行高
//                float push = ((currentPosition-timePoint)/sleepTime)*textHeight;

                // 坐标 = 行高 + 移动距离
                dy = textHeight +((currentPosition-timePoint)/sleepTime)*textHeight;
            }
            canvas.translate(0,-dy);

            //有歌词：
            // 1.绘制当前句；
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent, width / 2, height / 2, paint);
            // 2.绘制前面部分；
            float tempY = height / 2;
            for (int i = index - 1; i >= 0; i--) {
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }

                canvas.drawText(preContent, width / 2, tempY, whitePaint);

            }
            // 3,绘制后面部分
            tempY = height / 2;
            for (int i = index + 1; i < lyrics.size(); i++) {
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }

                canvas.drawText(nextContent, width / 2, tempY, whitePaint);

            }
        } else {
            //没有歌词
            canvas.drawText("没有找到歌词", width / 2, height / 2, paint);
        }
    }

    /**
     * 根据当前播放的位置，找出该高亮显示哪一句，该局的sleepTime和timePoint计算出来
     *
     * @param currentPosition 当前音乐播放的位置
     */
    public void setShowNextLyric(int currentPosition) {
        this.currentPosition = currentPosition;

        if (lyrics == null)
            return;

        for (int i = 1; i < lyrics.size(); i++) {

            //划出区域
            if (currentPosition < lyrics.get(i).getTimeout()) {

                int tempIndex = i - 1;//0->1
                if (currentPosition >= lyrics.get(tempIndex).getTimeout()) {

                    index = tempIndex;//歌词下标索引
                    sleepTime = lyrics.get(index).getSleepTime();
                    timePoint = lyrics.get(index).getTimeout();
                }


            }

        }


        invalidate();//强制绘制-onDraw()方法再次执行


    }
}
