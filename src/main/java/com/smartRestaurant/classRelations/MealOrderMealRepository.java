package com.smartRestaurant.classRelations;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MealOrderMealRepository extends ReactiveCrudRepository<MealOrderMeal,String>{
	@Query("SELECT meal_id FROM meal_order_meal WHERE meal_order_id = :mealOrderId")
	Flux<String> findMealIdsByMealOrderId(String mealOrderId);
	
	Mono<Void> deleteAllByMealOrderId(String mealOrderId);
	Mono<Void> deleteAllByMealId(String mealId);
}
