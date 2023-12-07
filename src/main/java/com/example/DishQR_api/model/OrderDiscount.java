package com.example.DishQR_api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDiscount {
    private Boolean isUsed;
    private Double discountPercentage;
    private Double oldCost;
}
