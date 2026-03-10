package com.example.expenseeye.repository.settings;

public class SettingsRepository {

    public SettingsRepository(String preferencesKey) {
        this.preferencesKey = preferencesKey;
    }
    private String preferencesKey;

    public void saveSettings(String key, String value) {

    }

    public String loadSetting(String key) {
        return key;
    }

    public void resetSettings() {

    }
}
