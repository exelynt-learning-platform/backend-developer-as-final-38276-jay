package com.booking.system.config;

import com.booking.system.domain.Role;
import com.booking.system.domain.User;
import com.booking.system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")    
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = new User("admin@booking.com", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN);
            User user = new User("user@booking.com", passwordEncoder.encode("user123"), Role.ROLE_USER);
            
            userRepository.save(admin);
            userRepository.save(user);
            
            System.out.println("====== SYSTEM DATA SEEDING COMPLETE ======");
            System.out.println("ADMIN ACCOUNT SEED: admin@booking.com / admin123");
            System.out.println("USER ACCOUNT SEED: user@booking.com / user123");
            System.out.println("===========================================");
        }
    }
}
