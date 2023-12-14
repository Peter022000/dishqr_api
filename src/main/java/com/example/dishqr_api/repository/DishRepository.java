package com.example.dishqr_api.repository;

import com.example.dishqr_api.model.Dish;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends MongoRepository <Dish, String> {

    @Query("{ 'ingredients' : { $in: ?0 } }")
    List<Dish> findByIngredientsIn(List<String> ingredients);
}
