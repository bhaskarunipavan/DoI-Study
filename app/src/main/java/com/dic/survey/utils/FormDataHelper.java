package com.dic.survey.utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class FormDataHelper {
    private static final String TAG = "FormDataHelper";

    private JSONObject data;

    public FormDataHelper() { data = new JSONObject(); }

    public FormDataHelper(String json) {
        try { data = new JSONObject(json != null ? json : "{}"); }
        catch (Exception e) { data = new JSONObject(); }
    }

    public void put(String key, String value) {
        try { data.put(key, value != null ? value : ""); } catch (Exception e) { Log.e(TAG, "put error", e); }
    }

    public void put(String key, boolean value) {
        try { data.put(key, value); } catch (Exception e) { Log.e(TAG, "put error", e); }
    }

    public void put(String key, int value) {
        try { data.put(key, value); } catch (Exception e) { Log.e(TAG, "put error", e); }
    }

    public void putList(String key, List<String> values) {
        try {
            JSONArray arr = new JSONArray();
            for (String v : values) arr.put(v);
            data.put(key, arr);
        } catch (Exception e) { Log.e(TAG, "putList error", e); }
    }

    public String getString(String key) { return data.optString(key, ""); }
    public boolean getBoolean(String key) { return data.optBoolean(key, false); }
    public int getInt(String key) { return data.optInt(key, 0); }

    public List<String> getList(String key) {
        List<String> result = new ArrayList<>();
        try {
            JSONArray arr = data.optJSONArray(key);
            if (arr != null) for (int i = 0; i < arr.length(); i++) result.add(arr.getString(i));
        } catch (Exception e) { Log.e(TAG, "getList error", e); }
        return result;
    }

    public boolean contains(String key) { return data.has(key); }

    public String toJson() { return data.toString(); }

    public JSONObject getSection(String section) {
        try {
            JSONObject sec = data.optJSONObject(section);
            return sec != null ? sec : new JSONObject();
        } catch (Exception e) { return new JSONObject(); }
    }

    public void putSection(String section, JSONObject obj) {
        try { data.put(section, obj); } catch (Exception e) { Log.e(TAG, "putSection error", e); }
    }
}
