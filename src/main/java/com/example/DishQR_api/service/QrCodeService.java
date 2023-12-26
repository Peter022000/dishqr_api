package com.example.DishQR_api.service;


import com.example.DishQR_api.dto.QrCodeDto;
import com.example.DishQR_api.mapper.QRCodeMapper;
import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.model.QrCode;
import com.example.DishQR_api.repository.QrCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class QrCodeService {

    private final QrCodeRepository qrCodeRepository;
    private final QRCodeMapper qrCodeMapper;

    public Optional<QrCode> checkCode(String id) {
        return qrCodeRepository.findById(id);
    }

    public void save(QrCodeDto qrCodeDto) {
        qrCodeRepository.save(qrCodeMapper.toEntity(qrCodeDto));
    }

    public ResponseEntity<?> getQRCodes() {
        return ResponseEntity.ok(qrCodeMapper.toDtoList(qrCodeRepository.findAll()));
    }

    public ResponseEntity<?> saveQRCode(QrCodeDto qrCodeDto) {
        return ResponseEntity.ok(qrCodeRepository.save(qrCodeMapper.toEntity(qrCodeDto)));
    }

    public ResponseEntity<?> deleteQRCode(String id) {
        Optional<QrCode> qrCodeOptional = qrCodeRepository.findById(id);

        if(qrCodeOptional.isPresent()){
            qrCodeRepository.deleteById(id);
            return ResponseEntity.ok("QR code deleted");
        } else {
            return ResponseEntity.badRequest().body("QR code not found");
        }
    }

    public ResponseEntity<?> updateQRCode(QrCodeDto qrCodeDto) {
        return ResponseEntity.ok(qrCodeRepository.save(qrCodeMapper.toEntity(qrCodeDto)));
    }

    public ResponseEntity<?> addQRCode(QrCodeDto qrCodeDto) {
        return ResponseEntity.ok(qrCodeRepository.save(qrCodeMapper.toEntity(qrCodeDto)));
    }
}
