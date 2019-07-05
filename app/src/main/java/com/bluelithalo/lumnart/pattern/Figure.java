package com.bluelithalo.lumnart.pattern;

import com.bluelithalo.lumnart.util.GLESTBAM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Texture metadata storage. This will be used to form the silhouette of the light source from the texture.
 * This silhouette is rendered independently of properties such as color, rotation, etc.
 * In addition, a pivot is defined, which acts as the reference point for matrix transformations on the light source.
 */
public class Figure implements JSONizable
{
    /**
     * The type of figure, which determines how it is rendered.
     */
    public enum Type
    {
        SHAPE, TEXT, IMAGE
    }

    private String identifier;
    private String preparedIdentifer;
    private Figure.Type figureType;
    private boolean prepared;

    private float[] pivot = null;
    private float[] preparedPivot = null;
    private float pivotMagnitude;
    private boolean isUsingDefaultPivot;

    private float[] defaultPivot = null;
    private float defaultWHRatio;
    private float[] texCoords = null;
    private FloatBuffer texCoordBuf = null;
    private int texSampler;

    /**
     * Initializes a default rectangular figure.
     */
    public Figure()
    {
        this.identifier = "ellipse";
        this.figureType = Figure.Type.SHAPE;
        this.prepared = false;

        this.pivot = new float[]{0.0f, 0.0f};
        this.pivotMagnitude = 0.0f;
        this.isUsingDefaultPivot = true;

        this.setDefaultPivot(new float[]{0.0f, 0.0f});
        this.setDefaultWHRatio(1.0f);
        this.setTexCoords(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        this.setTexSampler(0);

    }

    /**
     * Initializes a figure with an identifier for its rendered shape.
     * @param figIdentifier the name of the shape
     */
    public Figure(String figIdentifier)
    {
        this.identifier = figIdentifier;
        this.figureType = Figure.Type.SHAPE;
        this.prepared = false;


        this.pivot = new float[]{0.0f, 0.0f};
        this.pivotMagnitude = 0.0f;
        this.isUsingDefaultPivot = true;

        this.setDefaultPivot(new float[]{0.0f, 0.0f});
        this.setDefaultWHRatio(1.0f);
        this.setTexCoords(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        this.setTexSampler(0);
    }

    /**
     * Initializes a figure that copies all information from another given figure.
     * @param copy the figure from which to copy data
     */
    public Figure(Figure copy)
    {
        this.identifier = copy.getIdentifier();
        this.figureType = copy.getFigureType();
        this.prepared = false;


        this.pivot = Arrays.copyOf(copy.getPivot(), 2);
        this.pivotMagnitude = (float) Math.sqrt((pivot[0]*pivot[0])+(pivot[1]*pivot[1]));
        this.isUsingDefaultPivot = copy.isUsingDefaultPivot();

        this.setDefaultPivot(new float[]{0.0f, 0.0f});
        this.setDefaultWHRatio(1.0f);
        this.setTexCoords(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        this.setTexSampler(0);
    }

    /**
     * Initializes a figure by reading JSON metadata and building the figure from there.
     * @param figureObj the JSON metadata object where figure information is stored.
     * @throws JSONException if the JSON metadata does not match the expected data format.
     */
    public Figure(JSONObject figureObj) throws JSONException
    {
        this.identifier = figureObj.getString("identifier");
        this.figureType = Figure.Type.values()[figureObj.getInt("type")];
        this.prepared = false;


        JSONArray pivotArr = figureObj.getJSONArray("pivot");
        this.setPivot((float) pivotArr.getDouble(0), (float) pivotArr.getDouble(1));

        this.isUsingDefaultPivot = figureObj.getBoolean("isUsingDefaultPivot");

        this.setDefaultPivot(new float[]{0.0f, 0.0f});
        this.setDefaultWHRatio(1.0f);
        this.setTexCoords(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        this.setTexSampler(0);
    }

    /**
     * Prepares the figure to be rendered by loading it and its true metadata in the GLESTBAM texture manager,
     * then storing the metadata.
     */
    public void prepare()
    {
        if (this.isPrepared())
        {
            GLESTBAM.unloadShape(this.getPreparedIdentifier());
            this.setPrepared(false);
        }

        Figure loadedFigure = GLESTBAM.loadShape(identifier);
        this.defaultWHRatio = loadedFigure.getDefaultWHRatio();
        this.texCoords = loadedFigure.getTexCoords();
        this.defaultPivot = loadedFigure.getDefaultPivot();
        this.texSampler = loadedFigure.getTexSampler();

        if (this.isUsingDefaultPivot)
        {
            this.preparedPivot = this.defaultPivot;
        }
        else
        {
            this.preparedPivot = this.pivot;
        }

        initializeBuffer();
        updateBuffer();
        markAsPrepared();
    }

    public void clean()
    {
        if (!this.isPrepared())
        {
            return;
        }

        updateBuffer(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        GLESTBAM.unloadShape(this.getPreparedIdentifier());
        this.setPrepared(false);
    }

    /**
     * Retrieves the reference to this figure's texture coordinate buffer.
     * @return the reference to this figure's texture coordinate buffer.
     */
    public FloatBuffer getBuffer()
    {
        return texCoordBuf;
    }

    /**
     * Initializes this figure's texture coordinate buffer.
     */
    public void initializeBuffer()
    {
        ByteBuffer tcb = ByteBuffer.allocateDirect(Float.SIZE * texCoords.length);
        tcb.order(ByteOrder.nativeOrder());
        texCoordBuf = tcb.asFloatBuffer();
    }

    /**
     * Updates the buffer by overwriting it with the figure's default texture coordinates.
     */
    public void updateBuffer()
    {
        texCoordBuf.put(texCoords);
        texCoordBuf.position(0);
    }

    /**
     * Updates the buffer by overwriting it with the given texture coordinates.
     * @param newTexCoords the given texture coordinates.
     */
    public void updateBuffer(float[] newTexCoords)
    {
        texCoordBuf.put(newTexCoords);
        texCoordBuf.position(0);
    }

    /**
     * Retrieves the shape identifier for this figure.
     * @return the shape identifier for this figure.
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * Assign a new shape identifier to this figure.
     * @param newIdentifier the new shape identifier to assign.
     */
    public void setIdentifier(String newIdentifier)
    {
        this.identifier = newIdentifier;
    }

    /**
     * Retrieves the type of this figure, which denotes how it is rendered.
     * @return the type of this figure.
     */
    public Figure.Type getFigureType()
    {
        return figureType;
    }

    /**
     * Assigns a new definition for this type of this figure.
     * @param newFigureType the new type classification to be assigned.
     */
    public void setFigureType(Figure.Type newFigureType)
    {
        this.figureType = newFigureType;
    }

    /**
     * Retrieves whether the figure metadata has been loaded into the GLESTBAM texture manager.
     * @return true if metadata has been loaded, false otherwise.
     */
    public boolean isPrepared()
    {
        return prepared;
    }

    /**
     * Set whether the figure metadata has been loaded into the GLESTBAM texture manager.
     * @param newIsPrepared the new prepared state
     */
    protected void setPrepared(boolean newIsPrepared)
    {
        prepared = newIsPrepared;
    }

    /**
     * Retrieves the GLESTBAM-based identifier for this figure.
     * @return the GLESTBAM-based identifier
     */
    public String getPreparedIdentifier()
    {
        return preparedIdentifer;
    }

    /**
     * Marks the figure as being loaded into the GLESTBAM texture manager.
     */
    protected void markAsPrepared()
    {
        this.preparedIdentifer = this.identifier;
        this.prepared = true;
    }

    /**
     * Retrieves the original width-height ratio of a figure that would preserve its original appearance.
     * @return the original width-height ratio
     */
    public float getDefaultWHRatio()
    {
        return defaultWHRatio;
    }

    /**
     * Sets the default width-height ratio of a figure that would preserve its original appearance.
     * This function must only be used by the GLESTBAM texture manager for composing figure objects from known metadata.
     * @param newWHRatio the new default width-height ratio
     */
    public void setDefaultWHRatio(float newWHRatio)
    {
        this.defaultWHRatio = newWHRatio;
    }

    /**
     * Retrieves a reference to the texture coordinates of this figure,
     * which will be loaded into a buffer object for rendering purposes.
     * @return a reference to this figure's texture coordinates
     */
    public float[] getTexCoords()
    {
        return texCoords;
    }

    /**
     * Sets a reference to the texture coordinates of this figure,
     * which will be loaded into a buffer object for rendering purposes.
     * This function must only be used by the GLESTBAM texture manager for composing figure objects from known metadata.
     * @param newTexCoords a new reference to texture coordinates
     */
    public void setTexCoords(float[] newTexCoords)
    {
        this.texCoords = Arrays.copyOf(newTexCoords, newTexCoords.length);
    }

    /**
     * Retrieves the reference point where matrix transformations will be based around.
     * @return the reference point for matrix transformations
     */
    public float[] getPivot()
    {
        return pivot;
    }

    /**
     * Sets the reference point where matrix transformations will be based around.
     * @param newPivot the new reference point for matrix transformations
     */
    public void setPivot(float[] newPivot)
    {
        pivot = Arrays.copyOf(newPivot, 2);
        pivotMagnitude = (float) Math.sqrt((pivot[0]*pivot[0])+(pivot[1]*pivot[1]));
    }

    /**
     * Sets the reference point where matrix transformations will be based around.
     * @param newPivotX the x-coordinate of the new reference point for matrix transformations
     * @param newPivotY the y-coordinate of the new reference point for matrix transformations
     */
    public void setPivot(float newPivotX, float newPivotY)
    {
        pivot = new float[]{newPivotX, newPivotY};
        pivotMagnitude = (float) Math.sqrt((pivot[0]*pivot[0])+(pivot[1]*pivot[1]));
    }

    /**
     * Retrieves the prepared reference point where matrix transformations will be based around.
     * @return the reference point for matrix transformations
     */
    public float[] getPreparedPivot()
    {
        return preparedPivot;
    }

    /**
     * Sets the prepared reference point where matrix transformations will be based around.
     * @param newPreparedPivot the new prepared reference point for matrix transformations
     */
    public void setPreparedPivot(float[] newPreparedPivot)
    {
        preparedPivot = Arrays.copyOf(newPreparedPivot, 2);
    }

    /**
     * Sets the prepared reference point where matrix transformations will be based around.
     * @param newPreparedPivotX the x-coordinate of the new prepared reference point for matrix transformations
     * @param newPreparedPivotY the y-coordinate of the new prepared reference point for matrix transformations
     */
    public void setPreparedPivot(float newPreparedPivotX, float newPreparedPivotY)
    {
        preparedPivot = new float[]{newPreparedPivotX, newPreparedPivotY};
    }

    /**
     * Retrieves the default reference point where matrix transformations will be based around.
     * @return the default reference point for matrix transformations
     */
    public float[] getDefaultPivot()
    {
        return defaultPivot;
    }

    /**
     * Sets the default reference point where matrix transformations will be based around.
     * This function must only be used by the GLESTBAM texture manager for composing figure objects from known metadata.
     * @param newDefaultPivot the new reference point for matrix transformations
     */
    public void setDefaultPivot(float[] newDefaultPivot)
    {
        this.defaultPivot = Arrays.copyOf(newDefaultPivot, 2);
    }

    /**
     * Retrieves whether or not the default pivot is being enforced in the rendering of this figure.
     * @return true if the default pivot is being enforced, false if not
     */
    public boolean isUsingDefaultPivot()
    {
        return isUsingDefaultPivot;
    }

    /**
     * Sets whether or not the default pivot is being enforced in the rendering of this figure.
     * @param newIsUsingDefaultPivot the new default pivot usage state
     */
    public void setIsUsingDefaultPivot(boolean newIsUsingDefaultPivot)
    {
        this.isUsingDefaultPivot = newIsUsingDefaultPivot;
    }

    /**
     * Retrieves the current texture sampler ID that is associated with this figure in GLESTBAM texture manager.
     * @return the figure's associated texture sampler ID
     */
    public int getTexSampler()
    {
        return texSampler;
    }

    /**
     * Sets the current texture sampler ID that is associated with this figure in GLESTBAM texture manager.
     * This function must only be used by the GLESTBAM texture manager for composing figure objects from known metadata.
     * @param newTexSampler the new figure's associated texture sampler ID
     */
    public void setTexSampler(int newTexSampler)
    {
        texSampler = newTexSampler;
    }

    /**
     * Serializes the figure into JSON string format, including all of its pivot, type, and pivot usage state.
     * @return the JSON string representation of the figure
     */
    public String toJSONString()
    {
        StringBuilder jsonStringBuilder = new StringBuilder();

        jsonStringBuilder.append("{");

        jsonStringBuilder.append("\"identifier\":" + "\"" + this.getIdentifier() + "\"" + ",");
        jsonStringBuilder.append("\"type\":" + this.figureType.ordinal() + ",");
        jsonStringBuilder.append("\"pivot\":" + "[" + this.getPivot()[0] + "," + this.getPivot()[1] + "]" + ",");
        jsonStringBuilder.append("\"isUsingDefaultPivot\":" + this.isUsingDefaultPivot());

        jsonStringBuilder.append("}");

        return jsonStringBuilder.toString();
    }

    /**
     * Serializes the figure into JSON format, stores the serialized format into a JSON container object, and returns the object.
     * @return the JSON container object for the figure
     * @throws JSONException
     */
    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject figureObj = new JSONObject();

        figureObj.put("identifier", this.getIdentifier());
        figureObj.put("type", this.figureType.ordinal());

        JSONArray pivotArr = new JSONArray();
        pivotArr.put(this.getPivot()[0]);
        pivotArr.put(this.getPivot()[1]);

        figureObj.put("pivot", pivotArr);
        figureObj.put("isUsingDefaultPivot", this.isUsingDefaultPivot());

        return figureObj;
    }
}
