package com.example.expenseeye.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.expenseeye.data.dao.AccountDao;
import com.example.expenseeye.data.dao.CategoryDao;
import com.example.expenseeye.data.dao.TransactionDao;
import com.example.expenseeye.data.database.ExpenseEyeDatabase;
import com.example.expenseeye.data.model.Account;
import com.example.expenseeye.data.model.Category;
import com.example.expenseeye.data.model.Transaction;
import com.example.expenseeye.data.model.TransactionWithDetails;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for finance data.
 * Handles accounts, categories, transactions, and background DB writes.
 */
public class FinanceRepo {

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final CategoryDao categoryDao;

    private final ExecutorService executorService;

    public FinanceRepo(Context context) {

        ExpenseEyeDatabase db = ExpenseEyeDatabase.getInstance(context);

        accountDao = db.accountDao();
        transactionDao = db.transactionDao();
        categoryDao = db.categoryDao();

        executorService = Executors.newSingleThreadExecutor();

        // Ensure default data exists so FK constraints never fail
        createDefaultAccountAndCategory();
    }

    /**
     * Creates a default account and category to prevent foreign key issues.
     */
    private void createDefaultAccountAndCategory() {

        executorService.execute(() -> {

            try {
                if (accountDao.getAllAccounts().getValue() == null || accountDao.getAllAccounts().getValue().isEmpty()) {
                    Account defaultAccount = new Account();
                    defaultAccount.setId(1);
                    defaultAccount.setName("Cash"); // default Cash account
                    accountDao.insert(defaultAccount);

                    Account bankAccount = new Account();
                    bankAccount.setId(2);
                    bankAccount.setName("Bank Account");
                    accountDao.insert(bankAccount);

                    Account creditCard = new Account();
                    creditCard.setId(3);
                    creditCard.setName("Credit Card");
                    accountDao.insert(creditCard);
                }

                if (categoryDao.getAllCategories().getValue() == null || categoryDao.getAllCategories().getValue().isEmpty()) {
                    String[] defaultCategories = {"Food", "Transport", "Shopping", "Bills", "Entertainment"};
                    int idCounter = 1;
                    for (String catName : defaultCategories) {
                        Category category = new Category();
                        category.setId(idCounter++);
                        category.setName(catName);
                        categoryDao.insert(category);
                    }
                }

            } catch (Exception ignored) {
                // Already exists, ignore
            }
        });
    }

    // Account operations
    public void insertAccount(Account account) {
        executorService.execute(() -> accountDao.insert(account));
    }

    public void deleteAccount(Account account) {
        executorService.execute(() -> accountDao.delete(account));
    }

    public LiveData<List<Account>> getAllAccounts() {
        return accountDao.getAllAccounts();
    }

    // Category operations
    public void insertCategory(Category category) {
        executorService.execute(() -> categoryDao.insert(category));
    }

    public void deleteCategory(Category category) {
        executorService.execute(() -> categoryDao.delete(category));
    }

    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    // Transaction operations
    public void insertTransaction(Transaction transaction) {
        executorService.execute(() -> transactionDao.insert(transaction));
    }

    public void deleteTransaction(Transaction transaction) {
        executorService.execute(() -> transactionDao.delete(transaction));
    }

    public void updateTransaction(Transaction transaction) {
        executorService.execute(() -> transactionDao.update(transaction));
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return transactionDao.getAllTransactions();
    }

    public LiveData<List<TransactionWithDetails>> getTransactionsWithDetails() {
        return transactionDao.getTransactionsWithDetails();
    }
}