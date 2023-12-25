package com.example.DishQR_api.controller;

import com.example.DishQR_api.dto.DiscountSettingsDto;
import com.example.DishQR_api.dto.DishDto;
import com.example.DishQR_api.service.DiscountSettingsService;
import com.example.DishQR_api.service.DishService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path ="/discountSettings")
@AllArgsConstructor
public class DiscountSettingsController {

    private final DiscountSettingsService discountSettingsService;

    @PostMapping(path = "/saveDiscountSettings")
    public ResponseEntity<?> saveDiscountSettings(@RequestBody DiscountSettingsDto discountSettingsDto){
        return discountSettingsService.save(discountSettingsDto);
    }

    @GetMapping(path = "/getDiscountSettings")
    public ResponseEntity<?> getDiscountSettings(){
        return discountSettingsService.getDiscountSettings();
    }
}
