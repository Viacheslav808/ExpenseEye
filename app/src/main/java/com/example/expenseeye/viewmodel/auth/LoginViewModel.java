package com.example.expenseeye.viewmodel.auth;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.ViewModel;

import com.example.expenseeye.data.dao.CredentialDao;
import com.example.expenseeye.data.dao.UserDao;
import com.example.expenseeye.data.entities.Credential;
import com.example.expenseeye.data.repository.FinanceRepo;
import com.example.expenseeye.repository.auth.AuthRepository;
import com.example.expenseeye.service.PasswordHasher;

import java.util.concurrent.Executors;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final FinanceRepo financeRepo;

    public LoginViewModel(CredentialDao credentialDao, UserDao userDao, FinanceRepo financeRepo) {
        this.authRepository = new AuthRepository(credentialDao, userDao);
        this.financeRepo = financeRepo;
    }

    public interface LoginCallback {
        void onResult(int userId);
    }

    public boolean register(String name, String email, String password) {
        String salt = PasswordHasher.generateSalt();
        String passwordHash = PasswordHasher.hashPassword(password, salt);

        long userId = authRepository.registerUser(name, email, passwordHash, salt);
        return userId > 0;
    }

    public void login(String email, String password, LoginCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {

            Credential credential = authRepository.getCredentialByEmail(email);

            int userId = -1;

            if (credential != null) {
                boolean success = PasswordHasher.verifyPassword(
                        password,
                        credential.getPasswordHash(),
                        credential.getPasswordSalt()
                );

                if (success) {
                    userId = credential.getUserId();
                }
            }

            int finalUserId = userId;

            new Handler(Looper.getMainLooper()).post(() -> {
                callback.onResult(finalUserId);

                if (finalUserId != -1) {
                    financeRepo.runChecksOnLogin();
                }
            });
        });
    }
}