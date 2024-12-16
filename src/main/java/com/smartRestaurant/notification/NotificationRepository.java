package com.smartRestaurant.notification;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface NotificationRepository extends ReactiveCrudRepository<Notification, String> {

}
