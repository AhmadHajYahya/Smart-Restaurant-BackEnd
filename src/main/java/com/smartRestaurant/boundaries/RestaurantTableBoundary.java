package com.smartRestaurant.boundaries;

import com.smartRestaurant.table.RestaurantTable;

public class RestaurantTableBoundary {

	private String tableId;
	private Integer seats_number;
	private Boolean isTaken;
	private Boolean isBooked;
	private String userId;

	public RestaurantTableBoundary() {

	}

	public RestaurantTableBoundary(Integer seats_number, Boolean isTaken, Boolean isBooked, String userId) {
		super();
		this.seats_number = seats_number;
		this.isTaken = isTaken;
		this.isBooked = isBooked;
		this.userId = userId;
	}

	public RestaurantTableBoundary(RestaurantTable table) {
		this.tableId = table.getTableId();
		this.seats_number = table.getSeatsNumber();
		this.isTaken = table.getIsTaken();
		this.isBooked = table.getIsBooked();
		this.userId = table.getUserId();

	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public Integer getSeats_number() {
		return seats_number;
	}

	public void setSeats_number(Integer seats_number) {
		this.seats_number = seats_number;
	}

	public Boolean getIsTaken() {
		return isTaken;
	}

	public void setIsTaken(Boolean isTaken) {
		this.isTaken = isTaken;
	}

	public Boolean getIsBooked() {
		return isBooked;
	}

	public void setIsBooked(Boolean isBooked) {
		this.isBooked = isBooked;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public RestaurantTable toEntity() {
		RestaurantTable table = new RestaurantTable();

		table.setTableId(this.getTableId());
		table.setSeatsNumber(this.getSeats_number());
		table.setIsBooked(this.getIsBooked());
		table.setIsTaken(this.getIsTaken());
		table.setUserId(this.getUserId());

		return table;
	}

	@Override
	public String toString() {
		return "RestaurantTableBoundary [tableId=" + tableId + ", seats_number=" + seats_number + ", isTaken=" + isTaken
				+ ", isBooked=" + isBooked + ", userId=" + userId + "]";
	}

}
