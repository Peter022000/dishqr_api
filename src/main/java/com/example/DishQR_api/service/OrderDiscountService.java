package com.example.DishQR_api.service;


import com.example.DishQR_api.dto.DiscountSettingsDto;
import com.example.DishQR_api.dto.OrderDiscountDto;
import com.example.DishQR_api.dto.OrderDto;
import com.example.DishQR_api.model.OrderDiscount;
import com.example.DishQR_api.model.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderDiscountService {

    OrderService orderService;

    public OrderDto checkOrderDiscount(Boolean isLoggedIn, DiscountSettingsDto discountSettingsDto, OrderDto orderDto) {
        if(isLoggedIn) {
            return orderDto.toBuilder()
                    .orderDiscountDto(setOrderDiscount(orderDto.getOrderDiscountDto(), orderService.getNumberOfOrders(), discountSettingsDto, true))
                    .build();
        } else {
            return orderDto.toBuilder()
                    .orderDiscountDto(setOrderDiscount(orderDto.getOrderDiscountDto(), -1, discountSettingsDto, false))
                    .build();
        }
    }

    public OrderDiscountDto setOrderDiscount(OrderDiscountDto orderDiscountDto, Integer numberOfOrders, DiscountSettingsDto discountSettingsDto, Boolean isLoggedIn) {

        if (isLoggedIn) {
            orderDiscountDto = OrderDiscountDto.builder()
                    .isLoggedIn(true).build();
        } else {
            orderDiscountDto = OrderDiscountDto.builder()
                    .isLoggedIn(false).build();
        }

        orderDiscountDto = orderDiscountDto.toBuilder()
                .discountPercentage(discountSettingsDto.getDiscountPercentage())
                .isEnabled(discountSettingsDto.getIsEnabled())
                .ordersCount(numberOfOrders)
                .ordersRequired(discountSettingsDto.getOrdersRequired())
                .build();

        if(orderDiscountDto.getIsLoggedIn()
                && orderDiscountDto.getOrdersCount() > 0
                && orderDiscountDto.getIsEnabled()
                && orderDiscountDto.getOrdersRequired() % orderDiscountDto.getOrdersCount() == 0) {
            orderDiscountDto = orderDiscountDto.toBuilder()
                    .isUsed(true)
                    .build();
        } else {
            orderDiscountDto = orderDiscountDto.toBuilder()
                    .isUsed(false)
                    .build();
        }

        return orderDiscountDto;
    }
}
