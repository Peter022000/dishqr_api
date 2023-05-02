package com.example.DishQR_api.model;

import lombok.Data;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

@Data
public class OrderList {

    private String id;
    private String name;
    private Double price;
    private Integer quantity;
    private Double cost;

    public OrderList(String id, String name, Double price, Integer quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.cost = price*quantity;
    }
}
