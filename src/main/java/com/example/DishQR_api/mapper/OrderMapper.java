package com.example.DishQR_api.mapper;

import com.example.DishQR_api.dto.OrderDto;
import com.example.DishQR_api.model.Order;
import com.example.DishQR_api.model.OrderDiscount;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;
    private final OrderDiscountMapper orderDiscountMapper;

    public OrderDto toDto(Order order) {
        return OrderDto.builder()
                .tableNoId(order.getTableNoId())
                .cost(order.getCost())
                .orderDto(orderItemMapper.toDtoList(order.getOrder()))
                .paymentMethod(order.getPaymentMethod())
                .orderDiscountDto(orderDiscountMapper.toDto(order.getOrderDiscount()))
                .build();
    }

    public Order toEntity(OrderDto orderDto) {
        return Order.builder()
                .tableNoId(orderDto.getTableNoId())
                .cost(orderDto.getCost())
                .order(orderItemMapper.toEntityList(orderDto.getOrderDto()))
                .paymentMethod(orderDto.getPaymentMethod())
                .orderDiscount(orderDiscountMapper.toEntity(orderDto.getOrderDiscountDto()))
                .build();
    }

    public List<OrderDto> toDtoList(List<Order> orders) {
        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Order> toEntityList(List<OrderDto> orderDtos) {
        return orderDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
