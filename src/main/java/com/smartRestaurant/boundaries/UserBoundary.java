package com.smartRestaurant.boundaries;

import com.smartRestaurant.enums.Roles;
import com.smartRestaurant.user.User;

public abstract class UserBoundary {
	protected String userId;
	protected String username;
	protected String name;
	protected String phoneNumber;
	protected Roles role;
	protected Boolean isOnline;
	public UserBoundary() {
	}

	public UserBoundary(String username, String name, String phoneNumber, Roles role) {
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.name = name;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	public abstract User toEntity();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}
}
