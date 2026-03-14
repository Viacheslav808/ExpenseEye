package com.example.expenseeye.data;

import com.example.expenseeye.model.finance.AccountRecord;
import com.example.expenseeye.model.finance.CategoryRecord;
import com.example.expenseeye.model.finance.TransactionRecord;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinanceStore {
    private static final FinanceStore INSTANCE = new FinanceStore();
    private final List<AccountRecord> accounts = new ArrayList<>();
    private final List<CategoryRecord> categories = new ArrayList<>();
    private final List<TransactionRecord> transactions = new ArrayList<>();

    private FinanceStore() {
        seed();
    }

    public static FinanceStore getInstance() {
        return INSTANCE;
    }

    public synchronized List<AccountRecord> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public synchronized List<CategoryRecord> getCategories() {
        return new ArrayList<>(categories);
    }

    public synchronized List<TransactionRecord> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public synchronized JSONObject toJson() {
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
                    item.optInt("userId"),
                    item.optInt("accountId"),
                    item.optInt("categoryId"),
                    item.optString("type"),
                    item.optDouble("amount"),
                    LocalDate.parse(item.optString("date"))
            ));
        }

        accounts.clear();
        accounts.addAll(newAccounts);
        categories.clear();
        categories.addAll(newCategories);
        transactions.clear();
        transactions.addAll(newTransactions);
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
        // Android's JSONArray#put does not throw checked JSONException.
        array.put(value);
    }

    private void putSafely(JSONObject object, String key, Object value) {
        // JSONObject#putOpt skips null values and also does not throw checked JSONException.
        object.putOpt(key, value);
    }

    private void seed() {
        accounts.clear();
        categories.clear();
        transactions.clear();

        accounts.add(new AccountRecord(1, 1001, "Chequing"));
        accounts.add(new AccountRecord(2, 1001, "Credit Card"));

        categories.add(new CategoryRecord(1, "Salary"));
        categories.add(new CategoryRecord(2, "Rent"));
        categories.add(new CategoryRecord(3, "Groceries"));
        categories.add(new CategoryRecord(4, "Transport"));
        categories.add(new CategoryRecord(5, "Dining"));
        categories.add(new CategoryRecord(6, "Utilities"));

        transactions.add(new TransactionRecord(1, 1001, 1, 1, "income", 3300, LocalDate.of(2026, 1, 2)));
        transactions.add(new TransactionRecord(2, 1001, 1, 2, "expense", 930, LocalDate.of(2026, 1, 5)));
        transactions.add(new TransactionRecord(3, 1001, 2, 3, "expense", 260, LocalDate.of(2026, 1, 10)));
        transactions.add(new TransactionRecord(4, 1001, 2, 4, "expense", 120, LocalDate.of(2026, 1, 16)));
        transactions.add(new TransactionRecord(5, 1001, 1, 1, "income", 3400, LocalDate.of(2026, 2, 2)));
        transactions.add(new TransactionRecord(6, 1001, 1, 2, "expense", 930, LocalDate.of(2026, 2, 5)));
        transactions.add(new TransactionRecord(7, 1001, 2, 3, "expense", 290, LocalDate.of(2026, 2, 10)));
        transactions.add(new TransactionRecord(8, 1001, 2, 5, "expense", 220, LocalDate.of(2026, 2, 18)));
        transactions.add(new TransactionRecord(9, 1001, 1, 6, "expense", 145, LocalDate.of(2026, 2, 21)));
        transactions.add(new TransactionRecord(10, 1001, 1, 1, "income", 3400, LocalDate.of(2026, 3, 2)));
        transactions.add(new TransactionRecord(11, 1001, 1, 2, "expense", 930, LocalDate.of(2026, 3, 5)));
        transactions.add(new TransactionRecord(12, 1001, 2, 3, "expense", 310, LocalDate.of(2026, 3, 11)));
        transactions.add(new TransactionRecord(13, 1001, 2, 4, "expense", 160, LocalDate.of(2026, 3, 15)));
        transactions.add(new TransactionRecord(14, 1001, 2, 5, "expense", 205, LocalDate.of(2026, 3, 23)));
    }
}
