package com.example.expenseeye.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.expenseeye.data.dao.AccountDao;
import com.example.expenseeye.data.dao.CategoryDao;
import com.example.expenseeye.data.dao.TransactionDao;
import com.example.expenseeye.data.model.Account;
import com.example.expenseeye.data.model.Category;
import com.example.expenseeye.data.model.Transaction;

import java.util.concurrent.Executors;

@Database(
        entities = {Account.class, Transaction.class, Category.class},
        version = 1,
        exportSchema = false
)
public abstract class ExpenseEyeDatabase extends RoomDatabase {

    private static ExpenseEyeDatabase instance;

    public abstract TransactionDao transactionDao();
    public abstract AccountDao accountDao();
    public abstract CategoryDao categoryDao();

    public static synchronized ExpenseEyeDatabase getInstance(Context context) {

        if (instance == null) {

            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ExpenseEyeDatabase.class,
                            "expenseeye_db"
                    )

                    .fallbackToDestructiveMigration()
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);

                            Executors.newSingleThreadExecutor().execute(() -> {
                                AccountDao accountDao = instance.accountDao();
                                CategoryDao categoryDao = instance.categoryDao();

                                // Accounts
                                if (accountDao.getCountById(1) == 0) accountDao.insert(new Account(1, "Cash", 0.0));
                                if (accountDao.getCountById(2) == 0) accountDao.insert(new Account(2, "Bank Account", 0.0));
                                if (accountDao.getCountById(3) == 0) accountDao.insert(new Account(3, "Credit Card", 0.0));

                                // Categories
                                if (categoryDao.getCountById(1) == 0) categoryDao.insert(new Category(1, "General"));
                                if (categoryDao.getCountById(2) == 0) categoryDao.insert(new Category(2, "Food"));
                                if (categoryDao.getCountById(3) == 0) categoryDao.insert(new Category(3, "Transport"));
                                if (categoryDao.getCountById(4) == 0) categoryDao.insert(new Category(4, "Shopping"));
                                if (categoryDao.getCountById(5) == 0) categoryDao.insert(new Category(5, "Entertainment"));
                            });
                        }
                    })
                    .build();
        }

        return instance;
    }
}