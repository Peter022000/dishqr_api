package com.example.dishqr_api.service;

import com.example.dishqr_api.dto.DishDto;
import com.example.dishqr_api.dto.OrderDto;
import com.example.dishqr_api.dto.OrderItemDto;
import com.example.dishqr_api.mapper.DishMapper;
import com.example.dishqr_api.mapper.OrderMapper;
import com.example.dishqr_api.model.Dish;
import com.example.dishqr_api.model.Order;
import com.example.dishqr_api.repository.DishRepository;
import com.example.dishqr_api.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecommendationService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;

    public List<String> getRecommendedIngredientsForCustomer(String customerId, int topIngredientsCount) {
        Map<String, Long> ingredientCounts = new HashMap<>();

        // Pobierz historię zamówień klienta z repozytorium MongoDB
        List<Order> customerOrders = orderRepository.findAllByUserId(customerId);

        // Mapuj zamówienia na OrderDto za pomocą OrderMapper
        List<OrderDto> customerOrdersDto = orderMapper.toDtoList(customerOrders);

        // Zliczaj składniki
        customerOrdersDto.forEach(orderDto -> {
            List<OrderItemDto> orderItems = orderDto.getOrderDishesDto();
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

        // Sortuj składniki według liczby wystąpień malejąco
        List<String> recommendedIngredients = ingredientCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topIngredientsCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return recommendedIngredients;
    }

    public List<DishDto> getRecommendedDishesForCustomer(String customerId, int topIngredientsCount, int topDishesCount) {
        List<String> recommendedIngredients = getRecommendedIngredientsForCustomer(customerId, topIngredientsCount);

        // Pobierz dania zawierające rekomendowane składniki
        List<Dish> recommendedDishes = dishRepository.findByIngredientsIn(recommendedIngredients);


        // Sortuj dania według liczby składników w malejącej kolejności
        recommendedDishes.sort(Comparator.comparingInt(dish -> dish.getIngredients().size()));
        Collections.reverse(recommendedDishes);

        // Ogranicz do topDishesCount dań
        recommendedDishes = recommendedDishes.stream()
                .limit(topDishesCount* 2L)
                .collect(Collectors.toList());

        Collections.shuffle(recommendedDishes);

        recommendedDishes = recommendedDishes.stream()
                .limit(topDishesCount)
                .collect(Collectors.toList());

        // Mapuj dania na DishDto za pomocą odpowiedniego mappera
        return dishMapper.toDtoList(recommendedDishes);
    }
}
