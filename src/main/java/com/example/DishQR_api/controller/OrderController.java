package com.example.DishQR_api.controller;

import com.example.DishQR_api.dto.DiscountSettingsDto;
import com.example.DishQR_api.dto.DishDto;
import com.example.DishQR_api.dto.OrderDiscountDto;
import com.example.DishQR_api.dto.OrderDto;
import com.example.DishQR_api.mapper.DiscountSettingsMapper;
import com.example.DishQR_api.mapper.DishMapper;
import com.example.DishQR_api.mapper.OrderDiscountMapper;
import com.example.DishQR_api.model.*;
import com.example.DishQR_api.repository.DishRepository;
import com.example.DishQR_api.service.OrderDiscountService;
import com.example.DishQR_api.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path ="/order")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDiscountService orderDiscountService;
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;
    private final DiscountSettingsMapper discountSettingsMapper;

    @PostMapping(path = "/acceptOrder")
    public ResponseEntity<?> acceptOrder(@RequestBody(required=false) OrderDto orderDto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = null;

        DiscountSettingsDto discountSettingsDto = discountSettingsMapper.toDto(orderService.getDiscountSettings());

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.toString()))) {
            User user = (User) authentication.getPrincipal();
            userId = user.getId();
        }

        Boolean isLoggedIn = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.toString()));

        OrderDiscountDto orderDiscountDto = orderDiscountService.checkOrderDiscount(isLoggedIn, discountSettingsDto);

        orderDto = orderDto.toBuilder().orderDiscountDto(orderDiscountDto).build();

        return orderService.acceptOrder(orderDto, userId);
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Boolean isLoggedIn = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.toString()));

        DiscountSettingsDto discountSettingsDto = discountSettingsMapper.toDto(orderService.getDiscountSettings());

        OrderDiscountDto orderDiscountDto = orderDiscountService.checkOrderDiscount(isLoggedIn, discountSettingsDto);

        orderDto = orderDto.toBuilder().orderDiscountDto(orderDiscountDto).build();

        return orderService.addToOrder(orderDto, dishDto);
    }

    @PostMapping(path = "/removeFromOrder")
    public ResponseEntity<?> removeFromOrder(@RequestBody(required=false) OrderDto orderDto, @RequestParam String dishId){

        Optional<Dish> dish = dishRepository.findById(dishId);
        if(dish.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dish do not exist");
        }

        if(orderDto.getOrderDishesDto() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is empty");
        }

        DishDto dishDto = dishMapper.toDto(dish.get());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        DiscountSettingsDto discountSettingsDto = discountSettingsMapper.toDto(orderService.getDiscountSettings());

        Boolean isLoggedIn = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.toString()));

        OrderDiscountDto orderDiscountDto = orderDiscountService.checkOrderDiscount(isLoggedIn, discountSettingsDto);

        orderDto = orderDto.toBuilder().orderDiscountDto(orderDiscountDto).build();

        return orderService.removeFromOrder(orderDto, dishDto);
    }

    @GetMapping(path = "/getUserNumberOfOrders")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> getUserNumberOfOrders(){
        return orderService.getUserNumberOfOrders();
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
