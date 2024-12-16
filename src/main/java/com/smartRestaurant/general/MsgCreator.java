package com.smartRestaurant.general;

public class MsgCreator {

	public static String created(String str) {
		return str + " created successfully";
	}

	public static String updated(String str) {
		return str + " updated successfully";
	}

	public static String notFound(String str) {
		return str + " not found";
	}

	public static String nullOrEmpty() {
		return "Some needed fields are null or empty";
	}

	public static String fetched(String str) {
		return str + " fetched successfully";
	}
	
	public static String invalidDateTime(String type, Object obj) {
		return "Invalid " + type + ", must start from " + obj;
	}
}
