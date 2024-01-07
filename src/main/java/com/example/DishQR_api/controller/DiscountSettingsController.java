package com.example.DishQR_api.controller;

import com.example.DishQR_api.dto.DiscountSettingsDto;
import com.example.DishQR_api.service.DiscountSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path ="/discountSettings")
@AllArgsConstructor
public class DiscountSettingsController {

    private final DiscountSettingsService discountSettingsService;

    @Operation(summary = "Save or update discount settings")
    @PostMapping(path = "/saveDiscountSettings")
    public ResponseEntity<?> saveDiscountSettings(@RequestBody DiscountSettingsDto discountSettingsDto){
        return discountSettingsService.save(discountSettingsDto);
    }

    @Operation(summary = "Get discount settings")
    @GetMapping(path = "/getDiscountSettings")
    public ResponseEntity<?> getDiscountSettings(){
        return discountSettingsService.getDiscountSettings();
    }
}
