package com.example.expenseeye.ui.settings;

import com.example.expenseeye.viewmodel.settings.SettingsViewModel;

public class SettingsFragment {

    private final SettingsViewModel settingsViewModel;
    public SettingsFragment(SettingsViewModel settingsViewModel){
        this.settingsViewModel = settingsViewModel;
    }


    public void saveSettings(String key, String value) {
        settingsViewModel.changeString(key, value);
        System.out.println("Saved setting: " + key + " = " + value);
    }

    public void exportData() {
        boolean success = settingsViewModel.exportBackup();
        showBackupResult(success);
    }

    public void importData() {
        boolean success = settingsViewModel.restoreBackup();
        showBackupResult(success);
    }

    public void observeSettings() {
        System.out.println("Current settings: " + settingsViewModel.getSettingsRepository().getAllSettings());
    }

    public void showBackupResult(Boolean success) {
        if (success) {
            System.out.println("Backup operation successsful");
        } else {
            System.out.println("Backup operation failed");
        }
    }

}
