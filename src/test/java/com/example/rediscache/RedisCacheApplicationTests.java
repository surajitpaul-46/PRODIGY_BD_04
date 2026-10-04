package com.example.rediscache;

import com.example.rediscache.model.User;
import com.example.rediscache.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisCacheApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void testCachePerformanceAndInvalidation() {
        // First Call: Cache Miss (Triggers 500ms sleep)
        long startTime1 = System.currentTimeMillis();
        List<User> users1 = userService.getAllUsers();
        long duration1 = System.currentTimeMillis() - startTime1;

        assertNotNull(users1);
        assertTrue(duration1 >= 500, "First call should simulate DB hit and take >= 500ms");

        // Second Call: Cache Hit (Instant response from Redis)
        long startTime2 = System.currentTimeMillis();
        List<User> users2 = userService.getAllUsers();
        long duration2 = System.currentTimeMillis() - startTime2;

        assertEquals(users1.size(), users2.size());
        assertTrue(duration2 < 100, "Second call should be served from Redis cache in < 100ms");

        // Cache Invalidation Test: Create new user evicts cache
        User newUser = User.builder().name("David Miller").email("david@example.com").role("MANAGER").build();
        userService.createUser(newUser);

        // Third Call: Cache Miss due to invalidation (Triggers 500ms sleep)
        long startTime3 = System.currentTimeMillis();
        List<User> users3 = userService.getAllUsers();
        long duration3 = System.currentTimeMillis() - startTime3;

        assertEquals(users1.size() + 1, users3.size());
        assertTrue(duration3 >= 500, "Call after cache evict/invalidation should take >= 500ms");
    }
}
