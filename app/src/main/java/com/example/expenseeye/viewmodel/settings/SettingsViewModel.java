package com.example.expenseeye.viewmodel.settings;

import com.example.expenseeye.repository.backup.BackupRepository;
import com.example.expenseeye.repository.settings.SettingsRepository;

public class SettingsViewModel {

    public SettingsViewModel(SettingsRepository settingsRepository, BackupRepository backupRepository) {
        this.settingsRepository = settingsRepository;
        this.backupRepository = backupRepository;
    }
    private SettingsRepository settingsRepository;
    private BackupRepository backupRepository;

    public SettingsRepository getSettingsRepository () {
        return settingsRepository;
    }

    public BackupRepository getBackupRepository() {
        return backupRepository;
    }

    public void changeString(String key, String value) {
        settingsRepository.saveSettings(key,value);
    }

    public Boolean exportBackup() {

        return backupRepository.exportData();
    }

    public Boolean restoreBackup() {

        return backupRepository.importData();
    }
}
