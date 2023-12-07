package com.example.DishQR_api.config;

import com.example.DishQR_api.model.DiscountSettings;
import com.example.DishQR_api.model.Role;
import com.example.DishQR_api.model.User;
import com.example.DishQR_api.repository.DiscountSettingsRepository;
import com.example.DishQR_api.repository.UserRepository;
import com.example.DishQR_api.service.DiscountSettingsService;
import com.example.DishQR_api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DiscountSettingsRepository discountSettingsRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final DiscountSettingsService discountSettingsService;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {

            User admin = User
                    .builder()
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("password"))
                    .role(Role.ROLE_ADMIN)
                    .build();

            userService.save(admin);
            log.debug("created ADMIN user - {}", admin);
        }
        if(discountSettingsRepository.count() == 0) {
            DiscountSettings settings = DiscountSettings
                    .builder()
                    .isEnabled(true)
                    .discountPercentage(0.5)
                    .ordersRequired(2)
                    .build();
            discountSettingsService.save(settings);
            System.out.println("created discount settings");
        }
    }
}
