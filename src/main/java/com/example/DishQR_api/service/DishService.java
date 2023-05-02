package com.example.DishQR_api.service;


import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.model.Type;
import com.example.DishQR_api.repository.DishRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DishService {

    private final DishRepository dishRepository;

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }
}
