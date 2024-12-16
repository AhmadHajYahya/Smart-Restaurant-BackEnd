package com.smartRestaurant.receipt;

import org.springframework.http.ResponseEntity;

import com.smartRestaurant.boundaries.ReceiptBoundary;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

public interface ReceiptService {
	public Mono<ResponseEntity<ApiResponse>> getAllReceipts();

	public Mono<ResponseEntity<ApiResponse>> getReceipt(String id);

	public Mono<ResponseEntity<ApiResponse>> createReceipt(ReceiptBoundary receipt);

	public Mono<ResponseEntity<ApiResponse>> deleteReceipt(String id);
}
