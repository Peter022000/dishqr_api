package com.example.DishQR_api.repository;

import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository <Order, String> {
}
