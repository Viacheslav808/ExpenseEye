package com.example.expenseeye.repository.backup;

public class BackupRepository {
    private String filePath;

    public BackupRepository(String filepath) {
        this.filePath = filePath;
    }

    public void setFilePath(String filePatah) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
    public Boolean exportData() {
        return true;
    }

    public Boolean importData() {
        return true;
    }

    public void clearBackup() {

    }
}
