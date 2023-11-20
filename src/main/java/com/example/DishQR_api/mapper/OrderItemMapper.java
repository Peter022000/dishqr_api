package com.example.DishQR_api.mapper;

import com.example.DishQR_api.dto.OrderItemDto;
import com.example.DishQR_api.model.OrderItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderItemMapper {

    private final DishMapper dishMapper;

    public OrderItemDto toDto(OrderItem orderItem) {
        return OrderItemDto.builder()
                .dish(dishMapper.toDto(orderItem.getDish()))
                .quantity(orderItem.getQuantity())
                .cost(orderItem.getCost())
                .build();
    }

    public OrderItem toEntity(OrderItemDto orderItemDto) {
        return OrderItem.builder()
                .dish(dishMapper.toEntity(orderItemDto.getDish()))
                .quantity(orderItemDto.getQuantity())
                .cost(orderItemDto.getCost())
                .build();
    }

    public List<OrderItemDto> toDtoList(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<OrderItem> toEntityList(List<OrderItemDto> orderItemDtos) {
        return orderItemDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
