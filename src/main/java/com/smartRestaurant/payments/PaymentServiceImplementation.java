package com.smartRestaurant.payments;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Service
public class PaymentServiceImplementation implements PaymentService {

	@Value("${stripe.secret.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
	
	
	@Override
	public Mono<Map<String, Object>> createPaymentIntent(Map<String, Object> paymentRequest) throws StripeException {
		Long amount = Long.valueOf(paymentRequest.get("amount").toString());
        String currency = paymentRequest.get("currency").toString();
        Map<String, Object> responseData = new HashMap<>();
        try {
            PaymentIntent paymentIntent = buildPaymentIntent(amount, currency);
            responseData.put("clientSecret", paymentIntent.getClientSecret());
            return Mono.just(responseData);
        } catch (StripeException e) {
            e.printStackTrace();
            return Mono.error(e);
        }
	}

	@Override
	public PaymentIntent buildPaymentIntent(Long amount, String currency) throws StripeException {
		PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency(currency)
                        .addPaymentMethodType("card")
                        .build();
        return PaymentIntent.create(params);
	}

}
