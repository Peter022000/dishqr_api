package com.example.DishQR_api.dto;

import com.example.DishQR_api.model.DishType;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DishStatistic {
    @Id
    private String id;
    private String name;
    private Integer quantity;
}
