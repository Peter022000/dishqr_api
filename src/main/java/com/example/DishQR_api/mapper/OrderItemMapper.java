package com.example.DishQR_api.mapper;

import com.example.DishQR_api.dto.OrderItemDto;
import com.example.DishQR_api.model.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderItemMapper {

    public static OrderItemDto toDto(OrderItem orderItem) {
        return OrderItemDto.builder()
                .dish(DishMapper.toDto(orderItem.getDish()))
                .quantity(orderItem.getQuantity())
                .cost(orderItem.getCost())
                .build();
    }

    public static OrderItem toEntity(OrderItemDto orderItemDto) {
        return OrderItem.builder()
                .dish(DishMapper.toEntity(orderItemDto.getDish()))
                .quantity(orderItemDto.getQuantity())
                .cost(orderItemDto.getCost())
                .build();
    }

    public static List<OrderItemDto> toDtoList(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<OrderItem> toEntityList(List<OrderItemDto> orderItemDtos) {
        return orderItemDtos.stream()
                .map(OrderItemMapper::toEntity)
                .collect(Collectors.toList());
    }
}
