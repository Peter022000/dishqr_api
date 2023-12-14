package com.example.dishqr_api.service;


import com.example.dishqr_api.model.DiscountSettings;
import com.example.dishqr_api.repository.DiscountSettingsRepository;
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
