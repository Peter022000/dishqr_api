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
    UserService userService;

    public OrderDiscountDto checkOrderDiscount(Boolean isLoggedIn, String userId, DiscountSettingsDto discountSettingsDto) {
        System.out.println(userService.getUserLastDiscountOrderNumber(userId));
        System.out.println(orderService.getNumberOfOrders());
        System.out.println(orderService.getNumberOfOrders()-userService.getUserLastDiscountOrderNumber(userId));
        if(isLoggedIn) {
            return setOrderDiscount(orderService.getNumberOfOrders()-userService.getUserLastDiscountOrderNumber(userId), discountSettingsDto, true);
        } else {
            return setOrderDiscount( -1, discountSettingsDto, false);
        }
    }

    public OrderDiscountDto setOrderDiscount(Integer numberOfOrders, DiscountSettingsDto discountSettingsDto, Boolean isLoggedIn) {

        OrderDiscountDto orderDiscountDto;

        if (isLoggedIn) {
            orderDiscountDto = OrderDiscountDto.builder()
                    .isLoggedIn(true)
                    .discountPercentage(discountSettingsDto.getDiscountPercentage())
                    .isEnabled(discountSettingsDto.getIsEnabled())
                    .ordersCount(numberOfOrders)
                    .ordersRequired(discountSettingsDto.getOrdersRequired())
                    .build();
        } else {
            orderDiscountDto = OrderDiscountDto.builder()
                    .isLoggedIn(false)
                    .discountPercentage(discountSettingsDto.getDiscountPercentage())
                    .isEnabled(discountSettingsDto.getIsEnabled())
                    .ordersCount(numberOfOrders)
                    .ordersRequired(discountSettingsDto.getOrdersRequired())
                    .build();
        }

        if(orderDiscountDto.getIsLoggedIn()
                && orderDiscountDto.getOrdersCount() > 0
                && orderDiscountDto.getIsEnabled()
                && orderDiscountDto.getOrdersCount() % orderDiscountDto.getOrdersRequired() == 0) {
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
