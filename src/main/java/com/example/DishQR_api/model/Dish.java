package com.example.DishQR_api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("dishes")
public class Dish {
    @Id
    private String id;
    private DishType dishType;
    private String name;
    private Double price;
    private List<String> ingredients;

    public Dish(DishType dishType, String name, Double price, List<String> ingredients) {
        this.dishType = dishType;
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
    }
}
