package com.smartRestaurant.dateTimeHandler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smartRestaurant.booking.Booking;
import com.smartRestaurant.booking.BookingRepository;
import com.smartRestaurant.classRelations.BookingRestaurantTableRepository;
import com.smartRestaurant.table.RestaurantTable;

import reactor.core.publisher.Mono;

@Service
public class DateTimeChecker {

	private final BookingRepository bookingRepository;
	private final BookingRestaurantTableRepository bookingTableRepository;

	
	public DateTimeChecker(BookingRepository bookingRepository,
			BookingRestaurantTableRepository bookingTableRepository) {
		super();
		this.bookingRepository = bookingRepository;
		this.bookingTableRepository = bookingTableRepository;
	}

	// Check if there is a booking on this date in bookings Set.
	public boolean hasBookingOnThisDate(List<Booking> bookings, LocalDate date) {
		for (Booking booking : bookings) {
			if (booking.getDate().equals(date)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasBookingAtOrNearTime(List<Booking> bookings, LocalDate date, LocalTime time) {
	    LocalTime endRange = time.plusMinutes(29);
	    
	    for (Booking booking : bookings) {
	        if (booking.getDate().equals(date)) {
	            LocalTime bookingTime = booking.getTime();
	            if (bookingTime.equals(time) || (bookingTime.isAfter(time) && bookingTime.isBefore(endRange))) {
	                return true;
	            }
	        }
	    }
	    return false;
	}

	// check if this table has booking in half hour (30 min).
	public Mono<String> hasBookingAfterHalfHour(RestaurantTable table) {
		// the date now.
		LocalDate date = LocalDate.now();
		// the time now.
		LocalTime startRange = LocalTime.now();
		// the time after 30 min.
		LocalTime endRange = startRange.plusMinutes(30);

		return this.bookingTableRepository.findBookingIdsByTableId(table.getTableId())
				.flatMap(bookingId -> this.bookingRepository.findById(bookingId))
				.filter(booking -> booking.getDate().equals(date) && booking.getTime().isAfter(startRange)
						&& booking.getTime().isBefore(endRange))
				.next().map(Booking::getUserId).switchIfEmpty(Mono.empty());

	}

}
