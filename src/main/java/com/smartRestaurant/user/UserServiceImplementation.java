package com.smartRestaurant.user;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

import com.smartRestaurant.booking.BookingRepository;
import com.smartRestaurant.booking.BookingService;
import com.smartRestaurant.boundaries.GuestUserBoundary;
import com.smartRestaurant.boundaries.LoginRequestBoundary;
import com.smartRestaurant.boundaries.RegisteredUserBoundary;
import com.smartRestaurant.boundaries.UserBoundary;
import com.smartRestaurant.callWaiter.CallWaiterRepository;
import com.smartRestaurant.enums.Roles;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.ExceptionHandler;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.general.MyUtils;
import com.smartRestaurant.mealOrder.MealOrderRepository;
import com.smartRestaurant.mealOrder.MealOrderService;
import com.smartRestaurant.passwordHashing.PasswordUtils;
import com.smartRestaurant.security.JwtUtil;
import com.smartRestaurant.table.TableRepository;

import reactor.core.publisher.Mono;

@Service
public class UserServiceImplementation implements UserService {

	private final UserRepository userRepository;
	private final UserDataValidation dataValidation;
	private final TableRepository tableRepository;
	private final MealOrderRepository mealOrderRepository;
	private final BookingRepository bookingRepository;
	private final CallWaiterRepository callWaiterRepository;
	private final MealOrderService mealOrderService;
	private final BookingService bookingService;
	private JwtUtil jwtUtil;

	public UserServiceImplementation(UserRepository userRepository, UserDataValidation dataValidation, JwtUtil jwtUtil,
			MealOrderRepository mealOrderRepository, BookingRepository bookingRepository,
			TableRepository tableRepository, CallWaiterRepository callWaiterRepository,
			MealOrderService mealOrderService, BookingService bookingService) {
		super();
		this.userRepository = userRepository;
		this.dataValidation = dataValidation;
		this.jwtUtil = jwtUtil;
		this.tableRepository = tableRepository;
		this.mealOrderRepository = mealOrderRepository;
		this.bookingRepository = bookingRepository;
		this.callWaiterRepository = callWaiterRepository;
		this.mealOrderService = mealOrderService;
		this.bookingService = bookingService;
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getUser(String id, String phoneNumber) {
		Mono<User> user = null;
		if (id != null) {
			user = this.userRepository.findById(id);
		} else if (phoneNumber != null) {
			user = this.userRepository.findByPhoneNumber(phoneNumber);
		} else {
			return Mono.error(new IllegalArgumentException("Search field is null."));
		}
		return user.map(u -> {
			Object userBoundary;
			if (u.getRole() == Roles.GUEST_USER) {
				userBoundary = new GuestUserBoundary(u);
			} else {
				userBoundary = new RegisteredUserBoundary(u);
			}
			return MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("User"), userBoundary);
		}).switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("User"))))
				.onErrorResume(ExceptionHandler::handleErrors).log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> deleteUser(String id, String phoneNumber) {
		Mono<User> u;
		if (MyUtils.isNotNullAndNotEmpty(id)) {
			u = this.userRepository.findById(id);
		} else if (MyUtils.isNotNullAndNotEmpty(phoneNumber)) {
			u = this.userRepository.findByPhoneNumber(phoneNumber);
		} else {
			return MyUtils.MonoResponseEntity(HttpStatus.BAD_REQUEST, MsgCreator.notFound("User"), null);
		}

		return u.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("User"))))
				.flatMap(user -> this.tableRepository.findByuserId(user.getUserId()).flatMap(table -> {
					table.setIsTaken(false);
					table.setUserId(null);
					table.setNewEntry(false);
					return this.tableRepository.save(table);
				}).then(this.bookingRepository.findAllByUserId(user.getUserId())
						.flatMap(booking -> this.bookingService.deleteBooking(booking.getBookingId()))
						.then(Mono.empty())).then(this.callWaiterRepository.deleteAllByWaiterId(user.getUserId()))
						.then(this.mealOrderRepository.findAllByUserId(user.getUserId())
								.flatMap(order -> this.mealOrderService.deleteMealOrder(order.getmOrderID()))
								.then(Mono.empty()))
						.then(this.userRepository.delete(user))
						.then(MyUtils.MonoResponseEntity(HttpStatus.NO_CONTENT, "", null)))
				.onErrorResume(ExceptionHandler::handleErrors).log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllUsers() {
		return this.userRepository.findAll().map(user -> {
			if (user.getRole() == Roles.GUEST_USER) {
				return new GuestUserBoundary(user);
			} else {
				return new RegisteredUserBoundary(user);
			}
		}).collectList() // Collects all UserBoundary objects into a list
				.map(userBoundaries -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Users"),
						userBoundaries))
				.log();

	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllRegisteredUsers() {
		return this.userRepository.findAllByRole(Roles.REGISTERED_USER).map(RegisteredUserBoundary::new).collectList()
				.map(userBoundaries -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Registered users"),
						userBoundaries))
				.log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllGuestUsers() {
		return this.userRepository.findAllByRole(Roles.GUEST_USER).map(GuestUserBoundary::new).collectList()
				.map(userBoundaries -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Guest users"),
						userBoundaries))
				.log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllCooks() {
		return this.userRepository.findAllByRole(Roles.COOK).map(RegisteredUserBoundary::new).collectList().map(
				userBoundaries -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Cooks"), userBoundaries))
				.log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllWaiters() {
		return this.userRepository.findAllByRole(Roles.WAITER).map(RegisteredUserBoundary::new).collectList().map(
				userBoundaries -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Waiters"), userBoundaries))
				.log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllAdmins() {
		return this.userRepository.findAllByRole(Roles.ADMIN).map(RegisteredUserBoundary::new).collectList().map(
				userBoundaries -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Admins"), userBoundaries))
				.log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> createUser(UserBoundary user, ServerHttpResponse response) {
		if (!user.getRole().equals(Roles.GUEST_USER) && MyUtils.isNullOrEmpty(user.getUsername())) {
			return Mono.error(new IllegalArgumentException(MsgCreator.nullOrEmpty()));
		}
		if (MyUtils.isNullOrEmpty(user.getName()) || MyUtils.isNullOrEmpty(user.getPhoneNumber())
				|| MyUtils.isNull(user.getRole())) {
			return Mono.error(new IllegalArgumentException(MsgCreator.nullOrEmpty()));
		}

		if (user instanceof RegisteredUserBoundary) {
			RegisteredUserBoundary regUser = (RegisteredUserBoundary) user;
			if (MyUtils.isNullOrEmpty(regUser.getPassword()) || MyUtils.isNullOrEmpty(regUser.getEmail())) {
				return Mono.error(new IllegalArgumentException(MsgCreator.nullOrEmpty()));
			}
		}

		Mono<List<String>> validationResult = null;
		if (user instanceof RegisteredUserBoundary) {
			validationResult = this.dataValidation.validateRegisteredUserData((RegisteredUserBoundary) user);
		} else if (user instanceof GuestUserBoundary) {
			validationResult = this.dataValidation.validateGuestUserData((GuestUserBoundary) user);
		} else {
			validationResult = Mono.just(Collections.emptyList());
		}

		return validationResult.flatMap(errors -> {
			if (!errors.isEmpty()) {
				return Mono.error(new IllegalArgumentException("Validation failed: " + String.join(" ", errors)));
			}

			// Generate random ID.
			user.setUserId(UUID.randomUUID().toString());
			user.setIsOnline(true);
			String token = jwtUtil.generateToken(user.getPhoneNumber());
			ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", token).httpOnly(true).secure(true) // Set this to
																											// true if
																											// using
																											// HTTPS
					.path("/").build();
			response.addCookie(cookie);
			if (user instanceof RegisteredUserBoundary) {
				RegisteredUserBoundary u = (RegisteredUserBoundary) (user);
				u.setPassword(PasswordUtils.hashPassword(u.getPassword()));
				u.setBookingsCount(0);
				u.setOrdersCount(0);

				return this.userRepository.save(u.toEntity())
						.then(MyUtils.MonoResponseEntity(HttpStatus.OK, MsgCreator.created("User"), u));
			} else {
				user.setUsername(user.getRole() + user.getPhoneNumber());
				return this.userRepository.save(user.toEntity())
						.then(MyUtils.MonoResponseEntity(HttpStatus.OK, MsgCreator.created("User"), user));
			}

		}).onErrorResume(ExceptionHandler::handleErrors).log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> updateRegisteredUser(RegisteredUserBoundary user) {
		return this.dataValidation.validateSomeData(user).flatMap(errors -> {
			if (!errors.isEmpty()) {
				return Mono.error(new IllegalArgumentException("Validation failed: " + String.join(" ", errors)));
			}
			return this.userRepository.findById(user.getUserId())
					.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("User"))))
					.flatMap(userEntity -> {
						// Update user details if provided
						if (MyUtils.isNotNullAndNotEmpty(user.getPhoneNumber())) {
							userEntity.setPhoneNumber(user.getPhoneNumber());
						}
						if (MyUtils.isNotNullAndNotEmpty(user.getName())) {
							userEntity.setName(user.getName());
						}
						if (MyUtils.isNotNullAndNotEmpty(user.getEmail())) {
							userEntity.setEmail(user.getEmail());
						}
						if (MyUtils.isNotNullAndNotEmpty(user.getPassword())) {
							userEntity.setPassword(PasswordUtils.hashPassword(user.getPassword()));
						}
						userEntity.setNew(false);
						return this.userRepository.save(userEntity);
					}).map(savedUser -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.updated("User"),
							new RegisteredUserBoundary(savedUser)))
					.onErrorResume(ExceptionHandler::handleErrors);
		}).log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> login(LoginRequestBoundary boundary, ServerHttpResponse response) {
		return this.userRepository.findByUsername(boundary.getUsername())
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("User")))).flatMap(user -> {

					if (PasswordUtils.verifyPassword(boundary.getPassword(), user.getPassword())) {
						user.setIsOnline(true);
						user.setNew(false);
						String token = jwtUtil.generateToken(user.getPhoneNumber());
						ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", token)
							    .httpOnly(true)
							    .secure(true)  // Set to true if using HTTPS
							    .sameSite("None")  // Must be 'None' for cross-origin requests
							    .path("/")
							    .build();
							response.addCookie(cookie);


						RegisteredUserBoundary registeredUser = new RegisteredUserBoundary(user);

						return this.userRepository.save(user).flatMap(
								updatedUser -> MyUtils.MonoResponseEntity(HttpStatus.OK, "Welcome", registeredUser));
					}

					return Mono.error(new IllegalArgumentException("Password is wrong"));

				}).onErrorResume(ExceptionHandler::handleErrors).log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> updateOnlineStatus(String userId, Boolean isOnline) {
		return this.userRepository.findById(userId).flatMap(user -> {
			user.setIsOnline(isOnline);
			user.setNew(false);
			return this.userRepository.save(user).flatMap(updatedUser -> MyUtils.MonoResponseEntity(HttpStatus.OK,
					MsgCreator.updated("User online status"), null));
		});
	}

}
