package com.example.expenseeye.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Update;
import androidx.room.Query;

import com.example.expenseeye.data.model.Transaction;
import com.example.expenseeye.data.model.TransactionWithDetails;

import java.util.List;

@Dao
public interface TransactionDao {

    @Query(
            "SELECT t.* " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.accountId = a.id " +
                    "WHERE a.userId = :userId " +
                    "ORDER BY t.date DESC"
    )
    LiveData<List<Transaction>> getTransactionsForUser(int userId);

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query(
            "SELECT t.id, t.amount, t.description, t.date, " +
                    "a.name AS accountName, " +
                    "c.name AS categoryName " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.accountId = a.id " +
                    "JOIN categories c ON t.categoryId = c.id " +
                    "WHERE a.userId = :userId " +
                    "ORDER BY t.date DESC"
    )
    LiveData<List<TransactionWithDetails>> getTransactionsWithDetailsForUser(int userId);
}