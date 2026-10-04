package com.example.rediscache.service;

import com.example.rediscache.model.User;
import com.example.rediscache.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Cache key for all users constant
    public static final String USERS_CACHE = "usersCache";
    public static final String USER_BY_ID_CACHE = "userByIdCache";

    /**
     * Fetch all users. Result is cached in Redis 'usersCache'.
     * Artificial delay simulated to demonstrate cache speedup on DB fetch.
     */
    @Cacheable(value = USERS_CACHE, key = "'all'")
    public List<User> getAllUsers() {
        simulateSlowService();
        return userRepository.findAll();
    }

    /**
     * Fetch user by ID. Result is cached in Redis 'userByIdCache'.
     */
    @Cacheable(value = USER_BY_ID_CACHE, key = "#id")
    public User getUserById(Long id) {
        simulateSlowService();
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Create a new user. Evicts the 'usersCache' so fresh list will be fetched next time.
     */
    @CacheEvict(value = USERS_CACHE, key = "'all'", allEntries = true)
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Update existing user. Updates specific cache entry and evicts list cache.
     */
    @CachePut(value = USER_BY_ID_CACHE, key = "#id")
    @CacheEvict(value = USERS_CACHE, key = "'all'", allEntries = true)
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        existingUser.setName(userDetails.getName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setRole(userDetails.getRole());

        return userRepository.save(existingUser);
    }

    /**
     * Delete user by ID. Evicts both specific cache entry and list cache.
     */
    @CacheEvict(value = {USER_BY_ID_CACHE, USERS_CACHE}, key = "#id", allEntries = true)
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Clear all user caches manually.
     */
    @CacheEvict(value = {USERS_CACHE, USER_BY_ID_CACHE}, allEntries = true)
    public void clearAllCache() {
        // Purges caches
    }

    private void simulateSlowService() {
        try {
            // Simulate slow database response time (500ms)
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
