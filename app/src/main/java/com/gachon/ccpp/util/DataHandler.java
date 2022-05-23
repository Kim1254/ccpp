package com.gachon.ccpp.util;

import org.json.JSONException;
import org.json.JSONObject;

public class DataHandler {
    public enum MSG_ID {
        INVALID, LOGIN, CHAT, ALARM;
    }

    public static String packData(MSG_ID request, Object... args) {
        JSONObject result = new JSONObject();
        try {
            result.put("request", request);

            if ((args.length & 1) == 1)
                throw new JSONException("Invalid args input");

            for (int i = 0; i < args.length; i += 2)
                result.put((String)args[i], args[i + 1]);
        } catch (JSONException e) {
            return "";
        }
        return result.toString();
    }
}
