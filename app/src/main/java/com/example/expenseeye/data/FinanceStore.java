package com.example.expenseeye.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expenseeye.model.finance.AccountRecord;
import com.example.expenseeye.model.finance.CategoryRecord;
import com.example.expenseeye.model.finance.TransactionRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinanceStore {
    private static final FinanceStore INSTANCE = new FinanceStore();
    private static final int DEFAULT_USER_ID = 1001;

    private final List<AccountRecord> accounts = new ArrayList<>();
    private final List<CategoryRecord> categories = new ArrayList<>();
    private final List<TransactionRecord> transactions = new ArrayList<>();

    private FinanceDatabaseHelper dbHelper;

    private FinanceStore() {
        seedInMemory();
    }

    public static FinanceStore getInstance() {
        return INSTANCE;
    }

    public synchronized void initialize(Context context) {
        if (dbHelper != null || context == null) {
            return;
        }
        dbHelper = new FinanceDatabaseHelper(context.getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (isTableEmpty(db, "accounts")) {
            seedDatabase(db);
        }
        loadFromDatabase(db);
    }

    public synchronized List<AccountRecord> getAccounts() {
        refreshFromDatabaseIfAvailable();
        return new ArrayList<>(accounts);
    }

    public synchronized List<CategoryRecord> getCategories() {
        refreshFromDatabaseIfAvailable();
        return new ArrayList<>(categories);
    }

    public synchronized List<TransactionRecord> getTransactions() {
        refreshFromDatabaseIfAvailable();
        return new ArrayList<>(transactions);
    }

    public synchronized void addExpenseTransaction(String title, double amount, String accountName, String categoryName) {
        if (dbHelper == null) {
            int accountId = findOrAddAccountInMemory(accountName);
            int categoryId = findOrAddCategoryInMemory(categoryName);
            int nextId = transactions.size() + 1;
            transactions.add(0, new TransactionRecord(nextId, title, DEFAULT_USER_ID, accountId, categoryId, "expense", amount, LocalDate.now()));
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int accountId = findOrInsertAccount(db, accountName);
        int categoryId = findOrInsertCategory(db, categoryName);

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("user_id", DEFAULT_USER_ID);
        values.put("account_id", accountId);
        values.put("category_id", categoryId);
        values.put("type", "expense");
        values.put("amount", amount);
        values.put("date", LocalDate.now().toString());
        db.insert("transactions", null, values);

        loadFromDatabase(db);
    }

    public synchronized JSONObject toJson() {
        refreshFromDatabaseIfAvailable();
        JSONObject root = new JSONObject();
        putSafely(root, "accounts", accountsToJson());
        putSafely(root, "categories", categoriesToJson());
        putSafely(root, "transactions", transactionsToJson());
        return root;
    }

    public synchronized void applyJson(JSONObject root) {
        if (root == null) {
            return;
        }
        JSONArray accountArray = root.optJSONArray("accounts");
        JSONArray categoryArray = root.optJSONArray("categories");
        JSONArray transactionArray = root.optJSONArray("transactions");
        if (accountArray == null || categoryArray == null || transactionArray == null) {
            return;
        }

        List<AccountRecord> newAccounts = new ArrayList<>();
        List<CategoryRecord> newCategories = new ArrayList<>();
        List<TransactionRecord> newTransactions = new ArrayList<>();

        for (int i = 0; i < accountArray.length(); i++) {
            JSONObject item = accountArray.optJSONObject(i);
            if (item == null) {
                continue;
            }
            newAccounts.add(new AccountRecord(item.optInt("id"), item.optInt("userId"), item.optString("name")));
        }

        for (int i = 0; i < categoryArray.length(); i++) {
            JSONObject item = categoryArray.optJSONObject(i);
            if (item == null) {
                continue;
            }
            newCategories.add(new CategoryRecord(item.optInt("id"), item.optString("name")));
        }

        for (int i = 0; i < transactionArray.length(); i++) {
            JSONObject item = transactionArray.optJSONObject(i);
            if (item == null) {
                continue;
            }
            newTransactions.add(new TransactionRecord(
                    item.optInt("id"),
                    item.optString("title", ""),
                    item.optInt("userId"),
                    item.optInt("accountId"),
                    item.optInt("categoryId"),
                    item.optString("type"),
                    item.optDouble("amount"),
                    LocalDate.parse(item.optString("date"))
            ));
        }

        if (dbHelper == null) {
            accounts.clear();
            accounts.addAll(newAccounts);
            categories.clear();
            categories.addAll(newCategories);
            transactions.clear();
            transactions.addAll(newTransactions);
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("transactions", null, null);
            db.delete("categories", null, null);
            db.delete("accounts", null, null);

            for (AccountRecord account : newAccounts) {
                ContentValues values = new ContentValues();
                values.put("id", account.getId());
                values.put("user_id", account.getUserId());
                values.put("name", account.getName());
                db.insert("accounts", null, values);
            }
            for (CategoryRecord category : newCategories) {
                ContentValues values = new ContentValues();
                values.put("id", category.getId());
                values.put("name", category.getName());
                db.insert("categories", null, values);
            }
            for (TransactionRecord transaction : newTransactions) {
                ContentValues values = new ContentValues();
                values.put("id", transaction.getId());
                values.put("title", transaction.getTitle());
                values.put("user_id", transaction.getUserId());
                values.put("account_id", transaction.getAccountId());
                values.put("category_id", transaction.getCategoryId());
                values.put("type", transaction.getType());
                values.put("amount", transaction.getAmount());
                values.put("date", transaction.getDate().toString());
                db.insert("transactions", null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        loadFromDatabase(db);
    }

    private void refreshFromDatabaseIfAvailable() {
        if (dbHelper != null) {
            loadFromDatabase(dbHelper.getReadableDatabase());
        }
    }

    private void loadFromDatabase(SQLiteDatabase db) {
        accounts.clear();
        categories.clear();
        transactions.clear();

        try (Cursor cursor = db.rawQuery("SELECT id, user_id, name FROM accounts ORDER BY id", null)) {
            while (cursor.moveToNext()) {
                accounts.add(new AccountRecord(cursor.getInt(0), cursor.getInt(1), cursor.getString(2)));
            }
        }

        try (Cursor cursor = db.rawQuery("SELECT id, name FROM categories ORDER BY id", null)) {
            while (cursor.moveToNext()) {
                categories.add(new CategoryRecord(cursor.getInt(0), cursor.getString(1)));
            }
        }

        try (Cursor cursor = db.rawQuery("SELECT id, title, user_id, account_id, category_id, type, amount, date FROM transactions ORDER BY date DESC, id DESC", null)) {
            while (cursor.moveToNext()) {
                transactions.add(new TransactionRecord(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getString(5),
                        cursor.getDouble(6),
                        LocalDate.parse(cursor.getString(7))
                ));
            }
        }
    }

    private boolean isTableEmpty(SQLiteDatabase db, String tableName) {
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) == 0;
            }
        }
        return true;
    }

    private int findOrInsertAccount(SQLiteDatabase db, String accountName) {
        try (Cursor cursor = db.rawQuery("SELECT id FROM accounts WHERE name = ? LIMIT 1", new String[]{accountName})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        ContentValues values = new ContentValues();
        values.put("user_id", DEFAULT_USER_ID);
        values.put("name", accountName);
        return (int) db.insert("accounts", null, values);
    }

    private int findOrInsertCategory(SQLiteDatabase db, String categoryName) {
        try (Cursor cursor = db.rawQuery("SELECT id FROM categories WHERE name = ? LIMIT 1", new String[]{categoryName})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        ContentValues values = new ContentValues();
        values.put("name", categoryName);
        return (int) db.insert("categories", null, values);
    }

    private int findOrAddAccountInMemory(String accountName) {
        for (AccountRecord account : accounts) {
            if (account.getName().equalsIgnoreCase(accountName)) {
                return account.getId();
            }
        }
        int id = accounts.size() + 1;
        accounts.add(new AccountRecord(id, DEFAULT_USER_ID, accountName));
        return id;
    }

    private int findOrAddCategoryInMemory(String categoryName) {
        for (CategoryRecord category : categories) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category.getId();
            }
        }
        int id = categories.size() + 1;
        categories.add(new CategoryRecord(id, categoryName));
        return id;
    }

    private JSONArray accountsToJson() {
        JSONArray array = new JSONArray();
        for (AccountRecord account : accounts) {
            JSONObject item = new JSONObject();
            putSafely(item, "id", account.getId());
            putSafely(item, "userId", account.getUserId());
            putSafely(item, "name", account.getName());
            addSafely(array, item);
        }
        return array;
    }

    private JSONArray categoriesToJson() {
        JSONArray array = new JSONArray();
        for (CategoryRecord category : categories) {
            JSONObject item = new JSONObject();
            putSafely(item, "id", category.getId());
            putSafely(item, "name", category.getName());
            addSafely(array, item);
        }
        return array;
    }

    private JSONArray transactionsToJson() {
        JSONArray array = new JSONArray();
        for (TransactionRecord transaction : transactions) {
            JSONObject item = new JSONObject();
            putSafely(item, "id", transaction.getId());
            putSafely(item, "title", transaction.getTitle());
            putSafely(item, "userId", transaction.getUserId());
            putSafely(item, "accountId", transaction.getAccountId());
            putSafely(item, "categoryId", transaction.getCategoryId());
            putSafely(item, "type", transaction.getType());
            putSafely(item, "amount", transaction.getAmount());
            putSafely(item, "date", transaction.getDate().toString());
            addSafely(array, item);
        }
        return array;
    }

    private void addSafely(JSONArray array, Object value) {
        array.put(value);
    }

    private void putSafely(JSONObject object, String key, Object value) {
        try {
            object.put(key, value);
        } catch (JSONException ignored) {
            // ignore invalid JSON writes to keep store serialization resilient
        }
    }

    private void seedDatabase(SQLiteDatabase db) {
        seedInMemory();

        for (AccountRecord account : accounts) {
            ContentValues values = new ContentValues();
            values.put("id", account.getId());
            values.put("user_id", account.getUserId());
            values.put("name", account.getName());
            db.insert("accounts", null, values);
        }

        for (CategoryRecord category : categories) {
            ContentValues values = new ContentValues();
            values.put("id", category.getId());
            values.put("name", category.getName());
            db.insert("categories", null, values);
        }

        for (TransactionRecord transaction : transactions) {
            ContentValues values = new ContentValues();
            values.put("id", transaction.getId());
            values.put("title", transaction.getTitle());
            values.put("user_id", transaction.getUserId());
            values.put("account_id", transaction.getAccountId());
            values.put("category_id", transaction.getCategoryId());
            values.put("type", transaction.getType());
            values.put("amount", transaction.getAmount());
            values.put("date", transaction.getDate().toString());
            db.insert("transactions", null, values);
        }
    }

    private void seedInMemory() {
        accounts.clear();
        categories.clear();
        transactions.clear();

        accounts.add(new AccountRecord(1, DEFAULT_USER_ID, "Cash"));
        accounts.add(new AccountRecord(2, DEFAULT_USER_ID, "Bank Account"));
        accounts.add(new AccountRecord(3, DEFAULT_USER_ID, "Credit Card"));

        categories.add(new CategoryRecord(1, "Salary"));
        categories.add(new CategoryRecord(2, "Rent"));
        categories.add(new CategoryRecord(3, "Groceries"));
        categories.add(new CategoryRecord(4, "Transport"));
        categories.add(new CategoryRecord(5, "Dining"));
        categories.add(new CategoryRecord(6, "Utilities"));
        categories.add(new CategoryRecord(7, "Food"));
        categories.add(new CategoryRecord(8, "Shopping"));
        categories.add(new CategoryRecord(9, "Bills"));
        categories.add(new CategoryRecord(10, "Entertainment"));

        transactions.add(new TransactionRecord(1, "Salary", DEFAULT_USER_ID, 2, 1, "income", 3300, LocalDate.of(2026, 1, 2)));
        transactions.add(new TransactionRecord(2, "Rent", DEFAULT_USER_ID, 2, 2, "expense", 930, LocalDate.of(2026, 1, 5)));
        transactions.add(new TransactionRecord(3, "Groceries", DEFAULT_USER_ID, 3, 3, "expense", 260, LocalDate.of(2026, 1, 10)));
        transactions.add(new TransactionRecord(4, "Transport", DEFAULT_USER_ID, 3, 4, "expense", 120, LocalDate.of(2026, 1, 16)));
        transactions.add(new TransactionRecord(5, "Salary", DEFAULT_USER_ID, 2, 1, "income", 3400, LocalDate.of(2026, 2, 2)));
        transactions.add(new TransactionRecord(6, "Rent", DEFAULT_USER_ID, 2, 2, "expense", 930, LocalDate.of(2026, 2, 5)));
        transactions.add(new TransactionRecord(7, "Groceries", DEFAULT_USER_ID, 3, 3, "expense", 290, LocalDate.of(2026, 2, 10)));
        transactions.add(new TransactionRecord(8, "Dining", DEFAULT_USER_ID, 3, 5, "expense", 220, LocalDate.of(2026, 2, 18)));
        transactions.add(new TransactionRecord(9, "Utilities", DEFAULT_USER_ID, 2, 6, "expense", 145, LocalDate.of(2026, 2, 21)));
        transactions.add(new TransactionRecord(10, "Salary", DEFAULT_USER_ID, 2, 1, "income", 3400, LocalDate.of(2026, 3, 2)));
        transactions.add(new TransactionRecord(11, "Rent", DEFAULT_USER_ID, 2, 2, "expense", 930, LocalDate.of(2026, 3, 5)));
        transactions.add(new TransactionRecord(12, "Groceries", DEFAULT_USER_ID, 3, 3, "expense", 310, LocalDate.of(2026, 3, 11)));
        transactions.add(new TransactionRecord(13, "Transport", DEFAULT_USER_ID, 3, 4, "expense", 160, LocalDate.of(2026, 3, 15)));
        transactions.add(new TransactionRecord(14, "Dining", DEFAULT_USER_ID, 3, 5, "expense", 205, LocalDate.of(2026, 3, 23)));
    }
}
