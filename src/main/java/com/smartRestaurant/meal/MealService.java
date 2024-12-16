package com.smartRestaurant.meal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.smartRestaurant.boundaries.MealBoundary;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

public interface MealService {
	public Mono<ResponseEntity<ApiResponse>> getAllMeals(Boolean bool);

	public Mono<ResponseEntity<ApiResponse>> getMeal(String id);

	public Mono<ResponseEntity<ApiResponse>> addMeal(MealBoundary meal, MultipartFile image);

	public Mono<ResponseEntity<ApiResponse>> updateMeal(MealBoundary meal, MultipartFile image);

	public Mono<ResponseEntity<ApiResponse>> deleteMeal(String id);

	public Mono<ResponseEntity<ApiResponse>> rateMeal(String mealId, Double newRating);
}
