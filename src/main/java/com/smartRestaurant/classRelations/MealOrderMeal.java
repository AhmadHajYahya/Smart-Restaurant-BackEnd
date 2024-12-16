package com.smartRestaurant.classRelations;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("meal_order_meal")
public class MealOrderMeal implements Persistable<String> {
	@Id
	private String id;

	@Column("meal_order_id")
	private String mealOrderId;

	@Column("meal_id")
	private String mealId;

	@Transient
	private boolean isNew = true;

	public MealOrderMeal(String mealOrderId, String mealId) {
		super();
		this.mealOrderId = mealOrderId;
		this.mealId = mealId;
	}

	@Override
	public String getId() {
		return UUID.randomUUID().toString();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMealOrderId() {
		return mealOrderId;
	}

	public void setMealOrderId(String mealOrderId) {
		this.mealOrderId = mealOrderId;
	}

	public String getMealId() {
		return mealId;
	}

	public void setMealId(String mealId) {
		this.mealId = mealId;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

}
