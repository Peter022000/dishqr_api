package com.example.DishQR_api.config;

import com.example.DishQR_api.dto.DiscountSettingsDto;
import com.example.DishQR_api.dto.QrCodeDto;
import com.example.DishQR_api.model.*;
import com.example.DishQR_api.repository.DiscountSettingsRepository;
import com.example.DishQR_api.repository.DishRepository;
import com.example.DishQR_api.repository.QrCodeRepository;
import com.example.DishQR_api.repository.UserRepository;
import com.example.DishQR_api.service.DiscountSettingsService;
import com.example.DishQR_api.service.DishService;
import com.example.DishQR_api.service.QrCodeService;
import com.example.DishQR_api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final QrCodeRepository qrCodeRepository;
    private final DishRepository dishRepository;
    private final DiscountSettingsRepository discountSettingsRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final DiscountSettingsService discountSettingsService;
    private final DishService dishService;
    private final QrCodeService qrCodeService;

    @Override
    public void run(String... args) throws Exception {

        if(qrCodeRepository.count() == 0){
            QrCodeDto qrCode = QrCodeDto
                    .builder()
                    .qrCode("1")
                    .type(QrCodeType.tableNo)
                    .build();
            qrCodeService.save(qrCode);
        }
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
            DiscountSettingsDto settings = DiscountSettingsDto
                    .builder()
                    .isEnabled(true)
                    .discountPercentage(0.5)
                    .ordersRequired(2)
                    .build();
            discountSettingsService.save(settings);
            log.debug("created discount settings");
        }
        if (dishRepository.count() == 0) {
            DishesConfig dishesConfig = YamlLoader.loadDishesConfig("dishes.yaml");
            List<Dish> dishes = dishesConfig.getDishes();
            dishService.saveAll(dishes);
            log.debug("Loaded dishes from YAML file");
        }
    }
}
