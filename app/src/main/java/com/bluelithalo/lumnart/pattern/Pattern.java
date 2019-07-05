package com.bluelithalo.lumnart.pattern;

import android.graphics.Color;
import android.opengl.Matrix;

import com.bluelithalo.lumnart.util.ShaderUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * A collection of animated lights, contained within axis-aligned rendering frames known as sections, which are arranged within individual, stackable layers of lights.
 * In an entire tree of lights to be rendered, the pattern stands as the root of this tree.
 * This metadata within this pattern will be the first piece of information presented to users before viewing, editing, and sharing the patterns themselves.
 * This is the ultimate container of LUMNart lights.
 */
public class Pattern implements JSONizable, HasHideableComponents
{
    private ArrayList<Layer> layerList;
    private ArrayList<Boolean> layerHiddenList;

    private String name;
    private String author;
    private String description;
    private int colorCode;
    private String version;

    /**
     * Constructs a default pattern, which is just a squared white light in the center of the screen.
     */
    public Pattern()
    {
        layerList = new ArrayList<Layer>();
        layerHiddenList = new ArrayList<Boolean>();

        Random randomGen = new Random();

        name = "A New Pattern";
        author = "A Nice LUMNartist";
        description = "A beautiful animated paintbrush";
        colorCode = Color.HSVToColor(new float[]{360.0f * randomGen.nextFloat(), 1.0f * randomGen.nextFloat(), 0.25f});
        version = "1.0";

        insertLayer();
    }

    /**
     * Constructs a pattern that copies the attributes of another pattern.
     * @param copy the pattern to copy from
     */
    public Pattern(Pattern copy)
    {
        name = copy.getName();
        author = copy.getAuthor();
        description = copy.getDescription();
        colorCode = copy.getColorCode();
        version = copy.getVersion();

        layerList = new ArrayList<Layer>();
        layerHiddenList = new ArrayList<Boolean>();

        for (int i = 0; i < copy.getLayerCount(); i++)
        {
            Layer layerCopy = copy.getLayer(i);
            this.insertLayer(new Layer(layerCopy));
            this.layerHiddenList.set(i, copy.isLayerHidden(i));
        }
    }

    /**
     * Constructs a pattern from the contents of a JSON metadata container object.
     * @param patternObj the JSON metadata container object
     * @throws JSONException if there is a mismatch between the JSON metadata and expected data
     */
    public Pattern(JSONObject patternObj) throws JSONException
    {
        this.setName(patternObj.getString("name"));
        this.setAuthor(patternObj.getString("author"));
        this.setDescription(patternObj.getString("description"));
        this.setColorCode(patternObj.getInt("colorCode"));
        this.setVersion(patternObj.getString("version"));

        this.layerList = new ArrayList<Layer>();
        this.layerHiddenList = new ArrayList<Boolean>();
        JSONArray layers = patternObj.getJSONArray("layers");
        JSONArray hidden = patternObj.getJSONArray("hidden");

        for (int la = 0; la < layers.length(); la++)
        {
            JSONObject layerObj = layers.getJSONObject(la);
            Layer layer = new Layer(layerObj);
            this.insertLayer(layer);

            String hiddenStr = hidden.getString(la);
            this.layerHiddenList.set(la, hiddenStr.equals("T"));
        }
    }

    /**
     * Constructs a pattern from the contents of a JSON metadata string.
     * @param jsonPatternString the JSON metadata string
     * @throws JSONException if there is a mismatch between the JSON metadata and expected data
     */
    public Pattern(String jsonPatternString) throws JSONException
    {
        JSONObject patternObj = new JSONObject(jsonPatternString);

        this.setName(patternObj.getString("name"));
        this.setAuthor(patternObj.getString("author"));
        this.setDescription(patternObj.getString("description"));
        this.setColorCode(patternObj.getInt("colorCode"));
        this.setVersion(patternObj.getString("version"));

        this.layerList = new ArrayList<Layer>();
        this.layerHiddenList = new ArrayList<Boolean>();
        JSONArray layers = patternObj.getJSONArray("layers");
        JSONArray hidden = patternObj.getJSONArray("hidden");

        for (int la = 0; la < layers.length(); la++)
        {
            JSONObject layerObj = layers.getJSONObject(la);
            String layerObjStr = layerObj.toString();
            Layer layer = new Layer(layerObjStr);
            this.insertLayer(layer);

            String hiddenStr = hidden.getString(la);
            this.layerHiddenList.set(la, hiddenStr.equals("T"));
        }
    }

    /**
     * Pre-computes rendering data to save time when rendering this layer.
     */
    public void prepare()
    {
        for (int la = 0; la < getLayerCount(); la++)
        {
            Layer layer = getLayer(la);
            layer.prepare();
        }
    }

    public void clean()
    {
        for (int la = 0; la < getLayerCount(); la++)
        {
            Layer layer = getLayer(la);
            layer.clean();
        }
    }

    /**
     * Renders the pattern in an OpenGL context.
     * IMPORTANT: ensure that an OpenGL context is active, GLESTBAM texture manager is initialized, and ShaderUtils is initialized before calling this function.
     */
    public void draw()
    {
        for (int la = getLayerCount() - 1; la >= 0; la--)
        {
            if (layerHiddenList.get(la))
            {
                continue;
            }

            Layer layer = getLayer(la);

            ShaderUtils.pushViewMatrix();
            Matrix.translateM(ShaderUtils.vMatrix, 0, 0.0f, 0.0f, la * 1.0f);

            layer.draw();

            ShaderUtils.popViewMatrix();
        }
    }

    /**
     * Inserts a new, default layer of lights into the pattern.
     */
    public void insertLayer()
    {
        layerList.add(new Layer());
        layerHiddenList.add(false);
    }

    /**
     * Inserts an existing layer of lights into the pattern.
     * @param newLayer the existing layer of lights
     */
    public void insertLayer(Layer newLayer)
    {
        layerList.add(newLayer);
        layerHiddenList.add(false);
    }

    /**
     * Inserts an existing layer of lights into the pattern at a specific index.
     * @param layerNum the specific index
     * @param newLayer the existing layer of lights
     */
    public void insertLayer(int layerNum, Layer newLayer)
    {
        layerList.add(layerNum, newLayer);
        layerHiddenList.add(layerNum, false);
    }

    /**
     * Retrieves a reference to the layer that is indexed at some point of the current list of layers.
     * @param layerNum the index of the layer
     * @return a reference to the indexed layer
     */
    public Layer getLayer(int layerNum)
    {
        if (layerNum >= layerList.size() || layerNum <= -1)
        {
            return null;
        }

        Layer gotten = layerList.get(layerNum);
        return gotten;
    }

    /**
     * Replaces an existing layer, indexed at some point of the current list of layers, with a new layer.
     * @param newLayer the new layer to act as a replacement
     * @param layerNum the index of the existing layer
     */
    public void setLayer(Layer newLayer, int layerNum)
    {
        if (layerNum >= layerList.size() || layerNum <= -1)
        {
            return;
        }

        layerList.set(layerNum, newLayer);
    }

    /**
     * Removes an existing layer that is indexed at some point of the current list of layers.
     * @param layerNum the index of the existing layer
     */
    public void removeLayer(int layerNum)
    {
        if (layerNum >= layerList.size() || layerNum <= -1)
        {
            return;
        }

        layerList.remove(layerNum);
        layerHiddenList.remove(layerNum);
    }

    /**
     * Retrieves the number of layers that are contained within this pattern.
     * @return the number of layers in the pattern
     */
    public int getLayerCount()
    {
        return layerList.size();
    }

    /**
     * Retrieves the name of the pattern.
     * @return the pattern's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets a new name for the pattern.
     * @param newName the pattern's new name
     */
    public void setName(String newName)
    {
        this.name = newName;
    }

    /**
     * Retrieves the name of the user who authored this pattern.
     * @return the author's name
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * Sets the name of the user who authored this pattern.
     * @param newAuthor the author's new name
     */
    public void setAuthor(String newAuthor)
    {
        this.author = newAuthor;
    }

    /**
     * Retrieves the description of this pattern.
     * @return the pattern's current description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description of this pattern.
     * @param newDescription the pattern's new description
     */
    public void setDescription(String newDescription)
    {
        this.description = newDescription;
    }

    /**
     * Retrieves the color code of this pattern.
     * @return the pattern's current color code
     */
    public int getColorCode()
    {
        return colorCode;
    }

    /**
     * Sets the color code of this pattern.
     * @param newColorCode the pattern's new color code
     */
    public void setColorCode(int newColorCode)
    {
        this.colorCode = newColorCode;
    }

    /**
     * Retrieves the version of this pattern.
     * @return the pattern's current version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Sets the version of this pattern.
     * @param newVersion the pattern's new version
     */
    public void setVersion(String newVersion)
    {
        this.version = newVersion;
    }

    /**
     * Steps the animation of this pattern one unit forward.
     */
    public void stepForward()
    {
        for (int i = 0; i < this.getLayerCount(); i++)
        {
            this.getLayer(i).stepForward();
        }
    }

    /**
     * Steps the animation of this pattern one unit backwards.
     */
    public void stepBackward()
    {
        for (int i = 0; i < this.getLayerCount(); i++)
        {
            this.getLayer(i).stepBackward();
        }
    }

    /**
     * Resets the animation of this pattern to its zero time-point.
     */
    public void reset()
    {
        for (int i = 0; i < this.getLayerCount(); i++)
        {
            this.getLayer(i).reset();
        }
    }

    /**
     * Serializes the pattern into JSON string format, including all of its layers.
     * @return the JSON string representation of the pattern
     */
    public String toJSONString()
    {
        StringBuilder jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("{"); // pattern open

        jsonStringBuilder.append("\"name\":" + "\"" + this.getName() + "\"" + ",");
        jsonStringBuilder.append("\"author\":" + "\"" + this.getAuthor() + "\"" + ",");
        jsonStringBuilder.append("\"description\":" + "\"" + this.getDescription() + "\"" + ",");
        jsonStringBuilder.append("\"colorCode\":" + this.getColorCode() + ",");
        jsonStringBuilder.append("\"version\":" + "\"" + this.getVersion() + "\"" + ",");
        jsonStringBuilder.append("\"layers\":[");

        for (int la = 0; la < this.getLayerCount(); la++)
        {
            if (la != 0) { jsonStringBuilder.append(","); } // layer delimiter

            Layer layer = this.getLayer(la);
            jsonStringBuilder.append(layer.toJSONString());
        }

        jsonStringBuilder.append("]" + ",");
        jsonStringBuilder.append("\"hidden\":[");

        for (int la = 0; la < this.getLayerCount(); la++)
        {
            if (la != 0) { jsonStringBuilder.append(","); } // layer delimiter
            jsonStringBuilder.append(this.isLayerHidden(la) ? "T" : "F");
        }

        jsonStringBuilder.append("]");
        jsonStringBuilder.append("}"); // pattern close

        return jsonStringBuilder.toString();
    }

    /**
     * Serializes the pattern into JSON format, stores the serialized format into a JSON container object, and returns the object.
     * @return the JSON container object for the pattern
     * @throws JSONException
     */
    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject patternObj = new JSONObject();

        patternObj.put("name", this.getName());
        patternObj.put("author", this.getAuthor());
        patternObj.put("description", this.getDescription());
        patternObj.put("colorCode", this.getColorCode());
        patternObj.put("version", this.getVersion());

        JSONArray layerObjArr = new JSONArray();

        for (int la = 0; la < this.getLayerCount(); la++)
        {
            Layer layer = this.getLayer(la);
            JSONObject layerObj = layer.toJSONObject();
            layerObjArr.put(layerObj);
        }

        JSONArray layerHiddenArr = new JSONArray();

        for (int la = 0; la < this.getLayerCount(); la++)
        {
            layerHiddenArr.put(this.isLayerHidden(la) ? "T" : "F");
        }

        patternObj.put("layers", layerObjArr);
        patternObj.put("hidden", layerHiddenArr);

        return patternObj;
    }

    public void hideAllComponents(boolean recursiveHide)
    {
        for (int la = 0; la < this.getLayerCount(); la++)
        {
            this.hideComponent(la, recursiveHide);
        }
    }

    public void hideComponent(int componentIdx, boolean recursiveHide)
    {
        layerHiddenList.set(componentIdx, true);
        if (recursiveHide) { layerList.get(componentIdx).hideAllComponents(recursiveHide); }
    }

    public void unhideAllComponents(boolean recursiveUnhide)
    {
        for (int la = 0; la < this.getLayerCount(); la++)
        {
            this.unhideComponent(la, recursiveUnhide);
        }
    }

    public void unhideComponent(int componentIdx, boolean recursiveUnhide)
    {
        layerHiddenList.set(componentIdx, false);
        if (recursiveUnhide) { layerList.get(componentIdx).unhideAllComponents(recursiveUnhide); }
    }

    public boolean isComponentHidden(int componentIdx)
    {
        return layerHiddenList.get(componentIdx);
    }

    public void hideAllLayers(boolean recursiveHide)
    {
        hideAllComponents(recursiveHide);
    }

    public void hideLayer(int layerIdx, boolean recursiveHide)
    {
        hideComponent(layerIdx, recursiveHide);
    }

    public void unhideAllLayers(boolean recursiveUnhide)
    {
        unhideAllComponents(recursiveUnhide);
    }

    public void unhideLayer(int layerIdx, boolean recursiveUnhide)
    {
        unhideComponent(layerIdx, recursiveUnhide);
    }

    public boolean isLayerHidden(int layerIdx)
    {
        return isComponentHidden(layerIdx);
    }

}
