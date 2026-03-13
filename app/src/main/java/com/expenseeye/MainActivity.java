package com.expenseeye;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.expenseeye.data.repository.FinanceRepo;
import com.expenseeye.ui.transactions.TransactionListFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FinanceRepo financeRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        setContentView(R.layout.activity_main);

        // Initialize repository (this will create default account & category)
        financeRepo = new FinanceRepo(getApplicationContext());

        // Prevent fragment from loading multiple times
        if (savedInstanceState == null) {
            Log.d(TAG, "Adding TransactionListFragment");

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TransactionListFragment())
                    .commit();
        }
    }
}