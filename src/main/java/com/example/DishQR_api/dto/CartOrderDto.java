package com.example.DishQR_api.dto;

import com.example.DishQR_api.model.PaymentMethod;
import lombok.*;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartOrderDto {
    private String tableNoId;
    private Double cost;
    private List<OrderItemDto> orderDishesDto;
    private PaymentMethod paymentMethod;
    private OrderDiscountDto orderDiscountDto;
}
