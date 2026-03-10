package com.example.expenseeye.repository.backup;

import android.content.Context;

import com.example.expenseeye.service.backup.JSONBackupService;

public class BackupRepository {

    private final JSONBackupService service;

    public BackupRepository(JSONBackupService service) {
        this.service = service;
    }

    public boolean exportData(Context context, String json) {
        return service.exportData(context, json);
    }

    public String importData(Context context) {
        return service.importData(context);
    }
}
