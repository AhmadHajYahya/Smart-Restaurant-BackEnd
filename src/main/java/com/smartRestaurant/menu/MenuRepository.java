package com.smartRestaurant.menu;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.smartRestaurant.enums.Category;
import com.smartRestaurant.meal.Meal;

import reactor.core.publisher.Flux;

public interface MenuRepository extends ReactiveCrudRepository<Meal, String> {

	// ======== Sorting ==========
	Flux<Meal> findByCategoryAndIsAvailableTrueOrderByPriceAsc(Category category);

    // By category and title
    Flux<Meal> findByCategoryAndIsAvailableTrueOrderByTitle(Category category);

    // By category and rating
    Flux<Meal> findByCategoryAndIsAvailableTrueOrderByRatingAsc(Category category);

    // By category and order count (popularity)
    Flux<Meal> findByCategoryAndIsAvailableTrueOrderByOrderCountAsc(Category category);

	// =========== Searching ============

	// In title and description
	Flux<Meal> findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String str1, String str2);

	Flux<Meal> findByIsAvailableTrue();
	
	Flux<Meal> findAllByCategoryAndIsAvailableTrue(Category category);

}
