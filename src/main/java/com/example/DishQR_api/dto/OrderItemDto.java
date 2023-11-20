package com.example.DishQR_api.dto;

import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderItemDto {
    private DishDto dish;
    private Integer quantity;
    private Double cost;
}
