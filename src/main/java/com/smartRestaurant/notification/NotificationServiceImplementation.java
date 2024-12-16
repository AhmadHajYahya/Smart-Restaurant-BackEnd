package com.smartRestaurant.notification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import com.smartRestaurant.boundaries.NotificationBoundary;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.general.MyUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Service
public class NotificationServiceImplementation implements NotificationService {
	private final Sinks.Many<NotificationBoundary> alertSink;
	private final NotificationRepository notificationRepository;

	public NotificationServiceImplementation(Many<NotificationBoundary> alertSink,
			NotificationRepository notificationRepository) {
		super();
		this.alertSink = alertSink;
		this.notificationRepository = notificationRepository;
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> sendNotification(NotificationBoundary notification) {
		notification.setDate(LocalDate.now());
		notification.setTime(LocalTime.now());
		notification.setNotificationId(UUID.randomUUID().toString());
		return this.notificationRepository.save(notification.toEntity()).flatMap(savedNotification -> {
			alertSink.tryEmitNext(notification);
			return MyUtils.MonoResponseEntity(HttpStatus.CREATED, MsgCreator.created("Notification"),
					new NotificationBoundary(savedNotification));
		});

	}

	@Override
	public Flux<ServerSentEvent<NotificationBoundary>> streamNotifiaction() {
		return alertSink.asFlux().map(notification -> ServerSentEvent.builder(notification).build());
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllNotifications() {
		return this.notificationRepository.findAll().map(NotificationBoundary::new).collectList()
				.flatMap(boundaries -> MyUtils.MonoResponseEntity(HttpStatus.OK, MsgCreator.fetched("Notifications"),
						boundaries));
	}
}
