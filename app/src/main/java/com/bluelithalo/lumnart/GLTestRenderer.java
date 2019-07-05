package com.bluelithalo.lumnart;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.bluelithalo.lumnart.example.ScreenBoundsPattern;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.util.GLESTBAM;
import com.bluelithalo.lumnart.util.ShaderUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLTestRenderer implements GLSurfaceView.Renderer
{
    public Pattern pattern;
    public boolean editorMode;
    public boolean held;
    public boolean tapped;
    public int i;

    private Pattern screenBounds;


    @Override
    public void onSurfaceCreated(GL10 nope, EGLConfig config)
    {
        ShaderUtils.initializeShaderProgram(editorMode);
        ShaderUtils.setMatrixUniforms();

        GLESTBAM.initialize();

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        screenBounds = new ScreenBoundsPattern();
        screenBounds.prepare();

        pattern.prepare();
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        i = 0;
    }

    @Override
    public void onSurfaceChanged(GL10 nope, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 nope)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);

        if (!held)
        {
            tapped = false;
        }
        else
        {
            if (!tapped)
            {
                //Log.i("GLTestRenderer", pattern.toJSONString());

                if (i % 2 == 0)
                {
                    pattern.clean();
                }
                else
                {
                    pattern.reset();
                    pattern.prepare();
                }

                i++;
                tapped = true;
            }
        }

        if (i % 2 == 0)
        {
            pattern.draw();
            pattern.stepForward();
        }

        if (editorMode)
        {
            screenBounds.draw();
        }
    }
}
