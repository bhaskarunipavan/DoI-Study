package com.dic.survey.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String PREF_NAME = "DicSurveyPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_OFFICER_ID = "officer_id";
    private static final String KEY_OFFICER_NAME = "officer_name";
    private static final String KEY_DISTRICT = "district";
    private static final String KEY_ROLE = "role";
    private static final String KEY_SUPABASE_URL = "supabase_url";
    private static final String KEY_SUPABASE_KEY = "supabase_key";
    private static final String KEY_LAST_SYNC = "last_sync";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public PrefManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedIn(boolean loggedIn) { editor.putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply(); }
    public boolean isLoggedIn() { return prefs.getBoolean(KEY_IS_LOGGED_IN, false); }

    public void setOfficerId(String id) { editor.putString(KEY_OFFICER_ID, id).apply(); }
    public String getOfficerId() { return prefs.getString(KEY_OFFICER_ID, ""); }

    public void setOfficerName(String name) { editor.putString(KEY_OFFICER_NAME, name).apply(); }
    public String getOfficerName() { return prefs.getString(KEY_OFFICER_NAME, ""); }

    public void setDistrict(String district) { editor.putString(KEY_DISTRICT, district).apply(); }
    public String getDistrict() { return prefs.getString(KEY_DISTRICT, ""); }

    public void setRole(String role) { editor.putString(KEY_ROLE, role).apply(); }
    public String getRole() { return prefs.getString(KEY_ROLE, "officer"); }

    public void setSupabaseUrl(String url) { editor.putString(KEY_SUPABASE_URL, url).apply(); }
    public String getSupabaseUrl() { return prefs.getString(KEY_SUPABASE_URL, ""); }

    public void setSupabaseKey(String key) { editor.putString(KEY_SUPABASE_KEY, key).apply(); }
    public String getSupabaseKey() { return prefs.getString(KEY_SUPABASE_KEY, ""); }

    public void setLastSync(long ts) { editor.putLong(KEY_LAST_SYNC, ts).apply(); }
    public long getLastSync() { return prefs.getLong(KEY_LAST_SYNC, 0); }

    public void clearSession() {
        editor.remove(KEY_IS_LOGGED_IN).remove(KEY_OFFICER_ID)
              .remove(KEY_OFFICER_NAME).remove(KEY_DISTRICT)
              .remove(KEY_ROLE).apply();
    }
}
