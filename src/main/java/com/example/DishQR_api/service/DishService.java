package com.example.DishQR_api.service;


import com.example.DishQR_api.mapper.DishMapper;
import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.repository.DishRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final DishMapper dishMapper;

    public ResponseEntity<?> getAllDishes() {
        List<Dish> dishes = dishRepository.findAll();
        return ResponseEntity.ok(dishMapper.toDtoList(dishes));
    }

    public Long count() {
        return dishRepository.count();
    }

    public void saveAll(List<Dish> dishes) {
        dishRepository.saveAll(dishes);
    }
}
