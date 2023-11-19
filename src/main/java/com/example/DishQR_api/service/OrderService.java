package com.example.DishQR_api.service;


import com.example.DishQR_api.model.*;
import com.example.DishQR_api.repository.DishRepository;
import com.example.DishQR_api.repository.OrderRepository;
import com.example.DishQR_api.repository.QrCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;

    private final QrCodeRepository qrCodeRepository;

    public void sendOrder(Order order) {
        orderRepository.save(order);
    }

    public ResponseEntity<?> addToOrder(Order order, String dishId) {

        Optional<Dish> dish = dishRepository.findById(dishId);
        if(dish.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dish do not exist");
        }

        OrderItem newDishToList = OrderItem
                .builder()
                .dish(dish.get())
                .quantity(1)
                .build();

        if(order.getOrder() == null){
            List<OrderItem> orderItem = List.of(newDishToList);
            order.setOrder(orderItem);
        } else {
            Optional<OrderItem> existingDish = order.getOrder().stream()
                    .filter(r -> r.getDish().getId().equals(dishId))
                    .findFirst();

            if (existingDish.isPresent()) {
                OrderItem dishToUpdate = existingDish.get();
                dishToUpdate.setQuantity(dishToUpdate.getQuantity() + 1);
            } else {
                List<OrderItem> orderItem = order.getOrder();
                orderItem.add(newDishToList);
                order.setOrder(orderItem);
            }
        }
        order.setCost(recalculateCost(order));

        return ResponseEntity.ok(order);
    }

    public ResponseEntity<?> removeFromOrder(Order order, String dishId) {
        Optional<Dish> dish = dishRepository.findById(dishId);
        if(dish.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dish do not exist");
        }

        if(order.getOrder() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is empty");
        }

        Optional<OrderItem> existingDish = order.getOrder().stream()
                .filter(r -> r.getDish().getId().equals(dishId))
                .findFirst();

        if (existingDish.isPresent()) {
            OrderItem dishToRemove = existingDish.get();
            if (existingDish.get().getQuantity() == 1) {
                order.getOrder().remove(dishToRemove);
            } else {
                dishToRemove.setQuantity(dishToRemove.getQuantity() - 1);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order do not include dish");
        }

        order.setCost(recalculateCost(order));

        return ResponseEntity.ok(order);
    }

    public ResponseEntity<?> acceptOrder(Order order) {
        List<OrderItem> orderItems = order.getOrder();

        for (OrderItem orderItem : orderItems) {
            if (!isDishValid(orderItem)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One of the dishes is not valid");
            }
        }

        if(!isTotalCostValid(order)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Total cost is not valid");
        }

        if(!isPaymentMethodValid(order.getPaymentMethod())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment method is not valid");
        }

        if(!isTableNoValid(order.getTableNoId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Table number is not valid");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.toString()))) {
            User user = (User) authentication.getPrincipal();
            order.setUserId(user.getId());
        }

//        if (authentication instanceof AnonymousAuthenticationToken){
//            System.out.println(authentication.getName());
//        }


        order.setDate(LocalDateTime.now());

        return ResponseEntity.ok(orderRepository.save(order));
    }

    private double recalculateCost(Order order) {
        for (OrderItem orderItem : order.getOrder()) {
            orderItem.setCost(roundToTwoDecimalPlaces(orderItem.getDish().getPrice() * orderItem.getQuantity()));
        }

        return roundToTwoDecimalPlaces(order.getOrder().stream()
                .mapToDouble(OrderItem::getCost)
                .sum());
    }


    private boolean isDishValid(OrderItem orderItem) {
        Optional<Dish> optionalDish = dishRepository.findById(orderItem.getDish().getId());

        if (optionalDish.isPresent()) {
            Dish dbDish = optionalDish.get();

            if (!dbDish.getDishType().equals(orderItem.getDish().getDishType()) ||
                    !dbDish.getName().equals(orderItem.getDish().getName()) ||
                    !dbDish.getPrice().equals(orderItem.getDish().getPrice()) ||
                    !dbDish.getIngredients().equals(orderItem.getDish().getIngredients())) {
                return false;
            }

            return orderItem.getCost().equals(dbDish.getPrice() * orderItem.getQuantity());
        }

        return false;
    }

    private boolean isTotalCostValid(Order order) {
        Double dbCost = 0.0;
        for (OrderItem orderItem : order.getOrder()) {
            Optional<Dish> optionalDish = dishRepository.findById(orderItem.getDish().getId());

            if (optionalDish.isPresent()) {
                Dish dbDish = optionalDish.get();
                dbCost += dbDish.getPrice()*orderItem.getQuantity();
            }
        }

        dbCost = roundToTwoDecimalPlaces(dbCost);

        Double calculatedTotalCost = roundToTwoDecimalPlaces(order.getOrder().stream()
                .mapToDouble(OrderItem::getCost)
                .sum());

        return dbCost.equals(calculatedTotalCost);
    }

    private boolean isPaymentMethodValid(PaymentMethod paymentMethod){
        return paymentMethod != null;
    }

    private boolean isTableNoValid(String tableNoId){
        return tableNoId != null && qrCodeRepository.findById(tableNoId).isPresent();
    }

    private double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}
