package com.example.DishQR_api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("qrCode")
public class QrCode {
    @Id
    private String id;
    private String qrCode;
    private QrCodeType type;

    public QrCode(String qrCode, QrCodeType type) {
        this.qrCode = qrCode;
        this.type = type;
    }
}
