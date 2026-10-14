package com.landguard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landguard.dto.auth.LoginRequest;
import com.landguard.dto.auth.RegisterRequest;
import com.landguard.entity.User;
import com.landguard.repository.UserRepository;
import com.landguard.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthAndSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        if (!userRepository.existsByUsername("testuser")) {
            User user = new User(
                    UUID.randomUUID().toString(),
                    "testuser",
                    "testuser@landguard.internal",
                    passwordEncoder.encode("SecurePass123!"),
                    "ROLE_USER"
            );
            userRepository.save(user);
        }

        if (!userRepository.existsByUsername("adminuser")) {
            User admin = new User(
                    UUID.randomUUID().toString(),
                    "adminuser",
                    "admin@landguard.internal",
                    passwordEncoder.encode("AdminPass123!"),
                    "ROLE_ADMIN"
            );
            userRepository.save(admin);
        }
    }

    @Test
    @DisplayName("Password hashing: BCrypt must hash passwords with salt and verify correctly")
    void testPasswordHashing() {
        String rawPassword = "TestPassword@2026";
        String hashed = passwordEncoder.encode(rawPassword);

        assertNotEquals(rawPassword, hashed);
        assertTrue(hashed.startsWith("$2a$") || hashed.startsWith("$2b$"));
        assertTrue(passwordEncoder.matches(rawPassword, hashed));
        assertFalse(passwordEncoder.matches("WrongPassword", hashed));
    }

    @Test
    @DisplayName("User Login: Successful authentication returns valid signed JWT token")
    void testSuccessfulLogin() throws Exception {
        LoginRequest req = new LoginRequest("testuser", "SecurePass123!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @DisplayName("User Login: Invalid credentials returns generic 401 error message without credential leakage")
    void testInvalidLoginReturnsGenericError() throws Exception {
        LoginRequest req = new LoginRequest("testuser", "WrongPassword!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password."));
    }

    @Test
    @DisplayName("User Registration: Registers user and stores BCrypt hash in database")
    void testUserRegistration() throws Exception {
        String uniqueUser = "reguser_" + System.currentTimeMillis();
        RegisterRequest req = new RegisterRequest();
        req.setUsername(uniqueUser);
        req.setEmail(uniqueUser + "@example.com");
        req.setPassword("ValidPassword123!");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").isString());

        User saved = userRepository.findByUsername(uniqueUser).orElse(null);
        assertNotNull(saved);
        assertTrue(passwordEncoder.matches("ValidPassword123!", saved.getPasswordHash()));
        assertEquals("ROLE_USER", saved.getRole());
    }

    @Test
    @DisplayName("Privilege Escalation Prevention: Regular USER cannot access /api/v1/admin/sources (403 Forbidden)")
    void testPrivilegeEscalationPrevented() throws Exception {
        String userToken = tokenProvider.generateToken("testuser", "ROLE_USER");

        mockMvc.perform(get("/api/v1/admin/sources")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin Authorization: ADMIN can successfully access /api/v1/admin/sources")
    void testAdminAccessAllowed() throws Exception {
        String adminToken = tokenProvider.generateToken("adminuser", "ROLE_ADMIN");

        mockMvc.perform(get("/api/v1/admin/sources")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Anonymous Request: Unauthenticated request to /api/v1/admin/sources is rejected (401/403)")
    void testUnauthenticatedAccessRejected() throws Exception {
        mockMvc.perform(get("/api/v1/admin/sources"))
                .andExpect(status().isForbidden());
    }
}
