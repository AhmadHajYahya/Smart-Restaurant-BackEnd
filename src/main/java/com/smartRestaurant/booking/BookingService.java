package com.smartRestaurant.booking;

import org.springframework.http.ResponseEntity;

import com.smartRestaurant.boundaries.BookingBoundary;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

public interface BookingService {

	public boolean validGuestsNumber(int guestsNumber);

	public Mono<ResponseEntity<ApiResponse>> createBooking(BookingBoundary booking);

	public Mono<ResponseEntity<ApiResponse>> getAllBookings();

	public Mono<ResponseEntity<ApiResponse>> getBooking(String id);

	public Mono<ResponseEntity<ApiResponse>> deleteBooking(String id);

	public Mono<ResponseEntity<ApiResponse>> getUserBookingTime(String userId);

	public Mono<ResponseEntity<ApiResponse>> getAllUserBookings(String userId);
}
