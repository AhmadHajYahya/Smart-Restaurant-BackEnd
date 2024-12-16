package com.smartRestaurant.receipt;

import java.time.LocalDate;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
public interface ReceiptRepository extends ReactiveCrudRepository<Receipt,String>{
	@Query("SELECT MAX(CAST(SUBSTRING(receipt_id, 2) AS SIGNED)) FROM receipt")
	Mono<Integer> countAllReceipts();
	Flux<Receipt> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
	Mono<Void> deleteAllBymealOrderId(String mealOrderId);
}
