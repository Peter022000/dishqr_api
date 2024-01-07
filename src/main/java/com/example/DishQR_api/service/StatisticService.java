package com.example.DishQR_api.service;

import com.example.DishQR_api.dto.DishStatistic;
import com.example.DishQR_api.dto.StatisticsDto;
import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.model.Order;
import com.example.DishQR_api.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {

    public static StatisticsDto calculateDishStatistics(List<Order> orders, List<Dish> dishes) {
        StatisticsDto statisticsDto = new StatisticsDto();
        List<DishStatistic> dishStatistics = new ArrayList<>();

        for (Dish dish : dishes) {
            DishStatistic dishStatistic = new DishStatistic();
            dishStatistic.setId(dish.getId());
            dishStatistic.setName(dish.getName());
            dishStatistic.setQuantity(0);
            dishStatistics.add(dishStatistic);
        }

        statisticsDto.setDishes(dishStatistics);

        for (Order order : orders) {
            for (OrderItem orderItem : order.getOrderDishes()) {
                String dishId = orderItem.getDish().getId();
                Integer quantity = orderItem.getQuantity();

                DishStatistic dishStatistic = findDishStatisticById(dishId, statisticsDto.getDishes());

                if (dishStatistic != null) {
                    dishStatistic.setQuantity(dishStatistic.getQuantity() + quantity);
                }
            }
        }

        return statisticsDto;
    }

    private static DishStatistic findDishStatisticById(String dishId, List<DishStatistic> dishStatistics) {
        for (DishStatistic dishStatistic : dishStatistics) {
            if (dishStatistic.getId().equals(dishId)) {
                return dishStatistic;
            }
        }
        return null;
    }
}
