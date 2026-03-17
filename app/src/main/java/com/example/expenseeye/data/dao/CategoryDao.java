package com.example.expenseeye.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

import com.example.expenseeye.data.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    // Insert a single category, ignore if ID exists
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Category category);

    // Insert multiple categories at once
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Category> categories);

    @Delete
    void delete(Category category);

    // Return LiveData for reactive UI
    @Query("SELECT * FROM categories ORDER BY id ASC")
    LiveData<List<Category>> getAllCategories();

    // Check if an ID exists
    @Query("SELECT COUNT(*) FROM categories WHERE id = :id")
    int getCountById(int id);
}