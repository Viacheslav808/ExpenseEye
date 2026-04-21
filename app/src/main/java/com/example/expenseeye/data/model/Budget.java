package com.example.expenseeye.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.expenseeye.data.entities.User;

@Entity(
        tableName = "budgets",
        foreignKeys = {
                @ForeignKey(
                        entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"userId", "categoryId"}, unique = true),
                @Index(value = "categoryId"),
                @Index(value = "userId")
        }
)
public class Budget {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;

    @NonNull
    @ColumnInfo(defaultValue = "")
    private String name = "";

    private int categoryId;
    private double limitAmount;
    private long periodStart;
    private long periodEnd;

    public Budget() {}

    public Budget(int userId, @NonNull String name, int categoryId, double limitAmount,
                  long periodStart, long periodEnd) {
        this.userId = userId;
        this.name = name;
        this.categoryId = categoryId;
        this.limitAmount = limitAmount;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }

    public long getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(long periodStart) {
        this.periodStart = periodStart;
    }

    public long getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(long periodEnd) {
        this.periodEnd = periodEnd;
    }
}
