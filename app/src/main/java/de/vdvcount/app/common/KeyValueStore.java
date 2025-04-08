package de.vdvcount.app.common;

import android.content.Context;
import android.content.SharedPreferences;

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

}
