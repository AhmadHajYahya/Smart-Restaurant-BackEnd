package com.smartRestaurant.boundaries;

public class RateMealBoundary {
	private String mealId;
	private Double rating;
	
	
	public RateMealBoundary() {
		
	}


	public String getMealId() {
		return mealId;
	}


	public void setMealId(String mealId) {
		this.mealId = mealId;
	}


	public Double getRating() {
		return rating;
	}


	public void setRating(Double rating) {
		this.rating = rating;
	}
	
	
}
