package com.bluelithalo.lumnart.pattern;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.bluelithalo.lumnart.App;
import com.bluelithalo.lumnart.util.AABB;
import com.bluelithalo.lumnart.util.GLShader;
import com.bluelithalo.lumnart.util.ShaderUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A rectangular, axis-aligned frame to be filled with animated lights.
 * All lights are referenced and maintained within a sequential list of lights, which will all be rendered when making the draw call for this frame.
 * Any lights that are placed outside of this frame will not be rendered.
 * If desirable, lights may be transformed to fit entirely within the defined bounds of the frame. Otherwise the lights will cut off if when transition outward from the frame.
 */
public class Section implements JSONizable, HasHideableComponents
{
    private ArrayList<Light> lightList;
    private ArrayList<Boolean> lightHiddenList;
    private AABB boundingBox;

    private String name;
    private boolean fitting;

    /**
     * Constructs a rectangular frame that covers the entirety of the rendering space for lights.
     */
    public Section()
    {
        name = "New Section";
        lightList = new ArrayList<Light>();
        lightHiddenList = new ArrayList<Boolean>();
        boundingBox = new AABB();

        fitting = false;

        insertLight();
    }

    /**
     * Constructs a rectangular frame whose coverage is defined by the given minimum/maximum x/y-bounds.
     * @param xMin the lower x-bound
     * @param xMax the higher x-bound
     * @param yMin the lower y-bound
     * @param yMax the higher y-bound
     */
    public Section(float xMin, float xMax, float yMin, float yMax)
    {
        name = "New Section";
        lightList = new ArrayList<Light>();
        lightHiddenList = new ArrayList<Boolean>();
        boundingBox = new AABB(xMin, xMax, yMin, yMax);

        fitting = false;

        insertLight();
    }

    /**
     * Constructs a rectangular frame that copies the attributes of another given rectangular frame
     * @param copy the given rectangular frame to copy from
     */
    public Section(Section copy)
    {
        name = copy.getName();
        boundingBox = new AABB(copy.getBoundingBox());
        this.fitting = copy.hasFitting();

        lightList = new ArrayList<Light>();
        lightHiddenList = new ArrayList<Boolean>();

        for (int i = 0; i < copy.getLightCount(); i++)
        {
            Light lightCopy = copy.getLight(i);
            this.insertLight(new Light(lightCopy));
            this.lightHiddenList.set(i, copy.isLightHidden(i));
        }
    }

    /**
     * Constructs a rectangular frame based on the data within a JSON metadata container object.
     * @param sectionObj the JSON metadata container object
     * @throws JSONException if the JSON metadata does not match with the expected data
     */
    public Section(JSONObject sectionObj) throws JSONException
    {
        JSONArray bbox = sectionObj.getJSONArray("bbox");
        this.boundingBox = new AABB((float) bbox.getDouble(0), (float) bbox.getDouble(1), (float) bbox.getDouble(2), (float) bbox.getDouble(3));

        this.setName(sectionObj.getString("name"));
        this.setFitting(sectionObj.getBoolean("fitting"));

        this.lightList = new ArrayList<Light>();
        this.lightHiddenList = new ArrayList<Boolean>();
        JSONArray lights = sectionObj.getJSONArray("lights");
        JSONArray hidden = sectionObj.getJSONArray("hidden");

        for (int li = 0; li < lights.length(); li++)
        {
            JSONObject lightObj = lights.getJSONObject(li);
            Light light = new Light(lightObj);
            this.insertLight(light);

            String hiddenStr = hidden.getString(li);
            this.lightHiddenList.set(li, hiddenStr.equals("T"));
        }
    }

    /**
     * Constructs a rectangular frame based on the data within a JSON-formatted metadata string
     * @param jsonSectionString the JSON metadata string
     * @throws JSONException if the JSON metadata does not match with the expected data
     */
    public Section(String jsonSectionString) throws JSONException
    {
        JSONObject sectionObj = new JSONObject(jsonSectionString);

        JSONArray bbox = sectionObj.getJSONArray("bbox");
        this.boundingBox = new AABB((float) bbox.getDouble(0), (float) bbox.getDouble(1), (float) bbox.getDouble(2), (float) bbox.getDouble(3));

        this.setName(sectionObj.getString("name"));
        this.setFitting(sectionObj.getBoolean("fitting"));

        this.lightList = new ArrayList<Light>();
        this.lightHiddenList = new ArrayList<Boolean>();
        JSONArray lights = sectionObj.getJSONArray("lights");
        JSONArray hidden = sectionObj.getJSONArray("hidden");

        for (int li = 0; li < lights.length(); li++)
        {
            JSONObject lightObj = lights.getJSONObject(li);
            String lightObjStr = lightObj.toString();
            Light light = new Light(lightObjStr);
            this.insertLight(light);

            String hiddenStr = hidden.getString(li);
            this.lightHiddenList.set(li, hiddenStr.equals("T"));
        }
    }

    /**
     * Pre-computes rendering data to save time when rendering this frame of lights.
     */
    public void prepare()
    {
        for (int li = 0; li < lightList.size(); li++)
        {
            Light light = lightList.get(li);
            light.prepare();
        }
    }

    public void clean()
    {
        for (int li = 0; li < lightList.size(); li++)
        {
            Light light = lightList.get(li);
            light.clean();
        }
    }

    /**
     * Renders the frame of lights in an OpenGL context.
     * If fitting is visible, all lights will be scaled and translated so that they fit within the bounds of the frame,
     * as if that frame were resized from previously covering the dimensions of the rendering area.
     * IMPORTANT: ensure that an OpenGL context is visible, GLESTBAM texture manager is initialized, and ShaderUtils is initialized before calling this function.
     */
    public void draw()
    {
        float xFitTranslation = (boundingBox.getMaximumX() + boundingBox.getMinimumX()) * 0.5f;
        float yFitTranslation = (boundingBox.getMaximumY() + boundingBox.getMinimumY()) * 0.5f;
        float uniformFitScaling = Math.min(App.getAspectRatio(false) * (boundingBox.getMaximumX() - boundingBox.getMinimumX()), boundingBox.getMaximumY() - boundingBox.getMinimumY());

        int sectionAABBId = ShaderUtils.shader.getUniform(GLShader.Uniform.SECTION_AABB);
        GLES20.glUniform4f(sectionAABBId, boundingBox.getMinimumX(), boundingBox.getMaximumX(), boundingBox.getMinimumY(), boundingBox.getMaximumY());

        ShaderUtils.pushViewMatrix();

        if (fitting)
        {
            Matrix.translateM(ShaderUtils.vMatrix, 0, xFitTranslation, yFitTranslation, 0.0f);
            Matrix.scaleM(ShaderUtils.vMatrix, 0, uniformFitScaling, uniformFitScaling, 1.0f);
        }

        for (int i = 0; i < getLightCount(); i++)
        {
            if (lightHiddenList.get(i))
            {
                continue;
            }

            Light light = getLight(i);
            light.draw();
        }

        ShaderUtils.popViewMatrix();
    }

    /**
     * Inserts a new, default light into the sequential list of lights to be rendered.
     */
    public void insertLight()
    {
        lightList.add(new Light());
        lightHiddenList.add(false);
    }

    /**
     * Inserts a specific light into the sequential list of lights to be rendered.
     * @param newLight the new light to be rendered
     */
    public void insertLight(Light newLight)
    {
        lightList.add(newLight);
        lightHiddenList.add(false);
    }

    /**
     * Inserts a specific light into the sequential list of lights to be rendered, at a specific index.
     * @param lightNum the specific index
     * @param newLight the specific light
     */
    public void insertLight(int lightNum, Light newLight)
    {
        lightList.add(lightNum, newLight);
        lightHiddenList.add(lightNum, false);
    }

    /**
     * Retrieves a reference to a light at a specific index of the sequential list of lights to be rendered.
     * @param lightNum the index of the light within this frame's light list
     * @return a reference to the indexed light
     */
    public Light getLight(int lightNum)
    {
        if (lightNum >= lightList.size() || lightNum <= -1)
        {
            return null;
        }

        Light gotten = lightList.get(lightNum);
        return gotten;
    }

    /**
     * Replaces an existing light, indexed at a specific point of the sequential list of lights, with a new light.
     * @param newLight the new replacement light
     * @param lightNum the index of the light within this frame's light list
     */
    public void setLight(Light newLight, int lightNum)
    {
        if (lightNum >= lightList.size() || lightNum <= -1)
        {
            return;
        }

        lightList.set(lightNum, newLight);
    }

    /**
     * Removes an existing light at a specific index of the sequential list of lights.
     * @param lightNum the index of the existing light
     */
    public void removeLight(int lightNum)
    {
        if (lightNum >= lightList.size() || lightNum <= -1)
        {
            return;
        }

        lightList.remove(lightNum);
        lightHiddenList.remove(lightNum);
    }

    /**
     * Retrieves the number of lights contained within the sequential list of lights.
     * @return the number of lights in this frame
     */
    public int getLightCount()
    {
        return lightList.size();
    }

    /**
     * Retrieves a reference to the object that indicates the bounds of this frame.
     * @return a reference to the axis-aligned bounding box of this frame
     */
    public AABB getBoundingBox()
    {
        return boundingBox;
    }

    /**
     * Sets a new axis-aligned bounding box for this frame
     * @param newBoundingBox the new axis-aligned bounding box
     */
    public void setBoundingBox(AABB newBoundingBox)
    {
        boundingBox = new AABB(newBoundingBox);
    }

    /**
     * Retrieves the name of this frame.
     * @return the name of this frame
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of this frame
     * @param newName the new name of this frame
     */
    public void setName(String newName)
    {
        name = newName;
    }

    /**
     * Retrieves whether all lights will be scaled and translated to fit within the bounds of the frame, or if lights will be simply be cut off when they exit said bounds.
     * @return true if all lights will be transformed to fit within the frame, false if not
     */
    public boolean hasFitting()
    {
        return fitting;
    }

    /**
     * Sets whether all lights will be scaled and translated to fit within the bounds of the frame, or if lights will be simply be cut off when they exit said bounds.
     * @param newFitting true if all lights will be transformed to fit within the frame, false if not
     */
    public void setFitting(boolean newFitting)
    {
        fitting = newFitting;
    }

    /**
     * Steps the animation of this frame one unit forward.
     */
    public void stepForward()
    {
        for (int i = 0; i < this.getLightCount(); i++)
        {
            this.getLight(i).stepForward();
        }
    }

    /**
     * Steps the animation of this frame one unit backwards.
     */
    public void stepBackward()
    {
        for (int i = 0; i < this.getLightCount(); i++)
        {
            this.getLight(i).stepBackward();
        }
    }

    /**
     * Resets the animation of this frame.
     */
    public void reset()
    {
        for (int i = 0; i < this.getLightCount(); i++)
        {
            this.getLight(i).reset();
        }
    }

    /**
     * Serializes the frame into JSON string format, including all of its lights.
     * @return the JSON string representation of the frame
     */
    public String toJSONString()
    {
        StringBuilder jsonStringBuilder = new StringBuilder();

        jsonStringBuilder.append("{"); // section open

        jsonStringBuilder.append("\"bbox\":[");
        jsonStringBuilder.append(boundingBox.getMinimumX() + ",");
        jsonStringBuilder.append(boundingBox.getMaximumX() + ",");
        jsonStringBuilder.append(boundingBox.getMinimumY() + ",");
        jsonStringBuilder.append(boundingBox.getMaximumY() + "]" + ",");

        jsonStringBuilder.append("\"name\":" + "\"" + this.getName() + "\"" + ",");
        jsonStringBuilder.append("\"fitting\":" + this.hasFitting() + ",");

        jsonStringBuilder.append("\"lights\":[");
        for (int li = 0; li < this.getLightCount(); li++)
        {
            if (li != 0) { jsonStringBuilder.append(","); } // light delimiter

            Light light = this.getLight(li);
            jsonStringBuilder.append(light.toJSONString());
        }

        jsonStringBuilder.append("]" + ",");
        jsonStringBuilder.append("\"hidden\":[");

        for (int li = 0; li < this.getLightCount(); li++)
        {
            if (li != 0) { jsonStringBuilder.append(","); } // light delimiter
            jsonStringBuilder.append(this.isLightHidden(li) ? "T" : "F");
        }

        jsonStringBuilder.append("]");
        jsonStringBuilder.append("}"); // section close

        return jsonStringBuilder.toString();
    }

    /**
     * Serializes the frame into JSON format, stores the serialized format into a JSON container object, and returns the object.
     * @return the JSON container object for the frame
     * @throws JSONException
     */
    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject sectionObj = new JSONObject();

        JSONArray boundingBoxArr = new JSONArray();
        boundingBoxArr.put(boundingBox.getMinimumX());
        boundingBoxArr.put(boundingBox.getMaximumX());
        boundingBoxArr.put(boundingBox.getMinimumY());
        boundingBoxArr.put(boundingBox.getMaximumY());

        sectionObj.put("bbox", boundingBoxArr);

        sectionObj.put("name", this.getName());
        sectionObj.put("fitting", this.hasFitting());

        JSONArray lightObjArr = new JSONArray();

        for (int li = 0; li < this.getLightCount(); li++)
        {
            Light light = this.getLight(li);
            JSONObject lightObj = light.toJSONObject();
            lightObjArr.put(lightObj);
        }

        JSONArray lightHiddenArr = new JSONArray();

        for (int li = 0; li < this.getLightCount(); li++)
        {
            lightHiddenArr.put(this.isLightHidden(li) ? "T" : "F");
        }

        sectionObj.put("lights", lightObjArr);
        sectionObj.put("hidden", lightHiddenArr);

        return sectionObj;
    }

    public void hideAllComponents(boolean recursiveHide)
    {
        for (int li = 0; li < this.getLightCount(); li++)
        {
            this.hideComponent(li, recursiveHide);
        }
    }

    public void hideComponent(int componentIdx, boolean recursiveHide)
    {
        lightHiddenList.set(componentIdx, true);
    }

    public void unhideAllComponents(boolean recursiveUnhide)
    {
        for (int li = 0; li < this.getLightCount(); li++)
        {
            this.unhideComponent(li, recursiveUnhide);
        }
    }

    public void unhideComponent(int componentIdx, boolean recursiveUnhide)
    {
        lightHiddenList.set(componentIdx, false);
    }

    public boolean isComponentHidden(int componentIdx)
    {
        return lightHiddenList.get(componentIdx);
    }

    public void hideAllLights()
    {
        hideAllComponents(false);
    }

    public void hideLight(int lightIdx)
    {
        hideComponent(lightIdx, false);
    }

    public void unhideAllLights()
    {
        unhideAllComponents(false);
    }

    public void unhideLight(int lightIdx)
    {
        unhideComponent(lightIdx, false);
    }

    public boolean isLightHidden(int lightIdx)
    {
        return isComponentHidden(lightIdx);
    }
}
