package com.example.DishQR_api.mapper;

import com.example.DishQR_api.dto.DishDto;
import com.example.DishQR_api.model.Dish;

import java.util.List;
import java.util.stream.Collectors;

public class DishMapper {

    public static DishDto toDto(Dish dish) {
        return DishDto.builder()
                .id(dish.getId())
                .dishType(dish.getDishType())
                .name(dish.getName())
                .price(dish.getPrice())
                .ingredients(dish.getIngredients())
                .build();
    }

    public static Dish toEntity(DishDto dishDto) {
        return Dish.builder()
                .id(dishDto.getId())
                .dishType(dishDto.getDishType())
                .name(dishDto.getName())
                .price(dishDto.getPrice())
                .ingredients(dishDto.getIngredients())
                .build();
    }

    public static List<DishDto> toDtoList(List<Dish> dishes) {
        return dishes.stream()
                .map(DishMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<Dish> toEntityList(List<DishDto> dishDtos) {
        return dishDtos.stream()
                .map(DishMapper::toEntity)
                .collect(Collectors.toList());
    }
}
