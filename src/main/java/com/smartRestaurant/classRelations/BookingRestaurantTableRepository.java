package com.smartRestaurant.classRelations;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingRestaurantTableRepository extends ReactiveCrudRepository<BookingRestaurantTable, String> {
	// Fetch all booking IDs associated with a given table ID
	@Query("SELECT booking_id FROM booking_restaurantTable WHERE table_id = :tableId")
	Flux<String> findBookingIdsByTableId(String tableId);

	// Fetch all table IDs associated with a given booking ID
	@Query("SELECT table_id FROM booking_restaurantTable WHERE booking_id = :bookingId")
	Flux<String> findTableIdsByBookingId(String bookingId);
	
	Mono<Void> deleteByBookingId(String id);
}
