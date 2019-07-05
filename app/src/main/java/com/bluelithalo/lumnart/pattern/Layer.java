package com.bluelithalo.lumnart.pattern;

import android.opengl.GLES20;

import com.bluelithalo.lumnart.util.GLShader;
import com.bluelithalo.lumnart.util.ShaderUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A transparent overlay that acts as a container for axis-aligned sections of lights. All lights are rendered in an orthographic setting,
 * so any layer that is theoretically closer to phone screen plane will have its lights displayed over those that are farther from said plane.
 * This closeness to the screen plane is indicated by the layer's height, which is a value that is less than or equal to 0 (so it really indicates depth).
 * Each layer also has an alpha value that dictates the overall transparency of its contents (independent of a light's existing transparency by its color value).
 */
public class Layer implements JSONizable, HasHideableComponents
{
    private ArrayList<Section> sectionList;
    private ArrayList<Boolean> sectionHiddenList;

    private String name;
    private float alpha;

    /**
     * Constructs a default layer, which is fully opaque and set on the screen plane.
     */
    public Layer()
    {
        name = "New Layer";
        sectionList = new ArrayList<Section>();
        sectionHiddenList = new ArrayList<Boolean>();
        alpha = 1.0f;

        insertSection();
    }

    /**
     * Constructs a layer that is full opaque, but set to be at some custom height.
     * @param layerHeight the custom height
     */
    public Layer(int layerHeight)
    {
        name = "New Layer";
        sectionList = new ArrayList<Section>();
        sectionHiddenList = new ArrayList<Boolean>();
        alpha = 1.0f;

        insertSection();
    }

    /**
     * Constructs a layer that copies the attributes of an existing layer
     * @param copy the existing layer to copy from
     */
    public Layer(Layer copy)
    {
        this.name = copy.getName();
        this.alpha = copy.getAlpha();

        this.sectionList = new ArrayList<Section>();
        this.sectionHiddenList = new ArrayList<Boolean>();

        for (int i = 0; i < copy.getSectionCount(); i++)
        {
            Section sectionCopy = copy.getSection(i);
            insertSection(new Section(sectionCopy));
            this.sectionHiddenList.set(i, copy.isSectionHidden(i));
        }
    }

    /**
     * Constructs a layer from the contents of a JSON metadata container object.
     * @param layerObj the JSON metadata container object
     * @throws JSONException if there is a mismatch between the JSON data and expected data
     */
    public Layer(JSONObject layerObj) throws JSONException
    {
        this.setName(layerObj.getString("name"));
        this.setAlpha((float) layerObj.getDouble("alpha"));

        this.sectionList = new ArrayList<Section>();
        this.sectionHiddenList = new ArrayList<Boolean>();
        JSONArray sections = layerObj.getJSONArray("sections");
        JSONArray hidden = layerObj.getJSONArray("hidden");

        for (int se = 0; se < sections.length(); se++)
        {
            JSONObject sectionObj = sections.getJSONObject(se);
            Section section = new Section(sectionObj);
            this.insertSection(section);

            String hiddenStr = hidden.getString(se);
            this.sectionHiddenList.set(se, hiddenStr.equals("T"));
        }
    }

    /**
     * Constructs a layer from the contents of a JSON metadata string.
     * @param jsonLayerString the JSON metadata string
     * @throws JSONException if there is a mismatch between the JSON data and expected data
     */
    public Layer(String jsonLayerString) throws JSONException
    {
        JSONObject layerObj = new JSONObject(jsonLayerString);

        this.setName(layerObj.getString("name"));
        this.setAlpha((float) layerObj.getDouble("alpha"));

        this.sectionList = new ArrayList<Section>();
        this.sectionHiddenList = new ArrayList<Boolean>();
        JSONArray sections = layerObj.getJSONArray("sections");
        JSONArray hidden = layerObj.getJSONArray("hidden");

        for (int se = 0; se < sections.length(); se++)
        {
            JSONObject sectionObj = sections.getJSONObject(se);
            String sectionObjStr = sectionObj.toString();
            Section section = new Section(sectionObjStr);
            this.insertSection(section);

            String hiddenStr = hidden.getString(se);
            this.sectionHiddenList.set(se, hiddenStr.equals("T"));
        }
    }

    /**
     * Pre-computes rendering data to save time when rendering this layer.
     */
    public void prepare()
    {
        for (int se = 0; se < sectionList.size(); se++)
        {
            Section section = sectionList.get(se);
            section.prepare();
        }
    }

    public void clean()
    {
        for (int se = 0; se < sectionList.size(); se++)
        {
            Section section = sectionList.get(se);
            section.clean();
        }
    }

    /**
     * Renders the layer in an OpenGL context.
     * IMPORTANT: ensure that an OpenGL context is active, GLESTBAM texture manager is initialized, and ShaderUtils is initialized before calling this function.
     */
    public void draw()
    {
        int alphaId = ShaderUtils.shader.getUniform(GLShader.Uniform.ALPHA);
        GLES20.glUniform1f(alphaId, alpha);

        for (int se = 0; se < getSectionCount(); se++)
        {
            if (sectionHiddenList.get(se))
            {
                continue;
            }

            Section section = getSection(se);
            section.draw();
        }
    }

    /**
     * Inserts a new, default section to the current list of sections.
     */
    public void insertSection()
    {
        sectionList.add(new Section());
        sectionHiddenList.add(false);
    }

    /**
     * Inserts an existing section to the current list of sections.
     * @param newSection the new, existing section
     */
    public void insertSection(Section newSection)
    {
        sectionList.add(newSection);
        sectionHiddenList.add(false);
    }

    /**
     * Inserts an existing section into the layer at a specific index.
     * @param sectionNum the specific index
     * @param newSection the existing section
     */
    public void insertSection(int sectionNum, Section newSection)
    {
        sectionList.add(sectionNum, newSection);
        sectionHiddenList.add(sectionNum, false);
    }

    /**
     * Retrieve a reference to section at a certain index of the current list of sections.
     * @param sectionNum the index of the section in the list
     * @return the reference to the indexed section
     */
    public Section getSection(int sectionNum)
    {
        if (sectionNum >= sectionList.size() || sectionNum <= -1)
        {
            return null;
        }

        Section gotten = sectionList.get(sectionNum);
        return gotten;
    }

    /**
     * Replace an existing section in the current list of sections with a new section.
     * @param newSection the new section to act as a replacement
     * @param sectionNum the index of the section to replace
     */
    public void setSection(Section newSection, int sectionNum)
    {
        if (sectionNum >= sectionList.size() || sectionNum <= -1)
        {
            return;
        }

        sectionList.set(sectionNum, newSection);
    }

    /**
     * Removes an existing section from the current list of sections.
     * @param sectionNum the index of the section to remove
     */
    public void removeSection(int sectionNum)
    {
        if (sectionNum >= sectionList.size() || sectionNum <= -1)
        {
            return;
        }

        sectionList.remove(sectionNum);
        sectionHiddenList.remove(sectionNum);
    }

    /**
     * Retrieve the number of sections that are contained within this layer.
     * @return the number of sections within this layer
     */
    public int getSectionCount()
    {
        return sectionList.size();
    }

    /**
     * Retrieve the name of this layer.
     * @return the name of this layer
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of this layer.
     * @param newName the new name of this layer
     */
    public void setName(String newName)
    {
        name = newName;
    }

    /**
     * Retrieve the opacity value of this layer, in the range of [0.00, 1.00].
     * @return the opacity value of this layer
     */
    public float getAlpha()
    {
        return alpha;
    }

    /**
     * Set the opacity value of this layer, in the range of [0.00, 1.00].
     * @param newAlpha
     */
    public void setAlpha(float newAlpha)
    {
        alpha = Math.max(0.00f, Math.min(newAlpha, 1.00f));
    }

    /**
     * Steps the animation of this layer one unit forward.
     */
    public void stepForward()
    {
        for (int i = 0; i < this.getSectionCount(); i++)
        {
            this.getSection(i).stepForward();
        }
    }

    /**
     * Steps the animation of this frame one unit backward.
     */
    public void stepBackward()
    {
        for (int i = 0; i < this.getSectionCount(); i++)
        {
            this.getSection(i).stepBackward();
        }
    }

    /**
     * Resets the animation of this layer back to its zero time-point.
     */
    public void reset()
    {
        for (int i = 0; i < this.getSectionCount(); i++)
        {
            this.getSection(i).reset();
        }
    }

    /**
     * Serializes the layer into JSON string format, including all of its sections.
     * @return the JSON string representation of the layer
     */
    public String toJSONString()
    {
        StringBuilder jsonStringBuilder = new StringBuilder();

        jsonStringBuilder.append("{"); // layer open
        jsonStringBuilder.append("\"name\":" + "\"" + this.getName() + "\"" + ",");
        jsonStringBuilder.append("\"alpha\":" + this.getAlpha() + ",");

        jsonStringBuilder.append("\"sections\":[");
        for (int se = 0; se < this.getSectionCount(); se++)
        {
            if (se != 0) { jsonStringBuilder.append(","); } // section delimiter

            Section section = this.getSection(se);
            jsonStringBuilder.append(section.toJSONString());
        }

        jsonStringBuilder.append("]" + ",");
        jsonStringBuilder.append("\"hidden\":[");

        for (int se = 0; se < this.getSectionCount(); se++)
        {
            if (se != 0) { jsonStringBuilder.append(","); } // section delimiter
            jsonStringBuilder.append(this.isSectionHidden(se) ? "T" : "F");
        }

        jsonStringBuilder.append("]");
        jsonStringBuilder.append("}"); // layer close

        return jsonStringBuilder.toString();
    }

    /**
     * Serializes the layer into JSON format, stores the serialized format into a JSON container object, and returns the object.
     * @return the JSON container object for the layer
     * @throws JSONException
     */
    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject layerObj = new JSONObject();

        layerObj.put("name", this.getName());
        layerObj.put("alpha", this.getAlpha());

        JSONArray sectionObjArr = new JSONArray();

        for (int se = 0; se < this.getSectionCount(); se++)
        {
            Section section = this.getSection(se);
            JSONObject sectionObj = section.toJSONObject();
            sectionObjArr.put(sectionObj);
        }

        JSONArray sectionHiddenArr = new JSONArray();

        for (int se = 0; se < this.getSectionCount(); se++)
        {
            sectionHiddenArr.put(this.isSectionHidden(se) ? "T" : "F");
        }

        layerObj.put("sections", sectionObjArr);
        layerObj.put("hidden", sectionHiddenArr);

        return layerObj;
    }

    public void hideAllComponents(boolean recursiveHide)
    {
        for (int se = 0; se < this.getSectionCount(); se++)
        {
            this.hideComponent(se, recursiveHide);
        }
    }

    public void hideComponent(int componentIdx, boolean recursiveHide)
    {
        sectionHiddenList.set(componentIdx, true);
        if (recursiveHide) { sectionList.get(componentIdx).hideAllComponents(recursiveHide); }
    }

    public void unhideAllComponents(boolean recursiveUnhide)
    {
        for (int se = 0; se < this.getSectionCount(); se++)
        {
            this.unhideComponent(se, recursiveUnhide);
        }
    }

    public void unhideComponent(int componentIdx, boolean recursiveUnhide)
    {
        sectionHiddenList.set(componentIdx, false);
        if (recursiveUnhide) { sectionList.get(componentIdx).unhideAllComponents(recursiveUnhide); }
    }

    public boolean isComponentHidden(int componentIdx)
    {
        return sectionHiddenList.get(componentIdx);
    }

    public void hideAllSections(boolean recursiveHide)
    {
        hideAllComponents(recursiveHide);
    }

    public void hideSection(int sectionIdx, boolean recursiveHide)
    {
        hideComponent(sectionIdx, recursiveHide);
    }

    public void unhideAllSections(boolean recursiveUnhide)
    {
        unhideAllComponents(recursiveUnhide);
    }

    public void unhideSection(int sectionIdx, boolean recursiveUnhide)
    {
        unhideComponent(sectionIdx, recursiveUnhide);
    }

    public boolean isSectionHidden(int sectionIdx)
    {
        return isComponentHidden(sectionIdx);
    }

}
