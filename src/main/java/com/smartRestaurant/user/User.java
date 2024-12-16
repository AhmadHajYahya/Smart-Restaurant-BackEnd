package com.smartRestaurant.user;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.smartRestaurant.enums.Roles;

@Table("User")
public class User implements Persistable<String> {

	@Id
	private String userId;

	@Column("username")
	private String username;
	
	@Column("name")
	private String name;

	@Column("phone_number")
	private String phoneNumber;

	@Column("role")
	private Roles role;

	@Column("password")
	private String password;

	@Column("email")
	private String email;

	@Column("orders_count")
	private int ordersCount;

	@Column("bookings_count")
	private int bookingsCount;
	
	@Column("is_online")
	private Boolean isOnline;

	@Transient
	private boolean isNew = true;

	public User() {

	}

	public User(String username, String phoneNumber, Roles role) {
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.role = role;
	}

	public User(String username, String phoneNumber, Roles role, String password, String email, int ordersCount,
			int bookingsCount) {
		super();
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.password = password;
		this.email = email;
		this.ordersCount = ordersCount;
		this.bookingsCount = bookingsCount;
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
		return "User [userId=" + userId + ", username=" + username + ", phoneNumber=" + phoneNumber + ", role=" + role
				+ ", password=" + password + ", email=" + email + ", ordersCount=" + ordersCount + ", bookingsCount="
				+ bookingsCount + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(bookingsCount, email, ordersCount, password, phoneNumber, role, userId, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		return bookingsCount == other.bookingsCount && Objects.equals(email, other.email)
				&& ordersCount == other.ordersCount && Objects.equals(password, other.password)
				&& Objects.equals(phoneNumber, other.phoneNumber) && role == other.role && userId == other.userId
				&& Objects.equals(username, other.username);
	}

	@Override
	public String getId() {
		return userId;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

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
