package com.example.dishqr_api.model;

import lombok.*;

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
