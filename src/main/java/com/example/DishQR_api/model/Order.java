package com.example.DishQR_api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
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
    private List<OrderItem> order;
    private PaymentMethod paymentMethod;
    private LocalDateTime date;
}
