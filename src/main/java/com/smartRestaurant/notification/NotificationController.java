package com.smartRestaurant.notification;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartRestaurant.boundaries.NotificationBoundary;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
	private final NotificationService notificationService;

	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;

	}

	@PostMapping(path = "/send-notification", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> sendNotification(@RequestBody NotificationBoundary notification) {
		return notificationService.sendNotification(notification);
	}

	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<NotificationBoundary>> streamNotifications() {
		return notificationService.streamNotifiaction();
	}

	@GetMapping(value = "/all", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getAllNotifications() {
		return notificationService.getAllNotifications();
	}
}
