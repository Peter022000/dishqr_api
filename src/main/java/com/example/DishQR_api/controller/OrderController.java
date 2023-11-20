package com.example.DishQR_api.controller;

import com.example.DishQR_api.dto.DishDto;
import com.example.DishQR_api.dto.OrderDto;
import com.example.DishQR_api.dto.OrderItemDto;
import com.example.DishQR_api.mapper.DishMapper;
import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.model.Order;
import com.example.DishQR_api.model.OrderItem;
import com.example.DishQR_api.repository.DishRepository;
import com.example.DishQR_api.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path ="/order")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;

    @PostMapping(path = "/acceptOrder")
    public ResponseEntity<?> acceptOrder(@RequestBody(required=false) OrderDto orderDto){

        return orderService.acceptOrder(orderDto);
    }

    @GetMapping(path = "/getOrders")
    public ResponseEntity<?> getOrders(){
        return orderService.getOrders();
    }

    @PostMapping(path = "/addToOrder")
    public ResponseEntity<?> addToOrder(@RequestBody(required=false) OrderDto orderDto, @RequestParam String dishId){

        Optional<Dish> dish = dishRepository.findById(dishId);
        if(dish.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dish do not exist");
        }

        DishDto dishDto = dishMapper.toDto(dish.get());

        return orderService.addToOrder(orderDto, dishDto);
    }

    @PostMapping(path = "/removeFromOrder")
    public ResponseEntity<?> removeFromOrder(@RequestBody(required=false) OrderDto orderDto, @RequestParam String dishId){

        Optional<Dish> dish = dishRepository.findById(dishId);
        if(dish.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dish do not exist");
        }

        if(orderDto.getOrder() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is empty");
        }

        DishDto dishDto = dishMapper.toDto(dish.get());

        return orderService.removeFromOrder(orderDto, dishDto);
    }

    @GetMapping(path = "/getUserHistory")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> getUserHistory(){
        return orderService.getUserHistory();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleException(HttpMessageNotReadableException ex) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", ex.getLocalizedMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

}
