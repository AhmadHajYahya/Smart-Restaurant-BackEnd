package com.smartRestaurant.mealOrder;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartRestaurant.boundaries.NewMealOrderBoundary;
import com.smartRestaurant.enums.Status;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/mealOrders")
public class MealOrderController {
	private final MealOrderService mealOrderService;
	private final MealPreparationService mps;

	public MealOrderController(MealOrderService mealOrderService, MealPreparationService mps) {
		super();
		this.mealOrderService = mealOrderService;
		this.mps = mps;
	}

	// ========== CRUD Operations ==========

	@GetMapping(value = "/all", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getAllMealOrders(
			@RequestParam(name = "status", required = false) Status status) {
		return this.mealOrderService.getAllMealOrders(status);

	}

	@GetMapping(value = "/mealOrder", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getMealOrder(@RequestParam(name = "id", required = false) String id,@RequestParam(name = "userId", required = false) String userId) {
		return this.mealOrderService.getMealOrder(id,userId);
	}

	@PostMapping(value = "/add", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> createMealOrder(@RequestBody NewMealOrderBoundary mealOrder) {
		return this.mealOrderService.createMealOrder(mealOrder);
	}

	@PutMapping(value = "/update", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> updateMealOrder(
			@RequestParam(name = "orderId", required = true) String mealOrderId,
			@RequestParam(name = "status", required = true) Status status) {
		return this.mealOrderService.updateMealOrderStatus(mealOrderId, status);
	}

	@GetMapping(value = "/user-orders-history", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getUserMealOrderHistory(
			@RequestParam(name = "userId", required = true) String id) {
		return this.mealOrderService.getUserMealOrderHistory(id);
	}

	@GetMapping(value = "/prioritized-orders", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getPrioritizeOrders(
			@RequestParam(name = "status", required = true) Status status) {
		return this.mps.prioritizeMealOrders(status);
	}
}
