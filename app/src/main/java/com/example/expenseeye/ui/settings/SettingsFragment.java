package com.example.expenseeye.ui.settings;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.expenseeye.R;
import com.example.expenseeye.repository.settings.SettingsRepository;
import com.example.expenseeye.repository.backup.BackupRepository;
import com.example.expenseeye.service.backup.JSONBackupService;
import com.example.expenseeye.viewmodel.settings.SettingsViewModel;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;

    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingsViewModel = new SettingsViewModel(
                new SettingsRepository(requireContext()),
                new BackupRepository("backup.json")
        );
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
            System.out.println("Backup operation successful");
        } else {
            System.out.println("Backup operation failed");
        }
    }
}