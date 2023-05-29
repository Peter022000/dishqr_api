package com.example.DishQR_api.repository;

import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.model.QrCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QrCodeRepository extends MongoRepository <QrCode, String> {
}
