package com.smartRestaurant.callWaiter;

import org.springframework.http.ResponseEntity;

import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

public interface CallWaiterService {

	public Mono<ResponseEntity<ApiResponse>> callWaiter(String tableId);

	public Mono<ResponseEntity<ApiResponse>> getAllWaiterCalls(String waiterId);

	Mono<ResponseEntity<ApiResponse>> deleteCall(String callId);
}
