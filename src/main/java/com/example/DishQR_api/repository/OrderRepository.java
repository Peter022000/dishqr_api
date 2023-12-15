package com.example.DishQR_api.repository;

import com.example.DishQR_api.model.Order;
import com.example.DishQR_api.model.StatusType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository <Order, String> {
    @Query("{ 'userId' : ?0 }")
    List<Order> findAllByUserId(String userId);
    List<Order> findAllByStatus(StatusType status);
}
