package com.example.rediscache.controller;

import com.example.rediscache.model.User;
import com.example.rediscache.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        long startTime = System.currentTimeMillis();
        List<User> users = userService.getAllUsers();
        long executionTimeMs = System.currentTimeMillis() - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("data", users);
        response.put("executionTimeMs", executionTimeMs);
        response.put("cachedResponse", executionTimeMs < 100);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        User user = userService.getUserById(id);
        long executionTimeMs = System.currentTimeMillis() - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("data", user);
        response.put("executionTimeMs", executionTimeMs);
        response.put("cachedResponse", executionTimeMs < 100);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User with ID " + id + " deleted successfully. Cache evicted.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cache/clear")
    public ResponseEntity<Map<String, String>> clearCache() {
        userService.clearAllCache();
        Map<String, String> response = new HashMap<>();
        response.put("message", "All Redis caches manually purged.");
        return ResponseEntity.ok(response);
    }
}
