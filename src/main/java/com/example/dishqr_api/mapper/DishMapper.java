package com.example.dishqr_api.mapper;

import com.example.dishqr_api.dto.DishDto;
import com.example.dishqr_api.model.Dish;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DishMapper {

    public DishDto toDto(Dish dish) {
        return DishDto.builder()
                .id(dish.getId())
                .dishType(dish.getDishType())
                .name(dish.getName())
                .price(dish.getPrice())
                .ingredients(dish.getIngredients())
                .build();
    }

    public Dish toEntity(DishDto dishDto) {
        return Dish.builder()
                .id(dishDto.getId())
                .dishType(dishDto.getDishType())
                .name(dishDto.getName())
                .price(dishDto.getPrice())
                .ingredients(dishDto.getIngredients())
                .build();
    }

    public List<DishDto> toDtoList(List<Dish> dishes) {
        return dishes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Dish> toEntityList(List<DishDto> dishDtos) {
        return dishDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
