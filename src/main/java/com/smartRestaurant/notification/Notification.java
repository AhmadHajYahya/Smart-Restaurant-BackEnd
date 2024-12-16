package com.smartRestaurant.notification;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("notification")
public class Notification implements Persistable<String> {

	@Id
	private String notificationId;

	@Column("notifiaction_title")
	private String notifiactionTitle;

	@Column("notifiaction_contnet")
	private String notifiactionContnet;

	@Column("date")
	private LocalDate date;

	@Column("time")
	private LocalTime time;
	@Transient
	private boolean isNewEntry = true;

	public Notification(String notificationId, String notifiactionTitle, String notifiactionContnet, LocalDate date,
			LocalTime time) {
		super();
		this.notificationId = notificationId;
		this.notifiactionTitle = notifiactionTitle;
		this.notifiactionContnet = notifiactionContnet;
		this.date = date;
		this.time = time;
	}

	public Notification() {
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getNotifiactionTitle() {
		return notifiactionTitle;
	}

	public void setNotifiactionTitle(String notifiactionTitle) {
		this.notifiactionTitle = notifiactionTitle;
	}

	public String getNotifiactionContnet() {
		return notifiactionContnet;
	}

	public void setNotifiactionContnet(String notifiactionContnet) {
		this.notifiactionContnet = notifiactionContnet;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.notificationId;
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return this.isNewEntry;
	}

	public void setIsNew(boolean bool) {
		this.isNewEntry = bool;
	}

}
