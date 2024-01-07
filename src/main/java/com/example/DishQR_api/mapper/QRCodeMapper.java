package com.example.DishQR_api.mapper;

import com.example.DishQR_api.dto.QrCodeDto;
import com.example.DishQR_api.model.QrCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class QRCodeMapper {

    public QrCodeDto toDto(QrCode qrCode) {
        return QrCodeDto.builder()
                .id(qrCode.getId())
                .qrCode(qrCode.getQrCode())
                .type(qrCode.getType())
                .build();
    }

    public QrCode toEntity(QrCodeDto qrCodeDto) {
        return QrCode.builder()
                .id(qrCodeDto.getId())
                .qrCode(qrCodeDto.getQrCode())
                .type(qrCodeDto.getType())
                .build();
    }

    public List<QrCodeDto> toDtoList(List<QrCode> qrCodes) {
        return qrCodes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<QrCode> toEntityList(List<QrCodeDto> qrCodeDtos) {
        return qrCodeDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
