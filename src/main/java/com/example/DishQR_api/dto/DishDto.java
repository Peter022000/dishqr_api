package com.example.DishQR_api.dto;

import com.example.DishQR_api.model.DishType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document("dishes")
public class DishDto {
    @Id
    private String id;
    private DishType dishType;
    private String name;
    private Double price;
    private List<String> ingredients;
}
