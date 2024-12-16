package com.smartRestaurant.payments;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		super();
		this.paymentService = paymentService;
	}

	@PostMapping("/create-payment-intent")
	public Mono<Map<String, Object>> createPaymentIntent(@RequestBody Map<String, Object> paymentRequest) throws StripeException {
		return this.paymentService.createPaymentIntent(paymentRequest);
	}

}
