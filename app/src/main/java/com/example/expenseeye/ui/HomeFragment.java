package com.example.expenseeye.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.expenseeye.R;
import com.example.expenseeye.data.dao.CredentialDao;
import com.example.expenseeye.data.dao.UserDao;
import com.example.expenseeye.data.database.ExpenseEyeDatabase;
import com.example.expenseeye.data.entities.Credential;
import com.example.expenseeye.data.entities.User;
import com.example.expenseeye.data.repository.FinanceRepo;
import com.example.expenseeye.data.repository.FinanceRepoProvider;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView textWelcomeMessage;
    private TextView textWelcomeMessage2;
    private TextView textTotalBalance;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textWelcomeMessage = view.findViewById(R.id.text_welcome_message);
        textWelcomeMessage2 = view.findViewById(R.id.text_welcome_message2);
        textTotalBalance = view.findViewById(R.id.textTotalBalance);

        SharedPreferences prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        FinanceRepo financeRepo = FinanceRepoProvider.get(requireContext());

        if (userId != -1) {
            financeRepo.ensureDefaultAccountsForUser(userId);

            financeRepo.getTotalBalanceForUser(userId).observe(getViewLifecycleOwner(), total -> {
                String formattedBalance = String.format(Locale.US, "Total Balance: $%.2f", total);
                textTotalBalance.setText(formattedBalance);
            });

            new Thread(() -> {
                ExpenseEyeDatabase db = ExpenseEyeDatabase.getInstance(requireContext().getApplicationContext());
                UserDao userDao = db.userDao();
                CredentialDao credentialDao = db.credentialDao();

                User user = userDao.getUserById(userId);
                Credential credential = credentialDao.getCredentialByUserId(userId);

                requireActivity().runOnUiThread(() -> {
                    if (user != null) {
                        textWelcomeMessage.setText("Welcome, " + user.getUserName());
                    } else {
                        textWelcomeMessage.setText("Welcome");
                    }

                    if (credential != null) {
                        textWelcomeMessage2.setText(credential.getEmail());
                    } else {
                        textWelcomeMessage2.setText("Let's get tracking!");
                    }
                });
            }).start();

        } else {
            textWelcomeMessage.setText("Welcome");
            textWelcomeMessage2.setText("Please log in");
            textTotalBalance.setText("Total Balance: $0.00");
        }

        return view;
    }
}