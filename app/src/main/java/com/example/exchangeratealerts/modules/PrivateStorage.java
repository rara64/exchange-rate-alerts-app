package com.example.exchangeratealerts.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.view.View;

import com.example.exchangeratealerts.models.CurrencyAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Arrays;

public class PrivateStorage {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public PrivateStorage(Context context) {
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

    public void setUsernamePref(String username) {
        setPreferenceString("username",
                new String(
                        Base64.encode(username.getBytes(), Base64.DEFAULT),
                        Charset.defaultCharset()));
    }

    public String getUsernamePref() {
        return new String(
                Base64.decode(
                        getPreferenceString("username"),
                        Base64.DEFAULT), Charset.defaultCharset());
    }

    public void setPasswordPref(String password) {
        setPreferenceString("password",
                new String(
                        Base64.encode(password.getBytes(), Base64.DEFAULT),
                        Charset.defaultCharset()));
    }

    public String getPasswordPref() {
        return new String(
                Base64.decode(
                        getPreferenceString("password"),
                        Base64.DEFAULT), Charset.defaultCharset());
    }

    public void setTokenPref(String token) {
        setPreferenceString("user_token", token);
    }

    public String getTokenPref() {
        return getPreferenceString("user_token");
    }

    public void setAlertsPref(CurrencyAlert[] alerts) {
        try {
            JSONArray array = new JSONArray();

            for (CurrencyAlert alert: alerts) {
                JSONObject object = new JSONObject();

                object.putOpt("baseCurrency", alert.baseCurrency);
                object.putOpt("quoteCurrency", alert.quoteCurrency);
                object.putOpt("targetValue", alert.targetValue);

                array.put(object);
            }
            setPreferenceString("alerts", array.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAlertsPref() {
        return getPreferenceString("alerts");
    }
}
