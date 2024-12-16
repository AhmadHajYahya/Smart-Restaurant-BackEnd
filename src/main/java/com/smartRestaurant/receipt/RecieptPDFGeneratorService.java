package com.smartRestaurant.receipt;

import com.smartRestaurant.meal.Meal;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecieptPDFGeneratorService {
	public Mono<String> generatePdf(String mealOrderId, String receiptId,Double totalPrice,Flux<Meal> meals);
}
