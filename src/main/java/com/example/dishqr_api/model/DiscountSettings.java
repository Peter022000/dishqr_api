package com.example.dishqr_api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document("orderDiscount")
public class DiscountSettings {
    @Id
    private String id;
    private Boolean isEnabled;
    private Integer ordersRequired;
    private Double discountPercentage;
}
