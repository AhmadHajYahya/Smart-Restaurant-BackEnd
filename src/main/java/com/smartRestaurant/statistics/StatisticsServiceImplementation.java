package com.smartRestaurant.statistics;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.smartRestaurant.booking.BookingRepository;
import com.smartRestaurant.boundaries.MealBoundary;
import com.smartRestaurant.boundaries.StatisticsBoundary;
import com.smartRestaurant.enums.Roles;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.general.MyUtils;
import com.smartRestaurant.meal.MealRepository;
import com.smartRestaurant.mealOrder.MealOrderRepository;
import com.smartRestaurant.receipt.ReceiptRepository;
import com.smartRestaurant.table.TableRepository;
import com.smartRestaurant.user.UserRepository;

import reactor.core.publisher.Mono;

@Service
public class StatisticsServiceImplementation implements StatisticsService {
	private final UserRepository userRepository;
	private final TableRepository tableRespository;
	private final MealRepository mealRepository;
	private final MealOrderRepository mealOrderRepository;
	private final ReceiptRepository receiptRepository;
	private final BookingRepository bookingRepository;

	public StatisticsServiceImplementation(UserRepository userRepository, TableRepository tableRespository,
			MealRepository mealRepository, MealOrderRepository mealOrderRepository, ReceiptRepository receiptRepository,
			BookingRepository bookingRepository) {
		super();
		this.userRepository = userRepository;
		this.tableRespository = tableRespository;
		this.mealRepository = mealRepository;
		this.mealOrderRepository = mealOrderRepository;
		this.receiptRepository = receiptRepository;
		this.bookingRepository = bookingRepository;
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getStatistics() {
		return getAllUsersNumber().flatMap(
				allUser -> getAllGuestUsersNumber().flatMap(allGuestUsers -> getAllRegisteredUsersNumber().flatMap(
						allRegisteredUsers -> getAllCooksNumber().flatMap(allCooks -> getAllWaitersNumber().flatMap(
								allWaiters -> getAllTablesNumber().flatMap(allTables -> getAllMealsNumber().flatMap(
										allMeals -> getTopThreeMeals().flatMap(top3Meals -> getAllOrdersNumber()
												.flatMap(allOrders -> getThisMonthOrdersNumber()
														.flatMap(thisMonthOrders -> getAllBookingsNumber().flatMap(
																allBookings -> getThisMonthBookingsNumber().flatMap(
																		thisMonthBookings -> getThisMonthIncome()
																				.flatMap(thisMonthIncome -> {
																					StatisticsBoundary boundary = new StatisticsBoundary(
																							allUser, allGuestUsers,
																							allRegisteredUsers,
																							allCooks, allWaiters,
																							allTables, allMeals,
																							top3Meals, allOrders,
																							thisMonthOrders,
																							allBookings,
																							thisMonthBookings,
																							thisMonthIncome);
																					return Mono.just(boundary);
																				})
																				.flatMap(statisticsBoundary -> MyUtils
																						.MonoResponseEntity(
																								HttpStatus.OK,
																								MsgCreator.fetched(
																										"Statistics"),
																								statisticsBoundary))))))))))))));
	}

	private Mono<Integer> getAllUsersNumber() {
		return this.userRepository.findAll().collectList().flatMap(users -> Mono.just(users.size()));
	}

	private Mono<Integer> getAllGuestUsersNumber() {
		return this.userRepository.findAllByRole(Roles.GUEST_USER).collectList()
				.flatMap(users -> Mono.just(users.size()));
	}

	private Mono<Integer> getAllRegisteredUsersNumber() {
		return this.userRepository.findAllByRole(Roles.REGISTERED_USER).collectList()
				.flatMap(users -> Mono.just(users.size()));
	}

	private Mono<Integer> getAllCooksNumber() {
		return this.userRepository.findAllByRole(Roles.COOK).collectList().flatMap(users -> Mono.just(users.size()));
	}

	private Mono<Integer> getAllWaitersNumber() {
		return this.userRepository.findAllByRole(Roles.WAITER).collectList().flatMap(users -> Mono.just(users.size()));
	}

	private Mono<Integer> getAllTablesNumber() {
		return this.tableRespository.findAll().collectList().flatMap(tables -> Mono.just(tables.size()));
	}

	private Mono<Integer> getAllMealsNumber() {
		return this.mealRepository.findAll().collectList().flatMap(meals -> Mono.just(meals.size()));
	}

	private Mono<List<MealBoundary>> getTopThreeMeals() {
		return this.mealRepository.findTop3ByOrderByOrderCountDesc().map(MealBoundary::new).collectList();

	}

	private Mono<Integer> getAllOrdersNumber() {
		return this.mealOrderRepository.findAll().collectList().flatMap(orders -> Mono.just(orders.size()));
	}

	private Mono<Integer> getThisMonthOrdersNumber() {
		YearMonth currentMonth = YearMonth.now();
		LocalDate startOfMonth = currentMonth.atDay(1);
		LocalDate endOfMonth = currentMonth.atEndOfMonth();
		return this.mealOrderRepository.findAllByDateBetween(startOfMonth, endOfMonth).collectList()
				.flatMap(orders -> Mono.just(orders.size()));
	}

	private Mono<Integer> getAllBookingsNumber() {
		return this.bookingRepository.findAll().collectList().flatMap(bookings -> Mono.just(bookings.size()));
	}

	private Mono<Integer> getThisMonthBookingsNumber() {
		YearMonth currentMonth = YearMonth.now();
		LocalDate startOfMonth = currentMonth.atDay(1);
		LocalDate endOfMonth = currentMonth.atEndOfMonth();
		return this.bookingRepository.findAllByDateBetween(startOfMonth, endOfMonth).collectList()
				.flatMap(orders -> Mono.just(orders.size()));
	}

	private Mono<Double> getThisMonthIncome() {
		YearMonth currentMonth = YearMonth.now();
		LocalDate startOfMonth = currentMonth.atDay(1);
		LocalDate endOfMonth = currentMonth.atEndOfMonth();
		return this.receiptRepository.findAllByDateBetween(startOfMonth, endOfMonth).reduce(0.0,
				(subtotal, receipt) -> subtotal + receipt.getTotal_price());
	}

}
