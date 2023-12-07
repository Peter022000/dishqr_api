package com.example.DishQR_api.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DiscountSettingsDto {
    @Id
    private String id;
    private Boolean isEnabled;
    private Integer ordersRequired;
    private Double discountPercentage;
}
