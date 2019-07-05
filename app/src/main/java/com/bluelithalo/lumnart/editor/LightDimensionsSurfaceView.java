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


public class LightDimensionsSurfaceView extends SurfaceView implements  SurfaceHolder.Callback,
                                                                        SurfaceView.OnTouchListener
{
    private OnLightDimensionsEditListener mListener;
    private SurfaceHolder surfaceHolder;
    private Paint paint;

    private float[] startDimensions;
    private float[] endDimensions;
    private boolean uniformDimensions;

    private boolean movingTopLeft;
    private boolean movingTopRight;
    private boolean movingBottomLeft;
    private boolean movingBottomRight;

    private boolean endMovement;

    private final float TOUCH_RADIUS = 100.0f;

    public LightDimensionsSurfaceView(Context newContext)
    {
        super(newContext);
        initView();
    }

    public LightDimensionsSurfaceView(Context newContext, AttributeSet attrs)
    {
        super(newContext, attrs);
        initView();
    }

    public LightDimensionsSurfaceView(Context newContext, AttributeSet attrs, int defStyle)
    {
        super(newContext, attrs, defStyle);
        initView();

    }

    private void initView()
    {
        startDimensions = null;
        endDimensions = null;

        movingTopLeft = false;
        movingTopRight = false;
        movingBottomLeft = false;
        movingBottomRight = false;

        endMovement = false;

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
        if (startDimensions == null || endDimensions == null)
        {
            startDimensions = new float[]{0.5f, 0.5f};
            endDimensions = new float[]{0.5f, 0.5f};
        }

        float startWidth = (uniformDimensions) ? startDimensions[1] : startDimensions[0];
        float startHeight = startDimensions[1];
        float endWidth = (uniformDimensions) ? endDimensions[1] : endDimensions[0];
        float endHeight = endDimensions[1];

        float startLeft = ((-startWidth / 2.0f) * this.getWidth()) + (this.getWidth() / 2.0f);
        float startBottom = ((-startHeight / -2.0f) * this.getHeight()) + (this.getHeight() / 2.0f);
        float startRight = ((startWidth / 2.0f) * this.getWidth()) + (this.getWidth() / 2.0f);
        float startTop = ((startHeight / -2.0f) * this.getHeight()) + (this.getHeight() / 2.0f);

        float endLeft = ((-endWidth / 2.0f) * this.getWidth()) + (this.getWidth() / 2.0f);
        float endBottom = ((-endHeight / -2.0f) * this.getHeight()) + (this.getHeight() / 2.0f);
        float endRight = ((endWidth / 2.0f) * this.getWidth()) + (this.getWidth() / 2.0f);
        float endTop = ((endHeight / -2.0f) * this.getHeight()) + (this.getHeight() / 2.0f);

        float aspectRatio = App.getAspectRatio(true);
        float screenMinX = (this.getWidth() / 2.0f) - ((this.getWidth() * aspectRatio) / 2.0f);
        float screenMinY = 0.0f;
        float screenMaxX = (this.getWidth() / 2.0f) + ((this.getWidth() * aspectRatio) / 2.0f);
        float screenMaxY = this.getHeight() * 1.0f;

        float centerX = this.getWidth() / 2.0f;
        float centerY = this.getHeight() / 2.0f;

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

        // Draw start dimensions
        paint.setColor(getResources().getColor(R.color.light_dimensions_start_fill_color));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(startLeft, startTop, startRight, startBottom, paint);

        // Draw end dimensions
        paint.setColor(getResources().getColor(R.color.light_dimensions_end_fill_color));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(endLeft, endTop, endRight, endBottom, paint);

        if (!endMovement)
        {
            // Draw start dimensions outline
            paint.setColor(getResources().getColor(R.color.light_dimensions_start_outline_color));
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(startLeft, startTop, startRight, startBottom, paint);

            // Draw start dimensions' touchable corner buttons
            paint.setColor(getResources().getColor(R.color.light_dimensions_start_outline_color));
            paint.setStrokeWidth(2.5f);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(startLeft, startBottom, TOUCH_RADIUS, paint);
            canvas.drawCircle(startRight, startBottom, TOUCH_RADIUS, paint);
            canvas.drawCircle(startLeft, startTop, TOUCH_RADIUS, paint);
            canvas.drawCircle(startRight, startTop, TOUCH_RADIUS, paint);

            // Draw start dimensions' orientation guides
            paint.setColor(getResources().getColor(R.color.light_dimensions_start_outline_color));
            paint.setStrokeWidth(2.5f);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(centerX, centerY, startRight, centerY, paint);
            canvas.drawLine(centerX, centerY, centerX, startTop, paint);
        }
        else
        {
            // Draw end dimensions' outline
            paint.setColor(getResources().getColor(R.color.light_dimensions_end_outline_color));
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(endLeft, endTop, endRight, endBottom, paint);

            // Draw end dimensions' touchable corner buttons
            paint.setColor(getResources().getColor(R.color.light_dimensions_end_outline_color));
            paint.setStrokeWidth(2.5f);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(endLeft, endBottom, TOUCH_RADIUS, paint);
            canvas.drawCircle(endRight, endBottom, TOUCH_RADIUS, paint);
            canvas.drawCircle(endLeft, endTop, TOUCH_RADIUS, paint);
            canvas.drawCircle(endRight, endTop, TOUCH_RADIUS, paint);

            // Draw end dimensions' orientation guides
            paint.setColor(getResources().getColor(R.color.light_dimensions_end_outline_color));
            paint.setStrokeWidth(2.5f);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(centerX, centerY, endRight, centerY, paint);
            canvas.drawLine(centerX, centerY, centerX, endTop, paint);
        }

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        this.getParent().requestDisallowInterceptTouchEvent(true);
        boolean touchHandled = false;

        if (!endMovement)
        {
            touchHandled = this.handleTouchForStart(event);
        }
        else
        {
            touchHandled = this.handleTouchForEnd(event);
        }

        return touchHandled;
    }

    private boolean handleTouchForStart(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            float x = event.getX();
            float y = event.getY();

            float startWidth = (uniformDimensions) ? startDimensions[1] : startDimensions[0];
            float startHeight = startDimensions[1];

            float startLeft = ((-startWidth / 2.0f) * this.getWidth()) + (this.getWidth() / 2.0f);
            float startBottom = ((-startHeight / -2.0f) * this.getHeight()) + (this.getHeight() / 2.0f);
            float startRight = ((startWidth / 2.0f) * this.getWidth()) + (this.getWidth() / 2.0f);
            float startTop = ((startHeight / -2.0f) * this.getHeight()) + (this.getHeight() / 2.0f);

            if (getDistance(x, y, startLeft, startBottom) <= TOUCH_RADIUS)
            {
                movingBottomLeft = true;
            }
            else
            if (getDistance(x, y, startRight, startBottom) <= TOUCH_RADIUS)
            {
                movingBottomRight = true;
            }
            else
            if (getDistance(x, y, startLeft, startTop) <= TOUCH_RADIUS)
            {
                movingTopLeft = true;
            }
            else
            if (getDistance(x, y, startRight, startTop) <= TOUCH_RADIUS)
            {
                movingTopRight = true;
            }

            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            float x = Math.min(Math.max(0.0f, event.getX()), this.getWidth());
            float y = Math.min(Math.max(0.0f, event.getY()), this.getHeight());

            if (movingBottomLeft)
            {
                startDimensions[0] = (-2.0f * (x - (this.getWidth() / 2.0f))) / (this.getWidth() * 1.0f);
                startDimensions[1] = (2.0f * (y - (this.getHeight() / 2.0f))) / (this.getHeight() * 1.0f);
            }

            if (movingBottomRight)
            {
                startDimensions[0] = (2.0f * (x - (this.getWidth() / 2.0f))) / (this.getWidth() * 1.0f);
                startDimensions[1] = (2.0f * (y - (this.getHeight() / 2.0f))) / (this.getHeight() * 1.0f);
            }

            if (movingTopLeft)
            {
                startDimensions[0] = (-2.0f * (x - (this.getWidth() / 2.0f))) / (this.getWidth() * 1.0f);
                startDimensions[1] = (-2.0f * (y - (this.getHeight() / 2.0f))) / (this.getHeight() * 1.0f);
            }

            if (movingTopRight)
            {
                startDimensions[0] = (2.0f * (x - (this.getWidth() / 2.0f))) / (this.getWidth() * 1.0f);
                startDimensions[1] = (-2.0f * (y - (this.getHeight() / 2.0f))) / (this.getHeight() * 1.0f);
            }

            if (uniformDimensions) { startDimensions[0] = startDimensions[1]; }

            draw();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            movingBottomLeft = false;
            movingBottomRight = false;
            movingTopLeft = false;
            movingTopRight = false;

            draw();
            mListener.onEditLightDimensions(startDimensions, endDimensions);
            return true;
        }

        return false;
    }

    private boolean handleTouchForEnd(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            float x = event.getX();
            float y = event.getY();

            float endWidth = (uniformDimensions) ? endDimensions[1] : endDimensions[0];
            float endHeight = endDimensions[1];

            float endLeft = ((-endWidth / 2.0f) * this.getWidth()) + (this.getWidth() / 2.0f);
            float endBottom = ((-endHeight / -2.0f) * this.getHeight()) + (this.getHeight() / 2.0f);
            float endRight = ((endWidth / 2.0f) * this.getWidth()) + (this.getWidth() / 2.0f);
            float endTop = ((endHeight / -2.0f) * this.getHeight()) + (this.getHeight() / 2.0f);

            if (getDistance(x, y, endLeft, endBottom) <= TOUCH_RADIUS)
            {
                movingBottomLeft = true;
            }
            else
            if (getDistance(x, y, endRight, endBottom) <= TOUCH_RADIUS)
            {
                movingBottomRight = true;
            }
            else
            if (getDistance(x, y, endLeft, endTop) <= TOUCH_RADIUS)
            {
                movingTopLeft = true;
            }
            else
            if (getDistance(x, y, endRight, endTop) <= TOUCH_RADIUS)
            {
                movingTopRight = true;
            }

            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            float x = Math.min(Math.max(0.0f, event.getX()), this.getWidth());
            float y = Math.min(Math.max(0.0f, event.getY()), this.getHeight());

            if (movingBottomLeft)
            {
                endDimensions[0] = (-2.0f * (x - (this.getWidth() / 2.0f))) / (this.getWidth() * 1.0f);
                endDimensions[1] = (2.0f * (y - (this.getHeight() / 2.0f))) / (this.getHeight() * 1.0f);
            }

            if (movingBottomRight)
            {
                endDimensions[0] = (2.0f * (x - (this.getWidth() / 2.0f))) / (this.getWidth() * 1.0f);
                endDimensions[1] = (2.0f * (y - (this.getHeight() / 2.0f))) / (this.getHeight() * 1.0f);
            }

            if (movingTopLeft)
            {
                endDimensions[0] = (-2.0f * (x - (this.getWidth() / 2.0f))) / (this.getWidth() * 1.0f);
                endDimensions[1] = (-2.0f * (y - (this.getHeight() / 2.0f))) / (this.getHeight() * 1.0f);
            }

            if (movingTopRight)
            {
                endDimensions[0] = (2.0f * (x - (this.getWidth() / 2.0f))) / (this.getWidth() * 1.0f);
                endDimensions[1] = (-2.0f * (y - (this.getHeight() / 2.0f))) / (this.getHeight() * 1.0f);
            }

            if (uniformDimensions) { endDimensions[0] = endDimensions[1]; }

            draw();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            movingBottomLeft = false;
            movingBottomRight = false;
            movingTopLeft = false;
            movingTopRight = false;

            draw();
            mListener.onEditLightDimensions(startDimensions, endDimensions);
            return true;
        }

        return false;
    }

    private float getDistance(float x0, float y0, float x1, float y1)
    {
        return (float) Math.sqrt(((x1-x0)*(x1-x0)) + ((y1-y0)*(y1-y0)));
    }

    public void setEndMovement(boolean newEndMovement)
    {
        endMovement = newEndMovement;
        draw();
    }

    public float[] getStartDimensions()
    {
        return startDimensions;
    }

    public void setStartDimensions(float[] newStartDimensions)
    {
        boolean firstStartDimensions = (startDimensions == null);
        startDimensions = Arrays.copyOf(newStartDimensions, 2);
        if (uniformDimensions) { startDimensions[0] = startDimensions[1]; }
        if (!firstStartDimensions) { draw(); }
    }

    public float[] getEndDimensions()
    {
        return endDimensions;
    }

    public void setEndDimensions(float[] newEndDimensions)
    {
        boolean firstEndDimensions = (endDimensions == null);
        endDimensions = Arrays.copyOf(newEndDimensions, 2);
        if (uniformDimensions) { endDimensions[0] = endDimensions[1]; }
        if (!firstEndDimensions) { draw(); }
    }

    public boolean getUniformDimensions()
    {
        return uniformDimensions;
    }

    public void setUniformDimensions(boolean newUniformDimensions)
    {
        uniformDimensions = newUniformDimensions;
    }

    public OnLightDimensionsEditListener getOnLightDimensionsEditListener()
    {
        return mListener;
    }

    public void setOnLightDimensionsEditListener(OnLightDimensionsEditListener newOnLightDimensionsEditListener)
    {
        mListener = newOnLightDimensionsEditListener;
    }

    public interface OnLightDimensionsEditListener
    {
        void onEditLightDimensions(float[] editedStartDimensions, float[] editedEndDimensions);
    }
}