package com.smartRestaurant.boundaries;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartRestaurant.enums.Status;

public class MealOrderBoundary {
	private String mOrderID;
	private UserBoundary user;
	private RestaurantTableBoundary table;
	private LocalDate date;
	private LocalTime time;
	private Status status;
	private List<MealBoundary> meals;
	private Integer totalPreparingTime;

	public MealOrderBoundary() {

	}

	public MealOrderBoundary(String mOrderID, UserBoundary user, RestaurantTableBoundary table, LocalDate date,
			LocalTime time, Status status, List<MealBoundary> meals) {
		super();
		this.mOrderID = mOrderID;
		this.user = user;
		this.table = table;
		this.date = date;
		this.time = time;
		this.status = status;
		this.meals = meals;
	}
	public MealOrderBoundary(String mOrderID, UserBoundary user, RestaurantTableBoundary table, LocalDate date,
			LocalTime time, Status status, List<MealBoundary> meals,Integer totalPreparingTime) {
		super();
		this.mOrderID = mOrderID;
		this.user = user;
		this.table = table;
		this.date = date;
		this.time = time;
		this.status = status;
		this.meals = meals;
		this.totalPreparingTime = totalPreparingTime;
	}
	
	@JsonProperty("mOrderID")
	public String getmOrderID() {
		return mOrderID;
	}

	public void setmOrderID(String mOrderID) {
		this.mOrderID = mOrderID;
	}

	public UserBoundary getUser() {
		return user;
	}

	public void setUser(UserBoundary user) {
		this.user = user;
	}

	public RestaurantTableBoundary getTable() {
		return table;
	}

	public void setTable(RestaurantTableBoundary table) {
		this.table = table;
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<MealBoundary> getMeals() {
		return meals;
	}

	public void setMeals(List<MealBoundary> meals) {
		this.meals = meals;
	}

	@Override
	public String toString() {
		return "MealOrderBoundary [mOrderID=" + mOrderID + ", user=" + user + ", table=" + table + ", date=" + date
				+ ", time=" + time + ", status=" + status + ", meals=" + meals + "]";
	}


	public Integer getTotalPreparingTime() {
		return totalPreparingTime;
	}


	public void setTotalPreparingTime(Integer totalPreparingTime) {
		this.totalPreparingTime = totalPreparingTime;
	}

}
