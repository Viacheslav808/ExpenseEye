package com.example.expenseeye.viewmodel.settings;

import com.example.expenseeye.data.FinanceStore;
import com.example.expenseeye.repository.backup.BackupRepository;
import com.example.expenseeye.repository.settings.SettingsRepository;

import android.content.Context;

import org.json.JSONObject;

import java.util.Iterator;

public class SettingsViewModel {

    private final SettingsRepository settingsRepository;
    private final BackupRepository backupRepository;
    private final FinanceStore financeStore;

    public SettingsViewModel(SettingsRepository settingsRepository, BackupRepository backupRepository) {
        this.settingsRepository = settingsRepository;
        this.backupRepository = backupRepository;
        this.financeStore = FinanceStore.getInstance();
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
            JSONObject root = new JSONObject();
            root.put("settings", new JSONObject(settingsRepository.getAllSettings()));
            root.put("finance", financeStore.toJson());
            return backupRepository.exportData(context, root.toString());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean restoreBackup(Context context) {
        try {
            String jsonString = backupRepository.importData(context);
            if (jsonString == null) {
                return false;
            }

            JSONObject root = new JSONObject(jsonString);
            JSONObject settings = root.optJSONObject("settings");
            if (settings == null) {
                settings = root;
            }

            Iterator<String> keys = settings.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                settingsRepository.saveSettings(key, settings.getString(key));
            }

            JSONObject finance = root.optJSONObject("finance");
            if (finance != null) {
                financeStore.applyJson(finance);
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
