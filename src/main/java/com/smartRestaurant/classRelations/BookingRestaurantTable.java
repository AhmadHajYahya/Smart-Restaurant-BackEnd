package com.smartRestaurant.classRelations;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("booking_restaurantTable")
public class BookingRestaurantTable implements Persistable<String> {
	@Id
	private String id;

	@Column("booking_id")
	private String bookingId;

	@Column("table_id")
	private String tableId;
	
	@Transient
	private boolean isNew = true;

	public BookingRestaurantTable() {

	}

	public BookingRestaurantTable(String bookingId, String tableId) {
		super();
		this.bookingId = bookingId;
		this.tableId = tableId;
	}

	@Override
	public String getId() {
		return UUID.randomUUID().toString();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

}
