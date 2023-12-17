package com.example.DishQR_api.dto;

import com.example.DishQR_api.model.OrderDiscount;
import com.example.DishQR_api.model.OrderItem;
import com.example.DishQR_api.model.PaymentMethod;
import com.example.DishQR_api.model.StatusType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AcceptedOrderDto {
    @Id
    private String id;
    private String userId;
    private String tableNoId;
    private String tableNo;
    private Double cost;
    private List<OrderItemDto> orderDishesDto;
    private PaymentMethod paymentMethod;
    private LocalDateTime date;
    private StatusType status;
    private Boolean isPayed;
    private OrderDiscount orderDiscount;
}
