package com.example.expenseeye.ui.transactions;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expenseeye.data.model.Account;
import com.example.expenseeye.data.model.Category;
import com.example.expenseeye.data.model.Transaction;
import com.example.expenseeye.data.model.TransactionWithDetails;
import com.example.expenseeye.data.repository.FinanceRepo;
import com.example.expenseeye.data.repository.FinanceRepoProvider;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for transactions.
 * Handles UI data and runs database writes on a background thread.
 */
public class TransactionViewModel extends AndroidViewModel {

    private final FinanceRepo repository;
    private final int userId;

    // User-specific transactions
    private final LiveData<List<Transaction>> transactions;

    // User-specific transactions with account & category names
    private final LiveData<List<TransactionWithDetails>> transactionsWithDetails;

    // User-specific accounts, shared categories
    private final LiveData<List<Account>> accounts;
    private final LiveData<List<Category>> categories;

    // Background executor for DB writes
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TransactionViewModel(@NonNull Application application) {
        super(application);

        repository = FinanceRepoProvider.get(application);

        SharedPreferences prefs = application.getSharedPreferences("session", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            repository.ensureDefaultAccountsForUser(userId);
            transactions = repository.getTransactionsForUser(userId);
            transactionsWithDetails = repository.getTransactionsWithDetailsForUser(userId);
            accounts = repository.getAccountsForUser(userId);
        } else {
            transactions = repository.getAllTransactions();
            transactionsWithDetails = repository.getTransactionsWithDetails();
            accounts = repository.getAllAccounts();
        }

        categories = repository.getAllCategories();
    }

    // Transactions
    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }

    public LiveData<List<TransactionWithDetails>> getTransactionsWithDetails() {
        return transactionsWithDetails;
    }

    public void insertTransactionAsync(Transaction transaction) {
        executor.execute(() -> {
            try {
                repository.insertTransaction(transaction);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteTransactionAsync(Transaction transaction) {
        executor.execute(() -> {
            try {
                repository.deleteTransaction(transaction);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void updateTransactionAsync(Transaction transaction) {
        executor.execute(() -> {
            try {
                repository.updateTransaction(transaction);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Accounts & Categories
    public LiveData<List<Account>> getAllAccounts() {
        return accounts;
    }

    public LiveData<List<Category>> getAllCategories() {
        return categories;
    }
}