package com.example.DishQR_api.dto;

import com.example.DishQR_api.model.OrderDiscount;
import com.example.DishQR_api.model.PaymentMethod;
import com.example.DishQR_api.model.StatusType;
import lombok.*;
import org.springframework.data.annotation.Id;

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
    private Long date;
    private StatusType status;
    private Boolean isPayed;
    private OrderDiscount orderDiscount;
}
