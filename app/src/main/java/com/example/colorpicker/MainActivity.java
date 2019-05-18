package com.example.colorpicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements ColorPicker.colorPickerCallback{


    ImageView imageView;
    ColorPicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        picker = findViewById(R.id.color_picker);
        imageView = findViewById(R.id.image);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        imageView.setBackgroundColor(picker.getColor());
        return super.onTouchEvent(event);
    }

    @Override
    public void colorChange(int color) {
        imageView.setBackgroundColor(color);
    }
}
