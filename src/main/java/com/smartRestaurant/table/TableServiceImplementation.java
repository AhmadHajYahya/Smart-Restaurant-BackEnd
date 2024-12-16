package com.smartRestaurant.table;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.smartRestaurant.booking.Booking;
import com.smartRestaurant.booking.BookingRepository;
import com.smartRestaurant.boundaries.RestaurantTableBoundary;
import com.smartRestaurant.classRelations.BookingRestaurantTableRepository;
import com.smartRestaurant.dateTimeHandler.DateTimeChecker;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.ExceptionHandler;
import com.smartRestaurant.general.GeneralDeclarations;
import com.smartRestaurant.general.IdGenerator;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.general.MyUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TableServiceImplementation implements GeneralDeclarations, TableService {
	private final TableRepository tableRepository;
	private DateTimeChecker checker;
	private final BookingRepository bookingRepository;
	private final BookingRestaurantTableRepository bookingTableRepository;

	public TableServiceImplementation(TableRepository tableRepository, DateTimeChecker checker,
			BookingRepository bookingRepository, BookingRestaurantTableRepository bookingTableRepository) {
		super();
		this.tableRepository = tableRepository;
		this.checker = checker;
		this.bookingRepository = bookingRepository;
		this.bookingTableRepository = bookingTableRepository;
	}

	// ========== Service methods ==========
	@Override
	public Mono<ResponseEntity<ApiResponse>> takeTable(String userId, Integer guestsNumber) {
		return this.tableRepository.existsByUserId(userId)
				.flatMap(associated -> associated ? handleExistingTableAssignment(userId)
						: handleNewTableAssignment(userId, guestsNumber))
				.onErrorResume(ExceptionHandler::handleErrors).log();
	}

	private Mono<ResponseEntity<ApiResponse>> handleExistingTableAssignment(String userId) {
		return this.tableRepository.findByuserId(userId)
				.flatMap(table -> MyUtils.MonoResponseEntity(HttpStatus.BAD_REQUEST,
						"You are already assigned to table " + table.getTableId(), table.getTableId()));
	}

	private Mono<ResponseEntity<ApiResponse>> handleNewTableAssignment(String userId, Integer guestsNumber) {
		return this.bookingRepository.findByUserIdAndDate(userId, LocalDate.now())
				.flatMap(booking -> processBooking(booking, userId))
				.switchIfEmpty(findAndAssignTable(userId, guestsNumber));
	}

	private Mono<ResponseEntity<ApiResponse>> processBooking(Booking booking, String userId) {
		return this.bookingTableRepository.findTableIdsByBookingId(booking.getBookingId())
				.flatMap(tableId -> this.tableRepository.findById(tableId))
				.flatMap(table -> updateTableStatus(table, userId)).collectList().flatMap(tables -> {
					if (!tables.isEmpty()) {
						String tableIds = tables.stream().map(RestaurantTable::getTableId)
								.collect(Collectors.joining(", "));
						return MyUtils.MonoResponseEntity(HttpStatus.OK, "You are assigned to table " + tableIds,
								tables);
					}
					return MyUtils.MonoResponseEntity(HttpStatus.BAD_REQUEST, "No tables were found for your booking.",
							null);
				});
	}

	private Mono<RestaurantTable> updateTableStatus(RestaurantTable table, String userId) {
		table.setIsTaken(true);
		table.setUserId(userId);
		table.setNewEntry(false);
		return this.tableRepository.save(table);
	}

	private Mono<ResponseEntity<ApiResponse>> findAndAssignTable(String userId, Integer guestsNumber) {
		return this.tableRepository.findAllByIsTaken(false)
				.flatMap(table -> handlePotentiallyBookedTable(table, userId))
				.then(assignTableToUser(userId, guestsNumber)).switchIfEmpty(
						MyUtils.MonoResponseEntity(HttpStatus.BAD_REQUEST, "There are no available tables.", null));
	}

	private Mono<RestaurantTable> handlePotentiallyBookedTable(RestaurantTable table, String userId) {
		return table.getIsBooked() ? checker.hasBookingAfterHalfHour(table).flatMap(bookingUserId -> {
			table.setIsTaken(true);
			table.setUserId(bookingUserId);
			table.setNewEntry(false);
			return this.tableRepository.save(table);
		}) : Mono.just(table);
	}

	private Mono<ResponseEntity<ApiResponse>> assignTableToUser(String userId, Integer guestsNumber) {
		if (guestsNumber > 8) {
			return Mono.error(new IllegalArgumentException("This is a big number of guests, you have to book tables."));
		} else {
			if (guestsNumber % 2 != 0) {
				guestsNumber += 1;
			}
			return this.tableRepository.findFirstByIsTakenFalseAndSeatsNumber(guestsNumber).flatMap(table -> {
				table.setIsTaken(true);
				table.setUserId(userId);
				table.setNewEntry(false);
				return this.tableRepository.save(table).map(updatedTable -> MyUtils.responseEntity(HttpStatus.OK,
						"You are assigned to table " + updatedTable.getTableId(), updatedTable.getTableId()));
			});
		}
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> freeTable(String tableId) {
		return this.tableRepository.findById(tableId).flatMap(this::updateTableToFree)
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Table"))))
				.onErrorResume(ExceptionHandler::handleErrors).log();
	}

	private Mono<ResponseEntity<ApiResponse>> updateTableToFree(RestaurantTable table) {
		table.setIsTaken(false);
		table.setUserId(null);
		table.setNewEntry(false);
		return this.tableRepository.save(table)
				.then(MyUtils.MonoResponseEntity(HttpStatus.OK, "Thank you for coming", null));
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getGuestsLoad() {
		return this.tableRepository.countByIsTaken(true)
				.zipWith(this.tableRepository.count(), (taken, total) -> calculateLoad(taken, total))
				.onErrorResume(ExceptionHandler::handleErrors).log();
	}

	private ResponseEntity<ApiResponse> calculateLoad(long taken, long total) {
		double percentage = (double) taken / total * 100;
		Map<String, String> data = new HashMap<>();
		data.put("load", determineLoadLevel(percentage));
		data.put("timeToVisit", determineVisitTime(percentage));
		data.put("percentage", Math.round(percentage) + "%");
		return MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Guests Load"), data);
	}

	private String determineLoadLevel(double percentage) {
		if (percentage == 100)
			return "Full";
		if (percentage >= 75)
			return "Very High";
		if (percentage >= 50)
			return "High";
		if (percentage >= 25)
			return "Medium";
		return "Low";
	}

	private String determineVisitTime(double percentage) {
		if (percentage == 100)
			return "The restaurant is currently at full capacity. Please check back later.";
		if (percentage >= 75)
			return "The restaurant is nearly full. We recommend visiting after 30 minutes.";
		if (percentage >= 50)
			return "The restaurant is quite busy. We recommend visiting after 20 minutes.";
		if (percentage >= 25)
			return "The restaurant is moderately busy. We recommend visiting after 10 minutes.";
		return "The restaurant is not very busy. You can visit now.";
	}

	// ========== CRUD Operations ==========
	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllTables(Boolean isTaken) {
		Flux<RestaurantTableBoundary> tables;

		if (MyUtils.isNotNull(isTaken)) {
			tables = this.tableRepository.findAllByIsTaken(isTaken).map(RestaurantTableBoundary::new);
		} else {
			tables = this.tableRepository.findAll().map(RestaurantTableBoundary::new);
		}

		return tables.collectList() // Collects all the items in the Flux into a List
				.map(tableList -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Tables"), tableList)).log();
	}

	@Override
	public Mono<Map<Integer, List<RestaurantTable>>> getAllTablesMap() {
		return this.tableRepository.findAll().collect(groupingBy(RestaurantTable::getSeatsNumber, toList()));
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getTable(String id) {
		return this.tableRepository.findById(id).map(RestaurantTableBoundary::new)
				.map(tableBoundary -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Table"), tableBoundary))
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Table"))))
				.onErrorResume(ExceptionHandler::handleErrors).log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getTableIdByUserId(String userId) {
		return this.tableRepository.findByuserId(userId)
				.flatMap(table -> MyUtils.MonoResponseEntity(HttpStatus.OK, "", table.getTableId()))
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Table"))))
				.onErrorResume(ExceptionHandler::handleErrors).log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> addTable(RestaurantTableBoundary table) {
		if (table.getSeats_number() == null) {
			return Mono.error(new IllegalArgumentException("No seats number inserted"));
		} else if (table.getSeats_number() == 0 || table.getSeats_number() % 2 != 0
				|| !(table.getSeats_number() <= 8 && table.getSeats_number() >= 2)) {
			return Mono.error(new IllegalArgumentException("Seats number must be even and between 2-8"));
		}

		return this.tableRepository.countAllTables().defaultIfEmpty(0)
				.map(maxNum -> IdGenerator.generateTableId(maxNum + 1)).flatMap(tableId -> {
					table.setTableId(tableId);
					table.setIsBooked(false);
					table.setIsTaken(false);
					table.setUserId(null);
					return Mono.just(table).map(RestaurantTableBoundary::toEntity).flatMap(this.tableRepository::save)
							.map(RestaurantTableBoundary::new).map(boundary -> MyUtils
									.responseEntity(HttpStatus.CREATED, MsgCreator.created("Table"), boundary));
				}).onErrorResume(ExceptionHandler::handleErrors).log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> updateTable(RestaurantTableBoundary table) {
		return this.tableRepository.findById(table.getTableId()).flatMap(entity -> updateTableEntity(entity, table))
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Table"))))
				.onErrorResume(ExceptionHandler::handleErrors).log();
	}

	private Mono<ResponseEntity<ApiResponse>> updateTableEntity(RestaurantTable entity, RestaurantTableBoundary table) {
		if (MyUtils.isNotNull(table.getSeats_number()) && table.getSeats_number() % 2 == 0
				&& (table.getSeats_number() <= 8 && table.getSeats_number() >= 2)) {
			entity.setSeatsNumber(table.getSeats_number());
		}
		if (MyUtils.isNotNull(table.getIsBooked())) {
			entity.setIsBooked(table.getIsBooked());
		}
		if (MyUtils.isNotNull(table.getIsTaken())) {
			entity.setIsTaken(table.getIsTaken());
		}
		if (MyUtils.isNotNullAndNotEmpty(table.getUserId())) {
			entity.setUserId(table.getUserId());
		}
		entity.setNewEntry(false);
		return this.tableRepository.save(entity).map(savedEntity -> MyUtils.responseEntity(HttpStatus.OK,
				MsgCreator.updated("Table"), new RestaurantTableBoundary(savedEntity)));
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> deleteTable(String id) {
		return this.tableRepository.findById(id)
				.flatMap(table -> this.tableRepository.delete(table)
						.then(MyUtils.MonoResponseEntity(HttpStatus.NO_CONTENT, "", null)))
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Table"))))
				.onErrorResume(ExceptionHandler::handleErrors).log();
	}
}
