package com.smartRestaurant.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;

import com.smartRestaurant.boundaries.NotificationBoundary;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationService {
	Mono<ResponseEntity<ApiResponse>> sendNotification(NotificationBoundary notification);

	Flux<ServerSentEvent<NotificationBoundary>> streamNotifiaction();

	Mono<ResponseEntity<ApiResponse>> getAllNotifications();
}
