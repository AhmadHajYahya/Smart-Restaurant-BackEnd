package com.smartRestaurant.general;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;

public class ExceptionHandler {

	public static Mono<ResponseEntity<ApiResponse>> handleErrors(Throwable throwable) {
		if (throwable instanceof IllegalArgumentException || throwable instanceof IllegalStateException) {
			return MyUtils.MonoResponseEntity(HttpStatus.BAD_REQUEST, throwable.getMessage(), null);
		} else if (throwable instanceof SecurityException) {
			return MyUtils.MonoResponseEntity(HttpStatus.UNAUTHORIZED, throwable.getMessage(), null);
		}
		return MyUtils.MonoResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
	}
}
