package com.example.DishQR_api.service;


import com.example.DishQR_api.model.*;
import com.example.DishQR_api.repository.DishRepository;
import com.example.DishQR_api.repository.OrderRepository;
import com.example.DishQR_api.repository.QrCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<?> addToOrder(Order order, Dish newDish) {

        OrderItem newDishToList = OrderItem
                .builder()
                .dish(newDish)
                .quantity(1)
                .build();

        if(order.getOrder() == null){
            List<OrderItem> orderItem = List.of(newDishToList);
            order = order.toBuilder().order(orderItem).build();
        } else {
            order = addDish(order,newDishToList);
        }
        order = order.toBuilder().cost(recalculateCost(order)).build();

        return ResponseEntity.ok(order);
    }

    public Order addDish(Order order, OrderItem newDishToList){
        Optional<OrderItem> existingDish = order.getOrder().stream()
                .filter(r -> r.getDish().getId().equals(newDishToList.getDish().getId()))
                .findFirst();

        if (existingDish.isPresent()) {
            OrderItem dishToUpdate = existingDish.get();

            dishToUpdate = dishToUpdate.toBuilder().quantity(dishToUpdate.getQuantity()+1).build();

            List<OrderItem> orderItems = order.getOrder();
            orderItems.set(orderItems.indexOf(existingDish.get()), dishToUpdate);
            order = order.toBuilder().order(orderItems).build();

        } else {
            List<OrderItem> orderItems = order.getOrder();
            orderItems.add(newDishToList);
            order.toBuilder().order(orderItems).build();
        }
        return order;
    }

    public ResponseEntity<?> removeFromOrder(Order order, Dish dish) {
        Optional<OrderItem> existingDish = order.getOrder().stream()
                .filter(r -> r.getDish().getId().equals(dish.getId()))
                .findFirst();

        if (existingDish.isPresent()) {
            order = decrementQuantity(order, existingDish.get());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order do not include dish");
        }

        return ResponseEntity.ok(order.toBuilder().cost(recalculateCost(order)).build());
    }

    public Order decrementQuantity(Order order, OrderItem dishToRemove){
        List<OrderItem> orderItems = order.getOrder();

        if (dishToRemove.getQuantity() == 1) {
            orderItems.remove(dishToRemove);
        } else {
            OrderItem dishToRemoveAfter = dishToRemove.toBuilder().quantity(dishToRemove.getQuantity()-1).build();
            orderItems.set(orderItems.indexOf(dishToRemove), dishToRemoveAfter);
        }
        return order.toBuilder().order(orderItems).build();
    }

    public ResponseEntity<?> acceptOrder(Order order) {
        List<OrderItem> orderItems = order.getOrder();

        if(validateDishesInOrder(orderItems)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One of the dishes is not valid");
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

        order = order.toBuilder().status(StatusType.NEW).date(LocalDateTime.now()).build();

        return ResponseEntity.ok(orderRepository.save(order));
    }

    public boolean validateDishesInOrder(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            Optional<Dish> dbDish = dishRepository.findById(orderItem.getDish().getId());
            if(dbDish.isEmpty()){
                return false;
            }
            if (!isDishValid(orderItem, dbDish.get())) {
                return false;
            }
        }
        return true;
    }

    public double recalculateCost(Order order) {
        for (OrderItem orderItem : order.getOrder()) {
            orderItem.setCost(roundToTwoDecimalPlaces(orderItem.getDish().getPrice() * orderItem.getQuantity()));
        }

        return roundToTwoDecimalPlaces(order.getOrder().stream()
                .mapToDouble(OrderItem::getCost)
                .sum());
    }


    public boolean isDishValid(OrderItem orderItem, Dish dbDish) {
        return isDishTypeValid(orderItem.getDish().getDishType().toString(), dbDish.getDishType().toString()) &&
                isDishNameValid(orderItem.getDish().getName(),dbDish.getName()) &&
                isDishPriceValid(orderItem.getDish().getPrice(), dbDish.getPrice()) &&
                isDishIngredientsValid(orderItem.getDish().getIngredients(), dbDish.getIngredients()) &&
                isDishCostValid(orderItem.getCost(), orderItem.getQuantity(), dbDish.getPrice());
    }

    public boolean isDishTypeValid(String orderItemType, String dbDishType){
        return orderItemType.equals(dbDishType);
    }

    public boolean isDishNameValid(String orderItemName, String dbDishName){
        return orderItemName.equals(dbDishName);
    }

    public boolean isDishPriceValid(Double orderItemPrice, Double dbDishPrice){
        return orderItemPrice.equals(dbDishPrice);
    }

    public boolean isDishIngredientsValid(List<String> orderItemIngredients, List<String> dbDishIngredients){
        return orderItemIngredients.equals(dbDishIngredients);
    }

    public boolean isDishCostValid(Double orderItemCost, Integer orderItemQuantity, Double dbDishPrice){
        return orderItemCost.equals(orderItemQuantity * dbDishPrice);
    }

    public boolean isTotalCostValid(Order order) {
        Double dbCost = 0.0;
        for (OrderItem orderItem : order.getOrder()) {
            Optional<Dish> optionalDbDish = dishRepository.findById(orderItem.getDish().getId());

            if (optionalDbDish.isPresent()) {
                Dish dbDish = optionalDbDish.get();
                dbCost += dbDish.getPrice()*orderItem.getQuantity();
            }
        }

        dbCost = roundToTwoDecimalPlaces(dbCost);

        Double calculatedTotalCost = roundToTwoDecimalPlaces(order.getOrder().stream()
                .mapToDouble(OrderItem::getCost)
                .sum());

        return dbCost.equals(calculatedTotalCost);
    }

    public boolean isPaymentMethodValid(PaymentMethod paymentMethod){
        return paymentMethod != null;
    }

    public boolean isTableNoValid(String tableNoId){
        return tableNoId != null && qrCodeRepository.findById(tableNoId).isPresent();
    }

    public double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public ResponseEntity<?> getUserHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Order> orderHistory = orderRepository.findAllByUserId(user.getId());
        return ResponseEntity.ok(orderHistory);
    }

    public ResponseEntity<?> getOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }
}
