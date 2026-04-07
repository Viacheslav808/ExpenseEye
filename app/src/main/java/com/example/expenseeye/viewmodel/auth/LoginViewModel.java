package com.example.expenseeye.viewmodel.auth;

import android.os.Looper;

import androidx.lifecycle.ViewModel;

import com.example.expenseeye.data.dao.CredentialDao;
import com.example.expenseeye.data.dao.UserDao;
import com.example.expenseeye.data.entities.Credential;
import com.example.expenseeye.data.repository.FinanceRepo;
import com.example.expenseeye.repository.auth.AuthRepository;
import com.example.expenseeye.service.PasswordHasher;


import java.util.concurrent.Executors;
import android.os.Handler;
import android.util.Log;


public class LoginViewModel extends ViewModel {


    private final AuthRepository authRepository;
    private final FinanceRepo financeRepo;

    public LoginViewModel(CredentialDao credentialDao, UserDao userDao, FinanceRepo financeRepo) {
        this.authRepository = new AuthRepository(credentialDao, userDao);
        this.financeRepo = financeRepo;
    }

    public interface LoginCallback {
        void onResult(boolean success);
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

            boolean success = false;

            if (credential != null) {
                success = PasswordHasher.verifyPassword(
                        password,
                        credential.getPasswordHash(),
                        credential.getPasswordSalt()
                );
            }

            boolean finalSuccess = success;

            // Switch back to UI thread
            new Handler(Looper.getMainLooper()).post(() -> {
                callback.onResult(finalSuccess);
                if (finalSuccess) {
                    financeRepo.runChecksOnLogin();
                }
            });
        });
    }

}
