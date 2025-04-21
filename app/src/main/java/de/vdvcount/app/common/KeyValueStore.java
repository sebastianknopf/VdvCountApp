package de.vdvcount.app.common;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import de.vdvcount.app.App;

abstract class KeyValueStore {

    protected static SharedPreferences sharedPreferences;

    static {
        KeyValueStore.sharedPreferences = App.getStaticContext().getSharedPreferences("de.vdvcount.app.kvs", Context.MODE_PRIVATE);
    }

    public static String getString(String variableName, String defaultValue) {
        return KeyValueStore.sharedPreferences.getString(variableName, defaultValue);
    }

    public static Boolean getBoolean(String variableName, Boolean defaultValue) {
        return KeyValueStore.sharedPreferences.getBoolean(variableName, defaultValue);
    }

    public static Integer getInt(String variableName, Integer defaultValue) {
        return KeyValueStore.sharedPreferences.getInt(variableName, defaultValue);
    }

    public static String[] getStringArray(String variableName, String[] defaultValue) {
        String jsonString = KeyValueStore.sharedPreferences.getString(variableName, null);

        if (jsonString != null) {
            List<String> list = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                return defaultValue;
            }

            return list.toArray(new String[0]);
        } else {
            return defaultValue;
        }
    }

    public static void setString(String variableName, String value) {
        SharedPreferences.Editor editor = KeyValueStore.sharedPreferences.edit();
        editor.putString(variableName, value);
        editor.commit();
    }

    public static void setBoolean(String variableName, Boolean value) {
        SharedPreferences.Editor editor = KeyValueStore.sharedPreferences.edit();
        editor.putBoolean(variableName, value);
        editor.commit();
    }

    public static void setInt(String variableName, Integer value) {
        SharedPreferences.Editor editor = KeyValueStore.sharedPreferences.edit();
        editor.putInt(variableName, value);
        editor.commit();
    }

    public static void setStringArray(String variableName, String[] value) {
        JSONArray jsonArray = new JSONArray();
        for (String item : value) {
            jsonArray.put(item);
        }

        SharedPreferences.Editor editor = KeyValueStore.sharedPreferences.edit();
        editor.putString(variableName, jsonArray.toString());
        editor.commit();
    }

}
