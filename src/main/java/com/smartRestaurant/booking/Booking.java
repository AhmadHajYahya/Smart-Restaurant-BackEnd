package com.smartRestaurant.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

@Table("booking")
public class Booking implements Persistable<String> {
	@Id
	private String bookingId;

	@Column("user_id")
	private String userId;

	@Column("booking_date")
	@JsonFormat(pattern = "yyyy-MM-dd", shape= JsonFormat.Shape.STRING)
	private LocalDate date;

	@Column("booking_time")
	@JsonFormat(pattern = "HH:mm", shape= JsonFormat.Shape.STRING)
	private LocalTime time;

	@Column("guests_number")
	private Integer guestsNumber;

	@Transient
	private boolean isNew = true;

	public Booking() {
	}

	public Booking(String userId, int guestsNumber, LocalTime time, LocalDate date) {
		this.userId = userId;
		this.date = date;
		this.time = time;
		this.guestsNumber = guestsNumber;
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

	public Integer getGuestsNumber() {
		return guestsNumber;
	}

	public void setGuestsNumber(Integer guestsNumber) {
		this.guestsNumber = guestsNumber;
	}

	@Override
	public String toString() {
		return "Booking [bookingId=" + bookingId + ", userId=" + userId + ", date=" + date + ", time=" + time
				+ ", guestsNumber=" + guestsNumber + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(bookingId, date, time);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Booking)) {
			return false;
		}
		Booking other = (Booking) obj;
		return bookingId == other.bookingId && Objects.equals(date, other.date) && Objects.equals(time, other.time);
	}

	@Override
	public String getId() {
		return bookingId;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
}
