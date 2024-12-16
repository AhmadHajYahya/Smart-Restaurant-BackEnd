package com.smartRestaurant.boundaries;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartRestaurant.booking.Booking;

public class BookingBoundary {
	private String bookingId;
	private String userId;
	private Integer guestsNumber;
	private LocalDate date;
	private LocalTime time;

	public BookingBoundary() {
	}

	public BookingBoundary(Booking booking) {
		this.setBookingId(booking.getBookingId());
		this.setUserId(booking.getUserId());
		this.setGuestsNumber(booking.getGuestsNumber());
		this.setDate(booking.getDate());
		this.setTime(booking.getTime());
	}

	public BookingBoundary(Integer guestsNumber, LocalDate date, LocalTime time) {
		super();
		this.guestsNumber = guestsNumber;
		this.date = date;
		this.time = time;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getGuestsNumber() {
		return guestsNumber;
	}

	public void setGuestsNumber(Integer guestsNumber) {
		this.guestsNumber = guestsNumber;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", shape= JsonFormat.Shape.STRING)
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	@JsonFormat(pattern = "HH:mm", shape= JsonFormat.Shape.STRING)
	public LocalTime getTime() {
		return time;
	}
	
	public void setTime(LocalTime time) {
		this.time = time;
	}

	public Booking toEntity() {
		Booking booking = new Booking();
		booking.setBookingId(this.getBookingId());
		booking.setUserId(this.getUserId());
		booking.setGuestsNumber(this.getGuestsNumber());
		booking.setTime(this.getTime());
		booking.setDate(this.getDate());

		return booking;
	}
}
