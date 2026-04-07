package com.example.expenseeye.data.repository;

import android.content.Context;

public class FinanceRepoProvider {
    private static FinanceRepo instance;

    public static FinanceRepo get(Context context) {
        if (instance == null) {
            instance = new FinanceRepo(context.getApplicationContext());
        }
        return instance;
    }
}
