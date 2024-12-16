package com.smartRestaurant.boundaries;

import com.smartRestaurant.user.User;

public class GuestUserBoundary extends UserBoundary {

	public GuestUserBoundary() {
	}

	public GuestUserBoundary(User user) {
		this.userId = user.getUserId();
		this.username = user.getUsername();
		this.phoneNumber = user.getPhoneNumber();
		this.role = user.getRole();
		this.name = user.getName();
		this.isOnline = user.getIsOnline();
	}

	@Override
	public User toEntity() {
		User user = new User();
		user.setUserId(this.getUserId());
		user.setUsername(this.getUsername());
		user.setPhoneNumber(this.getPhoneNumber());
		user.setRole(this.getRole());
		user.setName(this.getName());
		user.setIsOnline(this.getIsOnline());
		return user;
	}

	@Override
	public String toString() {
		return "GuestUserBoundary [userId=" + userId + ", username=" + username + ", name=" + name + ", phoneNumber="
				+ phoneNumber + ", role=" + role + "]";
	}

}
