package com.example.dishqr_api.repository;

import com.example.dishqr_api.model.QrCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrCodeRepository extends MongoRepository <QrCode, String> {
}
