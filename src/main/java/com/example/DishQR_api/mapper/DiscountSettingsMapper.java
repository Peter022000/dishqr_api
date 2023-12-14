package com.example.DishQR_api.mapper;

import com.example.DishQR_api.dto.DiscountSettingsDto;

import com.example.DishQR_api.model.DiscountSettings;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DiscountSettingsMapper {

    public DiscountSettingsDto toDto(DiscountSettings discountSettings) {
        return DiscountSettingsDto.builder()
                .id(discountSettings.getId())
                .discountPercentage(discountSettings.getDiscountPercentage())
                .isEnabled(discountSettings.getIsEnabled())
                .ordersRequired(discountSettings.getOrdersRequired())
                .build();
    }

    public DiscountSettings toEntity(DiscountSettingsDto discountSettingsDto) {
        return DiscountSettings.builder()
                .id(discountSettingsDto.getId())
                .discountPercentage(discountSettingsDto.getDiscountPercentage())
                .ordersRequired(discountSettingsDto.getOrdersRequired())
                .isEnabled(discountSettingsDto.getIsEnabled())
                .build();
    }

    public List<DiscountSettingsDto> toDtoList(List<DiscountSettings> dishes) {
        return dishes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<DiscountSettings> toEntityList(List<DiscountSettingsDto> dishDtos) {
        return dishDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
