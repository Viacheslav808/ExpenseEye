package com.example.expenseeye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.example.expenseeye.data.repository.FinanceRepo;
import com.example.expenseeye.data.repository.FinanceRepoProvider;
import com.example.expenseeye.service.NotificationService;
import com.example.expenseeye.ui.HomeFragment;
import com.example.expenseeye.ui.budget.BudgetFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.expenseeye.ui.settings.SettingsFragment;
import com.example.expenseeye.ui.transactions.TransactionListFragment;
import com.example.expenseeye.ui.reports.ReportsFragment;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;


public class MainActivity extends AppCompatActivity {
    private String pendingBudgetMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FinanceRepo financeRepo = FinanceRepoProvider.get(getApplicationContext());
        NotificationService notificationService = financeRepo.getNotificationService();

        notificationService.setBudgetAlertListener(message -> {
            Log.d("LOGIN_FLOW", "Login success reached");
            pendingBudgetMessage = message;
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }



        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new HomeFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_settings) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new SettingsFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_transactions) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new TransactionListFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_reports) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new ReportsFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.navigation_budget) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new BudgetFragment())
                        .commit();
                return true;
            }
            return false;
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new HomeFragment())
                .commit();

        new Handler(Looper.getMainLooper()).post(() -> {
            if (pendingBudgetMessage != null) {
                Snackbar.make(findViewById(android.R.id.content), pendingBudgetMessage, Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.RED)
                        .setTextColor(Color.WHITE)
                        .show();
                pendingBudgetMessage = null;
            }
        });


    }
}