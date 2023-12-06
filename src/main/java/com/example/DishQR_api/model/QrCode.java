package com.example.DishQR_api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document("qrCode")
public class QrCode {
    @Id
    private String id;
    private String qrCode;
    private QrCodeType type;
}
