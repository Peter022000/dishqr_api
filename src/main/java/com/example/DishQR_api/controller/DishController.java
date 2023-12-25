package com.example.DishQR_api.controller;

import com.example.DishQR_api.dto.DishDto;
import com.example.DishQR_api.service.DishService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path ="/dishes")
@AllArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping(path = "/getAllDishes")
    public ResponseEntity<?> getAllDishes(){
        return dishService.getAllDishes();
    }

    @GetMapping(path = "/getDishById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDishById(@PathVariable String id){
        return dishService.getDishById(id);
    }

    @PostMapping(path = "/updateDish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDish(@RequestBody DishDto dishDto){
        return dishService.updateDish(dishDto);
    }

    @DeleteMapping(path = "/deleteDish/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDish(@PathVariable String id){
        return dishService.deleteDish(id);
    }

    @PostMapping(path = "/addDish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addDish(@RequestBody DishDto dishDto){
        return dishService.addDish(dishDto);
    }
}
