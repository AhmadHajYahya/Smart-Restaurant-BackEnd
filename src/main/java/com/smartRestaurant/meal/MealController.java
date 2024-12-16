package com.smartRestaurant.meal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartRestaurant.boundaries.RateMealBoundary;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/meals")
public class MealController {
	private final MealService mealService;

	public MealController(MealService mealService) {
		super();
		this.mealService = mealService;
	}

	@GetMapping(path = "/all", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getAllMeals(
			@RequestParam(name = "available", required = false) Boolean bool) {
		return this.mealService.getAllMeals(bool);

	}

	@GetMapping(path = "/meal", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getMeal(@RequestParam(name = "id", required = true) String id) {
		return this.mealService.getMeal(id);
	}

	@PostMapping(path = "/rate-meal", consumes = { MediaType.APPLICATION_JSON_VALUE } ,produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> rateMeal(@RequestBody RateMealBoundary boundary) {
		return this.mealService.rateMeal(boundary.getMealId(), boundary.getRating());
	}
}
