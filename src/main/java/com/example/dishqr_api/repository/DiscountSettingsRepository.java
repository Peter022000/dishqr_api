package com.example.dishqr_api.repository;

import com.example.dishqr_api.model.DiscountSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountSettingsRepository extends MongoRepository <DiscountSettings, String> {
}
