package com.example.DishQR_api.controller;

import com.example.DishQR_api.dto.DishDto;
import com.example.DishQR_api.mapper.DishMapper;
import com.example.DishQR_api.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path ="/dishes")
@AllArgsConstructor
public class DishController {

    private final DishService dishService;
    private final DishMapper dishMapper;

    @Operation(summary = "Get all dishes")
    @GetMapping(path = "/getAllDishes")
    public ResponseEntity<?> getAllDishes(){
        return ResponseEntity.ok(dishMapper.toDtoList(dishService.getAllDishes()));
    }

    @Operation(summary = "Get dish by id")
    @GetMapping(path = "/getDishById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDishById(@PathVariable String id){
        return dishService.getDishById(id);
    }

    @Operation(summary = "Update dish by id")
    @PostMapping(path = "/updateDish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDish(@RequestBody DishDto dishDto){
        return dishService.updateDish(dishDto);
    }

    @Operation(summary = "Delete dish by id")
    @DeleteMapping(path = "/deleteDish/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDish(@PathVariable String id){
        return dishService.deleteDish(id);
    }

    @Operation(summary = "Add dish")
    @PostMapping(path = "/addDish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addDish(@RequestBody DishDto dishDto){
        return dishService.addDish(dishDto);
    }
}
