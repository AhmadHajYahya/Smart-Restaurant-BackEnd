package com.smartRestaurant.mealOrder;


import java.time.LocalDate;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.smartRestaurant.enums.Status;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
public interface MealOrderRepository extends ReactiveCrudRepository<MealOrder,String>{
	Flux<MealOrder> findAllByStatus(Status status);
	
	@Query("SELECT MAX(CAST(SUBSTRING(m_order_id, 2) AS SIGNED)) FROM meal_order")
	Mono<Integer> countAllMealOrders();
	
	Mono<MealOrder> findByUserIdAndDateLikeAndStatusNot(String userId, LocalDate date, Status status);
	
	Flux<MealOrder> findAllByUserId(String userId);
	
	Flux<MealOrder> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}
