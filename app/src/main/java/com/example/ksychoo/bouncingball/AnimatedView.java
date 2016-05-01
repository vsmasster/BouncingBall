package com.example.ksychoo.bouncingball;

/**
 * Created by ksychoo on 24.04.16.
 */
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class AnimatedView extends ImageView implements SensorEventListener{
    private Context mContext;
    double x = -1;
    double y = -1;
    private double xVelocity = 0;
    private double yVelocity = 0;
    private double xGravity = 0;
    private double yGravity = 0;
    private Handler h;
    private final int FRAME_RATE = 30;
    private double lastTime;
    BitmapDrawable ball;

    private SensorManager sensorManager;


    public AnimatedView(Context context, AttributeSet attrs)  {
        super(context, attrs);
        mContext = context;
        h = new Handler();
        lastTime = System.currentTimeMillis() / 100.0;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);
        ball = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.ball);

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX() - ball.getBitmap().getWidth() / 2;
                y = event.getY() - ball.getBitmap().getHeight() / 2;

                xVelocity = 0;
                yVelocity = 0;
                return false;
            }
        });
    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
    protected void onDraw(Canvas c) {
        if (x<0 && y <0) {
            x = this.getWidth()/2;
            y = this.getHeight()/2;
        } else {
            double currTime = System.currentTimeMillis() / 100.0;
            yVelocity += (currTime - lastTime) * yGravity;
            xVelocity += (currTime - lastTime) * xGravity;
            lastTime = currTime;

            x += xVelocity;
            y += yVelocity;

            if ((x > this.getWidth() - ball.getBitmap().getWidth()) || (x < 0)) {
                xVelocity = xVelocity*-1;
                if (x < 0) x = 0;
                else x = this.getWidth() - ball.getBitmap().getWidth();
            }
            if ((y > this.getHeight() - ball.getBitmap().getHeight()) || (y < 0)) {
                yVelocity = yVelocity*-1;
                if (y < 0) y = 0;
                else y = this.getHeight() - ball.getBitmap().getHeight();
            }

        }
        c.drawBitmap(ball.getBitmap(), (int)x, (int)y, null);
        h.postDelayed(r, FRAME_RATE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_GRAVITY){
            xGravity = event.values[0] * (-1.0);
            yGravity = event.values[1];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}