package com.smartRestaurant.boundaries;

import java.util.List;

public class NewMealOrderBoundary {
	private String userId;
	private List<String> mealIds;
	private String tableId;

	public NewMealOrderBoundary() {

	}

	public NewMealOrderBoundary(String userId, List<String> mealIds, String tableId) {
		super();
		this.userId = userId;
		this.mealIds = mealIds;
		this.tableId = tableId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getMealIds() {
		return mealIds;
	}

	public void setMealIds(List<String> mealIds) {
		this.mealIds = mealIds;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	

}
