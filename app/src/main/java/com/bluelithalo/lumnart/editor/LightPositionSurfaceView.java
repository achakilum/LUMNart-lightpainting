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

import com.bluelithalo.lumnart.App;
import com.bluelithalo.lumnart.R;

import java.util.Arrays;


public class LightPositionSurfaceView extends SurfaceView implements    SurfaceHolder.Callback,
                                                                        SurfaceView.OnTouchListener
{
    private OnLightPositionEditListener mListener;
    private SurfaceHolder surfaceHolder;
    private Paint paint;

    private float[] startPosition;
    private float[] endPosition;

    private boolean movingStartPosition;
    private boolean movingEndPosition;

    private final float TOUCH_RADIUS = 100.0f;

    public LightPositionSurfaceView(Context newContext)
    {
        super(newContext);
        initView();
    }

    public LightPositionSurfaceView(Context newContext, AttributeSet attrs)
    {
        super(newContext, attrs);
        initView();
    }

    public LightPositionSurfaceView(Context newContext, AttributeSet attrs, int defStyle)
    {
        super(newContext, attrs, defStyle);
        initView();

    }

    private void initView()
    {
        startPosition = null;
        endPosition = null;

        movingStartPosition = false;
        movingEndPosition = false;

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
        if (startPosition == null || endPosition == null)
        {
            startPosition = new float[]{0.0f, 0.0f};
            endPosition = new float[]{0.0f, 0.0f};
        }

        float startX = (startPosition[0] * this.getWidth()) + (this.getWidth() / 2.0f);
        float startY = -((startPosition[1] * this.getHeight()) - (this.getHeight() / 2.0f));
        float endX = (endPosition[0] * this.getWidth()) + (this.getWidth() / 2.0f);
        float endY = -((endPosition[1] * this.getHeight()) - (this.getHeight() / 2.0f));

        float aspectRatio = App.getAspectRatio(true);
        float screenMinX = (this.getWidth() / 2.0f) - ((this.getWidth() * aspectRatio) / 2.0f);
        float screenMinY = 0.0f;
        float screenMaxX = (this.getWidth() / 2.0f) + ((this.getWidth() * aspectRatio) / 2.0f);
        float screenMaxY = this.getHeight() * 1.0f;

        Canvas canvas = surfaceHolder.lockCanvas();

        // Draw background
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);

        // Draw screen dimensions
        paint.setColor(getResources().getColor(R.color.surface_view_screen_color));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(screenMinX, screenMaxY, screenMaxX, screenMinY, paint);

        // Draw bridge
        paint.setColor(getResources().getColor(R.color.light_position_bridge_color));
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(startX, startY, endX, endY, paint);

        // Draw start position
        paint.setColor(getResources().getColor(R.color.light_position_start_color));
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(startX, startY, TOUCH_RADIUS, paint);
        canvas.drawLine(startX, startY - (TOUCH_RADIUS / 4.0f), startX, startY + (TOUCH_RADIUS / 4.0f), paint);
        canvas.drawLine(startX - (TOUCH_RADIUS / 4.0f), startY, startX + (TOUCH_RADIUS / 4.0f), startY , paint);

        // Draw end position
        paint.setColor(getResources().getColor(R.color.light_position_end_color));
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(endX, endY, TOUCH_RADIUS, paint);
        canvas.drawLine(endX, endY - (TOUCH_RADIUS / 4.0f), endX, endY + (TOUCH_RADIUS / 4.0f), paint);
        canvas.drawLine(endX - (TOUCH_RADIUS / 4.0f), endY, endX + (TOUCH_RADIUS / 4.0f), endY , paint);

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        this.getParent().requestDisallowInterceptTouchEvent(true);

        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            float x = event.getX();
            float y = event.getY();

            float startX = (startPosition[0] * this.getWidth()) + (this.getWidth() / 2.0f);
            float startY = -((startPosition[1] * this.getHeight()) - (this.getHeight() / 2.0f));
            float endX = (endPosition[0] * this.getWidth()) + (this.getWidth() / 2.0f);
            float endY = -((endPosition[1] * this.getHeight()) - (this.getHeight() / 2.0f));

            if (getDistance(x, y, startX, startY) <= TOUCH_RADIUS)
            {
                movingStartPosition = true;
            }
            else
            if (getDistance(x, y, endX, endY) <= TOUCH_RADIUS)
            {
                movingEndPosition = true;
            }

            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            float x = Math.min(Math.max(0.0f, event.getX()), this.getWidth());
            float y = Math.min(Math.max(0.0f, event.getY()), this.getHeight());

            if (movingStartPosition)
            {
                startPosition[0] = (x - (this.getWidth() / 2.0f)) / (this.getWidth() * 1.0f);
                startPosition[1] = -((y - (this.getHeight() / 2.0f)) / (this.getHeight() * 1.0f));
            }

            if (movingEndPosition)
            {
                endPosition[0] = (x - (this.getWidth() / 2.0f)) / (this.getWidth() * 1.0f);
                endPosition[1] = -((y - (this.getHeight() / 2.0f)) / (this.getHeight() * 1.0f));
            }

            draw();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            movingStartPosition = false;
            movingEndPosition = false;

            draw();
            mListener.onEditLightPosition(startPosition, endPosition);
            return true;
        }

        return false;
    }

    private float getDistance(float x0, float y0, float x1, float y1)
    {
        return (float) Math.sqrt(((x1-x0)*(x1-x0)) + ((y1-y0)*(y1-y0)));
    }

    public float[] getStartPosition()
    {
        return startPosition;
    }

    public void setStartPosition(float[] newStartPosition)
    {
        boolean firstStartPosition = (startPosition == null);
        startPosition = Arrays.copyOf(newStartPosition, 2);
        if (!firstStartPosition) { draw(); }
    }

    public float[] getEndPosition()
    {
        return endPosition;
    }

    public void setEndPosition(float[] newEndPosition)
    {
        boolean firstEndPosition = (endPosition == null);
        endPosition = Arrays.copyOf(newEndPosition, 2);
        if (!firstEndPosition) { draw(); }
    }

    public OnLightPositionEditListener getOnLightPositionEditListener()
    {
        return mListener;
    }

    public void setOnLightPositionEditListener(OnLightPositionEditListener newOnLightPositionEditListener)
    {
        mListener = newOnLightPositionEditListener;
    }

    public interface OnLightPositionEditListener
    {
        void onEditLightPosition(float[] editedStartPosition, float[] editedEndPosition);
    }
}