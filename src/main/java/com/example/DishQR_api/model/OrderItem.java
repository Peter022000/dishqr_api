package com.example.DishQR_api.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderItem {
    private Dish dish;
    private Integer quantity;
    private Double cost;
}
