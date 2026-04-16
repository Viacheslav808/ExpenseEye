package com.example.expenseeye.repository.auth;

import com.example.expenseeye.data.dao.CredentialDao;
import com.example.expenseeye.data.dao.UserDao;
import com.example.expenseeye.data.entities.Credential;
import com.example.expenseeye.data.entities.User;

public class AuthRepository {

    private final CredentialDao credentialDao;
    private final UserDao userDao;

    public AuthRepository(CredentialDao credentialDao, UserDao userDao) {
        this.credentialDao = credentialDao;
        this.userDao = userDao;
    }

    public Credential getCredentialByEmail(String email) {
        return credentialDao.getCredentialByEmail(email);
    }

    public long registerUser(String name, String email, String passwordHash, String salt) {
        // Prevent duplicate email registration
        Credential existingCredential = credentialDao.getCredentialByEmail(email);
        if (existingCredential != null) {
            return -1;
        }

        long userId = userDao.insert(new User(name, System.currentTimeMillis()));

        Credential credential = new Credential(
                (int) userId,
                email,
                passwordHash,
                salt
        );

        credentialDao.insert(credential);

        return userId;
    }

    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }
}