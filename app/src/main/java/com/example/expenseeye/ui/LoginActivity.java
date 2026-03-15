package com.example.expenseeye.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expenseeye.R;
import com.example.expenseeye.viewmodel.auth.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private Button buttonRegister;
    private Button buttonLogin;
    private TextView textStatus;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = new LoginViewModel();

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonLogin = findViewById(R.id.buttonLogin);
        textStatus = findViewById(R.id.textStatus);

        buttonRegister.setOnClickListener(v -> handleRegister());
        buttonLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleRegister() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            textStatus.setText("Please fill in all registration fields.");
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

        boolean success = loginViewModel.register(name, email, password);

        if (success) {
            textStatus.setText("Registration successful.");
        } else {
            textStatus.setText("Registration failed. Email may already exist.");
        }
    }

    private void handleLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            textStatus.setText("Please enter email and password.");
            return;
        }

        boolean success = loginViewModel.login(email, password);

        if (success) {
            textStatus.setText("Login successful.");
        } else {
            textStatus.setText("Invalid email or password.");
        }
    }
}