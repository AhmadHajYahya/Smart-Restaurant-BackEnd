package com.smartRestaurant.passwordRecovery;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.smartRestaurant.enums.Roles;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.ExceptionHandler;
import com.smartRestaurant.general.MyUtils;
import com.smartRestaurant.passwordHashing.PasswordUtils;
import com.smartRestaurant.user.UserRepository;
import com.smartRestaurant.validation.ValidatePassword;
import com.smartRestaurant.validation.ValidatePhoneNumber;
import com.smartRestaurant.validation.ValidationContext;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Service
public class PasswordRecoveryImplenetation implements PasswordRecoveryService {
	private final UserRepository userRepository;
	private Map<String, Integer> codesMap;
	private Random random;
	private final int MIN = 100000;
	private final int MAX = 999999;

	@PostConstruct
	private void init() {
		this.codesMap = new HashMap<>();
		this.random = new Random();
	}

	public PasswordRecoveryImplenetation(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> verifyPhoneNumber(String phoneNumber) {
		return this.validatePhoneNumber(phoneNumber).flatMap(valid -> {
			if (valid) {
				return this.userRepository.findByPhoneNumber(phoneNumber).flatMap(user -> {
					if (!user.getRole().equals(Roles.GUEST_USER)) {
						return MyUtils.MonoResponseEntity(HttpStatus.OK, "Phone number is valid.", user.getName());
					}
					return Mono.error(new SecurityException("You dont have password to recover."));
				});

			} else {
				return Mono.error(new IllegalArgumentException("Phone number is not valid."));
			}
		}).onErrorResume(ExceptionHandler::handleErrors);

	}

	private Mono<Boolean> validatePhoneNumber(String phoneNumber) {
		ValidationContext context = new ValidationContext(new ValidatePhoneNumber());
		return Mono.just(context.executeStrategy(phoneNumber)).flatMap(
				isValid -> isValid ? this.userRepository.existsByphoneNumberLike(phoneNumber) : Mono.just(false));
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> generateVerificationCode(String phoneNumber) {
		return generateCode().flatMap(generatedCode -> {
			addToMap(phoneNumber, generatedCode);
			return MyUtils.MonoResponseEntity(HttpStatus.OK, "Verification Code generated successfully.",
					generatedCode);
		});
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> verifyCode(String phoneNumber, int userCode) {
		return compareCode(phoneNumber, userCode).flatMap(isMatched -> {
			if (isMatched) {
				removeFromMap(phoneNumber);
				return MyUtils.MonoResponseEntity(HttpStatus.OK, "The verification code is matches.", null);
			} else {
				return MyUtils.MonoResponseEntity(HttpStatus.BAD_REQUEST,
						"The verification code is not matching the generated code.", null);
			}
		});
	}

	private Mono<Integer> generateCode() {
		return Mono.just(this.random.nextInt((MAX - MIN) + 1) + MIN);
	}

	private void addToMap(String phoneNumber, int generatedCode) {
		removeFromMap(phoneNumber);
		this.codesMap.put(phoneNumber, generatedCode);
	}

	private Mono<Boolean> compareCode(String phoneNumber, int userCode) {
		int generatedCode = this.codesMap.get(phoneNumber);
		return Mono.just(generatedCode == userCode);
	}

	private void removeFromMap(String phoneNumber) {
		if (this.codesMap.containsKey(phoneNumber))
			this.codesMap.remove(phoneNumber);
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> setNewPassword(String phoneNumber, String newPassword) {
		return validatePassword(newPassword).flatMap(valid -> {
			if (valid) {
				return this.userRepository.findByPhoneNumber(phoneNumber).flatMap(userEntity -> {
					userEntity.setPassword(PasswordUtils.hashPassword(newPassword));
					userEntity.setNew(false);
					return this.userRepository.save(userEntity);
				}).map(savedUser -> MyUtils.responseEntity(HttpStatus.OK, "Password changed successfully.",
						null));
			} else {
				return Mono.error(new IllegalArgumentException("Password is not valid"));
			}
		}).onErrorResume(ExceptionHandler::handleErrors);

	}

	private Mono<Boolean> validatePassword(String password) {
		ValidationContext context = new ValidationContext(new ValidatePassword());
		boolean isValid = context.executeStrategy(password);
		if (!isValid)
			return Mono.just(false);

		return this.userRepository.findAllPasswords().any(result -> PasswordUtils.verifyPassword(password, result))
				.map(isUsed -> !isUsed);
	}

}
