package com.smartRestaurant.realTimeUpdates;

import org.springframework.http.ResponseEntity;

import com.smartRestaurant.enums.Status;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

public interface RealTimeService {
	Mono<ResponseEntity<ApiResponse>> guestsLoad();

	Mono<ResponseEntity<ApiResponse>> statistics();

	Mono<ResponseEntity<ApiResponse>> orderStatus(String orderId);

	Mono<ResponseEntity<ApiResponse>> waiterCalls(String waiterId);

	Mono<ResponseEntity<ApiResponse>> menu();

	Mono<ResponseEntity<ApiResponse>> prioritizeOrders(Status status);

	Mono<ResponseEntity<ApiResponse>> allMealOrders(Status status);
}
