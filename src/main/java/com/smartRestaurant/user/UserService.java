package com.smartRestaurant.user;

import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;

import com.smartRestaurant.boundaries.LoginRequestBoundary;
import com.smartRestaurant.boundaries.RegisteredUserBoundary;
import com.smartRestaurant.boundaries.UserBoundary;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

public interface UserService {

	public Mono<ResponseEntity<ApiResponse>> getUser(String id, String phoneNumber);

	public Mono<ResponseEntity<ApiResponse>> createUser(UserBoundary user, ServerHttpResponse response);

	public Mono<ResponseEntity<ApiResponse>> updateRegisteredUser(RegisteredUserBoundary user);

	public Mono<ResponseEntity<ApiResponse>> getAllUsers();

	public Mono<ResponseEntity<ApiResponse>> getAllGuestUsers();

	public Mono<ResponseEntity<ApiResponse>> getAllRegisteredUsers();

	public Mono<ResponseEntity<ApiResponse>> getAllCooks();

	public Mono<ResponseEntity<ApiResponse>> getAllWaiters();

	public Mono<ResponseEntity<ApiResponse>> getAllAdmins();

	public Mono<ResponseEntity<ApiResponse>> deleteUser(String id, String phoneNumber);

	public Mono<ResponseEntity<ApiResponse>> updateOnlineStatus(String userId, Boolean isOnline);

	public Mono<ResponseEntity<ApiResponse>> login(LoginRequestBoundary boundary, ServerHttpResponse response);

}
