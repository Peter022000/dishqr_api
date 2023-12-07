package com.example.DishQR_api.service;


import com.example.DishQR_api.model.DiscountSettings;
import com.example.DishQR_api.repository.DiscountSettingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DiscountSettingsService {

    private final DiscountSettingsRepository discountSettingsRepository;

    public void save(DiscountSettings discountSettings) {
        discountSettingsRepository.save(discountSettings);
    }
}
