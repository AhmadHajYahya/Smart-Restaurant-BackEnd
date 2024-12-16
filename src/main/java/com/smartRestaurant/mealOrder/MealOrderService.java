package com.smartRestaurant.mealOrder;

import org.springframework.http.ResponseEntity;

import com.smartRestaurant.boundaries.NewMealOrderBoundary;
import com.smartRestaurant.enums.Status;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

public interface MealOrderService {

	public Mono<ResponseEntity<ApiResponse>> createMealOrder(NewMealOrderBoundary mealOrder);

	public Mono<ResponseEntity<ApiResponse>> getAllMealOrders(Status status);

	public Mono<ResponseEntity<ApiResponse>> getMealOrder(String id, String userId);

	public Mono<ResponseEntity<ApiResponse>> updateMealOrderStatus(String mealOrderId, Status status);

	public Mono<ResponseEntity<ApiResponse>> deleteMealOrder(String id);
	
	public Mono<ResponseEntity<ApiResponse>> getUserMealOrderHistory(String userId);
}
