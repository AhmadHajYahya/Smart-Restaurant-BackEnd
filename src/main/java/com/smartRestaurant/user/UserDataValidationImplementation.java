package com.smartRestaurant.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import com.smartRestaurant.boundaries.GuestUserBoundary;
import com.smartRestaurant.boundaries.RegisteredUserBoundary;
import com.smartRestaurant.general.MyUtils;
import com.smartRestaurant.passwordHashing.PasswordUtils;
import com.smartRestaurant.validation.*;

@Service
public class UserDataValidationImplementation implements UserDataValidation {
	private final UserRepository userRepository;

	@Autowired
	public UserDataValidationImplementation(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Mono<List<String>> validateRegisteredUserData(RegisteredUserBoundary user) {
		List<String> errorMsgs = new ArrayList<>();

		return validateUsername(user.getUsername()).flatMap(isValidUsername -> {
			if (!isValidUsername)
				errorMsgs.add(ErrorMessages.invalidUsername);
			return validateName(user.getName());
		}).flatMap(isValidName -> {
			if (!isValidName)
				errorMsgs.add(ErrorMessages.invalidName);
			return validateEmail(user.getEmail());
		}).flatMap(isValidEmail -> {
			if (!isValidEmail)
				errorMsgs.add(ErrorMessages.invalidEmail);
			return validatePhoneNumber(user.getPhoneNumber());
		}).flatMap(isValidPhone -> {
			if (!isValidPhone)
				errorMsgs.add(ErrorMessages.invalidPhoneNumber);
			return validatePassword(user.getPassword());
		}).map(isValidPassword -> {
			if (!isValidPassword)
				errorMsgs.add(ErrorMessages.invalidPassword);
			return errorMsgs;
		});
	}

	public Mono<List<String>> validateSomeData(RegisteredUserBoundary user) {
		List<String> errorMsgs = new ArrayList<>();

		return validateEmail(user.getEmail()).flatMap(isValidEmail -> {
			if (!isValidEmail && MyUtils.isNotNullAndNotEmpty(user.getEmail()))
				errorMsgs.add(ErrorMessages.invalidEmail);
			return validateName(user.getName());
		}).flatMap(isValidName -> {
			if (!isValidName && MyUtils.isNotNullAndNotEmpty(user.getName()))
				errorMsgs.add(ErrorMessages.invalidName);
			return validatePhoneNumber(user.getPhoneNumber());
		}).flatMap(isValidPhone -> {
			if (!isValidPhone && MyUtils.isNotNullAndNotEmpty(user.getPhoneNumber()))
				errorMsgs.add(ErrorMessages.invalidPhoneNumber);
			return validatePassword(user.getPassword());
		}).map(isValidPassword -> {
			if (!isValidPassword && MyUtils.isNotNullAndNotEmpty(user.getPassword()))
				errorMsgs.add(ErrorMessages.invalidPassword);
			return errorMsgs;
		});
	}

	private Mono<Boolean> validateUsername(String username) {
		ValidationContext context = new ValidationContext(new ValidateUsername());
		return Mono.just(context.executeStrategy(username))
				.flatMap(isValid -> isValid ? this.userRepository.existsByUsernameLike(username).map(exists -> !exists)
						: Mono.just(false));
	}

	private Mono<Boolean> validateName(String name) {
		ValidationContext context = new ValidationContext(new ValidateName());
		return Mono.just(context.executeStrategy(name))
				.flatMap(isValid -> isValid ? Mono.just(true) : Mono.just(false));
	}

	private Mono<Boolean> validateEmail(String email) {
		ValidationContext context = new ValidationContext(new ValidateEmail());
		return Mono.just(context.executeStrategy(email))
				.flatMap(isValid -> isValid ? this.userRepository.existsByEmailLike(email).map(exists -> !exists)
						: Mono.just(false));
	}

	private Mono<Boolean> validatePhoneNumber(String phoneNumber) {
		ValidationContext context = new ValidationContext(new ValidatePhoneNumber());
		return Mono.just(context.executeStrategy(phoneNumber))
				.flatMap(isValid -> isValid
						? this.userRepository.existsByphoneNumberLike(phoneNumber).map(exists -> !exists)
						: Mono.just(false));
	}

	private Mono<Boolean> validatePassword(String password) {
		ValidationContext context = new ValidationContext(new ValidatePassword());
		boolean isValid = context.executeStrategy(password);
		if (!isValid)
			return Mono.just(false);

		return this.userRepository.findAllPasswords().any(result -> PasswordUtils.verifyPassword(password, result))
				.map(isUsed -> !isUsed);
	}

	@Override
	public Mono<List<String>> validateGuestUserData(GuestUserBoundary user) {
		List<String> errorMsgs = new ArrayList<>();

		return validateName(user.getName()).flatMap(isValidName -> {
			if (!isValidName)
				errorMsgs.add(ErrorMessages.invalidName);
			return validatePhoneNumber(user.getPhoneNumber());
		}).map(isValidPhone -> {
			if (!isValidPhone)
				errorMsgs.add(ErrorMessages.invalidPhoneNumber);
			return errorMsgs;
		});
	}
}
