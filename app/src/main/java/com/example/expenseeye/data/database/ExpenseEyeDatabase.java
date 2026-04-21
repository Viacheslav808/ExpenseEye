package com.example.expenseeye.data.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.expenseeye.data.dao.AccountDao;
import com.example.expenseeye.data.dao.BudgetDao;
import com.example.expenseeye.data.dao.CategoryDao;
import com.example.expenseeye.data.dao.CredentialDao;
import com.example.expenseeye.data.dao.TransactionDao;
import com.example.expenseeye.data.dao.UserDao;
import com.example.expenseeye.data.entities.Credential;
import com.example.expenseeye.data.entities.User;
import com.example.expenseeye.data.model.Account;
import com.example.expenseeye.data.model.Budget;
import com.example.expenseeye.data.model.Category;
import com.example.expenseeye.data.model.Transaction;
import com.example.expenseeye.service.PasswordHasher;

import java.util.concurrent.Executors;

@Database(
        entities = {Account.class, Transaction.class, Category.class, User.class, Credential.class, Budget.class},
        version = 4,
        exportSchema = false
)
public abstract class ExpenseEyeDatabase extends RoomDatabase {

    private static ExpenseEyeDatabase instance;

    public abstract TransactionDao transactionDao();
    public abstract AccountDao accountDao();
    public abstract CategoryDao categoryDao();
    public abstract UserDao userDao();
    public abstract CredentialDao credentialDao();
    public abstract BudgetDao budgetDao();

    /**
     * Migration from v3 → v4
     * - Adds `name` column to `budgets` (default empty string so existing rows stay valid)
     * - Drops the old UNIQUE index on categoryId and replaces it with a non-unique one
     *   so users can create multiple budgets per category.
     */
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            // 1. Add the new name column with a default so existing rows are valid
            db.execSQL("ALTER TABLE budgets ADD COLUMN name TEXT NOT NULL DEFAULT ''");

            // 2. Drop the old unique index on categoryId
            db.execSQL("DROP INDEX IF EXISTS index_budgets_categoryId");

            // 3. Recreate it as a non-unique index
            db.execSQL("CREATE INDEX IF NOT EXISTS index_budgets_categoryId " +
                    "ON budgets(categoryId)");
        }
    };

    public static synchronized ExpenseEyeDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ExpenseEyeDatabase.class,
                            "expenseeye_db"
                    )
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigrationFrom(1, 2) // only old dev versions destroy; v3→v4 uses real migration
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(() -> {
                                AccountDao accountDao = instance.accountDao();
                                CategoryDao categoryDao = instance.categoryDao();
                                UserDao userDao = instance.userDao();
                                CredentialDao credentialDao = instance.credentialDao();

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

                                // Default user
                                long now = System.currentTimeMillis();
                                User defaultUser = new User("admin", now);
                                long userId = userDao.insert(defaultUser); // <-- autogenerated ID returned here

                                // Default credentials
                                String salt = PasswordHasher.generateSalt();
                                String hashedPassword = PasswordHasher.hashPassword("password123", salt);
                                Credential defaultCred = new Credential(
                                        (int) userId,
                                        "admin@example.com",
                                        hashedPassword,
                                        salt
                                );
                                credentialDao.insert(defaultCred);

                                Log.d("DB_SEED", "Inserted default userId = " + userId);
                                Log.d("DB_SEED", "Inserted email = admin@example.com");
                                Log.d("DB_SEED", "Inserted hash = " + hashedPassword);
                                Log.d("DB_SEED", "Inserted salt = " + salt);
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }
}