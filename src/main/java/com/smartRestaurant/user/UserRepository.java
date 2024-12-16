package com.smartRestaurant.user;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.smartRestaurant.enums.Roles;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {
	Mono<User> findByRole(Roles role);

	Mono<User> findByUsername(String username);
	
	Mono<User> findByPhoneNumber(String phoneNumber);
	
	Mono<Boolean> existsByUsernameLike(String username);

	Mono<Boolean> existsByphoneNumberLike(String phoneNumber);

	Mono<Boolean> existsByEmailLike(String email);

	@Query("SELECT password FROM User WHERE role = 'RegisteredUser'")
	Flux<String> findAllPasswords();

	Flux<User> findAllByRole(Roles role);
	Flux<User> findAllByRoleAndIsOnlineTrue(Roles role);
}
