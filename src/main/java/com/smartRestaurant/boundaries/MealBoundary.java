package com.smartRestaurant.boundaries;

import com.smartRestaurant.enums.Category;
import com.smartRestaurant.meal.Meal;

public class MealBoundary {
	private String mealId;
	private String title;
	private String description;
	private Double price;
	private Boolean isAvailable;
	private Category category;
	private String imageURL;
	private Double rating;
	private Long numberOfRating;
	private Long orderCount;
	private Integer preparingTime;

	public MealBoundary() {

	}

	public MealBoundary(Meal meal) {
		this.setMealId(meal.getMealId());
		this.setAvailable(meal.getIsAvailable());
		this.setCategory(meal.getCategory());
		this.setDescription(meal.getDescription());
		this.setImageURL(meal.getImageURL());
		this.setPrice(meal.getPrice());
		this.setTitle(meal.getTitle());
		this.setRating(meal.getRating());
		this.setOrderCount(meal.getOrderCount());
		this.setNumberOfRating(meal.getNumberOfRating());
		this.setPreparingTime(meal.getPreparingTime());
	}

	public MealBoundary(String title, String description, Double price, Boolean isAvailable, Category category,
			String imageURL, Double rating, Long numberOfRating, Long orderCount, Integer preparingTime) {
		super();
		this.title = title;
		this.description = description;
		this.price = price;
		this.isAvailable = isAvailable;
		this.category = category;
		this.imageURL = imageURL;
		this.rating = rating;
		this.numberOfRating = numberOfRating;
		this.orderCount = orderCount;
		this.preparingTime = preparingTime;
	}

	public String getMealId() {
		return mealId;
	}

	public void setMealId(String mealId) {
		this.mealId = mealId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Long getNumberOfRating() {
		return numberOfRating;
	}

	public void setNumberOfRating(Long numberOfRating) {
		this.numberOfRating = numberOfRating;
	}

	public Long getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Long orderCount) {
		this.orderCount = orderCount;
	}

	public Meal toEntity() {
		Meal meal = new Meal();

		meal.setMealId(this.getMealId());
		meal.setTitle(this.getTitle());
		meal.setDescription(this.getDescription());
		meal.setPrice(this.getPrice());
		meal.setAvailable(this.isAvailable());
		meal.setCategory(this.getCategory());
		meal.setImageURL(this.getImageURL());
		meal.setRating(this.getRating());
		meal.setOrderCount(this.getOrderCount());
		meal.setNumberOfRating(this.getNumberOfRating());
		meal.setPreparingTime(this.getPreparingTime());
		return meal;
	}

	public Integer getPreparingTime() {
		return preparingTime;
	}

	public void setPreparingTime(Integer preparingTime) {
		this.preparingTime = preparingTime;
	}
}