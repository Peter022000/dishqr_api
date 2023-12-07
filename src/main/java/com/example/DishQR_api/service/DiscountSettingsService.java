package com.example.DishQR_api.service;


import com.example.DishQR_api.model.DiscountSettings;
import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.repository.DiscountSettingsRepository;
import com.example.DishQR_api.repository.DishRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DiscountSettingsService {

    private final DiscountSettingsRepository discountSettingsRepository;

    public DiscountSettings save(DiscountSettings discountSettings) {
        return discountSettingsRepository.save(discountSettings);
    }
}
