package com.example.expenseeye.data.model;

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
        indices = { @Index(value = "categoryId", unique = true) }
)
public class Budget {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int categoryId;      // FK → Category (1 Category → 0..1 Budget)
    private double limitAmount;  // spending limit
    private long periodStart;    // start of budget period
    private long periodEnd;      // end of budget period

    public Budget() {}

    public Budget(int categoryId, double limitAmount, long periodStart, long periodEnd) {
        this.categoryId   = categoryId;
        this.limitAmount  = limitAmount;
        this.periodStart  = periodStart;
        this.periodEnd    = periodEnd;
    }

    // Getters & Setters
    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }

    public int getCategoryId()            { return categoryId; }
    public void setCategoryId(int v)      { this.categoryId = v; }

    public double getLimitAmount()        { return limitAmount; }
    public void setLimitAmount(double v)  { this.limitAmount = v; }

    public long getPeriodStart()          { return periodStart; }
    public void setPeriodStart(long v)    { this.periodStart = v; }

    public long getPeriodEnd()            { return periodEnd; }
    public void setPeriodEnd(long v)      { this.periodEnd = v; }
}
