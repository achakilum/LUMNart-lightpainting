package com.bluelithalo.lumnart.pattern;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A dynamically shifting property, meant to transform the appearance of a LUMNart light in a looped fashion.
 * Separate transition phases of a property's values are denoted by a sequential list of stages.  A ticking clock indicates the time passage of each stage.
 * In each tick, the current stage is sampled for an interpolated vector that represents the progress of its represented stage.
 * This vector is used to shape a LUMNart light in a single frame, and a set of sequentially sampled vectors will form the complete, looping animation of a LUMNart light.
 */
public class Property implements JSONizable
{
    /**
     * Denotes the specific properties of a LUMNart light that may be dynamically shifted.
     */
    public enum Type
    {
        Visible, Color, Position, Dimensions, Angle
    }

    private ArrayList<Stage> stageList;
    private int clock;
    private int curStageIdx;

    private int intendedVectorLength;

    /**
     * Constructs a dynamically shifting property with the assertion of a certain vector length for the transitioning values.
     * @param vectorLength the number of transitioning values
     */
    public Property(int vectorLength)
    {
        stageList = new ArrayList<Stage>();
        clock = 0;
        curStageIdx = 0;

        intendedVectorLength = vectorLength;
    }

    /**
     * Constructs a dynamically shifting property from the data of another property.
     * @param copy the shifting property to copy from
     */
    public Property(Property copy)
    {
        this.clock = copy.getClockTick();
        this.curStageIdx = copy.getCurrentStageIndex();
        this.intendedVectorLength = copy.getIntendedVectorLength();

        this.stageList = new ArrayList<Stage>();

        for (int i = 0; i < copy.getStageCount(); i++)
        {
            Stage stageCopy = copy.getStage(i);
            this.insertStage(new Stage(stageCopy));
        }
    }

    /**
     * Constructs a dynamically shifting property using the elements of a JSON metadata container.
     * @param propertyObj the JSON metadata container for the property
     * @param propertyIntendedVectorLength the expected number of transitioning values from the JSON metadata
     * @throws JSONException when the JSON metadata does not match the expected metadata
     */
    public Property(JSONObject propertyObj, int propertyIntendedVectorLength) throws JSONException
    {
        this.intendedVectorLength = propertyIntendedVectorLength;
        this.clock = 0;
        this.curStageIdx = 0;

        this.stageList = new ArrayList<Stage>();
        JSONArray stagesArr = propertyObj.getJSONArray("stages");

        for (int st = 0; st < stagesArr.length(); st++)
        {
            JSONObject stageObj = stagesArr.getJSONObject(st);
            Stage stage = new Stage(stageObj, this.intendedVectorLength);

            this.insertStage(stage);
        }
    }

    /**
     * Constructs a dynamically shifting property using the elements of a JSON metadata string.
     * @param jsonPropertyString the JSON metadata string for the property
     * @param propertyIntendedVectorLength the expected number of transitioning values from the JSON metadata
     * @throws JSONException when the JSON metadata does not match the expected metadata
     */
    public Property(String jsonPropertyString, int propertyIntendedVectorLength) throws JSONException
    {
        JSONObject propertyObj = new JSONObject(jsonPropertyString);

        this.intendedVectorLength = propertyIntendedVectorLength;
        this.clock = 0;
        this.curStageIdx = 0;

        this.stageList = new ArrayList<Stage>();
        JSONArray stagesArr = propertyObj.getJSONArray("stages");

        for (int st = 0; st < stagesArr.length(); st++)
        {
            JSONObject stageObj = stagesArr.getJSONObject(st);
            String stageObjStr = stageObj.toString();
            Stage stage = new Stage(stageObjStr, this.intendedVectorLength);

            this.insertStage(stage);
        }
    }

    /**
     * Steps one unit forward in the property's current stage.
     * If the transition timeline's end has been passed, iterate to the next stage in the loop, and set the clock to the starting time of that stage (always zero).
     */
    public void stepForward()
    {
        clock++;

        if (clock >= getStage(curStageIdx).getDuration())
        {
            curStageIdx = (curStageIdx + 1) % stageList.size();
            clock = 0;
        }
    }

    /**
     * Steps one unit backward in the property's current stage.
     * If the transition timeline's start has been passed, iterate to the previous stage in the loop, and set the clock to the ending time of that stage.
     */
    public void stepBackward()
    {
        clock--;

        if (clock < 0)
        {
            curStageIdx = (curStageIdx + stageList.size() - 1) % stageList.size();
            clock = getStage(curStageIdx).getDuration() - 1;
        }
    }

    /**
     * Resets this property back to its first stage, and resets the clock to zero.
     */
    public void reset()
    {
        curStageIdx = 0;
        clock = 0;
    }

    /**
     * Sets the current stage to be sampled, along with the clock position.
     * @param stageIdx the new stage to be sampled
     * @param stageClock the new clock position
     */
    public void seekTo(int stageIdx, int stageClock)
    {
        curStageIdx = stageIdx % stageList.size();
        clock = Math.max(0, Math.min(stageClock, getStage(curStageIdx).getDuration() - 1));
    }

    /**
     * Initializes a new stage with its start and end values initialized to zero, and inserts it.
     */
    public void insertStage()
    {
        Stage newStage = new Stage(intendedVectorLength);
        newStage.setStartVector(new float[intendedVectorLength]);
        newStage.setEndVector(new float[intendedVectorLength]);
        newStage.setDuration(60);
        newStage.setTransitionCurve(Stage.Transition.Linear);

        stageList.add(newStage);
    }

    /**
     * Inserts a new stage to the end of the list of phases.
     * @param newStage the new stage
     */
    public void insertStage(Stage newStage)
    {
        if (newStage.getVectorLength() != intendedVectorLength)
        {
            return;
        }

        stageList.add(newStage);
    }

    /**
     * Inserts a specific stage into the sequential list of stages to cycled through, at a specific index.
     * @param stageNum the specific index
     * @param newStage the specific stage
     */
    public void insertStage(int stageNum, Stage newStage)
    {
        if (newStage.getVectorLength() != intendedVectorLength)
        {
            return;
        }

        stageList.add(stageNum, newStage);
    }

    /**
     * Retrieves a reference to a stage by its index.
     * @param stageNum the index of a stage to be retrieved.
     * @return null of the index is out of bounds, or the stage otherwise.
     */
    public Stage getStage(int stageNum)
    {
        if (stageNum >= stageList.size() || stageNum <= -1)
        {
            return null;
        }

        Stage gotten = stageList.get(stageNum);
        return gotten;
    }

    /**
     * Sets the stage, indexed at a certain point of the stage list, to be the given new stage.
     * @param newStage the new stage to replace the old indexed stage
     * @param stageNum the index of the existing stage
     */
    public void setStage(Stage newStage, int stageNum)
    {
        if (stageNum >= stageList.size() || stageNum <= -1)
        {
            return;
        }

        if (newStage.getVectorLength() != intendedVectorLength)
        {
            return;
        }

        stageList.set(stageNum, newStage);
    }

    /**
     * Removes a stage that is indexed at a certain point of the stage list.
     * @param stageNum the index of the existing stage
     */
    public void removeStage(int stageNum)
    {
        if (stageNum >= stageList.size() || stageNum <= -1)
        {
            return;
        }

        stageList.remove(stageNum);
    }

    /**
     * Pre-processes the stage data contained within this property to avoid unnecesary computations when sampling vectors from a stage.
     */
    public void prepare()
    {
        for (int s = 0; s < stageList.size(); s++)
        {
            Stage curStage = stageList.get(s);
            curStage.prepare();
        }
    }

    /**
     * Samples the current stage for a vector whose values are interpolated depending on the current clock tick value.
     * @return the currently interpolated vector of the current stage
     */
    public float[] getCurrentVector()
    {
        Stage curStage = stageList.get(curStageIdx);
        float[] curVector = curStage.getVectorAtTick(clock);

        return curVector;
    }

    /**
     * Retrieves the current clock tick value.
     * @return the current clock tick value
     */
    public int getClockTick()
    {
        return clock;
    }

    /**
     * Sets a new clock tick value.
     * @param newClock the new clock tick value
     */
    public void setClockTick(int newClock)
    {
        clock = newClock;
    }

    /**
     * Retrieves the index of the stage being sampled currently.
     * @return the index of the currently sampled stage
     */
    public int getCurrentStageIndex()
    {
        return curStageIdx;
    }

    /**
     * Sets the index of the stage to be sampled.
     * @param newStageIdx the new stage index
     */
    public void setCurrentStageIndex(int newStageIdx)
    {
        curStageIdx = newStageIdx & stageList.size();
        clock = 0;
    }

    /**
     * Retrieves the expected number of values from each stage.
     * @return the expected number of values from each stage.
     */
    public int getIntendedVectorLength()
    {
        return intendedVectorLength;
    }

    /**
     * Retrieves the number of stages of transitions that are sequenced for shifting this property.
     * @return the number of stages currently in sequence
     */
    public int getStageCount()
    {
        int stageCount = stageList.size();
        return stageCount;
    }

    /**
     * Serializes the dynamically shifting property into JSON string format, including all of its stages.
     * @return the JSON string representation of the dynamically shifting property.
     */
    public String toJSONString()
    {
        StringBuilder jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("{"); // property open

        jsonStringBuilder.append("\"stages\":[");
        for (int st = 0; st < this.getStageCount(); st++)
        {
            if (st != 0) { jsonStringBuilder.append(","); } // stage delimiter

            Stage stage = this.getStage(st);
            jsonStringBuilder.append(stage.toJSONString());
        }

        jsonStringBuilder.append("]");
        jsonStringBuilder.append("}"); // property close

        return jsonStringBuilder.toString();
    }

    /**
     * Serializes the dynamically shifting property into JSON format, stores the serialized format into a JSON container object, and returns the object.
     * @return the JSON container object for the dynamically shifting property
     * @throws JSONException
     */
    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject propertyObj = new JSONObject();

        JSONArray stageObjArr = new JSONArray();

        for (int st = 0; st < this.getStageCount(); st++)
        {
            Stage stage = this.getStage(st);
            JSONObject stageObj = stage.toJSONObject();
            stageObjArr.put(stageObj);
        }

        propertyObj.put("stages", stageObjArr);

        return propertyObj;
    }
}