package com.smartRestaurant.boundaries;

import com.smartRestaurant.user.User;

public class RegisteredUserBoundary extends UserBoundary {
	private String password;
	private String email;
	private Integer ordersCount;
	private Integer bookingsCount;


	public RegisteredUserBoundary() {
	}

	public RegisteredUserBoundary(User user) {
		this.setUserId(user.getUserId());
		this.setUsername(user.getUsername());
		this.setPhoneNumber(user.getPhoneNumber());
		this.setName(user.getName());
		this.setRole(user.getRole());
		this.setEmail(user.getEmail());
		this.setOrdersCount(user.getOrdersCount());
		this.setBookingsCount(user.getBookingsCount());
		this.setIsOnline(user.getIsOnline());
	}

	@Override
	public User toEntity() {
		User user = new User();
		user.setUserId(this.getUserId());
		user.setUsername(this.getUsername());
		user.setPhoneNumber(this.getPhoneNumber());
		user.setName(this.getName());
		user.setRole(this.getRole());
		user.setEmail(this.getEmail());
		user.setPassword(this.getPassword());
		user.setOrdersCount(this.getOrdersCount());
		user.setBookingsCount(this.getBookingsCount());
		user.setIsOnline(this.getIsOnline());
		return user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getOrdersCount() {
		return ordersCount;
	}

	public void setOrdersCount(int ordersCount) {
		this.ordersCount = ordersCount;
	}

	public int getBookingsCount() {
		return bookingsCount;
	}

	public void setBookingsCount(int bookingsCount) {
		this.bookingsCount = bookingsCount;
	}

	@Override
	public String toString() {
		return "RegisteredUserBoundary [password=" + password + ", email=" + email + ", ordersCount=" + ordersCount
				+ ", bookingsCount=" + bookingsCount + ", userId=" + userId + ", username=" + username + ", name="
				+ name + ", phoneNumber=" + phoneNumber + ", role=" + role + "]";
	}


}
