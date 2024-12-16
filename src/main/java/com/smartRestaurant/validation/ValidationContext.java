package com.smartRestaurant.validation;

public class ValidationContext {
	private ValidationStrategy strategy;

	public ValidationContext(ValidationStrategy strategy) {
		this.strategy = strategy;
	}

	public boolean executeStrategy(String s) {
		return strategy.validate(s);
	}
}
