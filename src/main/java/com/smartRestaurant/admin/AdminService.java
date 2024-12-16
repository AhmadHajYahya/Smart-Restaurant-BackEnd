package com.smartRestaurant.admin;

import org.springframework.http.ResponseEntity;

import com.smartRestaurant.enums.Roles;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

public interface AdminService {

	Mono<ResponseEntity<ApiResponse>> delete(String obj, String id);
	Mono<ResponseEntity<ApiResponse>> getAllUsers(Roles type);
	Mono<String> getAdminDetails();
}
