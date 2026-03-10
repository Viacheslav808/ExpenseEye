package com.example.expenseeye.repository.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SettingsRepository {

    private final SharedPreferences prefs;

    public SettingsRepository(Context context) {
        this.prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
    }

    public void saveSettings(String key, String value) {
        prefs.edit().putString(key,value).apply();
    }

    public String loadSetting(String key) {

        return prefs.getString(key, null);
    }

    public void resetSettings() {
        prefs.edit().clear().apply();
    }

    public Map<String, ?> getAllSettings() {
        return prefs.getAll();
    }
}
