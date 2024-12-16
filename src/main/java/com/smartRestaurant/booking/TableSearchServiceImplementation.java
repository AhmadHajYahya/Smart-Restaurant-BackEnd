package com.smartRestaurant.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.smartRestaurant.classRelations.BookingRestaurantTableRepository;
import com.smartRestaurant.dateTimeHandler.DateTimeChecker;
import com.smartRestaurant.general.GeneralDeclarations;
import com.smartRestaurant.table.RestaurantTable;
import com.smartRestaurant.table.TableService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
public class TableSearchServiceImplementation implements GeneralDeclarations, TableSearchService {
	
	private DateTimeChecker checker;
	private final TableService tableService;
	private final BookingRestaurantTableRepository bookingTableRepository;
	private final BookingRepository bookingRepository;
	
	public TableSearchServiceImplementation(DateTimeChecker checker, TableService tableService,
			BookingRestaurantTableRepository bookingTableRepository, BookingRepository bookingRepository) {
		super();
		this.checker = checker;
		this.tableService = tableService;
		this.bookingTableRepository = bookingTableRepository;
		this.bookingRepository = bookingRepository;
	}

	@Override
	public Mono<Set<RestaurantTable>> searchTables(LocalDate date, LocalTime time, int guestsNumber) {
		// Calculate the midpoint of guest capacity to decide search strategy
		int mid = MINGUESTS + ((MAXGUESTS - MINGUESTS) / 2);
		// Check if the number of guests is even
		boolean isGuestNumberEven = guestsNumber % 2 == 0;

		// Adjust the number of guests if odd to find an even number of seats
		if (!isGuestNumberEven) {
			guestsNumber++;
		}
		int temp = guestsNumber;
		// Choose a searching strategy based on whether guestsNumber is less than or
		// equal to mid
		if (temp <= mid) {
			return find(date, time, guestsNumber).flatMap(foundTables -> {
				if (foundTables.isEmpty()) {
					Map<Integer, Integer> map = tablesMap(temp);
					return find(date, time, mapElementsAsArr(map));
				}
				return Mono.just(foundTables);
			});
		} else {
			Map<Integer, Integer> map = tablesMap(temp);
			return find(date, time, mapElementsAsArr(map));
		}
	}
	
	
	@Override
	public Mono<Set<RestaurantTable>> find(LocalDate date, LocalTime time, int... seats) {
		// Convert int[] to Integer[]
		Integer[] seatObjects = Arrays.stream(seats).boxed().toArray(Integer[]::new);

		// Get the map of all tables categorized by seat count
		return this.tableService.getAllTablesMap().flatMapMany(map ->
		// Create a Flux from the array of seat counts
		Flux.fromArray(seatObjects)
				// For each seat count, find available tables
				.flatMap(seatCount -> findAvailableTable(map.get(seatCount), date, time)))
				.collect(Collectors.toSet()); // Collect the results into a set
	}

	private Flux<RestaurantTable> findAvailableTable(List<RestaurantTable> tables, LocalDate date, LocalTime time) {
		if (tables == null) {
			return Flux.empty(); // Return empty Flux if no tables are available for the given seat count
		}
		return findLowestBookingSize(tables).filterWhen(table -> checkTableAvailability(table, date, time)).flux();
	}

	private Mono<Boolean> checkTableAvailability(RestaurantTable table, LocalDate date, LocalTime time) {
		// Find all booking IDs associated with the table ID
		return this.bookingTableRepository.findBookingIdsByTableId(table.getTableId())
				// For each booking ID, find the booking details
				.flatMap(bookingId -> this.bookingRepository.findById(bookingId))
				// Collect the bookings into a list
				.collectList()
				// Check if the table is available for the specified date and time
				.map(bookings -> isTableAvailable(bookings, date, time));
	}

	private boolean isTableAvailable(List<Booking> bookings, LocalDate date, LocalTime time) {
		// Check if there is no booking on the same date
		return !this.checker.hasBookingOnThisDate(bookings, date) ||
		// Check if there is no booking at the same time and within one hour
		// before/after the specified time
				!this.checker.hasBookingAtOrNearTime(bookings, date,time);
	}

	

	private int[] mapElementsAsArr(Map<Integer, Integer> map) {
		// Convert each map entry into an int stream, repeated by its value, then
		// collect into an array
		return map.entrySet().stream().flatMapToInt(e -> IntStream.generate(() -> e.getKey()).limit(e.getValue()))
				.toArray();
	}

	private Mono<RestaurantTable> findLowestBookingSize(List<RestaurantTable> tables) {
		// Convert the list of tables to a Flux
		return Flux.fromIterable(tables)
				// For each table, find the count of bookings associated with it
				.flatMap(table -> this.bookingTableRepository.findBookingIdsByTableId(table.getTableId()).count()
						// Map each table to a tuple containing the table and its booking count
						.map(count -> Tuples.of(table, count)))
				// Sort the tables by booking count in ascending order
				.sort((tuple1, tuple2) -> Long.compare(tuple1.getT2(), tuple2.getT2()))
				// Map the result to get the table with the lowest booking count
				.map(Tuple2::getT1)
				// Get the first table from the sorted result
				.next();
	}

	private Map<Integer, Integer> tablesMap(int guestsNumber) {
		Map<Integer, Integer> map = new HashMap<>();
		// Iterate over seats array to determine the number of tables needed for each
		// type of table
		for (int seats : SEATS) {
			int neededTables = guestsNumber / seats;
			if (neededTables > 0) {
				map.put(seats, neededTables);
				guestsNumber %= seats;
			}
			if (guestsNumber == 0)
				break;
		}
		return map;
	}
}
