package com.example.DishQR_api.repository;

import com.example.DishQR_api.model.DiscountSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountSettingsRepository extends MongoRepository <DiscountSettings, String> {
}
