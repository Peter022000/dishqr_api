package com.example.DishQR_api;

import com.example.DishQR_api.config.EnableMongoTestServer;
import com.example.DishQR_api.dto.CartOrderDto;
import com.example.DishQR_api.dto.DishDto;
import com.example.DishQR_api.dto.OrderItemDto;
import com.example.DishQR_api.mapper.DishMapper;
import com.example.DishQR_api.model.*;
import com.example.DishQR_api.repository.DishRepository;
import com.example.DishQR_api.repository.QrCodeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableMongoTestServer
@EnableMongoRepositories(basePackages = "com.example.DishQR_api.repository")
class IntegrationTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DishRepository dishRepository;

	@Autowired
	private QrCodeRepository qrCodeRepository;

	private static ObjectMapper mapper;

	@Autowired
	private DishMapper dishMapper;

	@BeforeAll
	static void setUp() {
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
	}

	@Test
	public void testAddDishToEmptyOrder() throws Exception {
		// Given
		Dish dish = Dish.builder()
				.id("1")
				.dishType(DishType.mainCourse)
				.name("Pizza")
				.price(10.0)
				.ingredients(Arrays.asList("Cheese", "Tomato"))
				.build();
		dishRepository.save(dish);

		DishDto dishDto = dishMapper.toDto(dish);

		CartOrderDto orderDto = CartOrderDto.builder()
				.tableNoId("table123")
				.cost(0.0)
				.orderDishesDto(List.of())
				.paymentMethod(PaymentMethod.cash)
				.build();

		// When
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/addToOrder")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(orderDto))
						.param("dishId", dish.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();
		CartOrderDto updatedOrderDto = mapper.readValue(content, CartOrderDto.class);

		// Then
		assertNotNull(updatedOrderDto);
		assertEquals(1, updatedOrderDto.getOrderDishesDto().size());
		assertEquals(dishDto.getId(), updatedOrderDto.getOrderDishesDto().get(0).getDishDto().getId());
		assertEquals(1, updatedOrderDto.getOrderDishesDto().get(0).getQuantity());
		assertEquals(dishDto.getPrice(), updatedOrderDto.getCost());
	}

	@Test
	public void testAddNonExistingDishToEmptyOrder() throws Exception {
		// Given
		CartOrderDto orderDto = CartOrderDto.builder()
				.tableNoId("table123")
				.cost(0.0)
				.orderDishesDto(List.of())
				.paymentMethod(PaymentMethod.cash)
				.build();

		// When
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/addToOrder")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(orderDto))
						.param("dishId", "nonExistingDishId"))  // provide a non-existing dish ID here
				.andExpect(MockMvcResultMatchers.status().isBadRequest())  // expect a bad request status
				.andReturn();

		String content = result.getResponse().getContentAsString();

		// Then
		assertEquals("Dish do not exist", content);
	}

	@Test
	public void testAddSameDishToOrder() throws Exception {
		// Given
		Dish dish = Dish.builder()
				.id("1")
				.dishType(DishType.mainCourse)
				.name("Pizza")
				.price(10.0)
				.ingredients(Arrays.asList("Cheese", "Tomato"))
				.build();
		dishRepository.save(dish);

		DishDto dishDto = dishMapper.toDto(dish);

		OrderItemDto orderItemDto = OrderItemDto.builder()
				.dishDto(dishDto)
				.quantity(1)
				.cost(10.0)
				.build();

		List<OrderItemDto> orderItems = Collections.singletonList(orderItemDto);

		CartOrderDto orderDto = CartOrderDto.builder()
				.tableNoId("table123")
				.cost(10.0)
				.orderDishesDto(orderItems)
				.paymentMethod(PaymentMethod.cash)
				.build();

		// When
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/addToOrder")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(orderDto))
						.param("dishId", dish.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();
		CartOrderDto updatedOrderDto = mapper.readValue(content, CartOrderDto.class);

		// Then
		assertNotNull(updatedOrderDto);
		assertEquals(1, updatedOrderDto.getOrderDishesDto().size());
		assertEquals(dishDto.getId(), updatedOrderDto.getOrderDishesDto().get(0).getDishDto().getId());
		assertEquals(2, updatedOrderDto.getOrderDishesDto().get(0).getQuantity());
		assertEquals(dishDto.getPrice() * 2, updatedOrderDto.getCost());
	}

	@Test
	public void testAddDifferentDishesToOrder() throws Exception {
		// Given
		Dish pizza = Dish.builder()
				.id("1")
				.dishType(DishType.mainCourse)
				.name("Pizza")
				.price(10.0)
				.ingredients(Arrays.asList("Cheese", "Tomato"))
				.build();
		dishRepository.save(pizza);

		Dish pasta = Dish.builder()
				.id("2")
				.dishType(DishType.mainCourse)
				.name("Pasta")
				.price(8.0)
				.ingredients(Arrays.asList("Tomato Sauce", "Basil"))
				.build();
		dishRepository.save(pasta);

		DishDto pizzaDto = dishMapper.toDto(pizza);

		DishDto pastaDto = dishMapper.toDto(pasta);

		CartOrderDto orderDto = CartOrderDto.builder()
				.tableNoId("table123")
				.cost(0.0)
				.orderDishesDto(Collections.singletonList(
						OrderItemDto.builder()
								.dishDto(pizzaDto)
								.quantity(1)
								.cost(10.0)
								.build()
				))
				.paymentMethod(PaymentMethod.cash)
				.build();

		// When
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/addToOrder")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(orderDto))
						.param("dishId", pasta.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();
		CartOrderDto updatedOrderDto = mapper.readValue(content, CartOrderDto.class);


		OrderItemDto pizzaOrderItem = updatedOrderDto.getOrderDishesDto().stream()
				.filter(orderItem -> orderItem.getDishDto().getId().equals(pizzaDto.getId()))
				.findFirst()
				.orElse(null);

		OrderItemDto pastaOrderItem = updatedOrderDto.getOrderDishesDto().stream()
				.filter(orderItem -> orderItem.getDishDto().getId().equals(pastaDto.getId()))
				.findFirst()
				.orElse(null);

		// Then
		assertNotNull(updatedOrderDto);
		assertEquals(2, updatedOrderDto.getOrderDishesDto().size());
		assertNotNull(pizzaOrderItem);
		assertEquals(1, pizzaOrderItem.getQuantity());
		assertEquals(pizzaDto.getPrice(), pizzaOrderItem.getCost());
		assertNotNull(pastaOrderItem);
		assertEquals(1, pastaOrderItem.getQuantity());
		assertEquals(pastaDto.getPrice(), pastaOrderItem.getCost());
	}

	@Test
	public void testDecreaseDishQuantityInOrder() throws Exception {
		// Given
		Dish pizza = Dish.builder()
				.id("1")
				.dishType(DishType.mainCourse)
				.name("Pizza")
				.price(10.0)
				.ingredients(Arrays.asList("Cheese", "Tomato"))
				.build();
		dishRepository.save(pizza);

		DishDto pizzaDto = dishMapper.toDto(pizza);

		CartOrderDto orderDto = CartOrderDto.builder()
				.tableNoId("table123")
				.cost(20.0)
				.orderDishesDto(Collections.singletonList(
						OrderItemDto.builder()
								.dishDto(pizzaDto)
								.quantity(2)
								.cost(20.0)
								.build()
				))
				.paymentMethod(PaymentMethod.cash)
				.build();

		// When
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/removeFromOrder")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(orderDto))
						.param("dishId", pizza.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();
		CartOrderDto updatedOrderDto = mapper.readValue(content, CartOrderDto.class);

		OrderItemDto pizzaOrderItem = updatedOrderDto.getOrderDishesDto().stream()
				.filter(orderItem -> orderItem.getDishDto().getId().equals(pizzaDto.getId()))
				.findFirst()
				.orElse(null);

		// Then
		assertNotNull(updatedOrderDto);
		assertEquals(1, updatedOrderDto.getOrderDishesDto().size());
		assertNotNull(pizzaOrderItem);
		assertEquals(1, pizzaOrderItem.getQuantity());
		assertEquals(pizzaDto.getPrice(), pizzaOrderItem.getCost());
		assertEquals(10.0, updatedOrderDto.getCost());
	}

	@Test
	public void testDecreaseNonExistingDishQuantityInOrder() throws Exception {
		// Given
		Dish pizza = Dish.builder()
				.id("1")
				.dishType(DishType.mainCourse)
				.name("Pizza")
				.price(10.0)
				.ingredients(Arrays.asList("Cheese", "Tomato"))
				.build();
		dishRepository.save(pizza);

		Dish pasta = Dish.builder()
				.id("2")
				.dishType(DishType.mainCourse)
				.name("Pasta")
				.price(8.0)
				.ingredients(Arrays.asList("Tomato Sauce", "Basil"))
				.build();
		dishRepository.save(pasta);

		DishDto pizzaDto = dishMapper.toDto(pizza);

		CartOrderDto orderDto = CartOrderDto.builder()
				.tableNoId("table123")
				.cost(20.0)
				.orderDishesDto(Collections.singletonList(
						OrderItemDto.builder()
								.dishDto(pizzaDto)
								.quantity(2)
								.cost(20.0)
								.build()
				))
				.paymentMethod(PaymentMethod.cash)
				.build();

		// When
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/removeFromOrder")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(orderDto))
						.param("dishId", pasta.getId()))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();

		String content = result.getResponse().getContentAsString();

		// Then
		assertEquals("Order do not include dish", content);
	}

	@Test
	public void testRemoveDishFromOrderQuantityOne() throws Exception {
		// Given
		Dish pizza = Dish.builder()
				.id("1")
				.dishType(DishType.mainCourse)
				.name("Pizza")
				.price(10.0)
				.ingredients(Arrays.asList("Cheese", "Tomato"))
				.build();
		dishRepository.save(pizza);

		DishDto pizzaDto = DishDto.builder()
				.id("1")
				.dishType(DishType.mainCourse)
				.name("Pizza")
				.price(10.0)
				.ingredients(Arrays.asList("Cheese", "Tomato"))
				.build();

		CartOrderDto orderDto = CartOrderDto.builder()
				.tableNoId("table123")
				.cost(10.0)
				.orderDishesDto(Collections.singletonList(
						OrderItemDto.builder()
								.dishDto(pizzaDto)
								.quantity(1)
								.cost(10.0)
								.build()
				))
				.paymentMethod(PaymentMethod.cash)
				.build();

		// When
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/removeFromOrder")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(orderDto))
						.param("dishId", pizza.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();
		CartOrderDto updatedOrderDto = mapper.readValue(content, CartOrderDto.class);

		// Then
		assertNotNull(updatedOrderDto);
		assertTrue(updatedOrderDto.getOrderDishesDto().isEmpty());
		assertEquals(0.0, updatedOrderDto.getCost(), 0.01);
	}

	@Test
	public void testAcceptOrder() throws Exception {
		// Given
		Dish pizza = Dish.builder()
				.id("1")
				.dishType(DishType.mainCourse)
				.name("Pizza")
				.price(10.0)
				.ingredients(Arrays.asList("Cheese", "Tomato"))
				.build();
		dishRepository.save(pizza);

		String qrCode = "table123";

		DishDto pizzaDto = dishMapper.toDto(pizza);

		CartOrderDto orderDto = CartOrderDto.builder()
				.tableNoId(qrCode)
				.cost(10.0)
				.orderDishesDto(Collections.singletonList(
						OrderItemDto.builder()
								.dishDto(pizzaDto)
								.quantity(1)
								.cost(10.0)
								.build()
				))
				.paymentMethod(PaymentMethod.cash)
				.build();

		qrCodeRepository.save(QrCode.builder().id(qrCode).qrCode(qrCode).build());

		// When
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/acceptOrder")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(orderDto)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();

		Order acceptedOrder = mapper.readValue(content, Order.class);

		// Then
		assertNotNull(acceptedOrder);
		assertNotNull(acceptedOrder.getId());
		assertEquals("table123", acceptedOrder.getTableNoId());
		assertEquals(10.0, acceptedOrder.getCost());
		assertNotNull(acceptedOrder.getOrderDishes());
		assertEquals(1, acceptedOrder.getOrderDishes().size());
		assertEquals("1", acceptedOrder.getOrderDishes().get(0).getDish().getId());
		assertEquals(1, acceptedOrder.getOrderDishes().get(0).getQuantity());
		assertEquals(10.0, acceptedOrder.getOrderDishes().get(0).getCost());
		assertEquals(PaymentMethod.cash, acceptedOrder.getPaymentMethod());
	}

	@Test
	public void testAcceptOrderWithInvalidDish() throws Exception {
		// Given
		DishDto invalidDishDto = DishDto.builder()
				.id("invalidDishId")
				.dishType(DishType.mainCourse)
				.name("Invalid name")
				.price(20.0)
				.ingredients(List.of("Ingredient"))
				.build();

		Dish invalidDishInDatabase = Dish.builder()
				.id("invalidDishId")
				.dishType(DishType.mainCourse)
				.name("Valid name")
				.price(20.0)
				.ingredients(List.of("Ingredient"))
				.build();
		dishRepository.save(invalidDishInDatabase);

		CartOrderDto orderDto = CartOrderDto.builder()
				.tableNoId("table123")
				.cost(20.0)
				.orderDishesDto(Collections.singletonList(
						OrderItemDto.builder()
								.dishDto(invalidDishDto)
								.quantity(1)
								.cost(20.0)
								.build()
				))
				.paymentMethod(PaymentMethod.cash)
				.build();


		// When
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/acceptOrder")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(orderDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();

		String content = result.getResponse().getContentAsString();

		// Then
		assertEquals("One of the dishes is not valid", content);
	}

	@Test
	public void testAcceptOrderWithNoDishes() throws Exception {
		// Given
		qrCodeRepository.save(QrCode.builder().id("6576b67b1d86b15e669bff1f").type(QrCodeType.tableNo).qrCode("1").build());

		CartOrderDto orderDto = CartOrderDto.builder()
				.tableNoId("6576b67b1d86b15e669bff1f")
				.cost(20.0)
				.orderDishesDto(List.of())
				.paymentMethod(PaymentMethod.cash)
				.build();

		// When
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/acceptOrder")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(orderDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();

		String content = result.getResponse().getContentAsString();

		// Then
		assertEquals("Order is empty", content);
	}


	private static String asJsonString(final Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
