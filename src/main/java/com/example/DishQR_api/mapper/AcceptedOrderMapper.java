package com.example.DishQR_api.mapper;

import com.example.DishQR_api.dto.AcceptedOrderDto;
import com.example.DishQR_api.dto.CartOrderDto;
import com.example.DishQR_api.model.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AcceptedOrderMapper {

    private final OrderItemMapper orderItemMapper;
    private final OrderDiscountMapper orderDiscountMapper;

    public AcceptedOrderDto toDto(Order order) {
        return AcceptedOrderDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .tableNoId(order.getTableNoId())
                .cost(order.getCost())
                .orderDishesDto(orderItemMapper.toDtoList(order.getOrderDishes()))
                .paymentMethod(order.getPaymentMethod())
                .date(order.getDate())
                .status(order.getStatus())
                .isPayed(order.getIsPayed())
                .orderDiscount(order.getOrderDiscount())
                .build();
    }

    public Order toEntity(AcceptedOrderDto acceptedOrderDto) {
        return Order.builder()
                .id(acceptedOrderDto.getId())
                .userId(acceptedOrderDto.getUserId())
                .tableNoId(acceptedOrderDto.getTableNoId())
                .cost(acceptedOrderDto.getCost())
                .orderDishes(orderItemMapper.toEntityList(acceptedOrderDto.getOrderDishesDto()))
                .paymentMethod(acceptedOrderDto.getPaymentMethod())
                .date(acceptedOrderDto.getDate())
                .status(acceptedOrderDto.getStatus())
                .isPayed(acceptedOrderDto.getIsPayed())
                .orderDiscount(acceptedOrderDto.getOrderDiscount())
                .build();
    }

    public List<AcceptedOrderDto> toDtoList(List<Order> orders) {
        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Order> toEntityList(List<AcceptedOrderDto> acceptedOrderDtos) {
        return acceptedOrderDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
