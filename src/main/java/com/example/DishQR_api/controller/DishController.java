package com.example.DishQR_api.controller;

import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.service.DishService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path ="/dishes")
@AllArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping(path = "/getAllDishes")
    public List<Dish> getAllDishes(){
        return dishService.getAllDishes();
    }
}
