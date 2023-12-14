package com.example.dishqr_api.dto;

import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderItemDto {
    private DishDto dishDto;
    private Integer quantity;
    private Double cost;
}
