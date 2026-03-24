package com.example.expenseeye.ui.settings;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.expenseeye.R;
import com.example.expenseeye.repository.settings.SettingsRepository;
import com.example.expenseeye.repository.backup.BackupRepository;
import com.example.expenseeye.service.backup.JSONBackupService;
import com.example.expenseeye.ui.LoginActivity;
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
                new BackupRepository(new JSONBackupService("backup.json"))
        );


        Button exportButton = view.findViewById(R.id.button_export);
        Button importButton = view.findViewById(R.id.button_import);
        Button logoutButton = view.findViewById(R.id.button_logout);


        exportButton.setOnClickListener(v -> exportData());
        importButton.setOnClickListener(v -> importData());
        logoutButton.setOnClickListener(v -> handleLogout());
    }
    private void handleLogout() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        requireActivity().finish();

        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show();
    }

    public void saveSettings(String key, String value) {
        settingsViewModel.changeString(key, value);
        System.out.println("Saved setting: " + key + " = " + value);
    }

    public void exportData() {
        boolean success = settingsViewModel.exportBackup(requireContext());

        if (success) {
            Toast.makeText(requireContext(), "Exported JSON file", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Export failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void importData() {
        boolean success = settingsViewModel.restoreBackup(requireContext());

        if (success) {
            Toast.makeText(requireContext(), "Imported JSON file", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Import failed", Toast.LENGTH_SHORT).show();
        }
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