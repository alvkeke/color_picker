package com.example.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ColorPicker extends View {

    int midX, midY;
    int radius;

    float touchX, touchY;

    Paint wheelPaint;
    Paint paint;

    int[] colors;

    int mReturnColor;

    public ColorPicker(Context context) {
        super(context);
        init();
    }
    public ColorPicker(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }
    public ColorPicker(Context context, AttributeSet attrs, int style){
        super(context, attrs, style);
        init();
    }

    void init(){
        wheelPaint = new Paint();
        paint = new Paint();

        paint.setColor(0xAA000000);

        int colorAngleStep = 360/12;

        colors = new int[12];
        float[] hsv = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = i * colorAngleStep;
            //HSVToColor,一组连续的颜色，从0分布到360.
            colors[i] = Color.HSVToColor(hsv);
        }

    }

    int getColor(){
        return mReturnColor;
    }

    void createPaint(){
        SweepGradient sweepGradient = new SweepGradient(midX, midY, colors, null);
        RadialGradient radialGradient = new RadialGradient(midX, midY, radius, 0xFFFFFFFF,
                0x00FFFFFF, Shader.TileMode.CLAMP);
        ComposeShader shader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);
        wheelPaint.setShader(sweepGradient);
    }

    private int ave(int s, int d, float p) {
        return s + java.lang.Math.round(p * (d - s));
    }

    private int toColor(int colors[], float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int)p;
        p -= i;

        // now p is just the fractional part [0...1) and i is the index
        int c0 = colors[i];
        int c1 = colors[i+1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        midX = w/2;
        midY = h/2;

        radius = Math.min(midX, midY);

        createPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(midX, midY, radius, wheelPaint);
        //paint.setColor(Color.WHITE);
        //canvas.drawCircle(midX, midY, (float)radius/2, paint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (int i = 0; i<360; i+=5){
                canvas.drawArc(touchX-15, touchY-15, touchX+15, touchY+15, i, 80, false, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            touchX = event.getX();
            touchY = event.getY();
            float cy = touchY - midY;
            float cx = touchX - midX;

            double angle = Math.atan2(cy, cx);
            double unit = angle/(2*3.141592653589f);
            if(unit<0){
                unit+=1;
            }
            mReturnColor = toColor(colors, (float) unit);

            setBackgroundColor(mReturnColor);
        }
        return true;
    }
}
