package com.smartRestaurant.user;

import java.util.List;

import com.smartRestaurant.boundaries.GuestUserBoundary;
import com.smartRestaurant.boundaries.RegisteredUserBoundary;

import reactor.core.publisher.Mono;

public interface UserDataValidation {
	public Mono<List<String>> validateRegisteredUserData(RegisteredUserBoundary user);

	public Mono<List<String>> validateGuestUserData(GuestUserBoundary user);
	
	public Mono<List<String>> validateSomeData(RegisteredUserBoundary user);
}
