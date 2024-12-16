package com.smartRestaurant.passwordRecovery;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartRestaurant.boundaries.StringBoundary;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.general.MyUtils;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/password-recovery")
public class PasswordRecoveryController {

	private final PasswordRecoveryService service;

	public PasswordRecoveryController(PasswordRecoveryService service) {
		super();
		this.service = service;
	}

	@PostMapping(value = "/verify-phone-number", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> verifyPhoneNumber(@RequestBody StringBoundary phoneNumber) {
		return service.verifyPhoneNumber(phoneNumber.getStr());
	}

	@PostMapping(value = "/generate-code", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> generateVerificationCode(@RequestBody StringBoundary phoneNumber) {
		return service.generateVerificationCode(phoneNumber.getStr());
	}

	@PostMapping(value = "/verify-code", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> verifyCode(@RequestBody Map<String, String> data) {
		if (data.containsKey("phoneNumber") && data.containsKey("userCode")) {
			int userCode = Integer.parseInt(data.get("userCode"));
			String phoneNumber = data.get("phoneNumber");
			return service.verifyCode(phoneNumber, userCode);
		}
		return MyUtils.MonoResponseEntity(HttpStatus.BAD_REQUEST, MsgCreator.nullOrEmpty(), null);
	}

	@PutMapping(value = "/set-new-password", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> setNewPassword(@RequestBody Map<String, String> data) {
		if (data.containsKey("phoneNumber") && data.containsKey("password")) {
			String password = data.get("password");
			String phoneNumber = data.get("phoneNumber");
			return service.setNewPassword(phoneNumber, password);
		}
		return MyUtils.MonoResponseEntity(HttpStatus.BAD_REQUEST, MsgCreator.nullOrEmpty(), null);
	}

}
