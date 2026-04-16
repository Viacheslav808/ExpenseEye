package com.example.expenseeye.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.expenseeye.data.entities.User;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "accounts",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "user_id",
                childColumns = "userId",
                onDelete = CASCADE
        ),
        indices = {@Index("userId")}
)
public class Account {

    @PrimaryKey(autoGenerate = false)
    private int id;

    private int userId;
    private String name;
    private double balance;

    // Required empty constructor for Room
    public Account() {}

    // Constructor for app usage
    @Ignore
    public Account(int id, int userId, String name, double balance) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.balance = balance;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return name != null ? name : "";
    }
}