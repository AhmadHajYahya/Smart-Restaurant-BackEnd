package com.smartRestaurant.boundaries;

import java.time.LocalDate;
import java.time.LocalTime;

import com.smartRestaurant.notification.Notification;

public class NotificationBoundary {
	private String notificationId;
	private String notificationTitle;
	private String notificationContent;
	private LocalDate date;
	private LocalTime time;
	public NotificationBoundary() {

	}

	public NotificationBoundary(Notification notification) {
		this.setNotificationId(notification.getNotificationId());
		this.setNotificationTitle(notification.getNotifiactionTitle());
		this.setNotificationContent(notification.getNotifiactionContnet());
		this.setDate(notification.getDate());
		this.setTime(notification.getTime());
	}

	public NotificationBoundary(String notificationId, String notificationTitle, String notificationContent) {
		super();
		this.notificationTitle = notificationTitle;
		this.notificationContent = notificationContent;
	}

	public String getNotificationContent() {
		return notificationContent;
	}

	public void setNotificationContent(String notificationContent) {
		this.notificationContent = notificationContent;
	}

	public String getNotificationTitle() {
		return notificationTitle;
	}

	public void setNotificationTitle(String notificationTitle) {
		this.notificationTitle = notificationTitle;
	}

	@Override
	public String toString() {
		return "NotificationBoundary{" + "notificationContent='" + notificationContent + '\'' + ","
				+ "notificationTitle='" + notificationTitle + '\'' + '}';
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}
	
	public Notification toEntity() {
		Notification n = new Notification();
		n.setNotificationId(this.getNotificationId());
		n.setNotifiactionTitle(this.getNotificationTitle());
		n.setNotifiactionContnet(this.getNotificationContent());
		n.setDate(this.getDate());
		n.setTime(this.getTime());
		return n;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
}
