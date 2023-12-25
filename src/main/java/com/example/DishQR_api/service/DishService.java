package com.example.DishQR_api.service;


import com.example.DishQR_api.dto.DishDto;
import com.example.DishQR_api.mapper.DishMapper;
import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.repository.DishRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final DishMapper dishMapper;

    public ResponseEntity<?> getAllDishes() {
        List<Dish> dishes = dishRepository.findAll();
        return ResponseEntity.ok(dishMapper.toDtoList(dishes));
    }

    public Long count() {
        return dishRepository.count();
    }

    public void saveAll(List<Dish> dishes) {
        dishRepository.saveAll(dishes);
    }

    public ResponseEntity<?> getDishById(String id) {
        Optional<Dish> dishOptional = dishRepository.findById(id);

        if(dishOptional.isPresent()){
            return ResponseEntity.ok(dishMapper.toDto(dishOptional.get()));
        } else {
            return ResponseEntity.badRequest().body("Dish not found");
        }
    }

    public ResponseEntity<?> updateDish(DishDto dishDto) {
        Dish dish = dishMapper.toEntity(dishDto);
        return ResponseEntity.ok(dishRepository.save(dish));
    }

    public ResponseEntity<?> deleteDish(String id) {
        Optional<Dish> dishOptional = dishRepository.findById(id);

        if(dishOptional.isPresent()){
            dishRepository.deleteById(id);
            return ResponseEntity.ok("Dish deleted");
        } else {
            return ResponseEntity.badRequest().body("Dish not found");
        }
    }

    public ResponseEntity<?> addDish(DishDto dishDto) {
        Dish dish = dishMapper.toEntity(dishDto);
        return ResponseEntity.ok(dishRepository.save(dish));
    }
}
