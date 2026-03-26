package com.example.expenseeye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.expenseeye.data.repository.FinanceRepo;
import com.example.expenseeye.ui.HomeFragment;
import com.example.expenseeye.ui.budget.BudgetFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.expenseeye.ui.settings.SettingsFragment;
import com.example.expenseeye.ui.transactions.TransactionListFragment;
import com.example.expenseeye.ui.reports.ReportsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }


        FinanceRepo financeRepo = new FinanceRepo(getApplicationContext());

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
    }
}