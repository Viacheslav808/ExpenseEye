package com.example.expenseeye.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expenseeye.MainActivity;
import com.example.expenseeye.R;
import com.example.expenseeye.data.dao.CredentialDao;
import com.example.expenseeye.data.dao.UserDao;
import com.example.expenseeye.data.database.ExpenseEyeDatabase;
import com.example.expenseeye.viewmodel.auth.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;

    private Button buttonPrimaryAction;
    private TextView textToggleMode;
    private TextView textSubtitle;
    private TextView textStatus;

    private LoginViewModel loginViewModel;

    private boolean isRegisterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ExpenseEyeDatabase db = ExpenseEyeDatabase.getInstance(getApplicationContext());
        CredentialDao credentialDao = db.credentialDao();
        UserDao userDao = db.userDao();

        loginViewModel = new LoginViewModel(credentialDao, userDao);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);

        buttonPrimaryAction = findViewById(R.id.buttonPrimaryAction);
        textToggleMode = findViewById(R.id.textToggleMode);
        textSubtitle = findViewById(R.id.textSubtitle);
        textStatus = findViewById(R.id.textStatus);

        updateModeUI();

        buttonPrimaryAction.setOnClickListener(v -> {
            if (isRegisterMode) {
                handleRegister();
            } else {
                handleLogin();
            }
        });

        textToggleMode.setOnClickListener(v -> {
            isRegisterMode = !isRegisterMode;
            clearFields();
            textStatus.setText("");
            updateModeUI();
        });
    }

    private void updateModeUI() {
        if (isRegisterMode) {
            textSubtitle.setText("Create your account");
            editName.setVisibility(View.VISIBLE);
            editConfirmPassword.setVisibility(View.VISIBLE);
            buttonPrimaryAction.setText("Register");
            textToggleMode.setText("Already have an account? Login");
        } else {
            textSubtitle.setText("Sign in to continue");
            editName.setVisibility(View.GONE);
            editConfirmPassword.setVisibility(View.GONE);
            buttonPrimaryAction.setText("Login");
            textToggleMode.setText("Need an account? Register");
        }
    }

    private void handleRegister() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            textStatus.setText("Please fill in all required fields.");
            return;
        }

        if (password.length() < 6) {
            textStatus.setText("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            textStatus.setText("Passwords do not match.");
            return;
        }

        new Thread(() -> {
            boolean success = loginViewModel.register(name, email, password);

            runOnUiThread(() -> {
                if (success) {
                    textStatus.setText("Registration successful. You can now log in.");
                    isRegisterMode = false;
                    clearFields();
                    updateModeUI();
                } else {
                    textStatus.setText("Registration failed. Email may already exist.");
                }
            });
        }).start();
    }

    private void handleLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            textStatus.setText("Please enter email and password.");
            return;
        }

        loginViewModel.login(email, password, success -> {
            if (success) {
                textStatus.setText("Login successful.");

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                textStatus.setText("Invalid email or password.");
            }
        });
    }

    private void clearFields() {
        editName.setText("");
        editEmail.setText("");
        editPassword.setText("");
        editConfirmPassword.setText("");
    }
}