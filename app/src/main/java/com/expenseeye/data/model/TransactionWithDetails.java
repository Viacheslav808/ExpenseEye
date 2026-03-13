package com.expenseeye.data.model;

import androidx.room.ColumnInfo;

public class TransactionWithDetails {

    public int id;

    public double amount;

    public String description;

    public long date;

    @ColumnInfo(name = "accountName")
    public String accountName;

    @ColumnInfo(name = "categoryName")
    public String categoryName;
}