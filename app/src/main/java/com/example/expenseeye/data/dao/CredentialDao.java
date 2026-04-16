package com.example.expenseeye.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.expenseeye.data.entities.Credential;

@Dao
public interface CredentialDao {

    @Insert
    long insert(Credential credential);

    @Query("SELECT * FROM credentials WHERE email = :email")
    Credential getCredentialByEmail(String email);

    @Query("SELECT * FROM credentials WHERE user_id = :userId LIMIT 1")
    Credential getCredentialByUserId(int userId);
}