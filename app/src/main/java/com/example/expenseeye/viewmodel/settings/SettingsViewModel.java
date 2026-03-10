package com.example.expenseeye.viewmodel.settings;

import com.example.expenseeye.repository.backup.BackupRepository;
import com.example.expenseeye.repository.settings.SettingsRepository;

import android.content.Context;

import org.json.JSONObject;

import java.util.Iterator;

public class SettingsViewModel {

    private final SettingsRepository settingsRepository;
    private final BackupRepository backupRepository;

    public SettingsViewModel(SettingsRepository settingsRepository, BackupRepository backupRepository) {
        this.settingsRepository = settingsRepository;
        this.backupRepository = backupRepository;
    }

    public SettingsRepository getSettingsRepository() {
        return settingsRepository;
    }

    public BackupRepository getBackupRepository() {
        return backupRepository;
    }

    public void changeString(String key, String value) {
        settingsRepository.saveSettings(key, value);
    }


    public boolean exportBackup(Context context) {
        try {
            JSONObject json = new JSONObject(settingsRepository.getAllSettings());
            return backupRepository.exportData(context, json.toString());
        } catch (Exception e) {
            return false;
        }
    }

    // IMPORT JSON → SETTINGS
    public boolean restoreBackup(Context context) {
        try {
            String jsonString = backupRepository.importData(context);
            if (jsonString == null) return false;

            JSONObject json = new JSONObject(jsonString);

            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                settingsRepository.saveSettings(key, json.getString(key));
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
