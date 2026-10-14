package com.landguard.controller;

import com.landguard.dto.auth.AuthResponse;
import com.landguard.dto.auth.LoginRequest;
import com.landguard.dto.auth.RegisterRequest;
import com.landguard.entity.AuditEvent;
import com.landguard.entity.User;
import com.landguard.repository.AuditEventRepository;
import com.landguard.repository.UserRepository;
import com.landguard.security.JwtTokenProvider;
import com.landguard.security.LoginRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final LoginRateLimiter rateLimiter;
    private final AuditEventRepository auditEventRepository;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            LoginRateLimiter rateLimiter,
            AuditEventRepository auditEventRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.rateLimiter = rateLimiter;
        this.auditEventRepository = auditEventRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest httpServletRequest) {
        String clientIp = httpServletRequest.getRemoteAddr();
        String rateLimitKey = clientIp + ":" + req.getUsername().toLowerCase();

        // 1. Rate limiting / lockout check
        if (rateLimiter.isBlocked(rateLimitKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(AuthResponse.error("Account temporarily locked due to multiple failed login attempts. Please try again in 15 minutes."));
        }

        // 2. Query user by username
        User user = userRepository.findByUsername(req.getUsername().trim()).orElse(null);

        // 3. Verify password hash using BCrypt
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash()) || !user.isEnabled()) {
            rateLimiter.recordFailedAttempt(rateLimitKey);
            recordAudit(req.getUsername(), "LOGIN_FAILED", "USER", null, clientIp, "Failed login attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.error("Invalid username or password."));
        }

        // 4. Success - reset limiter, generate JWT
        rateLimiter.recordSuccess(rateLimitKey);
        String token = tokenProvider.generateToken(user.getUsername(), user.getRole());
        recordAudit(user.getUsername(), "LOGIN_SUCCESS", "USER", user.getId(), clientIp, "Successful authentication");

        return ResponseEntity.ok(AuthResponse.success(token, user.getUsername(), user.getRole()));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest req,
            HttpServletRequest httpServletRequest) {
        String username = req.getUsername().trim();
        String email = req.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(AuthResponse.error("Username is already taken."));
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(AuthResponse.error("Email is already registered."));
        }

        User newUser = new User(
                UUID.randomUUID().toString(),
                username,
                email,
                passwordEncoder.encode(req.getPassword()),
                "ROLE_USER"
        );
        userRepository.save(newUser);
        recordAudit(username, "USER_REGISTERED", "USER", newUser.getId(), httpServletRequest.getRemoteAddr(), "New user self-registration");

        String token = tokenProvider.generateToken(newUser.getUsername(), newUser.getRole());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthResponse.success(token, newUser.getUsername(), newUser.getRole()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest httpServletRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "anonymous";
        recordAudit(username, "LOGOUT", "USER", null, httpServletRequest.getRemoteAddr(), "User logged out");
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> body) {
        // Generic response: Never disclose whether an arbitrary email/username has an account
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "If an active account exists with the provided information, password recovery instructions have been dispatched."
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false));
        }

        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("authenticated", false));
        }

        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }

    private void recordAudit(String actor, String action, String resourceType, String resourceId, String ip, String details) {
        try {
            AuditEvent event = new AuditEvent(
                    UUID.randomUUID().toString(),
                    actor != null ? actor : "anonymous",
                    action,
                    resourceType,
                    resourceId,
                    ip,
                    details
            );
            auditEventRepository.save(event);
        } catch (Exception ignored) {}
    }
}
