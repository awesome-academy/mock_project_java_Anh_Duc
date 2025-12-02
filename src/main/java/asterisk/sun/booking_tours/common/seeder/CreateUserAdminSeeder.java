package asterisk.sun.booking_tours.common.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.core.user.Role;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import asterisk.sun.booking_tours.core.user.UserStatus;

@Component
public class CreateUserAdminSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CreateUserAdminSeeder.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserAdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
    }

    private void seedAdminUser() {
        String adminEmail = "admin@example.com";
        String adminPassword = "123456";
        String adminAddress = "VN";

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setPhone("123456789");
            admin.setDateOfBirth(java.time.LocalDate.of(1991, 3, 23));
            admin.setIsVerified(true);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setAddress(adminAddress);

            userRepository.save(admin);
            logger.info("Admin user created successfully!");
            logger.info("   Username: {}", "admin");
            logger.info("   Email: {}", adminEmail);
        } else {
            logger.info("Admin user already exists with email: {}", adminEmail);
        }
    }

}
