package com.example.DishQR_api.service;

import com.example.DishQR_api.dto.AcceptedOrderDto;
import com.example.DishQR_api.dto.DishDto;
import com.example.DishQR_api.dto.CartOrderDto;
import com.example.DishQR_api.dto.OrderItemDto;
import com.example.DishQR_api.mapper.AcceptedOrderMapper;
import com.example.DishQR_api.mapper.DishMapper;
import com.example.DishQR_api.mapper.CartOrderMapper;
import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.model.Order;
import com.example.DishQR_api.repository.DishRepository;
import com.example.DishQR_api.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecommendationService {
    private final OrderRepository orderRepository;
    private final AcceptedOrderMapper acceptedOrderMapper;
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;

    public List<String> getRecommendedIngredientsForCustomer(String customerId, int topIngredientsCount) {
        Map<String, Long> ingredientCounts = new HashMap<>();

        List<Order> customerOrders = orderRepository.findAllByUserId(customerId);

        List<AcceptedOrderDto> customerOrdersDto = acceptedOrderMapper.toDtoList(customerOrders);

        customerOrdersDto.forEach(cartOrderDto -> {
            List<OrderItemDto> orderItems = cartOrderDto.getOrderDishesDto();
            orderItems.forEach(item -> {
                DishDto dish = item.getDishDto();
                if (dish != null && dish.getIngredients() != null) {
                    dish.getIngredients().forEach(ingredient -> {
                        ingredientCounts.merge(ingredient, 1L, Long::sum);
                    });
                }
            });
        });

//        // Sortowanie mapy według wartości (Long) w odwrotnej kolejności
//        Map<String, Long> sortedIngredientCounts = ingredientCounts.entrySet().stream()
//                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//                        (e1, e2) -> e1, LinkedHashMap::new));
//
//        // Wyświetlenie posortowanej mapy
//        sortedIngredientCounts.forEach((key, value) -> System.out.println(key + ": " + value));
//
//        System.out.println(sortedIngredientCounts);

        List<String> recommendedIngredients = ingredientCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topIngredientsCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return recommendedIngredients;
    }

    public List<DishDto> getRecommendedDishesForCustomer(String customerId, int topIngredientsCount, int topDishesCount) {
        List<String> recommendedIngredients = getRecommendedIngredientsForCustomer(customerId, topIngredientsCount);

        List<Dish> recommendedDishes = dishRepository.findByIngredientsIn(recommendedIngredients);


        recommendedDishes.sort(Comparator.comparingInt(dish -> dish.getIngredients().size()));
        Collections.reverse(recommendedDishes);

        recommendedDishes = recommendedDishes.stream()
                .limit(topDishesCount* 2L)
                .collect(Collectors.toList());

        Collections.shuffle(recommendedDishes);

        recommendedDishes = recommendedDishes.stream()
                .limit(topDishesCount)
                .collect(Collectors.toList());

        return dishMapper.toDtoList(recommendedDishes);
    }
}
