package com.example.videoapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.videoapp.R;

import java.util.ArrayList;
import java.util.List;

//五子棋 棋盘自定义视图
public class GobangBoardView extends View {

    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 10;
    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    //动态设置 棋子大小  是棋子 和行高的比例 避免出现比例不正确   棋子/ 行高 = 3 / 4
    private float rationPieceOfLineHeight = 3 * 1.0f / 4;

    //黑白棋子的集合
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlockArray = new ArrayList<>();

    //标志 当前白旗先手  还是 黑棋先手
    private boolean mIsWhite = true;   //白旗先手

    //游戏结束标志
    private boolean mIsGameOver;
    //判断谁是赢家 如果为true 白棋赢
    private boolean mIsWhiteWinner;

    private int MAX_COUNT_IN_LINE = 5;

    //在布局中使用 的构造方法
    public GobangBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置背景为了 看到自定义view 所占据的大小 而且是半透明的
//        setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true); //设置抗锯齿
        mPaint.setDither(true); //设置防抖动
        mPaint.setStyle(Paint.Style.STROKE); //画线

        //初始化黑白棋子
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //当控件宽高 有准确值 这样设置测量是没有问题的
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getSize(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getSize(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        //即使当前控件嵌套在scrollView 里面 或者 高度为0的时候  都可以使用该控件
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            //MeasureSpec.UNSPECIFIED 就是未指定的意思，在这个模式下父控件不会干涉子 View 想要多大的尺寸。
            // 此时宽度由高度决定
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        //宽高设置同样的值  正方形
        setMeasuredDimension(width, width);
    }

    //控件宽高发生改变的时候 会回调这个方法
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int pieceWidth = (int) (mLineHeight * rationPieceOfLineHeight);
        //动态修改 黑白棋子的大小

        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制棋盘
        drawBoard(canvas);
        //绘制棋子
        drawPieces(canvas);
        //判断游戏是否结束
        checkGameOver();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //判断游戏是否结束 如果没有结束 继续运行
        if (mIsGameOver)  //游戏已经结束了 不可触摸
            return false;
        int action = event.getAction();
        //如果是触摸事件是 松开的操作 就自己消费这个点击事件 由自己处理
        if (action == MotionEvent.ACTION_UP) {
            //获取 按下的点 x y 坐标
            int x = (int) event.getX();
            int y = (int) event.getY();

            // Point p = new Point(x, y);  避免黑白棋下在同一个点  或者 下的位置不在格子上 需要进行改进
            Point p = getValidPoint(x, y);

            //判断该棋盘点 是否已经有棋子存放
            if (mWhiteArray.contains(p) || mBlockArray.contains(p)) {
                return false; //触摸事件 就不没有用了
            }

            if (mIsWhite) {
                //如果当前是白棋先手
                mWhiteArray.add(p);
            } else {
                //如果当前是黑棋先手
                mBlockArray.add(p);
            }
            //重新绘制view
            invalidate();
            mIsWhite = (!mIsWhite);
            return true;
        }

        //ACTION_DOWN时候 return true 表明此view可以消耗事件
        return true;

    }

    //判断游戏是否结束
    private void checkGameOver() {
        //判断5个同色棋子是否在一条线上
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlockArray);

        if (whiteWin || blackWin) {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;

            String text = mIsWhiteWinner ? "白棋获胜" : "黑棋获胜";

            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

        }
    }


    private boolean checkFiveInLine(List<Point> points) {

        for (Point p : points) {
            int x = p.x;
            int y = p.y;

            //判断x轴上是否有5个连续的棋子
            boolean win = checkHorizontal(x, y, points);
            if (win) return true;
            win = checkVertical(x, y, points);
            if (win) return true;
            win = checkLeftDiagonal(x, y, points);
            if (win) return true;
            win = checkRightDiagonal(x, y, points);
            if (win) return true;
        }
        return false;
    }
    //举例一个最简单的 获胜方式  X轴5个相同的棋子
    //判断 水平有上5个连续的相同棋子
    private boolean checkHorizontal(int x, int y, List<Point> points) {

        int count = 1;
        //判断该棋子 水平 左边 是否有4个连续的相同的棋子
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                //如果左边有连续相同的棋子
                count++;
            } else {
                //如果左边没有相同的棋子 跳出循环
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;

        //判断该棋子 水平 右边 是否有4个连续的相同的棋子
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                //如果右边有连续相同的棋子
                count++;
            } else {
                //如果右边没有相同的棋子 跳出循环
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE)
            return true;

        return false;
    }

    //判断 垂直 有上5个连续的相同棋子
    private boolean checkVertical(int x, int y, List<Point> points) {

        int count = 1;
        //判断该棋子 垂直  上边 是否有4个连续的相同的棋子
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y + i))) {
                //如果上边有连续相同的棋子
                count++;
            } else {
                //如果上边没有相同的棋子 跳出循环
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;

        //判断该棋子 垂直 下边 是否有4个连续的相同的棋子
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                //如果下边有连续相同的棋子
                count++;
            } else {
                //如果下边没有相同的棋子 跳出循环
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE)
            return true;

        return false;
    }

    //判断 左斜 有上5个连续的相同棋子  从右上边 往 左下边 斜
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {

        int count = 1;
        //判断该棋子 左斜  上边 是否有4个连续的相同的棋子
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                //如果上边有连续相同的棋子
                count++;
            } else {
                //如果上边没有相同的棋子 跳出循环
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;

        //判断该棋子 左斜 下边 是否有4个连续的相同的棋子
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                //如果下边有连续相同的棋子
                count++;
            } else {
                //如果下边没有相同的棋子 跳出循环
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE)
            return true;

        return false;
    }

    //判断 右斜 有上5个连续的相同棋子  从左上边 往 右下边 斜
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {

        int count = 1;
        //判断该棋子 右斜  上边 是否有4个连续的相同的棋子
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                //如果上边有连续相同的棋子
                count++;
            } else {
                //如果上边没有相同的棋子 跳出循环
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;

        //判断该棋子 右斜   下边 是否有4个连续的相同的棋子
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                //如果下边有连续相同的棋子
                count++;
            } else {
                //如果下边没有相同的棋子 跳出循环
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE)
            return true;

        return false;
    }


    //绘制棋子
    private void drawPieces(Canvas canvas) {

        //处于性能效率考虑  mWhiteArray.size() 只需要调用一次 赋值给n
        //绘制白旗
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {

            Point whitePoint = mWhiteArray.get(i);
            //绘制的棋子的坐标
            //棋子坐标 加上 1 - 棋子比例 然后 /2  * mLineHeight 是为了让棋子处于棋盘中间
            float wx = ((whitePoint.x + (1 - rationPieceOfLineHeight) / 2) * mLineHeight);
            float wy = ((whitePoint.y + (1 - rationPieceOfLineHeight) / 2) * mLineHeight);
            //绘制白棋
            canvas.drawBitmap(mWhitePiece, wx, wy, null);
        }

        //绘制黑旗
        for (int i = 0, n = mBlockArray.size(); i < n; i++) {

            Point blackPoint = mBlockArray.get(i);
            //绘制的棋子的坐标
            //棋子坐标 加上 1 - 棋子比例 然后 /2  * mLineHeight 是为了让棋子处于棋盘中间
            float bx = ((blackPoint.x + (1 - rationPieceOfLineHeight) / 2) * mLineHeight);
            float by = ((blackPoint.y + (1 - rationPieceOfLineHeight) / 2) * mLineHeight);
            //绘制黑棋
            canvas.drawBitmap(mBlackPiece, bx, by, null);
        }
    }

    //获取合理的棋盘坐标 而不是view上的 x y 坐标 以棋盘上的每个点 作为xy点
    // 在某个区域点击放入棋子 就把棋子放入最接近棋盘坐标点的位置
    private Point getValidPoint(int x, int y) {

        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    //绘制棋盘
    private void drawBoard(Canvas canvas) {

        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        //画出横线
        for (int i = 0; i < MAX_LINE; i++) {
            //横线起始 结束 坐标
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);
            //纵坐标
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);
        }

    }

    //再来一局
    public void restart(){
        mBlockArray.clear();
        mWhiteArray.clear();
        mIsGameOver  = false;
        mIsWhiteWinner = false;
        invalidate();
    }

    /*
     view的存储与恢复 最重要的一点就是 在xml 控件上加入id
     */
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    //对棋子位置进行存储
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlockArray);
        return bundle;
    }

    //当Activity重建的时候 view会调用该方法 恢复棋盘
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlockArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
