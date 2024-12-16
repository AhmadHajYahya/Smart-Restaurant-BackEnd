package com.smartRestaurant.menu;

import org.springframework.http.ResponseEntity;

import com.smartRestaurant.enums.Category;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

public interface MenuService {

	Mono<ResponseEntity<ApiResponse>> getAvailableMeals();

	Mono<ResponseEntity<ApiResponse>> getAllByCategory(Category category);

	// ======== sort =========
	Mono<ResponseEntity<ApiResponse>> sortBy(Category category, String sortField);

	// ======== search ========

	// search in title and description.
	Mono<ResponseEntity<ApiResponse>> searchInTitleAndDescription(String str);

}
