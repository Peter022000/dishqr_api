package com.example.DishQR_api.dto;

import lombok.*;
import org.springframework.data.annotation.Id;

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
