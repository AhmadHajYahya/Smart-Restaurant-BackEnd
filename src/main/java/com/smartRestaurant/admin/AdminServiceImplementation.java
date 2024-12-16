package com.smartRestaurant.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.smartRestaurant.enums.Roles;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.MyUtils;
import com.smartRestaurant.meal.MealService;
import com.smartRestaurant.mealOrder.MealOrderService;
import com.smartRestaurant.table.TableService;
import com.smartRestaurant.user.UserService;

import reactor.core.publisher.Mono;

@Service
public class AdminServiceImplementation implements AdminService {
	private final MealService mealService;
	private final TableService tableService;
	private final UserService userService;
	private final MealOrderService mealOrderService;

	public AdminServiceImplementation(MealService mealService, TableService tableService, UserService userService,
			MealOrderService mealOrderService) {
		super();
		this.mealService = mealService;
		this.tableService = tableService;
		this.userService = userService;
		this.mealOrderService = mealOrderService;
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> delete(String obj, String id) {
		String object = obj.toLowerCase();
		switch (object) {
		case "meal":
			return this.mealService.deleteMeal(id);
		case "table":
			return this.tableService.deleteTable(id);
		case "user":
			return this.userService.deleteUser(id,null);
		case "order":
			return this.mealOrderService.deleteMealOrder(id);
		default:
			return null;
		}
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllUsers(Roles role) {
		if (role == null) {
			return this.userService.getAllUsers();
		} else if (role.equals(Roles.REGISTERED_USER)) {
			return this.userService.getAllRegisteredUsers();
		} else if (role.equals(Roles.GUEST_USER)) {
			return this.userService.getAllGuestUsers();
		} else if (role.equals(Roles.ADMIN)) {
			return this.userService.getAllAdmins();
		} else if (role.equals(Roles.COOK)) {
			return this.userService.getAllCooks();
		} else if (role.equals(Roles.WAITER)) {
			return this.userService.getAllWaiters();
		} else {
			return MyUtils.MonoResponseEntity(HttpStatus.BAD_REQUEST, "Role is unvalid", null);
		}

	}

	@Override
	public Mono<String> getAdminDetails() {
		String adminDetails = "Username: admin" + "\nPassword: admin0123";
		String msg = "\nPlease update your details before starting using the system.";
		return Mono.just(adminDetails + msg);

	}

}
