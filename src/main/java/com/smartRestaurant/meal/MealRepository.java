package com.smartRestaurant.meal;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MealRepository extends ReactiveCrudRepository<Meal, String> {
	Flux<Meal> findByIsAvailable(boolean bool);

	@Query("SELECT MAX(CAST(SUBSTRING(meal_id, 2) AS SIGNED)) FROM meal")
	Mono<Integer> countAllMeals();

	Flux<Meal> findTop3ByOrderByOrderCountDesc();
}
