package com.bluelithalo.lumnart.util;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.bluelithalo.lumnart.App;

import java.util.Arrays;
import java.util.Stack;

/**
 * Utilities that allow initialization/de-initialization/access to the OpenGL shader and manipulation of its transformation matrices.
 * These utilities maintain a view matrix stack and a model matrix stack.
 * It is necessary to push a current matrix into its respective stack if there is a plan to reuse that same matrix after a single set of operations (by popping it).
 * Once the matrices are manipulated, it is necessary to load all of them into the shader before the OpenGL draw call occurs.
 */
public class ShaderUtils
{
    public static GLShader shader = null;

    /**
     * The perspective matrix.
     */
    public static float[] pMatrix = null;
    /**
     * The view matrix.
     */
    public static float[] vMatrix = null;
    /**
     * The model matrix
     */
    public static float[] mMatrix = null;
    /**
     * The identity matrix
     */
    public static float[] iMatrix = null;

    private static int pMatrixUniform = -1;
    private static int vMatrixUniform = -1;
    private static int mMatrixUniform = -1;

    private static int vertexShaderProperty = -1;
    private static int fragmentShaderProperty = -1;
    private static int programProperty = -1;

    private static Stack<float[]> mMatrixStack = null;
    private static Stack<float[]> vMatrixStack = null;

    /**
     * Initializes the OpenGL shader program, its matrix buffers, and the matrix stacks.
     */
    public static void initializeShaderProgram(boolean editorMode)
    {
        shader = new GLShader();
        iMatrix = new float[16];
        pMatrix = new float[16];
        vMatrix = new float[16];
        mMatrix = new float[16];

        mMatrixStack = new Stack<float[]>();
        vMatrixStack = new Stack<float[]>();

        float aspectRatio = App.getAspectRatio(false);

        float left = (editorMode) ? -0.5f : (-0.5f / aspectRatio);
        float right = (editorMode) ? 0.5f : (0.5f / aspectRatio);
        float bottom = -0.5f;
        float top = 0.5f;
        float near = -100.0f;
        float far = 100.0f;

        Matrix.setIdentityM(iMatrix, 0);
        Matrix.setIdentityM(pMatrix, 0);
        Matrix.setIdentityM(vMatrix, 0);
        Matrix.setIdentityM(mMatrix, 0);

        Matrix.orthoM(pMatrix, 0, left, right, bottom, top, near, far);

        pMatrixUniform = shader.getUniform(GLShader.Uniform.P_MATRIX);
        vMatrixUniform = shader.getUniform(GLShader.Uniform.V_MATRIX);
        mMatrixUniform = shader.getUniform(GLShader.Uniform.M_MATRIX);

        vertexShaderProperty = shader.getProgramProperty(GLShader.ProgramProperty.VERTEX_SHADER);
        fragmentShaderProperty = shader.getProgramProperty(GLShader.ProgramProperty.FRAGMENT_SHADER);
        programProperty = shader.getProgramProperty(GLShader.ProgramProperty.PROGRAM);
    }

    /**
     * Frees the OpenGL shader program from memory.
     */
    public static void freeShaderProgram()
    {
        GLES20.glDetachShader(programProperty, vertexShaderProperty);
        GLES20.glDetachShader(programProperty, fragmentShaderProperty);

        GLES20.glDeleteShader(vertexShaderProperty);
        GLES20.glDeleteShader(fragmentShaderProperty);

        GLES20.glDeleteProgram(programProperty);
    }

    /**
     * Pushes the current view matrix to its stack.
     */
    public static void pushViewMatrix()
    {
        if (vMatrix == null)
        {
            vMatrixStack.push(Arrays.copyOf(iMatrix, iMatrix.length));
            return;
        }

        vMatrixStack.push(Arrays.copyOf(vMatrix, vMatrix.length));
    }

    /**
     * Pops a view matrix from its stack to take the place of the current view matrix.
     */
    public static void popViewMatrix()
    {
        if (vMatrixStack.empty())
        {
            vMatrix = Arrays.copyOf(iMatrix, iMatrix.length);
            return;
        }

        vMatrix = vMatrixStack.pop();
    }

    /**
     * Pushes the current model matrix to its stack.
     */
    public static void pushModelMatrix()
    {
        if (mMatrix == null)
        {
            mMatrixStack.push(Arrays.copyOf(iMatrix, iMatrix.length));
            return;
        }

        mMatrixStack.push(Arrays.copyOf(mMatrix, mMatrix.length));
    }

    /**
     * Pops a model matrix from its stack to take the place of the current model matrix.
     */
    public static void popModelMatrix()
    {
        if (mMatrixStack.empty())
        {
            mMatrix = Arrays.copyOf(iMatrix, iMatrix.length);
            return;
        }

        mMatrix = mMatrixStack.pop();
    }

    /**
     * Loads the current matrices into the active OpenGL shader program.
     */
    public static void setMatrixUniforms()
    {
        GLES20.glUniformMatrix4fv(pMatrixUniform, 1, false, pMatrix, 0);
        GLES20.glUniformMatrix4fv(vMatrixUniform, 1, false, vMatrix, 0);
        GLES20.glUniformMatrix4fv(mMatrixUniform, 1, false, mMatrix, 0);
    }

    /**
     * Resets all matrices (except the perspective matrix) to the identity matrix.
     */
    public static void resetMatrixUniforms()
    {
        GLES20.glUniformMatrix4fv(pMatrixUniform, 1, false, pMatrix, 0); // Perspective projection must remain constant
        GLES20.glUniformMatrix4fv(vMatrixUniform, 1, false, iMatrix, 0);
        GLES20.glUniformMatrix4fv(mMatrixUniform, 1, false, iMatrix, 0);
    }


}
