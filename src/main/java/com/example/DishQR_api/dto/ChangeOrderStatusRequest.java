package com.example.DishQR_api.dto;

import com.example.DishQR_api.model.StatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeOrderStatusRequest {
    AcceptedOrderDto acceptedOrderDto;
    StatusType newStatus;
}
