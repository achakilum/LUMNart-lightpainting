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
import com.bluelithalo.lumnart.util.AABB;


public class AABBSurfaceView extends SurfaceView implements     SurfaceHolder.Callback,
        SurfaceView.OnTouchListener
{
    private OnAABBEditListener mListener;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private AABB aabb;

    boolean movingTopLeft;
    boolean movingTopRight;
    boolean movingBottomLeft;
    boolean movingBottomRight;

    private final float TOUCH_RADIUS = 100.0f;

    public AABBSurfaceView(Context newContext)
    {
        super(newContext);
        initView();
    }

    public AABBSurfaceView(Context newContext, AttributeSet attrs)
    {
        super(newContext, attrs);
        initView();
    }

    public AABBSurfaceView(Context newContext, AttributeSet attrs, int defStyle)
    {
        super(newContext, attrs, defStyle);
        initView();

    }

    private void initView()
    {
        aabb = null;

        movingTopLeft = false;
        movingTopRight = false;
        movingBottomLeft = false;
        movingBottomRight = false;

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
        if (aabb == null) { aabb = new AABB(); }

        float aabbMinXLeft = (aabb.getMinimumX() * this.getWidth()) + (this.getWidth() / 2.0f);
        float aabbMinYBottom = -((aabb.getMinimumY() * this.getHeight()) - (this.getHeight() / 2.0f));
        float aabbMaxXRight = (aabb.getMaximumX() * this.getWidth()) + (this.getWidth() / 2.0f);
        float aabbMaxYTop = -((aabb.getMaximumY() * this.getHeight()) - (this.getHeight() / 2.0f));

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

        // Draw AABB
        paint.setColor(getResources().getColor(R.color.section_aabb_color));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(aabbMinXLeft, aabbMaxYTop, aabbMaxXRight, aabbMinYBottom, paint);

        // Draw AABB outline
        paint.setColor(getResources().getColor(R.color.section_aabb_outline_color));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(aabbMinXLeft, aabbMaxYTop, aabbMaxXRight, aabbMinYBottom, paint);

        // Draw touchable corner buttons
        paint.setColor(getResources().getColor(R.color.section_aabb_outline_color));
        paint.setStrokeWidth(2.5f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(aabbMinXLeft, aabbMinYBottom, TOUCH_RADIUS, paint);
        canvas.drawCircle(aabbMaxXRight, aabbMinYBottom, TOUCH_RADIUS, paint);
        canvas.drawCircle(aabbMinXLeft, aabbMaxYTop, TOUCH_RADIUS, paint);
        canvas.drawCircle(aabbMaxXRight, aabbMaxYTop, TOUCH_RADIUS, paint);

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            float x = event.getX();
            float y = event.getY();

            float aabbMinXLeft = (aabb.getMinimumX() * this.getWidth()) + (this.getWidth() / 2.0f);
            float aabbMinYBottom = -((aabb.getMinimumY() * this.getHeight()) - (this.getHeight() / 2.0f));
            float aabbMaxXRight = (aabb.getMaximumX() * this.getWidth()) + (this.getWidth() / 2.0f);
            float aabbMaxYTop = -((aabb.getMaximumY() * this.getHeight()) - (this.getHeight() / 2.0f));

            if (getDistance(x, y, aabbMinXLeft, aabbMinYBottom) <= TOUCH_RADIUS)
            {
                movingBottomLeft = true;
            }
            else
            if (getDistance(x, y, aabbMaxXRight, aabbMinYBottom) <= TOUCH_RADIUS)
            {
                movingBottomRight = true;
            }
            else
            if (getDistance(x, y, aabbMinXLeft, aabbMaxYTop) <= TOUCH_RADIUS)
            {
                movingTopLeft = true;
            }
            else
            if (getDistance(x, y, aabbMaxXRight, aabbMaxYTop) <= TOUCH_RADIUS)
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
                aabb.setMinimumX((x - (this.getWidth() / 2.0f)) / (this.getWidth() * 1.0f));
                aabb.setMinimumY(-((y - (this.getHeight() / 2.0f)) / (this.getHeight() * 1.0f)));
            }

            if (movingBottomRight)
            {
                aabb.setMaximumX((x - (this.getWidth() / 2.0f)) / (this.getWidth() * 1.0f));
                aabb.setMinimumY(-((y - (this.getHeight() / 2.0f)) / (this.getHeight() * 1.0f)));
            }

            if (movingTopLeft)
            {
                aabb.setMinimumX((x - (this.getWidth() / 2.0f)) / (this.getWidth() * 1.0f));
                aabb.setMaximumY(-((y - (this.getHeight() / 2.0f)) / (this.getHeight() * 1.0f)));
            }

            if (movingTopRight)
            {
                aabb.setMaximumX((x - (this.getWidth() / 2.0f)) / (this.getWidth() * 1.0f));
                aabb.setMaximumY(-((y - (this.getHeight() / 2.0f)) / (this.getHeight() * 1.0f)));
            }

            draw();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            float minX = Math.min(aabb.getMinimumX(), aabb.getMaximumX());
            float maxX = Math.max(aabb.getMinimumX(), aabb.getMaximumX());
            aabb.setMinimumX(minX);
            aabb.setMaximumX(maxX);

            float minY = Math.min(aabb.getMinimumY(), aabb.getMaximumY());
            float maxY = Math.max(aabb.getMinimumY(), aabb.getMaximumY());
            aabb.setMinimumY(minY);
            aabb.setMaximumY(maxY);

            movingBottomLeft = false;
            movingBottomRight = false;
            movingTopLeft = false;
            movingTopRight = false;

            draw();
            mListener.onEditAABB(aabb);
            return true;
        }

        return false;
    }

    private float getDistance(float x0, float y0, float x1, float y1)
    {
        return (float) Math.sqrt(((x1-x0)*(x1-x0)) + ((y1-y0)*(y1-y0)));
    }

    public AABB getAABB()
    {
        return aabb;
    }

    public void setAABB(AABB newAABB)
    {
        boolean firstAABB = (aabb == null);
        aabb = new AABB(newAABB);
        if (!firstAABB) { draw(); }
    }

    public OnAABBEditListener getOnAABBEditListener()
    {
        return mListener;
    }

    public void setOnAABBEditListener(OnAABBEditListener newOnAABBEditListener)
    {
        mListener = newOnAABBEditListener;
    }

    public interface OnAABBEditListener
    {
        void onEditAABB(AABB editedAABB);
    }
}