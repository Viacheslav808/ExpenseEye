package com.example.expenseeye.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import com.example.expenseeye.data.model.Account;

import java.util.List;

@Dao
public interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Account account);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Account> accounts);

    @Update
    void update(Account account);

    @Delete
    void delete(Account account);

    @Query("SELECT * FROM accounts ORDER BY id ASC")
    LiveData<List<Account>> getAllAccounts();

    @Query("SELECT * FROM accounts WHERE userId = :userId ORDER BY id ASC")
    LiveData<List<Account>> getAccountsForUser(int userId);

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    Account getAccountById(int id);

    @Query("SELECT COUNT(*) FROM accounts WHERE id = :id")
    int getCountById(int id);

    @Query("SELECT COUNT(*) FROM accounts WHERE userId = :userId")
    int getCountForUser(int userId);
}