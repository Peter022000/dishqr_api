package com.example.DishQR_api.mapper;

import com.example.DishQR_api.dto.CartOrderDto;
import com.example.DishQR_api.model.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CartOrderMapper {

    private final OrderItemMapper orderItemMapper;
    private final OrderDiscountMapper orderDiscountMapper;

    public CartOrderDto toDto(Order order) {
        return CartOrderDto.builder()
                .tableNoId(order.getTableNoId())
                .cost(order.getCost())
                .orderDishesDto(orderItemMapper.toDtoList(order.getOrderDishes()))
                .paymentMethod(order.getPaymentMethod())
                .orderDiscountDto(orderDiscountMapper.toDto(order.getOrderDiscount()))
                .build();
    }

    public Order toEntity(CartOrderDto cartOrderDto) {
        return Order.builder()
                .tableNoId(cartOrderDto.getTableNoId())
                .cost(cartOrderDto.getCost())
                .orderDishes(orderItemMapper.toEntityList(cartOrderDto.getOrderDishesDto()))
                .paymentMethod(cartOrderDto.getPaymentMethod())
                .orderDiscount(orderDiscountMapper.toEntity(cartOrderDto.getOrderDiscountDto()))
                .build();
    }

    public List<CartOrderDto> toDtoList(List<Order> orders) {
        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Order> toEntityList(List<CartOrderDto> cartOrderDtos) {
        return cartOrderDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
