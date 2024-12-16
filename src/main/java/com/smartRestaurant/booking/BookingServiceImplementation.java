package com.smartRestaurant.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.smartRestaurant.boundaries.BookingBoundary;
import com.smartRestaurant.classRelations.BookingRestaurantTable;
import com.smartRestaurant.classRelations.BookingRestaurantTableRepository;
import com.smartRestaurant.dateTimeHandler.DateTimeChecker;
import com.smartRestaurant.enums.Roles;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.ExceptionHandler;
import com.smartRestaurant.general.GeneralDeclarations;
import com.smartRestaurant.general.IdGenerator;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.general.MyUtils;
import com.smartRestaurant.table.RestaurantTable;
import com.smartRestaurant.table.TableRepository;
import com.smartRestaurant.table.TableService;
import com.smartRestaurant.user.User;
import com.smartRestaurant.user.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
public class BookingServiceImplementation implements GeneralDeclarations, BookingService {
	private final BookingRepository bookingRepository;
	private final TableRepository tableRepository;
	private final BookingRestaurantTableRepository bookingTableRepository;
	private final UserRepository userRepository;
	private final TableSearchService tableSearchService;

	public BookingServiceImplementation(BookingRepository bookingRepository, TableRepository tableRepository,
			BookingRestaurantTableRepository bookingTableRepository, UserRepository userRepository,
			DateTimeChecker checker, TableService tableService, TableSearchService tableSearchService) {
		super();
		this.bookingRepository = bookingRepository;
		this.tableRepository = tableRepository;
		this.bookingTableRepository = bookingTableRepository;
		this.userRepository = userRepository;
		this.tableSearchService = tableSearchService;
	}

	@Override
	public boolean validGuestsNumber(int guestsNumber) {
		return MINGUESTS <= guestsNumber && guestsNumber <= MAXGUESTS;
	}

	// ========== CRUD Operations ==========

	@Override
	public Mono<ResponseEntity<ApiResponse>> createBooking(BookingBoundary booking) {
		return validateBookingRequest(booking) // Validate the booking request
				.flatMap(validBooking -> checkUserPermissions(validBooking) // Check user permissions
						.flatMap(user -> ensureNoExistingBooking(validBooking) // Ensure no existing booking for the
																				// user on the same date
								.flatMap(existingBooking -> searchAndBookTables(validBooking) // Search for available
																								// tables and book them
										.flatMap(foundTables -> saveBookingAndTables(
												Tuples.of(validBooking, foundTables)) // Save the booking and associated
																						// tables
										)))).onErrorResume(ExceptionHandler::handleErrors); // Handle any errors that occur
																						// during the process
	}

	private Mono<BookingBoundary> validateBookingRequest(BookingBoundary booking) {
		// Check if the number of guests is valid
		if (!validGuestsNumber(booking.getGuestsNumber()) || booking.getGuestsNumber() == null) {
			return Mono.error(new IllegalArgumentException(
					String.format("Invalid, allowed guests number is %d-%d guests", MINGUESTS, MAXGUESTS)));
		}
		// Check if the date, time, and user ID are provided
		if (MyUtils.isNull(booking.getDate()) || MyUtils.isNull(booking.getTime())
				|| MyUtils.isNullOrEmpty(booking.getUserId())) {
			return Mono.error(new IllegalArgumentException(MsgCreator.nullOrEmpty()));
		}
		// Check if the booking date is not in the past
		if (booking.getDate().isBefore(LocalDate.now())) {
			return Mono.error(new IllegalArgumentException(MsgCreator.invalidDateTime("date", LocalDate.now())));
		}
		// Check if the booking time is not in the past if the booking date is today
		if (booking.getDate().equals(LocalDate.now()) && booking.getTime().isBefore(LocalTime.now())) {
			return Mono.error(new IllegalArgumentException(
					MsgCreator.invalidDateTime("time", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))));
		}
		return Mono.just(booking); // Return the validated booking
	}

	private Mono<User> checkUserPermissions(BookingBoundary booking) {
		return userRepository.findById(booking.getUserId()).flatMap(user -> {
			// Check if the user has the role of REGISTERED_USER
			if (!user.getRole().equals(Roles.REGISTERED_USER)) {
				return Mono.error(new SecurityException("You don't have permission to make a booking"));
			}
			return Mono.just(user); // Return the user if they have the necessary permissions
		});
	}

	private Mono<BookingBoundary> ensureNoExistingBooking(BookingBoundary booking) {
		return bookingRepository.findAllByUserIdAndDate(booking.getUserId(), booking.getDate()).collectList()
				.flatMap(existingBookings -> {
					// Check if there are any existing bookings for the user on the same date
					if (!existingBookings.isEmpty()) {
						return Mono.error(new IllegalStateException("This user already has a booking on this date"));
					}
					return Mono.just(booking); // Pass the booking boundary to the next step
				});
	}

	private Mono<Set<RestaurantTable>> searchAndBookTables(BookingBoundary booking) {
		return this.tableSearchService.searchTables(booking.getDate(), booking.getTime(), booking.getGuestsNumber())
				.flatMap(foundTables -> {
					// Check if any tables were found
					if (foundTables.isEmpty()) {
						return Mono.error(new IllegalStateException("There are no available tables to book."));
					}
					// Mark the found tables as booked and save them
					return Flux.fromIterable(foundTables).flatMap(table -> {
						table.setIsBooked(true);
						table.setNewEntry(false);
						return tableRepository.save(table);
					}).then(Mono.just(foundTables));
				});
	}

	private Mono<ResponseEntity<ApiResponse>> saveBookingAndTables(Tuple2<BookingBoundary, Set<RestaurantTable>> data) {
		BookingBoundary booking = data.getT1();
		Set<RestaurantTable> tables = data.getT2();

		Booking newBooking = booking.toEntity();
		return bookingRepository.countAllBookings().defaultIfEmpty(0)
				.map(maxNum -> IdGenerator.generateBookingId(maxNum + 1)).flatMap(bId -> {
					newBooking.setBookingId(bId);
					return bookingRepository.save(newBooking).flatMap(savedBooking -> {
						return userRepository.findById(savedBooking.getUserId()).flatMap(user -> {
							// Update the user's booking count
							user.setBookingsCount(user.getBookingsCount() + 1);
							user.setNew(false);
							return userRepository.save(user);
						}).thenMany(Flux.fromIterable(tables)
								// Save the association between the booking and the tables
								.flatMap(table -> bookingTableRepository.save(
										new BookingRestaurantTable(savedBooking.getBookingId(), table.getTableId()))))
								// Return the response entity with the result of the booking save operation
								.then(MyUtils.MonoResponseEntity(HttpStatus.OK, MsgCreator.created("Booking"),
										new BookingBoundary(savedBooking)));
					});
				});
	}

	

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllBookings() {
		return this.bookingRepository.findAll().map(BookingBoundary::new).collectList().map(bookingBoundaries -> MyUtils
				.responseEntity(HttpStatus.OK, MsgCreator.fetched("Bookings"), bookingBoundaries)).log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getBooking(String id) {
		return this.bookingRepository.findById(id)
				.map(booking -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Booking"),
						new BookingBoundary(booking)))
				.switchIfEmpty(MyUtils.MonoResponseEntity(HttpStatus.NOT_FOUND, MsgCreator.notFound("Booking"), null))
				.log();

	}

	@Override
	// delete or cancellation, same function.
	public Mono<ResponseEntity<ApiResponse>> deleteBooking(String bookginId) {

		return this.bookingRepository.findById(bookginId).flatMap(booking ->
		// Fetch all table IDs associated with the booking
		this.bookingTableRepository.findTableIdsByBookingId(bookginId).collectList()
				.flatMap(tableIds -> this.bookingTableRepository.deleteByBookingId(bookginId)
						.thenMany(Flux.fromIterable(tableIds)).flatMap(tableId -> this.bookingTableRepository
								.findBookingIdsByTableId(tableId).collectList().flatMap(bookingIds -> {
									// Check if there are no more bookings associated with this table
									if (bookingIds.isEmpty() || (bookingIds.size() == 1 && bookingIds.contains(bookginId))) {
										return tableRepository.findById(tableId).flatMap(table -> {
											table.setIsBooked(false); // Update the table's booking status
											table.setNewEntry(false);
											return tableRepository.save(table); // Save the updated table
										});
									}
									return Mono.empty();
								}))
						.then())
				.then(this.bookingRepository.delete(booking))
				.then(MyUtils.MonoResponseEntity(HttpStatus.NO_CONTENT, "", null))
				.switchIfEmpty(MyUtils.MonoResponseEntity(HttpStatus.NOT_FOUND, MsgCreator.notFound("Booking"), null)));
	}

	// Retrieves the time of the booking that the user has today.
	@Override
	public Mono<ResponseEntity<ApiResponse>> getUserBookingTime(String userId) {
		return this.bookingRepository.findByUserIdAndDate(userId, LocalDate.now()).flatMap(booking -> MyUtils
				.MonoResponseEntity(HttpStatus.OK, "Booking time fetched successfully", booking.getTime()));
	}
	
	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllUserBookings(String userId) {
		return this.bookingRepository.findAllByUserId(userId).map(BookingBoundary::new).collectList().map(bookingBoundaries -> MyUtils
				.responseEntity(HttpStatus.OK, MsgCreator.fetched("Bookings"), bookingBoundaries)).log();
	}

}
