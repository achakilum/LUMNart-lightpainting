package com.bluelithalo.lumnart;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.bluelithalo.lumnart.pattern.Pattern;

public class GLTestView extends GLSurfaceView
{
    private final GLTestRenderer lRenderer;

    public GLTestView(Context context, Pattern pattern, boolean editorMode)
    {
        super(context);

        setEGLContextClientVersion(2);

        lRenderer = new GLTestRenderer();
        lRenderer.pattern = pattern;
        lRenderer.editorMode = editorMode;

        setRenderer(lRenderer);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN)
        {
            lRenderer.held = true;
        }
        else
        if (action == MotionEvent.ACTION_UP)
        {
            lRenderer.held = false;
        }

        return true;
    }
}
