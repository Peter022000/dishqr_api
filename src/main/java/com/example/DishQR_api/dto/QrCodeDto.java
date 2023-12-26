package com.example.DishQR_api.dto;

import com.example.DishQR_api.model.QrCodeType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QrCodeDto {
    @Id
    private String id;
    private String qrCode;
    private QrCodeType type;
}
