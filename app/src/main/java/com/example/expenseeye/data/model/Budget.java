package com.example.expenseeye.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "budgets",
        foreignKeys = {
                @ForeignKey(
                        entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        // Non-unique index now — multiple budgets per category are allowed
        indices = { @Index(value = "categoryId") }
)
public class Budget {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(defaultValue = "")
    private String name = ""; // user-defined label, e.g. "Weekly Groceries"

    private int categoryId;       // FK → Category
    private double limitAmount;   // spending limit
    private long periodStart;     // start of budget period
    private long periodEnd;       // end of budget period

    public Budget() {}

    public Budget(@NonNull String name, int categoryId, double limitAmount,
                  long periodStart, long periodEnd) {
        this.name = name;
        this.categoryId = categoryId;
        this.limitAmount = limitAmount;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int v) { this.categoryId = v; }

    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double v) { this.limitAmount = v; }

    public long getPeriodStart() { return periodStart; }
    public void setPeriodStart(long v) { this.periodStart = v; }

    public long getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(long v) { this.periodEnd = v; }
}