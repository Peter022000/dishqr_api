package com.example.DishQR_api.service;


import com.example.DishQR_api.dto.DishDto;
import com.example.DishQR_api.dto.OrderDto;
import com.example.DishQR_api.dto.OrderItemDto;
import com.example.DishQR_api.mapper.OrderMapper;
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
    private final OrderMapper orderMapper;

    public ResponseEntity<?> addToOrder(OrderDto orderDto, DishDto newDishDto) {

        OrderItemDto newDishToList = OrderItemDto
                .builder()
                .dish(newDishDto)
                .quantity(1)
                .build();

        if(orderDto.getOrder() == null){
            List<OrderItemDto> orderItem = List.of(newDishToList);
            orderDto = orderDto.toBuilder().order(orderItem).build();
        } else {
            orderDto = addDish(orderDto,newDishToList);
        }
        orderDto = orderDto.toBuilder().cost(recalculateCost(orderDto)).build();

        return ResponseEntity.ok(orderDto);
    }

    public OrderDto addDish(OrderDto orderDto, OrderItemDto newDishToListDto){
        Optional<OrderItemDto> existingDish = orderDto.getOrder().stream()
                .filter(r -> r.getDish().getId().equals(newDishToListDto.getDish().getId()))
                .findFirst();

        if (existingDish.isPresent()) {
            OrderItemDto dishToUpdate = existingDish.get();

            dishToUpdate = dishToUpdate.toBuilder().quantity(dishToUpdate.getQuantity()+1).build();

            List<OrderItemDto> orderItems = orderDto.getOrder();
            orderItems.set(orderItems.indexOf(existingDish.get()), dishToUpdate);
            orderDto = orderDto.toBuilder().order(orderItems).build();

        } else {
            List<OrderItemDto> orderItems = orderDto.getOrder();
            orderItems.add(newDishToListDto);
            orderDto.toBuilder().order(orderItems).build();
        }
        return orderDto;
    }

    public ResponseEntity<?> removeFromOrder(OrderDto orderDto, DishDto dishDto) {
        Optional<OrderItemDto> existingDish = orderDto.getOrder().stream()
                .filter(r -> r.getDish().getId().equals(dishDto.getId()))
                .findFirst();

        if (existingDish.isPresent()) {
            orderDto = decrementQuantity(orderDto, existingDish.get());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order do not include dish");
        }

        return ResponseEntity.ok(orderDto.toBuilder().cost(recalculateCost(orderDto)).build());
    }

    public OrderDto decrementQuantity(OrderDto orderDto, OrderItemDto dishToRemoveDto){
        List<OrderItemDto> orderItemsDto = orderDto.getOrder();

        if (dishToRemoveDto.getQuantity() == 1) {
            orderItemsDto.remove(dishToRemoveDto);
        } else {
            OrderItemDto dishToRemoveDtoAfter = dishToRemoveDto.toBuilder().quantity(dishToRemoveDto.getQuantity()-1).build();
            orderItemsDto.set(orderItemsDto.indexOf(dishToRemoveDto), dishToRemoveDtoAfter);
        }
        return orderDto.toBuilder().order(orderItemsDto).build();
    }

    public ResponseEntity<?> acceptOrder(OrderDto orderDto) {
        List<OrderItemDto> orderItems = orderDto.getOrder();

        if(!validateDishesInOrder(orderItems)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One of the dishes is not valid");
        }

        if(!isTotalCostValid(orderDto)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Total cost is not valid");
        }

        if(!isPaymentMethodValid(orderDto.getPaymentMethod())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment method is not valid");
        }

        if(!isTableNoValid(orderDto.getTableNoId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Table number is not valid");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


//        if (authentication instanceof AnonymousAuthenticationToken){
//            System.out.println(authentication.getName());
//        }

        Order order = orderMapper.toEntity(orderDto);

        order = order.toBuilder().status(StatusType.NEW).date(LocalDateTime.now()).build();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.toString()))) {
            User user = (User) authentication.getPrincipal();
            order.setUserId(user.getId());
        }

        return ResponseEntity.ok(orderRepository.save(order));
    }

    public boolean validateDishesInOrder(List<OrderItemDto> orderItemsDto) {
        for (OrderItemDto orderItemDto : orderItemsDto) {
            System.out.println(orderItemDto);
            Optional<Dish> dbDish = dishRepository.findById(orderItemDto.getDish().getId());
            if(dbDish.isEmpty()){
                return false;
            }
            if (!isDishValid(orderItemDto, dbDish.get())) {
                return false;
            }
        }
        return true;
    }

    public double recalculateCost(OrderDto orderDto) {
        for (OrderItemDto orderItemDto : orderDto.getOrder()) {
            orderItemDto.setCost(roundToTwoDecimalPlaces(orderItemDto.getDish().getPrice() * orderItemDto.getQuantity()));
        }

        return roundToTwoDecimalPlaces(orderDto.getOrder().stream()
                .mapToDouble(OrderItemDto::getCost)
                .sum());
    }

    public boolean isDishValid(OrderItemDto orderItemDto, Dish dbDish) {
        return isDishTypeValid(orderItemDto.getDish().getDishType().toString(), dbDish.getDishType().toString()) &&
                isDishNameValid(orderItemDto.getDish().getName(),dbDish.getName()) &&
                isDishPriceValid(orderItemDto.getDish().getPrice(), dbDish.getPrice()) &&
                isDishIngredientsValid(orderItemDto.getDish().getIngredients(), dbDish.getIngredients()) &&
                isDishCostValid(orderItemDto.getCost(), orderItemDto.getQuantity(), dbDish.getPrice());
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

    public boolean isTotalCostValid(OrderDto orderDto) {
        Double dbCost = 0.0;
        for (OrderItemDto orderItem : orderDto.getOrder()) {
            Optional<Dish> optionalDbDish = dishRepository.findById(orderItem.getDish().getId());
            if (optionalDbDish.isPresent()) {
                Dish dbDish = optionalDbDish.get();
                dbCost += dbDish.getPrice()*orderItem.getQuantity();
            }
        }

        dbCost = roundToTwoDecimalPlaces(dbCost);

        Double calculatedTotalCost = roundToTwoDecimalPlaces(orderDto.getOrder().stream()
                .mapToDouble(OrderItemDto::getCost)
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
