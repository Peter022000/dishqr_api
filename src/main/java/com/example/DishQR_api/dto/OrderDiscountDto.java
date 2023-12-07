package com.example.DishQR_api.dto;

import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDiscountDto {
    private Boolean isEnabled;
    private Double discountPercentage;
    private Boolean isLoggedIn;
    private Integer ordersRequired;
    private Integer ordersCount;
    private Double oldCost;
    private Boolean isUsed;
}
