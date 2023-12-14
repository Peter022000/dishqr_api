package com.example.DishQR_api.config;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class YamlLoader {

    public static DishesConfig loadDishesConfig(String filePath) {
        try {
            InputStream inputStream = Objects.requireNonNull(
                    YamlLoader.class.getClassLoader().getResourceAsStream(filePath),
                    "Resource not found: " + filePath
            );
            YamlReader yamlReader = new YamlReader(new InputStreamReader(inputStream));
            return yamlReader.read(DishesConfig.class);
        } catch (Exception e) {
            System.out.println("Error loading dishes from YAML file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error loading dishes from YAML file", e);
        }
    }
}
