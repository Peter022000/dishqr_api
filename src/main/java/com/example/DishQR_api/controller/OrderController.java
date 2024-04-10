package com.example.DishQR_api.controller;

import com.example.DishQR_api.dto.*;
import com.example.DishQR_api.mapper.DiscountSettingsMapper;
import com.example.DishQR_api.mapper.DishMapper;
import com.example.DishQR_api.model.*;
import com.example.DishQR_api.repository.DishRepository;
import com.example.DishQR_api.service.OrderDiscountService;
import com.example.DishQR_api.service.OrderService;
import com.example.DishQR_api.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path ="/order")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDiscountService orderDiscountService;

    private final RecommendationService recommendationService;

    private final DishRepository dishRepository;
    private final DishMapper dishMapper;
    private final DiscountSettingsMapper discountSettingsMapper;

    @Operation(summary = "Accept new order")
    @PostMapping(path = "/acceptOrder")
    public ResponseEntity<?> acceptOrder(@RequestBody(required=false) CartOrderDto cartOrderDto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = null;

        DiscountSettingsDto discountSettingsDto = discountSettingsMapper.toDto(orderService.getDiscountSettings());

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.toString()))) {
            User user = (User) authentication.getPrincipal();
            userId = user.getId();
        }

        Boolean isLoggedIn = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.toString()));

        OrderDiscountDto orderDiscountDto = orderDiscountService.checkOrderDiscount(isLoggedIn, userId, discountSettingsDto);

        cartOrderDto = cartOrderDto.toBuilder().orderDiscountDto(orderDiscountDto).build();

        return orderService.acceptOrder(cartOrderDto, userId);
    }

    @Operation(summary = "Get all orders")
    @GetMapping(path = "/getOrders")
    public ResponseEntity<?> getOrders(){
        return orderService.getOrders();
    }

    @Operation(summary = "Add dish to order")
    @PostMapping(path = "/addToOrder")
    public ResponseEntity<?> addToOrder(@RequestBody(required=false) CartOrderDto cartOrderDto, @RequestParam String dishId){

        Optional<Dish> dish = dishRepository.findById(dishId);
        if(dish.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dish do not exist");
        }

        DishDto dishDto = dishMapper.toDto(dish.get());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = null;

        Boolean isLoggedIn = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.toString()));

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.toString()))) {
            User user = (User) authentication.getPrincipal();
            userId = user.getId();
        }

        DiscountSettingsDto discountSettingsDto = discountSettingsMapper.toDto(orderService.getDiscountSettings());

        OrderDiscountDto orderDiscountDto = orderDiscountService.checkOrderDiscount(isLoggedIn, userId, discountSettingsDto);

        cartOrderDto = cartOrderDto.toBuilder().orderDiscountDto(orderDiscountDto).build();

        return orderService.addToOrder(cartOrderDto, dishDto);
    }

    @Operation(summary = "Remove dish from order")
    @PostMapping(path = "/removeFromOrder")
    public ResponseEntity<?> removeFromOrder(@RequestBody(required=false) CartOrderDto cartOrderDto, @RequestParam String dishId){

        Optional<Dish> dish = dishRepository.findById(dishId);
        if(dish.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dish do not exist");
        }

        if(cartOrderDto.getOrderDishesDto() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is empty");
        }

        DishDto dishDto = dishMapper.toDto(dish.get());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        DiscountSettingsDto discountSettingsDto = discountSettingsMapper.toDto(orderService.getDiscountSettings());

        Boolean isLoggedIn = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.toString()));

        String userId = null;

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.toString()))) {
            User user = (User) authentication.getPrincipal();
            userId = user.getId();
        }

        OrderDiscountDto orderDiscountDto = orderDiscountService.checkOrderDiscount(isLoggedIn, userId, discountSettingsDto);

        cartOrderDto = cartOrderDto.toBuilder().orderDiscountDto(orderDiscountDto).build();

        return orderService.removeFromOrder(cartOrderDto, dishDto);
    }

    @Operation(summary = "Get users number of orders")
    @GetMapping(path = "/getUserNumberOfOrders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserNumberOfOrders(){
        return orderService.getUserNumberOfOrders();
    }

    @Operation(summary = "Get user orders history")
    @GetMapping(path = "/getUserHistory")
    @PreAuthorize("hasRole('USER')")
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

    @Operation(summary = "Get orders by status")
    @GetMapping("/getOrdersByStatus")
    public ResponseEntity<?> getOrdersByStatus(@RequestParam StatusType statusType) {
        List<AcceptedOrderDto> orders = orderService.getOrdersByStatus(statusType);
        orders.sort(Comparator.comparing(AcceptedOrderDto::getDate).reversed());
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Get orders by status accepted today")
    @GetMapping("/getOrdersByStatusToday")
    public ResponseEntity<?> getOrdersByStatusAndTime(@RequestParam StatusType statusType) {
        List<AcceptedOrderDto> orders = orderService.getOrdersByStatusToday(statusType);
        orders.sort(Comparator.comparing(AcceptedOrderDto::getDate).reversed());
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Change order status")
    @PostMapping("/changeOrderStatus")
    public ResponseEntity<?> changeOrderStatus(@RequestBody ChangeOrderStatusRequest request) {
        if(request.getNewStatus() == StatusType.COMPLETED && !request.getAcceptedOrderDto().getIsPayed()) {
            return ResponseEntity.badRequest().body("Order must by payed to complete");
        }
        return orderService.changeOrderStatus(request.getAcceptedOrderDto().getId(), request.getNewStatus());
    }

    @Operation(summary = "Set order is payed")
    @PostMapping("/setIsPayed")
    public ResponseEntity<?> setPayed(@RequestBody AcceptedOrderDto acceptedOrderDto) {
        return orderService.setPayed(acceptedOrderDto);
    }

    @Operation(summary = "Get recommended dish for user")
    @GetMapping(path = "/getRecommendation")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getRecommendation(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();
        String userId = user.getId();

        if(orderService.getNumberOfOrders() == 0) {
            ResponseEntity.badRequest().body("History is empty");
        }

        int topIngredientsCount = 5;
        int topDishesCount = 5;

        return ResponseEntity.ok(recommendationService.getRecommendedDishesForCustomer(userId, topIngredientsCount, topDishesCount));
    }
}
