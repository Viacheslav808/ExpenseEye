package com.example.expenseeye.viewmodel.auth;

import androidx.lifecycle.ViewModel;

import com.example.expenseeye.data.entities.Credential;
import com.example.expenseeye.repository.auth.AuthRepository;
import com.example.expenseeye.service.PasswordHasher;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;

    public LoginViewModel() {
        this.authRepository = new AuthRepository();
    }

    public boolean register(String name, String email, String password) {
        String salt = PasswordHasher.generateSalt();
        String passwordHash = PasswordHasher.hashPassword(password, salt);

        return authRepository.registerUser(name, email, passwordHash, salt);
    }

    public boolean login(String email, String password) {
        Credential credential = authRepository.getCredentialByEmail(email);

        if (credential == null) {
            return false;
        }

        return PasswordHasher.verifyPassword(
                password,
                credential.getPasswordHash(),
                credential.getPasswordSalt()
        );
    }
}