package com.example.DishQR_api.controller;

import com.example.DishQR_api.model.Order;
import com.example.DishQR_api.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path ="/order")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(path = "/sendOrder")
    public ResponseEntity<Order> sendOrder(@RequestBody Order order){
        orderService.sendOrder(order);

        return ResponseEntity.ok(order);
    }
}
