package com.example.expenseeye.ui.transactions;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expenseeye.data.model.Account;
import com.example.expenseeye.data.model.Category;
import com.example.expenseeye.data.model.Transaction;
import com.example.expenseeye.data.model.TransactionWithDetails;
import com.example.expenseeye.data.repository.FinanceRepo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for transactions.
 * Handles UI data and runs database writes on a background thread.
 */
public class TransactionViewModel extends AndroidViewModel {

    private final FinanceRepo repository;

    // Basic transactions
    private final LiveData<List<Transaction>> transactions;

    // Transactions with account & category names (JOIN query)
    private final LiveData<List<TransactionWithDetails>> transactionsWithDetails;

    // LiveData for Accounts & Categories
    private final LiveData<List<Account>> accounts;
    private final LiveData<List<Category>> categories;

    // Background executor for DB writes
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TransactionViewModel(@NonNull Application application) {
        super(application);

        repository = new FinanceRepo(application);

        transactions = repository.getAllTransactions();
        transactionsWithDetails = repository.getTransactionsWithDetails();

        // Fetch accounts and categories from DB
        accounts = repository.getAllAccounts();
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