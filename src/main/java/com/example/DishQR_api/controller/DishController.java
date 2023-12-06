package com.example.DishQR_api.controller;

import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.service.DishService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping(path = "/modifyDish")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Dish> modifyDish(){
        return dishService.getAllDishes();
    }

    @PostMapping(path = "/addDish")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Dish> addDish(){
        return dishService.getAllDishes();
    }

    @DeleteMapping(path = "/addDish")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Dish> deleteDish(){
        return dishService.getAllDishes();
    }

}
