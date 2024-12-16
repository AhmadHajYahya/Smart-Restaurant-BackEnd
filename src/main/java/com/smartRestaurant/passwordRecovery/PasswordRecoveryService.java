package com.smartRestaurant.passwordRecovery;

import org.springframework.http.ResponseEntity;

import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

public interface PasswordRecoveryService {

	Mono<ResponseEntity<ApiResponse>> verifyPhoneNumber(String phoneNumber);

	Mono<ResponseEntity<ApiResponse>> generateVerificationCode(String phoneNumber);

	Mono<ResponseEntity<ApiResponse>> verifyCode(String phoneNumber, int userCode);

	Mono<ResponseEntity<ApiResponse>> setNewPassword(String phoneNumber, String newPassword);

}
