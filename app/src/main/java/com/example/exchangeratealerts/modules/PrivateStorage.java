package com.example.exchangeratealerts.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

public class PrivateStorage {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public PrivateStorage(View view) {
        Context context = view.getContext();
        preferences = context.getSharedPreferences(
                context.getPackageName(),
                Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public String getPreferenceString(String preferenceKey) {
        return preferences.getString(preferenceKey, "");
    }

    public void setPreferenceString(String preferenceKey, String value) {
        editor.putString(preferenceKey, value);
        editor.apply();
    }
}
