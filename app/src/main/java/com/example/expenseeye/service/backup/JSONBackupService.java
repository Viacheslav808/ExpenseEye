package com.example.expenseeye.service.backup;

public class JSONBackupService {

    private String filePath;

    public JSONBackupService(String filePath){
        this.filePath = filePath;
    }

    public void setFilePath(String filePath) {
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
}
