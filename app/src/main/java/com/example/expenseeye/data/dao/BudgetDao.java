package com.example.expenseeye.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expenseeye.data.model.Budget;

import java.util.List;

@Dao
public interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Budget budget);

    @Update
    void update(Budget budget);

    @Delete
    void delete(Budget budget);

    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY id ASC")
    LiveData<List<Budget>> getBudgetsForUser(int userId);

    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY id ASC")
    List<Budget> getBudgetsForUserSync(int userId);

    @Query("SELECT * FROM budgets WHERE userId = :userId AND categoryId = :categoryId LIMIT 1")
    Budget getBudgetForCategory(int userId, int categoryId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
            "FROM transactions t " +
            "JOIN accounts a ON t.accountId = a.id " +
            "WHERE a.userId = :userId " +
            "AND t.categoryId = :categoryId " +
            "AND t.date >= :from AND t.date <= :to")
    double getSpentForCategory(int userId, int categoryId, long from, long to);
}