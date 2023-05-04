package com.example.DishQR_api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

enum PaymentMethod {
    card, cash
}

@Data
@Document("orders")
public class Order {
    @Id
    private String id;
    private Integer tableNo;
    private Double cost;
    private List<OrderList> order;
    private PaymentMethod paymentMethod;

    public Order(Integer tableNo, Double cost, List<OrderList> order, PaymentMethod paymentMethod) {
        this.tableNo = tableNo;
        this.cost = cost;
        this.order = order;
        this.paymentMethod = paymentMethod;
    }
}
