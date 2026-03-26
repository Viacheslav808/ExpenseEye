package com.example.expenseeye.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

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

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final CategoryDao categoryDao;

    private final ExecutorService executorService;

    private final NotificationService notificationService;

    private final BudgetDao budgetDao;


    public FinanceRepo(Context context) {

        ExpenseEyeDatabase db = ExpenseEyeDatabase.getInstance(context);

        accountDao = db.accountDao();
        transactionDao = db.transactionDao();
        budgetDao = db.budgetDao();
        categoryDao = db.categoryDao();

        executorService = Executors.newSingleThreadExecutor();

        notificationService = new NotificationService(context);

        // Ensure default data exists so FK constraints never fail
        createDefaultAccountAndCategory();
    }

    private void runChecks(Transaction t) {

        Budget budget = budgetDao.getBudgetForCategory(t.getCategoryId());
        if (budget == null) return; // no budget set

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
                t.getCategoryId(),
                startOfMonth,
                endOfMonth
        );
        Log.d("BudgetCheck", "runChecks() called for categoryId=" + t.getCategoryId());

        Log.d("BudgetCheck", "Budget limit = " + budget.getLimitAmount());

        Log.d("BudgetCheck", "Spent this month = " + spent);

        if (spent > budget.getLimitAmount()) {
            notificationService.sendBudgetAlert(
                    "You exceeded the budget for category " + t.getCategoryId()
            );
        }
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
        executorService.execute(() -> {
            transactionDao.insert(transaction);
            runChecks(transaction);   // now runs AFTER insert completes
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

    public LiveData<List<Transaction>> getAllTransactions() {
        return transactionDao.getAllTransactions();
    }

    public LiveData<List<TransactionWithDetails>> getTransactionsWithDetails() {
        return transactionDao.getTransactionsWithDetails();
    }
}