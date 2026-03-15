package com.example.expenseeye.repository.auth;

import com.example.expenseeye.data.entities.User;
import com.example.expenseeye.data.entities.Credential;

import java.util.HashMap;
import java.util.Map;

/*
 * AuthRepository handles authentication logic such as
 * registering users and retrieving credentials for login.
 *
 * This implementation uses simple in-memory storage to
 * keep the system lightweight and aligned with the project scope.
 */
public class AuthRepository {

    // Temporary in-memory storage
    private final Map<String, Credential> credentialStore = new HashMap<>();
    private final Map<Integer, User> userStore = new HashMap<>();

    private int userIdCounter = 1;

    /**
     * Register a new user account
     */
    public boolean registerUser(String name, String email, String passwordHash, String salt) {

        // enforce one account per email
        if (credentialStore.containsKey(email)) {
            return false;
        }

        int newUserId = userIdCounter++;

        // create user entity
        User user = new User(name, System.currentTimeMillis());
        user.setUserId(newUserId);

        // create credential entity
        Credential credential = new Credential(
                newUserId,
                email,
                passwordHash,
                salt
        );

        // store objects
        userStore.put(newUserId, user);
        credentialStore.put(email, credential);

        return true;
    }

    /**
     * Find credential by email (used for login)
     */
    public Credential getCredentialByEmail(String email) {
        return credentialStore.get(email);
    }

    /**
     * Retrieve user by ID
     */
    public User getUserById(int userId) {
        return userStore.get(userId);
    }
}