package com.smartRestaurant.booking;

import java.time.LocalDate;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BookingRepository extends ReactiveCrudRepository<Booking, String> {
	Flux<Booking> findAllByUserIdAndDate(String userId, LocalDate date);
	Flux<Booking> findAllByUserId(String userId);
	Mono<Booking> findByUserIdAndDate(String userId, LocalDate date);
	
	@Query("SELECT MAX(CAST(SUBSTRING(booking_id, 2) AS SIGNED)) FROM booking")
	Mono<Integer> countAllBookings();
	
	Flux<Booking> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
	
	Mono<Void> deleteAllByUserId(String userId);
}
