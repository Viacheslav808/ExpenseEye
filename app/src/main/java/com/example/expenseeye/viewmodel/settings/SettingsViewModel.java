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

    public void changeSting(String key, String value) {

    }

    public Boolean exportBackup() {
        return true;
    }

    public Boolean restoreBackup() {

        return true;
    }
}
