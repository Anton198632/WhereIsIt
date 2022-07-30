package com.example.whereisit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class InversRectView extends View {

    float density = 1;

    public InversRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics metrics = new DisplayMetrics();
        ((AppCompatActivity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;

    }


    @Override
    protected void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#3E000000"));

        Rect rect = new Rect();
        rect.left = 0;
        rect.top = 0;
        rect.right = canvas.getWidth();
        rect.bottom = (int) (32 * density);
        canvas.drawRect(rect, paint);

        rect = new Rect();
        rect.left = 0;
        rect.top = (int) (32 * density);
        ;
        rect.right = (int) (32 * density);
        rect.bottom = (int) (32 * density + 50 * density);
        canvas.drawRect(rect, paint);

        rect = new Rect();
        rect.left = canvas.getWidth() - (int) (32 * density);
        rect.top = (int) (32 * density);
        ;
        rect.right = canvas.getWidth();
        rect.bottom = (int) (32 * density + 50 * density);
        canvas.drawRect(rect, paint);

        rect = new Rect();
        rect.left = 0;
        rect.top = (int) (32 * density + 50 * density);
        rect.right = canvas.getWidth();
        rect.bottom = canvas.getHeight();
        canvas.drawRect(rect, paint);

        super.onDraw(canvas);
    }
}
