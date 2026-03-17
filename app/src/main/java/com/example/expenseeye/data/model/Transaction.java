package com.example.expenseeye.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "transactions",
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index("accountId"),
                @Index("categoryId")
        }
)
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double amount;

    private String description;

    private int accountId;

    private int categoryId;

    private long date;

    // Empty constructor required by Room
    public Transaction() {}

    // Constructor for app usage
    @Ignore
    public Transaction(double amount, String description, int accountId, int categoryId, long date) {
        this.amount = amount;
        this.description = description;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.date = date;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }


    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", accountId=" + accountId +
                ", categoryId=" + categoryId +
                ", date=" + date +
                '}';
    }
}