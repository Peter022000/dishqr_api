package com.example.DishQR_api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document("orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private String tableNoId;
    private Double cost;
    private List<OrderItem> orderDishes;
    private PaymentMethod paymentMethod;
    private Long date;
    private StatusType status;
    private Boolean isPayed;
    private OrderDiscount orderDiscount;
}
