package com.example.expenseeye.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FinanceDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "expense_eye.db";
    private static final int DATABASE_VERSION = 1;

    public FinanceDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE accounts (id INTEGER PRIMARY KEY, user_id INTEGER NOT NULL, name TEXT NOT NULL UNIQUE)");
        db.execSQL("CREATE TABLE categories (id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE)");
        db.execSQL("CREATE TABLE transactions ("
                + "id INTEGER PRIMARY KEY, "
                + "title TEXT NOT NULL, "
                + "user_id INTEGER NOT NULL, "
                + "account_id INTEGER NOT NULL, "
                + "category_id INTEGER NOT NULL, "
                + "type TEXT NOT NULL, "
                + "amount REAL NOT NULL, "
                + "date TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS transactions");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS accounts");
        onCreate(db);
    }
}
