package com.smartRestaurant.general;

public class IdGenerator {

	// Generate table id.
	public static String generateTableId(int number) {
		return "T" + number;
	}

	// Generate meal id.
	public static String generateMealId(int number) {
		return "M" + number;
	}

	// Generate meal order id.
	public static String generateMealOrderId(int number) {
		return "O" + number;
	}

	// Generate booking id.
	public static String generateBookingId(int number) {
		return "B" + number;
	}
	
	public static String generateReceiptId(int number) {
		return "R" + number;
	}
}
