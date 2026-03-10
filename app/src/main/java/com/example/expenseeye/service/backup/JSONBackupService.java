package com.example.expenseeye.service.backup;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class JSONBackupService {

    private final String fileName;

    public JSONBackupService(String fileName){
        this.fileName = fileName;
    }

    // MUST match BackupRepository.exportData(Context, String)
    public boolean exportData(Context context, String json) {
        try {
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloads, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json.getBytes());
            fos.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public String importData(Context context) {
        try {
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloads, fileName);

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();
            fis.close();

            return builder.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}