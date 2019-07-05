package com.bluelithalo.lumnart.util;

import android.opengl.GLES20;
import android.util.Log;

/**
 * The multi-channel signed distance field shader for the purpose of rendering pseudo-vector graphics.
 * Outlines of text and shapes are thrown into a multi-channel signed distance field generator to create assets for LUMNart.
 * The distance fields in the texture preserve a majority of the original outline,
 * but the multiple channels of colors in the texture work to preserve sharp corners in the outline.
 * Thus, MSDF textures can be scaled up to 64x their original size without loss in quality, and therefore reduce texture memory usage.
 * Note that this shader supports the raw rendering of textures as well, for the purpose of rendering user-selected images.
 */
public class GLShader
{
    /**
     * Vertex shader attribute values
     */
    public enum Attribute
    {
        POSITION, TEX_COORD
    }

    /**
     * Vertex/fragment shader uniform values
     */
    public enum Uniform
    {
        P_MATRIX, V_MATRIX, M_MATRIX,
        COLOR,
        SECTION_AABB, ALPHA,
        TEXTURE_SAMPLER, MSDF_SMOOTHING, OUTLINE_WIDTH
    }

    /**
     * Properties of the whole shader program
     */
    public enum ProgramProperty
    {
        VERTEX_SHADER, FRAGMENT_SHADER, PROGRAM
    }

    private int vertexShader;
    private int fragmentShader;
    private int program;

    private int aPosition;
    private int aTexCoord;

    private int uPMatrix;
    private int uVMatrix;
    private int uMMatrix;

    private int uColor;
    private int uSectionAABB;
    private int uAlpha;
    private int uTextureSampler;
    private int uMSDFSmoothing;
    private int uOutlineWidth;

    /**
     * Constructs the MSDF shader for OpenGL.
     * The code for the vertex shader and fragment shader is compiled and linked to the shader program,
     * and the program is set to be active in the OpenGL context.
     */
    public GLShader()
    {

        // Compile shaders
        String vertexShaderSource = "" +
                "attribute vec3 aPosition;" +
                "attribute vec2 aTexCoord;" +
                "" +
                "uniform mat4 uPMatrix;" +
                "uniform mat4 uVMatrix;" +
                "uniform mat4 uMMatrix;" +
                "" +
                "varying vec4 vVMPosition;" +
                "varying vec2 vTexCoord;" +
                "" +
                "void main(void)" +
                "{" +
                "   vVMPosition = uVMatrix * uMMatrix * vec4(aPosition, 1.0);" +
                "   vTexCoord = aTexCoord;" +
                "" +
                "   gl_Position = uPMatrix * vVMPosition;" +
                "}" +
                "";


        String fragmentShaderSource = "" +
                "precision mediump float;" +
                "" +
                "uniform vec4 uSectionAABB;" + // vec4(xMin, xMax, yMin, yMax)
                "uniform float uAlpha;" +
                "uniform sampler2D uTextureSampler;" +
                "uniform float uMSDFSmoothing;" +
                "uniform float uOutlineWidth;" +
                "uniform vec4 uColor;" +
                "" +
                "varying vec4 vVMPosition;" +
                "varying vec2 vTexCoord;" +
                "" +
                "void main(void)" +
                "{" +
                "   vec4 texColor = texture2D(uTextureSampler, vTexCoord);" +
                "   vec4 lightColor = uColor;" +
                "" +
                "   float smoothing = min(uMSDFSmoothing, 0.5);" + // using min() to prevent smoothstep bounds from going out of [0,1] bound
                "   float signedDistance = max(min(texColor.r, texColor.g), min(max(texColor.r, texColor.g), texColor.b));" + // color component median calculation
                "   lightColor.a *= uAlpha * smoothstep(0.5 - smoothing, 0.5 + smoothing, signedDistance) * (1.0 - smoothstep(uOutlineWidth - smoothing, uOutlineWidth + smoothing, signedDistance));" +
                "   lightColor.a *= step(uSectionAABB[0], vVMPosition.x) * step(vVMPosition.x, uSectionAABB[1]) * step(uSectionAABB[2], vVMPosition.y) * step(vVMPosition.y, uSectionAABB[3]);" +
                "" +
                "   gl_FragColor = lightColor;" +
                "}" +
                "";

        vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderSource);
        GLES20.glCompileShader(vertexShader);

        fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderSource);
        GLES20.glCompileShader(fragmentShader);

        // Link shader program
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);

        GLES20.glLinkProgram(program);

        // Enable shader program
        GLES20.glUseProgram(program);

        aPosition = GLES20.glGetAttribLocation(program, "aPosition");
        aTexCoord = GLES20.glGetAttribLocation(program, "aTexCoord");
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glEnableVertexAttribArray(aTexCoord);

        uPMatrix = GLES20.glGetUniformLocation(program, "uPMatrix");
        uVMatrix = GLES20.glGetUniformLocation(program, "uVMatrix");
        uMMatrix = GLES20.glGetUniformLocation(program, "uMMatrix");

        uColor = GLES20.glGetUniformLocation(program, "uColor");
        uSectionAABB = GLES20.glGetUniformLocation(program, "uSectionAABB");
        uAlpha = GLES20.glGetUniformLocation(program, "uAlpha");
        uTextureSampler = GLES20.glGetUniformLocation(program, "uTextureSampler");
        uMSDFSmoothing = GLES20.glGetUniformLocation(program, "uMSDFSmoothing");
        uOutlineWidth = GLES20.glGetUniformLocation(program, "uOutlineWidth");

        if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
        {
            //Log.e("GLShader", "Failed to initialize shader program.");
        }
    }

    /**
     * Retrieve a shader attribute ID from the current OpenGL context.
     * @param attribName the shader attribute
     * @return the shader attribute's ID
     */
    public int getAttrib(GLShader.Attribute attribName)
    {
        int attrib = 0;

        switch(attribName)
        {
            case POSITION:
                attrib = aPosition;
                break;
            case TEX_COORD:
                attrib = aTexCoord;
                break;
            default:
                attrib = -5;
                break;
        }

        return attrib;
    }

    /**
     * Retrieve a shader uniform ID from the current OpenGL context.
     * @param uniformName the shader uniform
     * @return the shader uniform's ID
     */
    public int getUniform(GLShader.Uniform uniformName)
    {
        int uniform = 0;

        switch(uniformName)
        {
            case P_MATRIX:
                uniform = uPMatrix;
                break;
            case V_MATRIX:
                uniform = uVMatrix;
                break;
            case M_MATRIX:
                uniform = uMMatrix;
                break;
            case COLOR:
                uniform = uColor;
                break;

            case SECTION_AABB:
                uniform = uSectionAABB;
                break;
            case ALPHA:
                uniform = uAlpha;
                break;
            case TEXTURE_SAMPLER:
                uniform = uTextureSampler;
                break;
            case MSDF_SMOOTHING:
                uniform = uMSDFSmoothing;
                break;
            case OUTLINE_WIDTH:
                uniform = uOutlineWidth;
                break;
            default:
                uniform = -5;
                break;
        }

        return uniform;
    }

    /**
     * Retrieve a shader program property ID from the current OpenGL context.
     * @param programPropertyName the shader program property
     * @return the shader program property's ID
     */
    public int getProgramProperty(GLShader.ProgramProperty programPropertyName)
    {
        int programProperty = 0;

        switch(programPropertyName)
        {
            case VERTEX_SHADER:
                programProperty = vertexShader;
                break;
            case FRAGMENT_SHADER:
                programProperty = fragmentShader;
                break;
            case PROGRAM:
                programProperty = program;
                break;
            default:
                programProperty = -5;
                break;
        }

        return programProperty;
    }

}
