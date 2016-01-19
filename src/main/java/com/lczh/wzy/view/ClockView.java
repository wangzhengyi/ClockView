package com.lczh.wzy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

/**
 * Created by wzy on 16-1-19.
 */
public class ClockView extends View {
    private static final int DEFAULT_MEASURE_SIZE = 400;
    private static final int DEFAULT_PADDING = 20;
    private static final int DEFAULT_DEGREE_LEN = 60;
    private static final String TAG = "ClockView";

    private Handler mHander = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            invalidate();
        }
    };

    private Runnable updateRunnabe = new Runnable() {
        @Override
        public void run() {
            while (true) {
                mHander.sendEmptyMessage(0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        new Thread(updateRunnabe).start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 将其设置为正方形
        setMeasuredDimension(measureClockView(widthMeasureSpec),
                measureClockView(widthMeasureSpec));
    }

    private int measureClockView(int measureSpec) {
        int measureSize = MeasureSpec.getSize(measureSpec);
        int measureMode = MeasureSpec.getMode(measureSpec);
        int result = DEFAULT_MEASURE_SIZE;

        if (measureMode == MeasureSpec.EXACTLY) {
            result = measureSize;
        } else {
            if (measureMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, measureSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 获取圆心坐标
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int radius = width / 2 - DEFAULT_PADDING;

        // 画外圆
        Paint paintCircle = new Paint();
        paintCircle.setStrokeWidth(5);
        paintCircle.setAntiAlias(true);
        paintCircle.setStyle(Paint.Style.STROKE);
        // 设置画笔为蓝色
        paintCircle.setColor(Color.parseColor("#0eead1"));
        canvas.drawCircle(width / 2, height / 2, radius, paintCircle);

        // 画刻度(1~12)和数字
        Paint paintDegree = new Paint();
        paintDegree.setAntiAlias(true);
        paintDegree.setColor(Color.parseColor("#0eead1"));
        paintDegree.setStyle(Paint.Style.FILL);

        Paint paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextSize(100);
        int degreeLen;
        for (int i = 0; i < 60; i ++) {
            if (i % 5 == 0) {
                paintDegree.setStrokeWidth(10);
                degreeLen = DEFAULT_DEGREE_LEN;
                String degree = String.valueOf(i / 5);
                if (degree.equals("0")) {
                    degree = "12";
                }
                Log.e(TAG, "degree=" + degree);
                canvas.drawText(degree, (width - paintText.measureText(degree)) / 2, DEFAULT_PADDING + degreeLen + 100, paintText);
            } else {
                paintDegree.setStrokeWidth(5);
                degreeLen = DEFAULT_DEGREE_LEN / 2;
            }
            canvas.drawLine(width / 2, DEFAULT_PADDING, width / 2,
                    DEFAULT_PADDING + degreeLen, paintDegree);
            // 通过canvas旋转来避免几何计算刻度值
            canvas.rotate(6, width / 2, height / 2);
        }

        // 画时针、分针、秒针
        Paint paintHour = new Paint();
        paintHour.setAntiAlias(true);
        paintHour.setStrokeWidth(15);
        paintHour.setColor(Color.BLACK);

        Paint paintMinute = new Paint();
        paintMinute.setAntiAlias(true);
        paintMinute.setStrokeWidth(10);
        paintMinute.setColor(Color.YELLOW);

        Paint paintSecond = new Paint();
        paintSecond.setAntiAlias(true);
        paintSecond.setStrokeWidth(5);
        paintSecond.setColor(Color.RED);

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

        Log.e(TAG, "hour=" + hour + ", minute=" + minute + ", second=" + second);
        drawHour(canvas, paintHour, hour);
        drawMinute(canvas, paintMinute, minute);
        drawSecond(canvas, paintSecond, second);
    }

    private void drawSecond(Canvas canvas, Paint paintSecond, int second) {
        int startX = getMeasuredWidth() / 2;
        int startY = getMeasuredHeight() / 2;
        int len = 500;
        int stopX = (int) (len * Math.sin(second * 6.0 / 180f * Math.PI) + getMeasuredWidth() / 2);
        int stopY = (int) (len * Math.cos(second * 6.0 / 180f * Math.PI) * -1 + getMeasuredWidth() / 2);
        Log.e(TAG, "startX=" + startX + ", startY=" + startY + ", stopX=" + stopX + ", stopY=" + stopY);
        canvas.drawLine(startX, startY, stopX, stopY, paintSecond);
    }

    private void drawMinute(Canvas canvas, Paint paintMinute, int minute) {
        int startX = getMeasuredWidth() / 2;
        int startY = getMeasuredHeight() / 2;
        int len = 400;
        int stopX = (int) (len * Math.sin(minute * 6 / 180f * Math.PI) + getMeasuredWidth() / 2);
        int stopY = (int) (len * Math.cos(minute * 6 / 180f * Math.PI) * -1 + getMeasuredWidth() / 2);
        canvas.drawLine(startX, startY, stopX, stopY, paintMinute);
    }

    private void drawHour(Canvas canvas, Paint paintHour, int hour) {
        int startX = getMeasuredWidth() / 2;
        int startY = getMeasuredHeight() / 2;
        int len = 300;
        int stopX = (int) (len * Math.sin(hour * 30 / 180f * Math.PI) + getMeasuredWidth() / 2);
        int stopY = (int) (len * Math.cos(hour * 30 / 180f * Math.PI) * -1 + getMeasuredWidth() / 2);
        canvas.drawLine(startX, startY, stopX, stopY, paintHour);
    }
}
