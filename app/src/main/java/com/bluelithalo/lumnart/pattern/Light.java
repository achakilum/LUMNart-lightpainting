package com.bluelithalo.lumnart.pattern;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.bluelithalo.lumnart.util.GLShader;
import com.bluelithalo.lumnart.util.ShaderUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

/**
 * The LUMNart light source: a single, illuminated, animated object.  This is the technical equivalent of a physical light source that would be used in long-exposure photography to create light paintings.
 * This is a single light source with an initial silhouette for its appearance (denoted by its representational Figure). It is displayed on a frame (such as the entire mobile screen, or a Section of a Pattern),
 * and it is free to have its rotation, position, size, color, and other properties dynamically shifting, unlike that of a constant physical light source such as a flashlight.
 * This is the essential unit of LUMNart light paintings, and the artist is given the freedom to manipulate these units in order to create truly unique works of photographic art.
 *
 * Before you call the prepare() and draw() functions, make sure that this Light object is referenced within an active OpenGL ES context, and ensure that the GLESTBAM texture manager and ShaderUtils matrices are initialized.
 * The whole animation will fail to render otherwise.
 */
public class Light implements JSONizable
{
    // OpenGL drawing buffers, and a flag for initializing them.
    private static FloatBuffer vertexBuf = null;
    private static ShortBuffer orderBuf = null;
    private static boolean initialized = false;

    private String name;

    // A representation of the untampered silhouette for this particular LUMNart light.
    private Figure figure;
    private boolean uniformDimensions;
    private float outlineWidth;

    // Continuously shifting properties that make the LUMNart light source stand out from others.
    private Property visible;
    private Property color;
    private Property position;
    private Property dimensions;
    private Property angle;

    // Editor attributes
    private boolean highlight;

    /**
     * Constructs a default light source, which is a white square that covers the center of the frame.
     */
    public Light()
    {
        if (!initialized)
        {
            initializeDrawingBuffers();
            initialized = true;
        }

        this.setFigure("ellipse");

        name = "New Light";

        uniformDimensions = false;
        outlineWidth = 1.5f;

        visible = new Property(1);
        color = new Property(4);
        position = new Property(2);
        dimensions = new Property(2);
        angle = new Property(1);

        insertStageForProperty(Property.Type.Visible);
        insertStageForProperty(Property.Type.Color);
        insertStageForProperty(Property.Type.Position);
        insertStageForProperty(Property.Type.Dimensions);
        insertStageForProperty(Property.Type.Angle);

        highlight = false;
    }

    /**
     * Constructs a light source which copies the attributes of another light source.
     * @param copy the light source to copy from
     */
    public Light(Light copy)
    {
        if (!initialized)
        {
            initializeDrawingBuffers();
            initialized = true;
        }

        name = copy.getName();

        if (copy.getFigure() instanceof TextFigure)
        {
            figure = new TextFigure((TextFigure) copy.getFigure());
        }
        else
        {
            figure = new Figure(copy.getFigure());
        }

        uniformDimensions = copy.hasUniformDimensions();
        outlineWidth = copy.getOutlineWidth();

        visible = new Property(copy.getProperty(Property.Type.Visible));
        color = new Property(copy.getProperty(Property.Type.Color));
        position = new Property(copy.getProperty(Property.Type.Position));
        dimensions = new Property(copy.getProperty(Property.Type.Dimensions));
        angle = new Property(copy.getProperty(Property.Type.Angle));

        highlight = false;
    }

    /**
     * Constructs a light source from a container of JSON metadata
     * @param lightObj the JSON metadata container
     * @throws JSONException when the JSON metadata does not match the specified JSON data model for a light source.
     */
    public Light(JSONObject lightObj) throws JSONException
    {
        if (!initialized)
        {
            initializeDrawingBuffers();
            initialized = true;
        }

        this.setName(lightObj.getString("name"));

        JSONObject figureObj = lightObj.getJSONObject("figure");
        Figure.Type figureType = Figure.Type.values()[figureObj.getInt("type")];

        switch(figureType)
        {
            case SHAPE:
                figure = new Figure(figureObj);
                break;
            case TEXT:
                figure = new TextFigure(figureObj);
                break;
        }

        this.setUniformDimensions(lightObj.getBoolean("uniformDimensions"));
        this.setOutlineWidth((float) lightObj.getDouble("outlineWidth"));

        JSONObject visibleObj = lightObj.getJSONObject("visibleProperty");
        JSONObject colorObj = lightObj.getJSONObject("colorProperty");
        JSONObject positionObj = lightObj.getJSONObject("positionProperty");
        JSONObject dimensionsObj = lightObj.getJSONObject("dimensionProperty");
        JSONObject angleObj = lightObj.getJSONObject("angleProperty");

        visible = new Property(visibleObj, 1);
        color = new Property(colorObj, 4);
        position = new Property(positionObj, 2);
        dimensions = new Property(dimensionsObj, 2);
        angle = new Property(angleObj, 1);

        highlight = false;
    }

    /**
     * Constructs a light source from a string of JSON metadata
     * @param jsonLightString the JSON metadata string
     * @throws JSONException when the JSON metadata does not match the specified JSON data model for a light source.
     */
    public Light(String jsonLightString) throws JSONException
    {
        if (!initialized)
        {
            initializeDrawingBuffers();
            initialized = true;
        }

        JSONObject lightObj = new JSONObject(jsonLightString);

        this.setName(lightObj.getString("name"));

        JSONObject figureObj = lightObj.getJSONObject("figure");
        Figure.Type figureType = Figure.Type.values()[figureObj.getInt("type")];

        switch(figureType)
        {
            case SHAPE:
                figure = new Figure(figureObj);
                break;
            case TEXT:
                figure = new TextFigure(figureObj);
                break;
        }

        this.setUniformDimensions(lightObj.getBoolean("uniformDimensions"));
        this.setOutlineWidth((float) lightObj.getDouble("outlineWidth"));

        JSONObject visibleObj = lightObj.getJSONObject("visibleProperty");
        JSONObject colorObj = lightObj.getJSONObject("colorProperty");
        JSONObject positionObj = lightObj.getJSONObject("positionProperty");
        JSONObject dimensionsObj = lightObj.getJSONObject("dimensionProperty");
        JSONObject angleObj = lightObj.getJSONObject("angleProperty");

        String visibleObjStr = visibleObj.toString();
        String colorObjStr = colorObj.toString();
        String positionObjStr = positionObj.toString();
        String dimensionsObjStr = dimensionsObj.toString();
        String angleObjStr = angleObj.toString();

        visible = new Property(visibleObjStr, 1);
        color = new Property(colorObjStr, 4);
        position = new Property(positionObjStr, 2);
        dimensions = new Property(dimensionsObjStr, 2);
        angle = new Property(angleObjStr, 1);

        highlight = false;
    }

    /**
     * Pre-processes light source data to save computation time when rendering each frame during its animation.
     * IMPORTANT: ensure that an OpenGL context is active, GLESTBAM texture manager is initialized, and ShaderUtils is initialized before calling this function.
     */
    public void prepare()
    {
        figure.prepare();
    }

    public void clean()
    {
        figure.clean();
    }

    /**
     * Renders the light source in an OpenGL context.
     * IMPORTANT: ensure that an OpenGL context is active, GLESTBAM texture manager is initialized, and ShaderUtils is initialized before calling this function.
     */
    public void draw()
    {
        int positionId = ShaderUtils.shader.getAttrib(GLShader.Attribute.POSITION);
        int texCoordId = ShaderUtils.shader.getAttrib(GLShader.Attribute.TEX_COORD);
        int colorId = ShaderUtils.shader.getUniform(GLShader.Uniform.COLOR);

        float[] curVisibleVector = visible.getCurrentVector();
        if (curVisibleVector[0] < 0.5f)
        {
            return;
        }

        float[] curColorVector = color.getCurrentVector();
        float[] curDimensionsVector = dimensions.getCurrentVector();
        float[] curAngleVector = angle.getCurrentVector();
        float[] curPositionVector = position.getCurrentVector();

        GLES20.glUniform4fv(colorId, 1, curColorVector, 0);

        GLES20.glVertexAttribPointer(positionId, 3, GLES20.GL_FLOAT, false, 12, vertexBuf);
        GLES20.glVertexAttribPointer(texCoordId, 2, GLES20.GL_FLOAT, false, 8, figure.getBuffer());

        float[] curPivot = figure.getPreparedPivot();
        float finalWidth = (uniformDimensions) ? (figure.getDefaultWHRatio() * curDimensionsVector[1]) : curDimensionsVector[0];
        GLES20.glUniform1f(ShaderUtils.shader.getUniform(GLShader.Uniform.MSDF_SMOOTHING), 1.0f / (256.0f * Math.abs(Math.min(finalWidth, curDimensionsVector[1]))));
        GLES20.glUniform1i(ShaderUtils.shader.getUniform(GLShader.Uniform.TEXTURE_SAMPLER), figure.getTexSampler());
        GLES20.glUniform1f(ShaderUtils.shader.getUniform(GLShader.Uniform.OUTLINE_WIDTH), outlineWidth);

        Matrix.rotateM(ShaderUtils.mMatrix, 0, curAngleVector[0], 0.0f, 0.0f, -1.0f); // Rotation based on right hand rule
        Matrix.scaleM(ShaderUtils.mMatrix, 0, finalWidth, curDimensionsVector[1], 1.0f);
        Matrix.translateM(ShaderUtils.mMatrix, 0, -1.0f * curPivot[0], -1.0f * curPivot[1], 0.0f);

        if (figure.getFigureType() == Figure.Type.SHAPE)
        {
            ShaderUtils.pushViewMatrix();
            Matrix.translateM(ShaderUtils.vMatrix, 0, curPositionVector[0], curPositionVector[1], 0.0f);

            ShaderUtils.setMatrixUniforms();
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, orderBuf);

            ShaderUtils.popViewMatrix();
        }
        else
        if (figure.getFigureType() == Figure.Type.TEXT)
        {
            TextFigure textFigure = (TextFigure) figure;

            ShaderUtils.pushViewMatrix();
            ShaderUtils.pushModelMatrix();
            Matrix.translateM(ShaderUtils.vMatrix, 0, curPositionVector[0], curPositionVector[1], 0.0f);

            while (textFigure.hasNextCharacter())
            {
                textFigure.bufferNextCharacter();
                GLES20.glUniform1i(ShaderUtils.shader.getUniform(GLShader.Uniform.TEXTURE_SAMPLER), textFigure.getTexSampler());

                ShaderUtils.setMatrixUniforms();
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, orderBuf);

                Matrix.translateM(ShaderUtils.mMatrix, 0, textFigure.getCurrentCharAdvance(), 0.0f, 0.0f);
            }

            textFigure.resetCharacterSeeker();
            ShaderUtils.popModelMatrix();
            ShaderUtils.popViewMatrix();
        }

        Matrix.setIdentityM(ShaderUtils.mMatrix, 0);
    }

    /**
     * Steps the LUMNart light's animation properties one frame forward.
     */
    public void stepForward()
    {
        visible.stepForward();
        color.stepForward();
        position.stepForward();
        dimensions.stepForward();
        angle.stepForward();
    }

    /**
     * Steps the LUMNart light's animation properties one frame backward.
     */
    public void stepBackward()
    {
        visible.stepBackward();
        color.stepBackward();
        position.stepBackward();
        dimensions.stepBackward();
        angle.stepBackward();
    }

    /**
     * Resets the LUMNart light's animation properties back to their starting points.
     */
    public void reset()
    {
        visible.reset();
        color.reset();
        position.reset();
        dimensions.reset();
        angle.reset();
    }

    /**
     * Allocates drawing buffers for a rectangular primitive in OpenGL.
     * This represents the bounding box of the Figure silhouette.
     */
    private static void initializeDrawingBuffers()
    {
        float[] vertexArr = { 0.5f,  0.5f,  0.0f,
                             -0.5f,  0.5f,  0.0f,
                             -0.5f, -0.5f,  0.0f,
                              0.5f, -0.5f,  0.0f};

        ByteBuffer vb = ByteBuffer.allocateDirect(Float.SIZE * vertexArr.length);
        vb.order(ByteOrder.nativeOrder());
        vertexBuf = vb.asFloatBuffer();
        vertexBuf.put(vertexArr);
        vertexBuf.position(0);

        short[] orderArr = {0, 1, 2,
                            0, 2, 3};

        ByteBuffer ob = ByteBuffer.allocateDirect(Short.SIZE * orderArr.length);
        ob.order(ByteOrder.nativeOrder());
        orderBuf = ob.asShortBuffer();
        orderBuf.put(orderArr);
        orderBuf.position(0);
    }

    /**
     * Inserts a non-animated stage of a property's shifting process, usually its first stage.
     * @param propertyCode a code representing the exact property to be modified.
     */
    private void insertStageForProperty(Property.Type propertyCode)
    {
        Property p = null;
        float[] defaultVector = null;

        switch (propertyCode) {
            case Visible:
                p = visible;
                defaultVector = new float[]{1.0f};
                break;
            case Color:
                p = color;
                defaultVector = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
                break;
            case Position:
                p = position;
                defaultVector = new float[]{0.0f, 0.0f};
                break;
            case Dimensions:
                p = dimensions;
                defaultVector = new float[]{0.5f, 0.5f};
                break;
            case Angle:
                p = angle;
                defaultVector = new float[]{0.0f};
                break;
            default:
                System.out.println("insertStage(): Invalid property");
                break;
        }

        Stage newStage = new Stage(defaultVector.length);
        newStage.setStartVector(Arrays.copyOf(defaultVector, defaultVector.length));
        newStage.setEndVector(Arrays.copyOf(defaultVector, defaultVector.length));
        newStage.setDuration(60);
        newStage.setTransitionCurve(Stage.Transition.Linear);

        p.insertStage(newStage);
    }

    /**
     * Retrieve the name that identifies this LUMNart light.
     * @return the light's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the new name that identifies this LUMNart light.
     * @param newName the new name
     */
    public void setName(String newName)
    {
        name = newName;
    }

    /**
     * Retrieves whether or not the dimensions of a light source match the width-height ratio of the original Figure silhouette.
     * @return true if the Figure silhouette's original width-height ratio is preserved, false otherwise.
     */
    public boolean hasUniformDimensions()
    {
        return uniformDimensions;
    }

    /**
     * Sets whether or not the dimensions of a LUMNart light source match the width-height ratio of the original Figure silhouette.
     * @param newUniformDimensions the new dimensional uniformity setting
     */
    public void setUniformDimensions(boolean newUniformDimensions)
    {
        uniformDimensions = newUniformDimensions;
    }

    /**
     * Retrieves the width of this LUMNart light's outline.
     * @return this light's outline width.
     */
    public float getOutlineWidth()
    {
        return outlineWidth;
    }

    /**
     * Sets the new width of this LUMNart light's outline.
     * @param newOutlineWidth the new outline width
     */
    public void setOutlineWidth(float newOutlineWidth)
    {
        outlineWidth = newOutlineWidth;
    }

    /**
     * Retrieves whether or not this LUMNart light will be highlighted by an outline.
     * @return true if there is highlighting, false otherwise.
     */
    public boolean isHighlighted()
    {
        return highlight;
    }

    /**
     * Sets whether or not this LUMNart light will be highlighted by an outline.
     * @param newHighlighted the new highlight setting
     */
    public void setHighlighted(boolean newHighlighted)
    {
        highlight = newHighlighted;
    }

    /**
     * Retrieves a reference to a specific shifting property of the LUMNart light source.
     * @return a reference to a shifting property
     */
    public Property getProperty(Property.Type propertyCode)
    {
        Property p = null;

        switch(propertyCode)
        {
            case Visible:
                p = visible;
                break;
            case Color:
                p = color;
                break;
            case Position:
                p = position;
                break;
            case Dimensions:
                p = dimensions;
                break;
            case Angle:
                p = angle;
                break;
            default:
                System.out.println("insertStage(): Invalid property");
                break;
        }

        return p;
    }

    /**
     * Retrieves a reference to the silhouette representation of the LUMNart light source.
     * @return the reference to the Figure silhouette
     */
    public Figure getFigure()
    {
        return figure;
    }

    /**
     * Sets a new reference to a new silhouette representation of the LUMNart light.
     * @param newFigure the new reference to a Figure silhouette
     */
    public void setFigure(Figure newFigure)
    {
        if (newFigure instanceof TextFigure)
        {
            figure = new TextFigure((TextFigure) newFigure);
        }
        else
        {
            figure = new Figure(newFigure);
        }
    }

    /**
     * Sets a new reference to a new silhouette representation of the LUMNart light, based on the name of a known figure.
     * @param figureName the name of a known figure
     */
    public void setFigure(String figureName)
    {
        figure = new Figure(figureName);
    }

    /**
     * Serializes the LUMNart light into JSON string format, including all of its  fundamental shifting properties and extra drawing attributes.
     * @return the JSON string representation of the LUMNart light.
     */
    public String toJSONString()
    {
        StringBuilder jsonStringBuilder = new StringBuilder();

        jsonStringBuilder.append("{"); // light open

        jsonStringBuilder.append("\"name\":" + "\"" + this.getName() + "\"" + ",");
        jsonStringBuilder.append("\"uniformDimensions\":" + this.hasUniformDimensions() + ",");
        jsonStringBuilder.append("\"outlineWidth\":" + this.getOutlineWidth() + ",");

        Figure figure = this.getFigure();

        jsonStringBuilder.append("\"figure\":");
        jsonStringBuilder.append(figure.toJSONString());
        jsonStringBuilder.append(",");

        Property[] properties = new Property[]{ this.getProperty(Property.Type.Visible),
                this.getProperty(Property.Type.Color),
                this.getProperty(Property.Type.Position),
                this.getProperty(Property.Type.Dimensions),
                this.getProperty(Property.Type.Angle)};

        String[] propertyNames = new String[]{ "visibleProperty",
                "colorProperty",
                "positionProperty",
                "dimensionProperty",
                "angleProperty"};

        for (int pr = 0; pr < properties.length; pr++)
        {
            if (pr != 0) { jsonStringBuilder.append(","); } // property delimiter

            Property property = properties[pr];
            jsonStringBuilder.append("\"" + propertyNames[pr] + "\":");
            jsonStringBuilder.append(property.toJSONString());
        }

        jsonStringBuilder.append("}"); // light close

        return jsonStringBuilder.toString();
    }

    /**
     * Serializes the LUMNart light into JSON format, stores the serialized format into a JSON container object, and returns the object.
     * @return the JSON container object for the LUMNart light
     * @throws JSONException
     */
    @Override
    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject lightObj = new JSONObject();

        lightObj.put("name", this.getName());
        lightObj.put("uniformDimensions", this.hasUniformDimensions());
        lightObj.put("outlineWidth", (double) this.getOutlineWidth());

        Figure figure = this.getFigure();
        JSONObject figureObj = figure.toJSONObject();
        lightObj.put("figure", figureObj);

        Property[] properties = new Property[]{ this.getProperty(Property.Type.Visible),
                this.getProperty(Property.Type.Color),
                this.getProperty(Property.Type.Position),
                this.getProperty(Property.Type.Dimensions),
                this.getProperty(Property.Type.Angle)};

        String[] propertyNames = new String[]{ "visibleProperty",
                "colorProperty",
                "positionProperty",
                "dimensionProperty",
                "angleProperty"};

        for (int pr = 0; pr < properties.length; pr++)
        {
            Property property = properties[pr];
            JSONObject propertyObj = property.toJSONObject();
            lightObj.put(propertyNames[pr], propertyObj);
        }

        return lightObj;
    }
}















