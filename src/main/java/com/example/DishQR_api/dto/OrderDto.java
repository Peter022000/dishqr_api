package com.example.DishQR_api.dto;

import com.example.DishQR_api.model.OrderDiscount;
import com.example.DishQR_api.model.PaymentMethod;
import com.example.DishQR_api.model.StatusType;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDto {
    private String tableNoId;
    private Double cost;
    private List<OrderItemDto> orderDishesDto;
    private PaymentMethod paymentMethod;
    private OrderDiscountDto orderDiscountDto;
}
