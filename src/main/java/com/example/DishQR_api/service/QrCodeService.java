package com.example.DishQR_api.service;


import com.example.DishQR_api.model.QrCode;
import com.example.DishQR_api.repository.QrCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class QrCodeService {

    private final QrCodeRepository qrCodeRepository;

    public Optional<QrCode> checkCode(String id) {
        return qrCodeRepository.findById(id);
    }

    public void save(QrCode qrCode) {
        qrCodeRepository.save(qrCode);
    }
}
