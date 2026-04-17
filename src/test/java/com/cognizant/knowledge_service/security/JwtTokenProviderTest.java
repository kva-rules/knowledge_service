package com.cognizant.knowledge_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private String jwtSecret = "test-jwt-secret-key-for-authentication-minimum-256-bits-required";
    private long jwtExpiration = 86400000L;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", jwtExpiration);
    }

    private String generateTestToken(String userId, List<String> roles) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(userId)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

    @Test
    @DisplayName("Should validate valid token")
    void validateToken_ValidToken() {
        String userId = UUID.randomUUID().toString();
        String token = generateTestToken(userId, List.of("ADMIN"));

        boolean result = jwtTokenProvider.validateToken(token);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false for invalid token")
    void validateToken_InvalidToken() {
        boolean result = jwtTokenProvider.validateToken("invalid-token");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should extract user ID from token")
    void getUserIdFromToken_Success() {
        String userId = UUID.randomUUID().toString();
        String token = generateTestToken(userId, List.of("ADMIN"));

        String result = jwtTokenProvider.getUserIdFromToken(token);

        assertEquals(userId, result);
    }

    @Test
    @DisplayName("Should extract roles from token")
    void getRolesFromToken_Success() {
        String userId = UUID.randomUUID().toString();
        List<String> roles = List.of("ADMIN", "MANAGER");
        String token = generateTestToken(userId, roles);

        List<String> result = jwtTokenProvider.getRolesFromToken(token);

        assertEquals(2, result.size());
        assertTrue(result.contains("ADMIN"));
        assertTrue(result.contains("MANAGER"));
    }

    @Test
    @DisplayName("Should return false for expired token")
    void validateToken_ExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .claim("roles", List.of("ADMIN"))
                .issuedAt(new Date(System.currentTimeMillis() - 200000))
                .expiration(new Date(System.currentTimeMillis() - 100000))
                .signWith(key)
                .compact();

        boolean result = jwtTokenProvider.validateToken(expiredToken);

        assertFalse(result);
    }
}
