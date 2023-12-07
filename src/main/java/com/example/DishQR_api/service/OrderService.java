package com.example.DishQR_api.service;


import com.example.DishQR_api.dto.*;
import com.example.DishQR_api.mapper.OrderMapper;
import com.example.DishQR_api.model.*;
import com.example.DishQR_api.repository.DiscountSettingsRepository;
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
    private final DiscountSettingsRepository discountSettingsRepository;
    private final OrderMapper orderMapper;

    public ResponseEntity<?> addToOrder(OrderDto orderDto, DishDto newDishDto) {

        OrderItemDto newDishToList = OrderItemDto
                .builder()
                .dishDto(newDishDto)
                .quantity(1)
                .build();


        if(orderDto.getOrderDishesDto() == null){
            List<OrderItemDto> orderItem = List.of(newDishToList);
            orderDto = orderDto.toBuilder().orderDishesDto(orderItem).build();
        } else {
            orderDto = addDish(orderDto,newDishToList);
        }

        return ResponseEntity.ok(recalculateCost(orderDto));
    }

    public OrderDto addDish(OrderDto orderDto, OrderItemDto newDishToListDto){
        Optional<OrderItemDto> existingDish = orderDto.getOrderDishesDto().stream()
                .filter(r -> r.getDishDto().getId().equals(newDishToListDto.getDishDto().getId()))
                .findFirst();

        if (existingDish.isPresent()) {
            OrderItemDto dishToUpdate = existingDish.get();

            dishToUpdate = dishToUpdate.toBuilder().quantity(dishToUpdate.getQuantity()+1).build();

            List<OrderItemDto> orderItems = orderDto.getOrderDishesDto();
            orderItems.set(orderItems.indexOf(existingDish.get()), dishToUpdate);
            orderDto = orderDto.toBuilder().orderDishesDto(orderItems).build();

        } else {
            List<OrderItemDto> orderItems = orderDto.getOrderDishesDto();
            orderItems.add(newDishToListDto);
            orderDto.toBuilder().orderDishesDto(orderItems).build();
        }
        return orderDto;
    }

    public ResponseEntity<?> removeFromOrder(OrderDto orderDto, DishDto dishDto) {
        Optional<OrderItemDto> existingDish = orderDto.getOrderDishesDto().stream()
                .filter(r -> r.getDishDto().getId().equals(dishDto.getId()))
                .findFirst();

        if (existingDish.isPresent()) {
            orderDto = decrementQuantity(orderDto, existingDish.get());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order do not include dish");
        }

        return ResponseEntity.ok(recalculateCost(orderDto));
    }

    public OrderDto decrementQuantity(OrderDto orderDto, OrderItemDto dishToRemoveDto){
        List<OrderItemDto> orderItemsDto = orderDto.getOrderDishesDto();

        if (dishToRemoveDto.getQuantity() == 1) {
            orderItemsDto.remove(dishToRemoveDto);
        } else {
            OrderItemDto dishToRemoveDtoAfter = dishToRemoveDto.toBuilder().quantity(dishToRemoveDto.getQuantity()-1).build();
            orderItemsDto.set(orderItemsDto.indexOf(dishToRemoveDto), dishToRemoveDtoAfter);
        }
        return orderDto.toBuilder().orderDishesDto(orderItemsDto).build();
    }

    public ResponseEntity<?> acceptOrder(OrderDto orderDto, String userId) {

        orderDto = recalculateCost(orderDto);

        List<OrderItemDto> orderItems = orderDto.getOrderDishesDto();

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

//        if (authentication instanceof AnonymousAuthenticationToken){
//            System.out.println(authentication.getName());
//        }


        Order order = orderMapper.toEntity(orderDto);

        order = order.toBuilder().status(StatusType.NEW).date(LocalDateTime.now()).build();


        if (userId != null) {
            order = order.toBuilder().userId(userId).build();
        }

        return ResponseEntity.ok(orderRepository.save(order));
    }

    public boolean validateDishesInOrder(List<OrderItemDto> orderItemsDto) {
        for (OrderItemDto orderItemDto : orderItemsDto) {
            Optional<Dish> dbDish = dishRepository.findById(orderItemDto.getDishDto().getId());
            if(dbDish.isEmpty()){
                return false;
            }
            if (!isDishValid(orderItemDto, dbDish.get())) {
                return false;
            }
        }
        return true;
    }

    public OrderDto recalculateCost(OrderDto orderDto) {
        for (OrderItemDto orderItemDto : orderDto.getOrderDishesDto()) {
            orderItemDto.setCost(orderItemDto.getDishDto().getPrice() * orderItemDto.getQuantity());
        }

        double cost = roundToTwoDecimalPlaces(orderDto.getOrderDishesDto().stream()
                .mapToDouble(OrderItemDto::getCost)
                .sum());

        orderDto = orderDto.toBuilder()
                .orderDiscountDto(orderDto.getOrderDiscountDto()
                        .toBuilder()
                        .oldCost(cost)
                        .build())
                .cost(checkDiscount(orderDto.getOrderDiscountDto().getIsUsed(),orderDto.getOrderDiscountDto().getDiscountPercentage(), cost))
                .build();

        return orderDto;
    }

    public boolean isDishValid(OrderItemDto orderItemDto, Dish dbDish) {
        return isDishTypeValid(orderItemDto.getDishDto().getDishType().toString(), dbDish.getDishType().toString()) &&
                isDishNameValid(orderItemDto.getDishDto().getName(),dbDish.getName()) &&
                isDishPriceValid(orderItemDto.getDishDto().getPrice(), dbDish.getPrice()) &&
                isDishIngredientsValid(orderItemDto.getDishDto().getIngredients(), dbDish.getIngredients()) &&
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
        for (OrderItemDto orderItem : orderDto.getOrderDishesDto()) {
            Optional<Dish> optionalDbDish = dishRepository.findById(orderItem.getDishDto().getId());
            if (optionalDbDish.isPresent()) {
                Dish dbDish = optionalDbDish.get();
                dbCost += dbDish.getPrice()*orderItem.getQuantity();
            }
        }

        if(orderDto.getOrderDiscountDto().getIsUsed()) {
                dbCost = dbCost * orderDto.getOrderDiscountDto().getDiscountPercentage();
        }

        dbCost = roundToTwoDecimalPlaces(dbCost);

        Double calculatedTotalCost = roundToTwoDecimalPlaces(orderDto.getOrderDishesDto().stream()
                .mapToDouble(OrderItemDto::getCost)
                .sum());

        if(orderDto.getOrderDiscountDto().getIsUsed()) {
            calculatedTotalCost = calculatedTotalCost * orderDto.getOrderDiscountDto().getDiscountPercentage();
        }

        calculatedTotalCost = roundToTwoDecimalPlaces(calculatedTotalCost);


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
        return ResponseEntity.ok(getHistory());
    }

    public ResponseEntity<?> getOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    public ResponseEntity<?> getUserNumberOfOrders() {
        return ResponseEntity.ok(getNumberOfOrders());
    }

    public List<Order> getHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return orderRepository.findAllByUserId(user.getId());
    }

    public Integer getNumberOfOrders(){
        return getHistory().size();
    }

    public Double checkDiscount(Boolean isUsed, Double discountPercentage, Double cost){

        if(isUsed){
            cost = cost * discountPercentage;
        }

        return roundToTwoDecimalPlaces(cost);
    }

    public DiscountSettings getDiscountSettings(){
        return discountSettingsRepository.findAll().get(0);
    }
}
