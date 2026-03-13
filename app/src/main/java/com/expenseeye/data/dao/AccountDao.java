package com.expenseeye.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

import com.expenseeye.data.model.Account;

import java.util.List;

@Dao
public interface AccountDao {

    // Insert single account, ignore if ID exists
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Account account);

    // Insert multiple accounts at once
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Account> accounts);

    @Delete
    void delete(Account account);

    // LiveData version for reactive UI
    @Query("SELECT * FROM accounts ORDER BY id ASC")
    LiveData<List<Account>> getAllAccounts();

    // Check if an ID exists
    @Query("SELECT COUNT(*) FROM accounts WHERE id = :id")
    int getCountById(int id);
}