package com.example.DishQR_api.mapper;

import com.example.DishQR_api.dto.OrderDiscountDto;
import com.example.DishQR_api.dto.OrderDto;
import com.example.DishQR_api.model.Order;
import com.example.DishQR_api.model.OrderDiscount;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderDiscountMapper {


    public OrderDiscountDto toDto(OrderDiscount orderDiscount) {
        return OrderDiscountDto.builder()
                .discountPercentage(orderDiscount.getDiscountPercentage())
                .isUsed(orderDiscount.getIsUsed())
                .oldCost(orderDiscount.getOldCost())
                .build();
    }

    public OrderDiscount toEntity(OrderDiscountDto orderDiscountDto) {
        return OrderDiscount.builder()
                .discountPercentage(orderDiscountDto.getDiscountPercentage())
                .isUsed(orderDiscountDto.getIsUsed())
                .oldCost(orderDiscountDto.getOldCost())
                .build();
    }

    public List<OrderDiscountDto> toDtoList(List<OrderDiscount> OrderDiscount) {
        return OrderDiscount.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<OrderDiscount> toEntityList(List<OrderDiscountDto> OrderDiscountDto) {
        return OrderDiscountDto.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
