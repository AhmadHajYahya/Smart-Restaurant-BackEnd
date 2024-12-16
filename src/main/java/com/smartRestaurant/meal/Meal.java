package com.smartRestaurant.meal;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.smartRestaurant.enums.Category;

@Table("meal")
public class Meal implements Persistable<String> {
	@Id
	private String mealId;

	@Column("title")
	private String title;

	@Column("description")
	private String description;

	@Column("price")
	private double price;

	@Column("is_available")
	private boolean isAvailable;

	@Column("category")
	private Category category;

	@Column("image")
	private String imageURL;

	@Column("rating")
	private Double rating;

	@Column("number_of_rating")
	private Long numberOfRating;

	@Column("order_count")
	private Long orderCount;
	
	@Column("preparing_time")
	private Integer preparingTime; // in minutes.

	@Transient
	private boolean isNew = true;

	public Meal() {

	}

	public Meal(String title, String description, double price, boolean isAvailable, Category category, String image) {
		this.title = title;
		this.description = description;
		this.price = price;
		this.isAvailable = isAvailable;
		this.category = category;
		this.imageURL = image;
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

	public Long getNumberOfRating() {
		return numberOfRating;
	}

	public void setNumberOfRating(Long numberOfRating) {
		this.numberOfRating = numberOfRating;
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean getIsAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
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

	@Override
	public String toString() {
		return "Meal [mealId=" + mealId + ", title=" + title + ", description=" + description + ", price=" + price
				+ ", isAvailable=" + isAvailable + ", category=" + category + ", imageURL=" + imageURL + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, description, imageURL, isAvailable, mealId, price, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Meal)) {
			return false;
		}
		Meal other = (Meal) obj;
		return category == other.category && Objects.equals(description, other.description)
				&& Objects.equals(imageURL, other.imageURL) && isAvailable == other.isAvailable
				&& mealId == other.mealId && Double.doubleToLongBits(price) == Double.doubleToLongBits(other.price)
				&& Objects.equals(title, other.title);
	}

	@Override
	public String getId() {
		return mealId;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Long getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Long orderCount) {
		this.orderCount = orderCount;
	}

	public void updateRating(double newRating) {
		double oldRating = this.getRating();
		long numberOfRatings = this.getNumberOfRating();

		double newAverageRating = (oldRating * numberOfRatings + newRating) / (numberOfRatings + 1);

		this.setRating(newAverageRating);
		this.setNumberOfRating(numberOfRating + 1);
	}
	
	public void updateOrderCount() {
		this.setOrderCount(this.getOrderCount()+1);
	}

	public int getPreparingTime() {
		return preparingTime;
	}

	public void setPreparingTime(int preparingTime) {
		this.preparingTime = preparingTime;
	}

}
