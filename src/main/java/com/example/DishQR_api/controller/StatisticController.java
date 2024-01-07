package com.example.DishQR_api.controller;

import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.model.Order;
import com.example.DishQR_api.service.DishService;
import com.example.DishQR_api.service.OrderService;
import com.example.DishQR_api.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path ="/statistics")
@AllArgsConstructor

public class StatisticController {

    private final OrderService orderService;
    private final DishService dishService;
    private final StatisticService statisticService;

    @Operation(summary = "Get ordered dishes statistics")
    @GetMapping("/getStatistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDishStatistics() {
        List<Order> orders = orderService.getAllOrders();
        List<Dish> dishes = dishService.getAllDishes();

        return ResponseEntity.ok(statisticService.calculateDishStatistics(orders,dishes));
    }
}
