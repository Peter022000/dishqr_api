package com.example.DishQR_api.dto;

import com.example.DishQR_api.model.OrderItem;
import com.example.DishQR_api.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Integer tableNo;
    private Double cost;
    private List<OrderItem> order;
    private PaymentMethod paymentMethod;
}
