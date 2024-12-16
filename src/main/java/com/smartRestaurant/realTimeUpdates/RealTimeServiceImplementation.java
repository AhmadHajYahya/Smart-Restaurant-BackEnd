package com.smartRestaurant.realTimeUpdates;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.smartRestaurant.callWaiter.CallWaiterService;
import com.smartRestaurant.enums.Status;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.mealOrder.MealOrderService;
import com.smartRestaurant.mealOrder.MealPreparationService;
import com.smartRestaurant.menu.MenuService;
import com.smartRestaurant.statistics.StatisticsService;
import com.smartRestaurant.table.TableService;

import reactor.core.publisher.Mono;

@Service
public class RealTimeServiceImplementation implements RealTimeService {
	private final TableService tableService;
	private final StatisticsService statisticsService;
	private final MealOrderService mealOrderService;
	private final CallWaiterService callWaiterService;
	private final MenuService menuService;
	private final MealPreparationService mps;

	public RealTimeServiceImplementation(TableService tableService, StatisticsService statisticsService,
			MealOrderService mealOrderService, CallWaiterService callWaiterService, MenuService menuService,
			MealPreparationService mps) {
		super();
		this.tableService = tableService;
		this.statisticsService = statisticsService;
		this.mealOrderService = mealOrderService;
		this.callWaiterService = callWaiterService;
		this.menuService = menuService;
		this.mps = mps;
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> guestsLoad() {
		return this.tableService.getGuestsLoad();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> statistics() {
		return this.statisticsService.getStatistics();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> orderStatus(String orderId) {
		return this.mealOrderService.getMealOrder(orderId,null);
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> waiterCalls(String waiterId) {
		return this.callWaiterService.getAllWaiterCalls(waiterId);
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> menu() {
		return this.menuService.getAvailableMeals();
	}
	
	@Override
	public Mono<ResponseEntity<ApiResponse>> prioritizeOrders(Status status) {
		return this.mps.prioritizeMealOrders(status);
	}
	@Override
	public Mono<ResponseEntity<ApiResponse>> allMealOrders(Status status) {
		return this.mealOrderService.getAllMealOrders(status);
	}

}
