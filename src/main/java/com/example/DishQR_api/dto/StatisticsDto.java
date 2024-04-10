package com.example.DishQR_api.dto;

import lombok.*;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StatisticsDto {
    List<DishStatistic> dishes;
}
