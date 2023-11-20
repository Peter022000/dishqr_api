package com.example.DishQR_api.mapper;

import com.example.DishQR_api.dto.OrderDto;
import com.example.DishQR_api.model.Order;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDto toDto(Order order) {
        return OrderDto.builder()
                .userId(order.getUserId())
                .tableNoId(order.getTableNoId())
                .cost(order.getCost())
                .order(OrderItemMapper.toDtoList(order.getOrder()))
                .paymentMethod(order.getPaymentMethod())
                .date(order.getDate())
                .status(order.getStatus())
                .build();
    }

    public static Order toEntity(OrderDto orderDto) {
        return Order.builder()
                .userId(orderDto.getUserId())
                .tableNoId(orderDto.getTableNoId())
                .cost(orderDto.getCost())
                .order(OrderItemMapper.toEntityList(orderDto.getOrder()))
                .paymentMethod(orderDto.getPaymentMethod())
                .date(orderDto.getDate())
                .status(orderDto.getStatus())
                .build();
    }

    public static List<OrderDto> toDtoList(List<Order> orders) {
        return orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<Order> toEntityList(List<OrderDto> orderDtos) {
        return orderDtos.stream()
                .map(OrderMapper::toEntity)
                .collect(Collectors.toList());
    }
}
