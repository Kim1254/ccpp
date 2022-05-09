package com.gachon.ccpp.util;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class DataHandler {
    public enum REQUEST_TYPE {
        INVALID, LOGIN, ACCESS, WRITE, ERROR;
    }

    public enum DATA_TYPE {
        OBJECT, INTEGER, FLOAT, DOUBLE, BOOLEAN;
    }

    public static String packData(REQUEST_TYPE request, DATA_TYPE type, Object value) {
        return packData(request, type, value, null);
    }

    public static String packData(REQUEST_TYPE request, DATA_TYPE type, Object value, @Nullable Object extra) {
        JSONObject result = new JSONObject();
        try {
            result.put("request", request);
            result.put("type", type);
            switch (type) {
                case OBJECT:
                    result.put("value", value);
                    break;
                case INTEGER:
                    result.put("value", ((Integer)value).intValue());
                    break;
                case FLOAT:
                    result.put("value", ((Float)value).floatValue());
                    break;
                case DOUBLE:
                    result.put("value", ((Double)value).doubleValue());
                    break;
                case BOOLEAN:
                    result.put("value", ((Boolean)value).booleanValue());
                    break;
            }

            if (extra != null)
                result.put("extra", extra);

        } catch (JSONException e) {
            return "";
        }
        return result.toString();
    }

    public static JSONObject parse(String jsonString) throws JSONException {
        return new JSONObject(jsonString);
    }
}
