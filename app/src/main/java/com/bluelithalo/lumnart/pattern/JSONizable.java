package com.bluelithalo.lumnart.pattern;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Denotes objects that are serializable to the JSON format.
 */
public interface JSONizable
{
    /**
     * Serializes the contents of the object into JSON format and returns the respective string.
     * @return the JSON-serialized object in string format.
     */
    String toJSONString();

    /**
     * Serializes the contents of the object into JSON format, stores the serialized form into a container object, and returns the container object.
     * @return the JSON metadata container object
     * @throws JSONException
     */
    JSONObject toJSONObject() throws JSONException;
}
