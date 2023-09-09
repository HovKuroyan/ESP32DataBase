package com.example.esp32database.ChoosingArea;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String STRING_LIST_KEY = "stringList";

    public static void saveStringList(Context context, List<String> stringList) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(stringList);
        editor.putString(STRING_LIST_KEY, json);
        editor.apply();
    }

    public static List<String> getStringList(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = preferences.getString(STRING_LIST_KEY, null);

        if (json != null) {
            Gson gson = new Gson();
            return new ArrayList<>(Arrays.asList(gson.fromJson(json, String[].class)));
        } else {
            return new ArrayList<>();
        }
    }
    public static boolean doesListExist(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.contains(STRING_LIST_KEY);
    }
}
