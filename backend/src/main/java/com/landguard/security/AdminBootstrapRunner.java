package com.landguard.security;

import com.landguard.entity.User;
import com.landguard.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminBootstrapRunner(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.admin.username:admin}") String adminUsername,
            @Value("${app.security.admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByRole("ROLE_ADMIN")) {
            String password = adminPassword;
            boolean generated = false;

            if (password == null || password.trim().isEmpty()) {
                password = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                generated = true;
            }

            User admin = new User(
                    UUID.randomUUID().toString(),
                    adminUsername,
                    adminUsername + "@landguard.internal",
                    passwordEncoder.encode(password),
                    "ROLE_ADMIN"
            );
            userRepository.save(admin);

            if (generated) {
                log.info("==================================================================================");
                log.info("INITIAL ADMIN USER PROVISIONED:");
                log.info("Username: {}", adminUsername);
                log.info("Generated Password: {}", password);
                log.info("IMPORTANT: Save this password or set ADMIN_BOOTSTRAP_PASSWORD environment variable!");
                log.info("==================================================================================");
            } else {
                log.info("Initial admin user '{}' provisioned successfully from environment configuration.", adminUsername);
            }
        }
    }
}
