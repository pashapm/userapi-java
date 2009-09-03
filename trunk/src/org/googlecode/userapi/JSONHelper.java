package org.googlecode.userapi;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class JSONHelper {
    public static JSONArray objectToArray(JSONObject object) throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < object.length(); i++) {
            if (object.has(String.valueOf(i)))
                array.put(object.get(String.valueOf(i)));
        }
        return array;
    }
}
