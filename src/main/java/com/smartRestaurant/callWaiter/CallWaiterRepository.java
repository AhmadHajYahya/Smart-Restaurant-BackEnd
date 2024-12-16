package com.smartRestaurant.callWaiter;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CallWaiterRepository extends ReactiveCrudRepository<CallWaiter, String> {
	Flux<CallWaiter> findAllByWaiterId(String waiterId);
	Mono<Void> deleteAllByWaiterId(String waiterId);
}
