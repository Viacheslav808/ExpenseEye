package com.example.expenseeye.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.expenseeye.data.dao.AccountDao;
import com.example.expenseeye.data.dao.BudgetDao;
import com.example.expenseeye.data.dao.CategoryDao;
import com.example.expenseeye.data.dao.TransactionDao;
import com.example.expenseeye.data.database.ExpenseEyeDatabase;
import com.example.expenseeye.data.model.Account;
import com.example.expenseeye.data.model.Budget;
import com.example.expenseeye.data.model.Category;
import com.example.expenseeye.data.model.Transaction;
import com.example.expenseeye.data.model.TransactionWithDetails;
import com.example.expenseeye.service.NotificationService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for finance data.
 * Handles accounts, categories, transactions, and background DB writes.
 */
public class FinanceRepo {

    private final Context appContext;
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final CategoryDao categoryDao;
    private final BudgetDao budgetDao;

    private final ExecutorService executorService;
    private final NotificationService notificationService;

    public FinanceRepo(Context context) {
        appContext = context.getApplicationContext();

        ExpenseEyeDatabase db = ExpenseEyeDatabase.getInstance(appContext);

        accountDao = db.accountDao();
        transactionDao = db.transactionDao();
        budgetDao = db.budgetDao();
        categoryDao = db.categoryDao();

        executorService = Executors.newSingleThreadExecutor();
        notificationService = new NotificationService(appContext);

        // Seed only shared categories here
        createDefaultCategories();
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    private int getCurrentUserId() {
        SharedPreferences prefs = appContext.getSharedPreferences("session", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    /**
     * Creates default categories only.
     * Accounts are now created per-user, not globally.
     */
    private void createDefaultCategories() {
        executorService.execute(() -> {
            try {
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

    /**
     * Ensure that a newly logged-in / newly registered user has their own default accounts.
     */
    public void ensureDefaultAccountsForUser(int userId) {
        executorService.execute(() -> {
            try {
                if (accountDao.getCountForUser(userId) == 0) {
                    accountDao.insert(new Account(userId * 100 + 1, userId, "Cash", 0.0));
                    accountDao.insert(new Account(userId * 100 + 2, userId, "Bank Account", 0.0));
                    accountDao.insert(new Account(userId * 100 + 3, userId, "Credit Card", 0.0));
                }
            } catch (Exception ignored) {
                // Ignore duplicate insert attempts
            }
        });
    }

    /**
     * Get only the accounts that belong to the logged-in user.
     */
    public LiveData<List<Account>> getAccountsForUser(int userId) {
        return accountDao.getAccountsForUser(userId);
    }

    /**
     * Total balance only for the logged-in user.
     */
    public LiveData<Double> getTotalBalanceForUser(int userId) {
        return Transformations.map(accountDao.getAccountsForUser(userId), accounts -> {
            double total = 0;
            if (accounts != null) {
                for (Account account : accounts) {
                    total += account.getBalance();
                }
            }
            return total;
        });
    }

    private void runChecks(Transaction t) {
        runChecksForCategory(t.getCategoryId());
    }

    private void runChecksForCategory(int categoryId) {
        int userId = getCurrentUserId();
        if (userId == -1) return;

        Log.d("BudgetCheck", "runChecksForCategory() for category " + categoryId);

        Budget budget = budgetDao.getBudgetForCategory(userId, categoryId);
        Log.d("BudgetCheck", "Budget exists? " + (budget != null));
        if (budget == null) return;

        long now = System.currentTimeMillis();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(now);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);

        long startOfMonth = cal.getTimeInMillis();
        long endOfMonth = now;

        double spent = budgetDao.getSpentForCategory(
                userId,
                categoryId,
                startOfMonth,
                endOfMonth
        );
        Log.d("BudgetCheck", "Spent = " + spent + " / Limit = " + budget.getLimitAmount());

        if (spent > budget.getLimitAmount()) {
            Log.d("BudgetCheck", "OVER BUDGET — sending notification");
            notificationService.sendBudgetAlert(
                    NotificationService.Level.EXCEEDED,
                    "You exceeded the budget for category " + categoryId
            );
        }
    }

    public void runChecksOnLogin() {
        Log.d("BudgetCheck", "runChecksOnLogin() STARTED");
        executorService.execute(() -> {
            int userId = getCurrentUserId();
            if (userId == -1) return;

            Log.d("BudgetCheck", "runChecksOnLogin() INSIDE EXECUTOR");
            List<Budget> budgets = budgetDao.getBudgetsForUserSync(userId);
            Log.d("BudgetCheck", "Budgets found: " + budgets.size());

            for (Budget b : budgets) {
                Log.d("BudgetCheck", "Checking category from login: " + b.getCategoryId());
                runChecksForCategory(b.getCategoryId());
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
        Log.d("BudgetCheck", "insertTransaction() CALLED");
        executorService.execute(() -> {
            Log.d("BudgetCheck", "insertTransaction() INSIDE EXECUTOR");
            transactionDao.insert(transaction);
            Log.d("BudgetCheck", "Transaction inserted, running checks");
            runChecks(transaction);
        });
    }

    public void deleteTransaction(Transaction transaction) {
        executorService.execute(() -> transactionDao.delete(transaction));
    }

    public void updateTransaction(Transaction transaction) {
        executorService.execute(() -> {
            transactionDao.update(transaction);
            runChecks(transaction);
        });
    }

    // Legacy/global methods kept only so older code compiles.
    // New code should use the user-specific methods below.
    public LiveData<List<Transaction>> getAllTransactions() {
        throw new UnsupportedOperationException("Use getTransactionsForUser(userId) instead");
    }

    public LiveData<List<TransactionWithDetails>> getTransactionsWithDetails() {
        throw new UnsupportedOperationException("Use getTransactionsWithDetailsForUser(userId) instead");
    }

    public LiveData<List<Transaction>> getTransactionsForUser(int userId) {
        return transactionDao.getTransactionsForUser(userId);
    }

    public LiveData<List<TransactionWithDetails>> getTransactionsWithDetailsForUser(int userId) {
        return transactionDao.getTransactionsWithDetailsForUser(userId);
    }
}