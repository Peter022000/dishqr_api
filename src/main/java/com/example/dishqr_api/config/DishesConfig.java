package com.example.dishqr_api.config;

import com.example.dishqr_api.model.Dish;
import lombok.Data;

import java.util.List;

@Data
public class DishesConfig {
    private List<Dish> dishes;
}
