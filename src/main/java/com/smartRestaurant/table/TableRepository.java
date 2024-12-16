package com.smartRestaurant.table;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TableRepository extends ReactiveCrudRepository<RestaurantTable, String> {
	Flux<RestaurantTable> findAllByIsTaken(boolean value);
	Mono<RestaurantTable> findFirstByIsTakenFalse();
	Mono<RestaurantTable> findFirstByIsTakenFalseAndSeatsNumber(Integer seatsNumber);
	
	Mono<RestaurantTable> findByuserId(String userId);
	
	Mono<Boolean> existsByUserId(String userId);
	
	@Query("SELECT MAX(CAST(SUBSTRING(table_id, 2) AS SIGNED)) FROM restaurant_table")
	Mono<Integer> countAllTables();
	
	Mono<Long> countByIsTaken(boolean isTaken);
}
