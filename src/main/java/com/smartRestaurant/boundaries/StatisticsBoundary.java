package com.smartRestaurant.boundaries;

import java.util.List;

public class StatisticsBoundary {
	private Integer allUsersNumber;
	private Integer allGuestUsersNumber;
	private Integer allRegisteredUsersNumber;
	private Integer allCooksNumber;
	private Integer allWaitersNumber;
	private Integer allTablesNumber;
	private Integer allMealsNumber;
	private List<MealBoundary> topThreeMeals;
	private Integer allOrdersNumber;
	private Integer thisMonthOrdersNumber;
	private Integer allBookingsNumber;
	private Integer thisMonthBookingsNumber;
	private Double thisMonthIncome;

	public StatisticsBoundary() {

	}

	public StatisticsBoundary(Integer allUsersNumber, Integer allGuestUsersNumber, Integer allRegisteredUsersNumber,
			Integer allCooksNumber, Integer allWaitersNumber, Integer allTablesNumber, Integer allMealsNumber,
			List<MealBoundary> topThreeMeals, Integer allOrdersNumber, Integer thisMonthOrdersNumber,
			Integer allBookingsNumber, Integer thisMonthBookingsNumber, Double thisMonthIncome) {
		super();
		this.allUsersNumber = allUsersNumber;
		this.allGuestUsersNumber = allGuestUsersNumber;
		this.allRegisteredUsersNumber = allRegisteredUsersNumber;
		this.allCooksNumber = allCooksNumber;
		this.allWaitersNumber = allWaitersNumber;
		this.allTablesNumber = allTablesNumber;
		this.allMealsNumber = allMealsNumber;
		this.topThreeMeals = topThreeMeals;
		this.allOrdersNumber = allOrdersNumber;
		this.thisMonthOrdersNumber = thisMonthOrdersNumber;
		this.allBookingsNumber = allBookingsNumber;
		this.thisMonthBookingsNumber = thisMonthBookingsNumber;
		this.thisMonthIncome = thisMonthIncome;
	}

	public Integer getAllUsersNumber() {
		return allUsersNumber;
	}

	public void setAllUsersNumber(Integer allUsersNumber) {
		this.allUsersNumber = allUsersNumber;
	}

	public Integer getAllGuestUsersNumber() {
		return allGuestUsersNumber;
	}

	public void setAllGuestUsersNumber(Integer allGuestUsersNumber) {
		this.allGuestUsersNumber = allGuestUsersNumber;
	}

	public Integer getAllRegisteredUsersNumber() {
		return allRegisteredUsersNumber;
	}

	public void setAllRegisteredUsersNumber(Integer allRegisteredUsersNumber) {
		this.allRegisteredUsersNumber = allRegisteredUsersNumber;
	}

	public Integer getAllCooksNumber() {
		return allCooksNumber;
	}

	public void setAllCooksNumber(Integer allCooksNumber) {
		this.allCooksNumber = allCooksNumber;
	}

	public Integer getAllWaitersNumber() {
		return allWaitersNumber;
	}

	public void setAllWaitersNumber(Integer allWaitersNumber) {
		this.allWaitersNumber = allWaitersNumber;
	}

	public Integer getAllTablesNumber() {
		return allTablesNumber;
	}

	public void setAllTablesNumber(Integer allTablesNumber) {
		this.allTablesNumber = allTablesNumber;
	}

	public Integer getAllMealsNumber() {
		return allMealsNumber;
	}

	public void setAllMealsNumber(Integer allMealsNumber) {
		this.allMealsNumber = allMealsNumber;
	}

	public List<MealBoundary> getTopThreeMeals() {
		return topThreeMeals;
	}

	public void setTopThreeMeals(List<MealBoundary> topThreeMeals) {
		this.topThreeMeals = topThreeMeals;
	}

	public Integer getAllOrdersNumber() {
		return allOrdersNumber;
	}

	public void setAllOrdersNumber(Integer allOrdersNumber) {
		this.allOrdersNumber = allOrdersNumber;
	}

	public Integer getThisMonthOrdersNumber() {
		return thisMonthOrdersNumber;
	}

	public void setThisMonthOrdersNumber(Integer thisMonthOrdersNumber) {
		this.thisMonthOrdersNumber = thisMonthOrdersNumber;
	}

	public Integer getAllBookingsNumber() {
		return allBookingsNumber;
	}

	public void setAllBookingsNumber(Integer allBookingsNumber) {
		this.allBookingsNumber = allBookingsNumber;
	}

	public Integer getThisMonthBookingsNumber() {
		return thisMonthBookingsNumber;
	}

	public void setThisMonthBookingsNumber(Integer thisMonthBookingsNumber) {
		this.thisMonthBookingsNumber = thisMonthBookingsNumber;
	}

	public Double getThisMonthIncome() {
		return thisMonthIncome;
	}

	public void setThisMonthIncome(Double thisMonthIncome) {
		this.thisMonthIncome = thisMonthIncome;
	}

}
