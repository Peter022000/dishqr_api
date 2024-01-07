package com.example.DishQR_api.service;


import com.example.DishQR_api.dto.DiscountSettingsDto;
import com.example.DishQR_api.mapper.DiscountSettingsMapper;
import com.example.DishQR_api.repository.DiscountSettingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DiscountSettingsService {

    private final DiscountSettingsRepository discountSettingsRepository;
    private final DiscountSettingsMapper discountSettingsMapper;

    public ResponseEntity<?> save(DiscountSettingsDto discountSettingsDto) {
        return ResponseEntity.ok(discountSettingsRepository.save(discountSettingsMapper.toEntity(discountSettingsDto)));
    }

    public ResponseEntity<?> getDiscountSettings() {
        return ResponseEntity.ok(discountSettingsMapper.toDto(discountSettingsRepository.findAll().get(0)));
    }
}
