package com.smartRestaurant.payments;

import java.util.Map;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import reactor.core.publisher.Mono;

public interface PaymentService {
	public Mono<Map<String, Object>> createPaymentIntent(Map<String, Object> paymentRequest) throws StripeException;

	public PaymentIntent buildPaymentIntent(Long amount, String currency) throws StripeException;
}
