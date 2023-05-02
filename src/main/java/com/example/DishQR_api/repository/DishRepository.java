package com.example.DishQR_api.repository;

import com.example.DishQR_api.model.Dish;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishRepository extends MongoRepository <Dish, String> {
}
