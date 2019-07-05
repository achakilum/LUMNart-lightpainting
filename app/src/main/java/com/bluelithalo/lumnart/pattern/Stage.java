package com.bluelithalo.lumnart.pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * A transitional phase from one vector-based set of values to another.
 * This phase must be defined with a constant vector length for both the starting vector, ending vector, and the interpolated sample vector.
 * Additionally, each phase must have a non-zero duration, which indicates the number of clock ticks that may pass from its start to end.
 * This transitional phase may be sampled for an interpolated vector at any point between the start duration (zero) and end duration (non-zero),
 * and the result of this interpolation depends on what type of transition will happen in this phase.
 */
public class Stage implements JSONizable
{
    /**
     * The type of transition, recognized as a type of curve from the start vector to the end vector.
     */
    public enum Transition
    {
        None, Instant, Linear, Sinusoidal
    }

    private int vectorLength;

    private float[] startVector;
    private float[] endVector;
    private float[] curVector;
    private int duration;
    private Transition transitionCurve;

    // An array of precomputed vectors, initialized and filled upon calling the prepape() function.
    private float[][] preparedVectors;

    /**
     * Constructs a transitional phase, of a certain vector length, that is 60 clock ticks long.
     * @param stageVectorLength the expected vector length
     */
    public Stage(int stageVectorLength)
    {
    	this(stageVectorLength, 60);
    }

    /**
     * Constructs a transitional phase, of a certain vector length, with a specified duration of clock ticks.
     * @param stageVectorLength the expected vector length
     * @param durationInTicks the duration of the phase in clock ticks
     */
    public Stage(int stageVectorLength, int durationInTicks)
    {
        this.vectorLength = stageVectorLength;

        this.startVector = new float[stageVectorLength];
        this.endVector = new float[stageVectorLength];
        this.curVector = new float[stageVectorLength];
        this.duration = Math.max(1, durationInTicks);
        this.transitionCurve = Transition.Linear;

        this.preparedVectors = null;
    }

    /**
     * Constructs a transitional phase that copies the attributes of another transitional phase
     * @param copy the transitional phase to copy from
     */
    public Stage(Stage copy)
    {
        this.vectorLength = copy.getVectorLength();

        this.startVector = Arrays.copyOf(copy.getStartVector(), copy.getVectorLength());
        this.endVector = Arrays.copyOf(copy.getEndVector(), copy.getVectorLength());
        this.curVector = new float[vectorLength];
        this.duration = copy.getDuration();
        this.transitionCurve = copy.getTransitionCurve();

        this.preparedVectors = null;
    }

    /**
     * Constructs a transitional phase from a JSON metadata container object, with an expected vector length.
     * @param stageObj the JSON metadata container object
     * @param stageVectorLength the expected vector length
     * @throws JSONException if the JSON metadata does not match the expected metadata
     */
    public Stage(JSONObject stageObj, int stageVectorLength) throws JSONException
    {
        this.vectorLength = stageVectorLength;
        this.startVector = new float[stageVectorLength];
        this.endVector = new float[stageVectorLength];
        this.curVector = new float[stageVectorLength];

        JSONArray startVecArr = stageObj.getJSONArray("startVector");
        JSONArray endVecArr = stageObj.getJSONArray("endVector");

        float[] startVector = new float[stageVectorLength];
        float[] endVector = new float[stageVectorLength];

        for (int v = 0; v < stageVectorLength; v++)
        {
            startVector[v] = (float) startVecArr.getDouble(v);
            endVector[v] = (float) endVecArr.getDouble(v);
        }

        this.setStartVector(startVector);
        this.setEndVector(endVector);

        this.setDuration(stageObj.getInt("duration"));
        this.setTransitionCurve(Stage.Transition.values()[stageObj.getInt("transitionCurve")]);

        this.preparedVectors = null;
    }

    /**
     * Constructs a transitional phase from a JSON metadata string, with an expected vector length.
     * @param jsonStageString the JSON metadata string
     * @param stageVectorLength the expected vector length
     * @throws JSONException if the JSON metadata does not match the expected metadata
     */
    public Stage(String jsonStageString, int stageVectorLength) throws JSONException
    {
        JSONObject stageObj = new JSONObject(jsonStageString);

        this.vectorLength = stageVectorLength;
        this.startVector = new float[stageVectorLength];
        this.endVector = new float[stageVectorLength];
        this.curVector = new float[stageVectorLength];

        JSONArray startVecArr = stageObj.getJSONArray("startVector");
        JSONArray endVecArr = stageObj.getJSONArray("endVector");

        float[] startVector = new float[stageVectorLength];
        float[] endVector = new float[stageVectorLength];

        for (int v = 0; v < stageVectorLength; v++)
        {
            startVector[v] = (float) startVecArr.getDouble(v);
            endVector[v] = (float) endVecArr.getDouble(v);
        }

        this.setStartVector(startVector);
        this.setEndVector(endVector);

        this.setDuration(stageObj.getInt("duration"));
        this.setTransitionCurve(Stage.Transition.values()[stageObj.getInt("transitionCurve")]);

        this.preparedVectors = null;
    }

    /**
     * Pre-computes each interpolated vector of a transitional phase to avoid unnecessary, possibly heavy computations later on.
     */
    public void prepare()
    {
        this.preparedVectors = new float[duration][vectorLength];

        for (int ti = 0; ti < duration; ti++)
        {
            float[] calculatedVector = this.getVectorAtTick(ti);

            for (int e = 0; e < calculatedVector.length; e++)
            {
                this.preparedVectors[ti][e] = calculatedVector[e];
            }
        }
    }

    /**
     * Retrieves a pre-computed interpolated vector at a certain clock tick.
     * @param tick the specific clock tick within this transitional phase's duration
     * @return the pre-computed interpolated vector
     */
    public float[] getPreparedVectorAtTick(int tick)
    {
        return this.preparedVectors[tick];
    }

    /**
     * Computes an interpolated vector between the given starting and ending vector, depending on the type of transition and specified clock tick, and returns it.
     * @param tick the specific clock tick within this transitional phase's duration
     * @return the computed interpolated vector
     */
    public float[] getVectorAtTick(int tick)
    {
    	if (transitionCurve == Stage.Transition.None)
    	{
    		for (int i = 0; i < vectorLength; i++)
    		{
    			curVector[i] = startVector[i];
    		}
    	}
        else
        if (transitionCurve == Stage.Transition.Instant)
        {
            for (int i = 0; i < vectorLength; i++)
            {
            	curVector[i] = endVector[i];
            }
        }
    	else
    	if (transitionCurve == Stage.Transition.Linear)
    	{
    		for (int i = 0; i < vectorLength; i++)
    		{
    			curVector[i] = (((duration - tick) / (duration * 1.0f)) * startVector[i])
    						+ ((tick / (duration * 1.0f)) * endVector[i]);
    		}	
    	}
    	else
    	if (transitionCurve == Stage.Transition.Sinusoidal)
    	{
    		for (int i = 0; i < vectorLength; i++)
    		{
    			curVector[i] = (float) (((startVector[i] - endVector[i]) * 0.5f) * Math.cos(Math.PI * (tick * 1.0f) / duration) + ((startVector[i] + endVector[i]) * 0.5f));
    		}
    	}

    	return curVector;
    }

    /**
     * Retrieves the expected vector length
     * @return the expected vector length
     */
    public int getVectorLength()
    {
    	return vectorLength;
    }

    /**
     * Retrieves a reference to the starting vector of this transitional phase.
     * @return the starting vector
     */
    public float[] getStartVector()
    {
    	return startVector;
    }

    /**
     * Retrieves a reference to the ending vector of this transitional phase.
     * @return the ending vector
     */
    public float[] getEndVector()
    {
    	return endVector;
    }

    /**
     * Retrieves the duration of this transitional phase
     * @return the duration
     */
    public int getDuration()
    {
    	return duration;
    }

    /**
     * Retrieves the type of transition (recognized as a curve) that will occur between the starting and ending vectors, determining how the interpolated vectors will turn out.
     * @return the type of transition
     */
    public Transition getTransitionCurve()
    {
    	return transitionCurve;
    }

    /**
     * Sets a new expected vector length and copies the original start/end vector values into newly made vectors.
     * If greater than the original length, the new vector will be padded with zeroes where values cannot be inserted.
     * @param newVectorLength the new expected vector length
     */
    public void setVectorLength(int newVectorLength)
    {
    	if (newVectorLength <= 0) { return; }
    	
    	vectorLength = newVectorLength;
    	startVector = Arrays.copyOf(startVector, newVectorLength);
    	endVector = Arrays.copyOf(endVector, newVectorLength);
    }

    /**
     * Sets a new sequence of values for the starting vector.
     * @param newStartVector the new starting vector
     */
    public void setStartVector(float[] newStartVector)
    {
    	if (newStartVector.length != vectorLength) { return; }

    	for (int i = 0; i < vectorLength; i++)
    	{
    		startVector[i] = newStartVector[i];
    	}
    }

    /**
     * Sets a new sequence of values for the ending vector.
     * @param newEndVector the new ending vector
     */
    public void setEndVector(float[] newEndVector)
    {
    	if (newEndVector.length != vectorLength) { return; }

    	for (int i = 0; i < vectorLength; i++)
    	{
    		endVector[i] = newEndVector[i];
    	}
    }

    /**
     * Sets a new duration for this transitional phase.
     * If less than 1, the duration will be set to 1.
     * @param newDuration the new duration
     */
    public void setDuration(int newDuration)
    {
    	duration = Math.max(1, newDuration);
    }

    /**
     * Sets a new type of transition (recognized as a curve) that will occur between the starting and ending vectors, determining how the interpolated vectors will turn out.
     * @param newTransitionCurve the new type of transition
     */
    public void setTransitionCurve(Stage.Transition newTransitionCurve)
    {
    	transitionCurve = newTransitionCurve;
    }

    /**
     * Serializes the transition phase into JSON string format, including the starting and ending vectors.
     * @return the JSON string representation of the transition phase
     */
    public String toJSONString()
    {
        StringBuilder jsonStringBuilder = new StringBuilder();

        float[] startVec = this.getStartVector();
        float[] endVec = this.getEndVector();

        jsonStringBuilder.append("{"); // stage open

        jsonStringBuilder.append("\"startVector\":" + "[");
        for (int sv = 0; sv < this.getVectorLength(); sv++)
        {
            if (sv != 0) { jsonStringBuilder.append(","); } // vector delimiter

            jsonStringBuilder.append(startVec[sv]);
        }
        jsonStringBuilder.append("]");
        jsonStringBuilder.append(",");

        jsonStringBuilder.append("\"endVector\":" + "[");
        for (int ev = 0; ev < this.getVectorLength(); ev++)
        {
            if (ev != 0) { jsonStringBuilder.append(","); } // vector delimiter

            jsonStringBuilder.append(endVec[ev]);
        }
        jsonStringBuilder.append("]");
        jsonStringBuilder.append(",");

        jsonStringBuilder.append("\"duration\":" + this.getDuration() + ",");
        jsonStringBuilder.append("\"transitionCurve\":" + this.getTransitionCurve().ordinal());
        jsonStringBuilder.append("}"); // stage close

        return jsonStringBuilder.toString();
    }

    /**
     * Serializes the transition phase into JSON format, stores the serialized format into a JSON container object, and returns the object.
     * @return the JSON container object for the transition phase
     * @throws JSONException
     */
    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject stageObj = new JSONObject();

        float[] startVec = this.getStartVector();
        float[] endVec = this.getEndVector();

        JSONArray startVectorArr = new JSONArray();

        for (int sv = 0; sv < this.getVectorLength(); sv++)
        {
            startVectorArr.put(startVec[sv]);
        }

        JSONArray endVectorArr = new JSONArray();

        for (int ev = 0; ev < this.getVectorLength(); ev++)
        {
            endVectorArr.put(endVec[ev]);
        }

        stageObj.put("startVector", startVectorArr);
        stageObj.put("endVector", endVectorArr);
        stageObj.put("duration", this.getDuration());
        stageObj.put("transitionCurve", this.getTransitionCurve().ordinal());

        return stageObj;
    }

}















