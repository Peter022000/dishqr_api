package com.example.DishQR_api.service;


import com.example.DishQR_api.dto.*;
import com.example.DishQR_api.mapper.AcceptedOrderMapper;
import com.example.DishQR_api.mapper.CartOrderMapper;
import com.example.DishQR_api.model.*;
import com.example.DishQR_api.repository.DiscountSettingsRepository;
import com.example.DishQR_api.repository.DishRepository;
import com.example.DishQR_api.repository.OrderRepository;
import com.example.DishQR_api.repository.QrCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final QrCodeRepository qrCodeRepository;
    private final DiscountSettingsRepository discountSettingsRepository;
    private final CartOrderMapper cartOrderMapper;
    private final AcceptedOrderMapper acceptedOrderMapper;
    private final UserService userService;
    private SimpMessagingTemplate messagingTemplate;

    public ResponseEntity<?> setPayed(AcceptedOrderDto acceptedOrderDto) {
        Order order = acceptedOrderMapper.toEntity(acceptedOrderDto);
        order = order.toBuilder().isPayed(true).build();
        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(acceptedOrderMapper.toDto(savedOrder));
    }

    public List<AcceptedOrderDto> getOrdersByStatus(StatusType status) {
        List<Order> orders = orderRepository.findAllByStatus(status);
        List<AcceptedOrderDto> ordersDto = acceptedOrderMapper.toDtoList(orders);
        return ordersDto;
    }
    public List<AcceptedOrderDto> getOrdersByStatusToday(StatusType status) {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Warsaw"));
        Instant startOfDay = today.atStartOfDay(ZoneId.of("Europe/Warsaw")).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.of("Europe/Warsaw")).toInstant();

        List<Order> orders = orderRepository.findAllByStatusAndDateBetween(status, startOfDay.toEpochMilli(), endOfDay.toEpochMilli());
        List<AcceptedOrderDto> ordersDto = acceptedOrderMapper.toDtoList(orders);
        return ordersDto;
    }

    public ResponseEntity<?> changeOrderStatus(String orderId, StatusType newStatus) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order = order.toBuilder().status(newStatus).build();

            Order savedOrder = orderRepository.save(order);

            return ResponseEntity.ok(acceptedOrderMapper.toDto(savedOrder));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order not found with id: " + orderId);
        }
    }

    public ResponseEntity<?> addToOrder(CartOrderDto cartOrderDto, DishDto newDishDto) {

        OrderItemDto newDishToList = OrderItemDto
                .builder()
                .dishDto(newDishDto)
                .quantity(1)
                .build();


        if(cartOrderDto.getOrderDishesDto() == null){
            List<OrderItemDto> orderItem = List.of(newDishToList);
            cartOrderDto = cartOrderDto.toBuilder().orderDishesDto(orderItem).build();
        } else {
            cartOrderDto = addDish(cartOrderDto,newDishToList);
        }

        return ResponseEntity.ok(recalculateCost(cartOrderDto));
    }

    public CartOrderDto addDish(CartOrderDto cartOrderDto, OrderItemDto newDishToListDto){
        Optional<OrderItemDto> existingDish = cartOrderDto.getOrderDishesDto().stream()
                .filter(r -> r.getDishDto().getId().equals(newDishToListDto.getDishDto().getId()))
                .findFirst();

        if (existingDish.isPresent()) {
            OrderItemDto dishToUpdate = existingDish.get();

            dishToUpdate = dishToUpdate.toBuilder().quantity(dishToUpdate.getQuantity()+1).build();

            List<OrderItemDto> orderItems = cartOrderDto.getOrderDishesDto();
            orderItems.set(orderItems.indexOf(existingDish.get()), dishToUpdate);
            cartOrderDto = cartOrderDto.toBuilder().orderDishesDto(orderItems).build();

        } else {
            List<OrderItemDto> orderItems = cartOrderDto.getOrderDishesDto();
            orderItems.add(newDishToListDto);
            cartOrderDto.toBuilder().orderDishesDto(orderItems).build();
        }
        return cartOrderDto;
    }

    public ResponseEntity<?> removeFromOrder(CartOrderDto cartOrderDto, DishDto dishDto) {
        Optional<OrderItemDto> existingDish = cartOrderDto.getOrderDishesDto().stream()
                .filter(r -> r.getDishDto().getId().equals(dishDto.getId()))
                .findFirst();

        if (existingDish.isPresent()) {
            cartOrderDto = decrementQuantity(cartOrderDto, existingDish.get());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order do not include dish");
        }

        return ResponseEntity.ok(recalculateCost(cartOrderDto));
    }

    public CartOrderDto decrementQuantity(CartOrderDto cartOrderDto, OrderItemDto dishToRemoveDto){
        List<OrderItemDto> orderItemsDto = cartOrderDto.getOrderDishesDto();

        if (dishToRemoveDto.getQuantity() == 1) {
            orderItemsDto.remove(dishToRemoveDto);
        } else {
            OrderItemDto dishToRemoveDtoAfter = dishToRemoveDto.toBuilder().quantity(dishToRemoveDto.getQuantity()-1).build();
            orderItemsDto.set(orderItemsDto.indexOf(dishToRemoveDto), dishToRemoveDtoAfter);
        }
        return cartOrderDto.toBuilder().orderDishesDto(orderItemsDto).build();
    }

    public ResponseEntity<?> acceptOrder(CartOrderDto cartOrderDto, String userId) {

        cartOrderDto = recalculateCost(cartOrderDto);

        List<OrderItemDto> orderItems = cartOrderDto.getOrderDishesDto();

        if(cartOrderDto.getOrderDishesDto().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is empty");
        }

        if(!validateDishesInOrder(orderItems)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One of the dishes is not valid");
        }

        if(!isTotalCostValid(cartOrderDto)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Total cost is not valid");
        }

        if(!isPaymentMethodValid(cartOrderDto.getPaymentMethod())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment method is not valid");
        }

        if(!isTableNoValid(cartOrderDto.getTableNoId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Table number is not valid");
        }

//        if (authentication instanceof AnonymousAuthenticationToken){
//            System.out.println(authentication.getName());
//        }


        Order order = cartOrderMapper.toEntity(cartOrderDto);

        order = order.toBuilder().status(StatusType.NEW).isPayed(false).date(Instant.now().atZone(ZoneId.of("Europe/Warsaw")).toInstant().toEpochMilli()).build();

        if (userId != null) {
            order = order.toBuilder().userId(userId).build();
        }

        if(order.getOrderDiscount().getIsUsed()){
            userService.updateUserLastDiscountOrderNumber(userId, this.getNumberOfOrders()+1);
        }

        Order savedOrder = orderRepository.save(order);

        messagingTemplate.convertAndSend("/topic/newOrder", acceptedOrderMapper.toDto(savedOrder));

        return ResponseEntity.ok(savedOrder);
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

    public CartOrderDto recalculateCost(CartOrderDto cartOrderDto) {
        for (OrderItemDto orderItemDto : cartOrderDto.getOrderDishesDto()) {
            orderItemDto.setCost(orderItemDto.getDishDto().getPrice() * orderItemDto.getQuantity());
        }

        double cost = roundToTwoDecimalPlaces(cartOrderDto.getOrderDishesDto().stream()
                .mapToDouble(OrderItemDto::getCost)
                .sum());

        cartOrderDto = cartOrderDto.toBuilder()
                .orderDiscountDto(cartOrderDto.getOrderDiscountDto()
                        .toBuilder()
                        .oldCost(cost)
                        .build())
                .cost(checkDiscount(cartOrderDto.getOrderDiscountDto().getIsUsed(), cartOrderDto.getOrderDiscountDto().getDiscountPercentage(), cost))
                .build();

        return cartOrderDto;
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

    public boolean isTotalCostValid(CartOrderDto cartOrderDto) {
        Double dbCost = 0.0;
        for (OrderItemDto orderItem : cartOrderDto.getOrderDishesDto()) {
            Optional<Dish> optionalDbDish = dishRepository.findById(orderItem.getDishDto().getId());
            if (optionalDbDish.isPresent()) {
                Dish dbDish = optionalDbDish.get();
                dbCost += dbDish.getPrice()*orderItem.getQuantity();
            }
        }

        if(cartOrderDto.getOrderDiscountDto().getIsUsed()) {
                dbCost = dbCost * cartOrderDto.getOrderDiscountDto().getDiscountPercentage();
        }

        dbCost = roundToTwoDecimalPlaces(dbCost);

        Double calculatedTotalCost = roundToTwoDecimalPlaces(cartOrderDto.getOrderDishesDto().stream()
                .mapToDouble(OrderItemDto::getCost)
                .sum());

        if(cartOrderDto.getOrderDiscountDto().getIsUsed()) {
            calculatedTotalCost = calculatedTotalCost * cartOrderDto.getOrderDiscountDto().getDiscountPercentage();
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
        List<Order> userOrders = getHistory();
        return ResponseEntity.ok(acceptedOrderMapper.toDtoList(userOrders));
    }

    public ResponseEntity<?> getOrders() {
        List<Order> orders = orderRepository.findAllByStatus(StatusType.NEW);
        List<AcceptedOrderDto> acceptedOrderDto = acceptedOrderMapper.toDtoList(orders);
        return ResponseEntity.ok(acceptedOrderDto);
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
            cost = cost-(cost * discountPercentage);
        }

        return roundToTwoDecimalPlaces(cost);
    }

    public DiscountSettings getDiscountSettings(){
        return discountSettingsRepository.findAll().get(0);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
