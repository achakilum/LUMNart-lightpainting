package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.bluelithalo.lumnart.R;


public class LightAngleSurfaceView extends SurfaceView implements  SurfaceHolder.Callback,
        SurfaceView.OnTouchListener
{
    private OnLightAngleEditListener mListener;
    private SurfaceHolder surfaceHolder;
    private Paint paint;

    private boolean firstDrawing;
    private float angle;
    private boolean editingEnd;

    private float touchStartX;
    private float touchStartY;
    private float touchStartAngle;

    private static final float ROTATION_SENSITIVITY = 0.5f;

    public LightAngleSurfaceView(Context newContext)
    {
        super(newContext);
        initView();
    }

    public LightAngleSurfaceView(Context newContext, AttributeSet attrs)
    {
        super(newContext, attrs);
        initView();
    }

    public LightAngleSurfaceView(Context newContext, AttributeSet attrs, int defStyle)
    {
        super(newContext, attrs, defStyle);
        initView();

    }

    private void initView()
    {
        firstDrawing = true;
        angle = 0.0f;
        editingEnd = false;

        this.setFocusable(true);

        if (surfaceHolder == null)
        {
            surfaceHolder = this.getHolder();
            surfaceHolder.addCallback(this);
        }

        if (paint == null)
        {
            paint = new Paint();
        }

        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setOnTouchListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        this.setBackgroundColor(Color.BLACK);
        draw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {

    }

    private void draw()
    {
        firstDrawing = false;
        float circleRadius = this.getWidth() * 0.4f; // Square canvas, so no need for height in calculation

        float centerX = this.getWidth() / 2.0f;
        float centerY = this.getHeight() / 2.0f;
        float pointerX = centerX + (circleRadius * (float) Math.cos(Math.toRadians(-angle + 90.0f)));
        float pointerY = centerY - (circleRadius * (float) Math.sin(Math.toRadians(-angle + 90.0f)));

        Canvas canvas = surfaceHolder.lockCanvas();

        // Draw background
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);

        // Draw circle
        paint.setColor((editingEnd) ? getResources().getColor(R.color.light_angle_end_color) : getResources().getColor(R.color.light_angle_start_color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        canvas.drawCircle(centerX, centerY, circleRadius, paint);

        // Draw pointer
        paint.setColor((editingEnd) ? getResources().getColor(R.color.light_angle_end_color) : getResources().getColor(R.color.light_angle_start_color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        canvas.drawLine(centerX, centerY, pointerX, pointerY, paint);

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        this.getParent().requestDisallowInterceptTouchEvent(true);

        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            touchStartX = event.getX();
            touchStartY = event.getY();
            touchStartAngle = angle;

            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            float x = event.getX();
            float y = event.getY();

            float dx = ((y > this.getHeight() / 2.0f) ? -1.0f : 1.0f) * (x - touchStartX);
            float dy = ((x < this.getWidth() / 2.0f) ? -1.0f : 1.0f) * (y - touchStartY);

            angle = touchStartAngle + (ROTATION_SENSITIVITY * (dx + dy));

            draw();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            draw();
            mListener.onEditLightAngle(angle, editingEnd);
            return true;
        }

        return false;
    }

    public float getAngle()
    {
        return angle;
    }

    public void setAngle(float newAngle)
    {
        angle = newAngle;
        if (!firstDrawing) { draw(); }
    }

    public boolean isEditingEnd()
    {
        return editingEnd;
    }

    public void setEditingEnd(boolean newEditingEnd)
    {
        editingEnd = newEditingEnd;
    }

    public OnLightAngleEditListener getOnLightAngleEditListener()
    {
        return mListener;
    }

    public void setOnLightDimensionsEditListener(OnLightAngleEditListener newOnLightAngleEditListener)
    {
        mListener = newOnLightAngleEditListener;
    }

    public interface OnLightAngleEditListener
    {
        void onEditLightAngle(float editedAngle, boolean isEditingEnd);
    }
}