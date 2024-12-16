package com.smartRestaurant.booking;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartRestaurant.boundaries.BookingBoundary;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bookings")
public class BookingController {
	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		super();
		this.bookingService = bookingService;
	}

	@GetMapping(value = "/all", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getAllBookings() {
		return this.bookingService.getAllBookings();
	}

	@GetMapping(value = "/booking", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getBooking(@RequestParam(name = "id", required = true) String id) {
		return this.bookingService.getBooking(id);
	}

	@PostMapping(value = "/add", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> createBooking(@RequestBody BookingBoundary booking) {
		return this.bookingService.createBooking(booking);
	}

	@DeleteMapping(value = "/delete", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> deleteBooking(@RequestParam(name = "bookingId", required = true) String bookingId) {
		return this.bookingService.deleteBooking(bookingId);
	}

	
	@GetMapping(value = "/booking-time", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> userBookingTime(
			@RequestParam(name = "userId", required = true) String userId) {
		return this.bookingService.getUserBookingTime(userId);
	}
	
	@GetMapping(value = "/all/user-bookings", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getAllUserBookings(@RequestParam(name = "userId", required = true) String userId) {
		return this.bookingService.getAllUserBookings(userId);
	}
}
