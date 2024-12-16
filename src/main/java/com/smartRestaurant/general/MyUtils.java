package com.smartRestaurant.general;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;

public class MyUtils {
	public static boolean isNotNullAndNotEmpty(String str) {
		return str != null && !str.isEmpty();
	}

	public static boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static boolean isNotNull(Object obj) {
		return obj != null;
	}

	public static boolean isNull(Object obj) {
		return obj == null;
	}

	public static Mono<ResponseEntity<ApiResponse>> MonoResponseEntity(HttpStatus status, String msg, Object obj) {
		return Mono.just(new ResponseEntity<>(new ApiResponse(status.value(), msg, obj), status));
	}

	public static ResponseEntity<ApiResponse> responseEntity(HttpStatus status, String msg, Object obj) {
		return new ResponseEntity<>(new ApiResponse(status.value(), msg, obj), status);
	}
}
