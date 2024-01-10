package com.example.DishQR_api;

import com.example.DishQR_api.config.EnableMongoTestServer;
import com.example.DishQR_api.dto.*;
import com.example.DishQR_api.model.Dish;
import com.example.DishQR_api.model.DishType;
import com.example.DishQR_api.model.PaymentMethod;
import com.example.DishQR_api.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableMongoTestServer
@EnableMongoRepositories(basePackages = "com.example.DishQR_api.repository")
class UnitTests {
	@InjectMocks
	private OrderService orderService;
	private CartOrderDto orderDto;
	private DishDto dishDto;

	@BeforeEach
	void setUp() {
		orderDto = CartOrderDto.builder()
				.tableNoId("1")
				.cost(0.0)
				.orderDishesDto(Collections.emptyList())
				.paymentMethod(PaymentMethod.card)
				.build();

		dishDto = DishDto.builder()
				.id("1")
				.dishType(DishType.mainCourse)
				.name("Example Dish")
				.price(10.0)
				.ingredients(Arrays.asList("Ingredient1", "Ingredient2"))
				.build();
	}

	@Test
	void testIsDishValid_ValidDish() {
		// Given
		Dish dbDish = Dish.builder()
				.id("1")
				.dishType(DishType.mainCourse)
				.name("Chicken Curry")
				.price(10.0)
				.ingredients(List.of("Chicken", "Curry Sauce"))
				.build();

		OrderItemDto orderItemDto = OrderItemDto.builder()
				.dishDto(DishDto.builder()
						.id("1")
						.dishType(DishType.mainCourse)
						.name("Chicken Curry")
						.price(10.0)
						.ingredients(List.of("Chicken", "Curry Sauce"))
						.build())
				.quantity(2)
				.cost(20.0)
				.build();

		// When
		boolean result = orderService.isDishValid(orderItemDto, dbDish);

		// Then
		assertTrue(result);
	}

	@Test
	void testIsDishValid_InvalidDish() {
		// Given
		Dish dbDish = Dish.builder()
				.id("1")
				.dishType(DishType.mainCourse)
				.name("Chicken Curry")
				.price(10.0)
				.ingredients(List.of("Chicken", "Curry Sauce"))
				.build();

		OrderItemDto orderItemDto = OrderItemDto.builder()
				.dishDto(DishDto.builder()
						.id("1")
						.dishType(DishType.mainCourse)
						.name("Invalid Dish Name")
						.price(10.0)
						.ingredients(List.of("Chicken", "Curry Sauce"))
						.build())
				.quantity(2)
				.cost(20.0)
				.build();

		// When
		boolean result = orderService.isDishValid(orderItemDto, dbDish);

		// Then
		assertFalse(result);
	}

	@Test
	void testIsDishTypeValid_ValidType() {
		// Given
		String orderItemType = DishType.mainCourse.name();
		String dbDishType = DishType.mainCourse.name();

		// When
		boolean result = orderService.isDishTypeValid(orderItemType, dbDishType);

		// Then
		assertTrue(result);
	}

	@Test
	void testIsDishTypeValid_InvalidType() {
		// Given
		String orderItemType = DishType.mainCourse.name();
		String dbDishType = DishType.soup.name();

		// When
		boolean result = orderService.isDishTypeValid(orderItemType, dbDishType);

		// Then
		assertFalse(result);
	}

	@Test
	void testIsDishNameValid_ValidName() {
		// Given
		String orderItemName = "Chicken Curry";
		String dbDishName = "Chicken Curry";

		// When
		boolean result = orderService.isDishNameValid(orderItemName, dbDishName);

		// Then
		assertTrue(result);
	}

	@Test
	void testIsDishNameValid_InvalidName() {
		// Given
		String orderItemName = "Chicken Curry";
		String dbDishName = "Chicken";

		// When
		boolean result = orderService.isDishNameValid(orderItemName, dbDishName);

		// Then
		assertFalse(result);
	}

	@Test
	void testIsDishPriceValid_ValidPrice() {
		// Given
		Double orderItemPrice = 10.0;
		Double dbDishPrice = 10.0;

		// When
		boolean result = orderService.isDishPriceValid(orderItemPrice, dbDishPrice);

		// Then
		assertTrue(result);
	}

	@Test
	void testIsDishPriceValid_InvalidPrice() {
		// Given
		Double orderItemPrice = 15.0;
		Double dbDishPrice = 10.0;

		// When
		boolean result = orderService.isDishPriceValid(orderItemPrice, dbDishPrice);

		// Then
		assertFalse(result);
	}

	@Test
	void testRecalculateCost() {
		// Given
		OrderItemDto orderItemDto1 = OrderItemDto.builder()
				.dishDto(dishDto)
				.quantity(2)
				.cost(20.0)
				.build();

		DishDto secondDishDto = DishDto.builder()
				.id("2")
				.dishType(DishType.mainCourse)
				.name("Second Dish")
				.price(15.0)
				.ingredients(Arrays.asList("Ingredient1", "Ingredient2"))
				.build();

		OrderItemDto orderItemDto2 = OrderItemDto.builder()
				.dishDto(secondDishDto)
				.quantity(1)
				.cost(15.0)
				.build();

		orderDto.setOrderDishesDto(Arrays.asList(orderItemDto1, orderItemDto2));
		orderDto.setOrderDiscountDto(OrderDiscountDto.builder().isUsed(false).build());

		// When
		double result = orderService.recalculateCost(orderDto).getCost();

		// Then
		assertEquals(35.0, result);
	}

	@Test
	void testRoundToTwoDecimalPlaces() {
		// Given
		double input = 12.3456;
		double expectedOutput = 12.35;

		// When
		double result = orderService.roundToTwoDecimalPlaces(input);

		// Then
		assertEquals(expectedOutput, result);
	}
}
