package com.example.dishqr_api.model;

import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderItem {
    private Dish dish;
    private Integer quantity;
    private Double cost;
}
