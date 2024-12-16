package com.smartRestaurant.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartRestaurant.boundaries.GuestUserBoundary;
import com.smartRestaurant.boundaries.LoginRequestBoundary;
import com.smartRestaurant.boundaries.RegisteredUserBoundary;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.MyUtils;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}

	// get the user by id OR by phone number. the url must have only one param.
	@GetMapping(value = "/user", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getUser(@RequestParam(name = "id", required = false) String id,
			@RequestParam(name = "phoneNumber", required = false) String phoneNumber) {

		if ((id == null) == (phoneNumber == null)) { // XNOR operation: true if both are null or both are non-null
			return MyUtils.MonoResponseEntity(HttpStatus.BAD_REQUEST,
					"Exactly one of 'id' or 'phoneNumber' must be provided", null);
		}
		return this.userService.getUser(id, phoneNumber);
	}

	@PostMapping(value = "/register", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> createRegisteredUser(@RequestBody RegisteredUserBoundary user,ServerHttpResponse response) {
		return this.userService.createUser(user,response);
	}

	@PostMapping(value = "/guest", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> createGuestUser(@RequestBody GuestUserBoundary user,ServerHttpResponse response) {
		return this.userService.createUser(user,response);
	}

	@PutMapping(value = "/registered/update", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> updateRegisteredUser(@RequestBody RegisteredUserBoundary user) {
		return this.userService.updateRegisteredUser(user);
	}

	@PostMapping(value = "/login", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> login(@RequestBody LoginRequestBoundary boundary,ServerHttpResponse response) {
		return this.userService.login(boundary,response);
	}

	@PutMapping(value = "/update/online", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> updateOnlineStatus(
			@RequestParam(name = "userId", required = true) String userId,
			@RequestParam(name = "isOnline", required = true) Boolean isOnline) {
		return this.userService.updateOnlineStatus(userId, isOnline);
	}

	@DeleteMapping(value = "/delete/guest", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> createGuestUser(
			@RequestParam(name = "phoneNumber", required = true) String phoneNumber) {
		return this.userService.deleteUser(null,phoneNumber);
	}
}
