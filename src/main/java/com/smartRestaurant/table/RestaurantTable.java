package com.smartRestaurant.table;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("restaurant_table")
public class RestaurantTable implements Persistable<String> {

	@Id
	private String tableId;

	@Column("seats_number")
	private Integer seatsNumber;

	@Column("is_taken")
	private Boolean isTaken;

	@Column("is_booked")
	private Boolean isBooked;

	@Column("user_id")
	private String userId;

	@Transient
	private boolean isNewEntry = true;

	public RestaurantTable() {
	}

	public RestaurantTable(int seatsNumber) {
		this();
		this.seatsNumber = seatsNumber;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public Integer getSeatsNumber() {
		return seatsNumber;
	}

	public void setSeatsNumber(Integer seatsNumber) {
		this.seatsNumber = seatsNumber;
	}

	public Boolean getIsTaken() {
		return isTaken;
	}

	public void setIsTaken(Boolean isTaken) {
		this.isTaken = isTaken;
	}

	public Boolean getIsBooked() {
		return isBooked;
	}

	public void setIsBooked(Boolean isBooked) {
		this.isBooked = isBooked;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(tableId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof RestaurantTable))
			return false;
		RestaurantTable other = (RestaurantTable) obj;
		return tableId == other.tableId;
	}

	@Override
	public boolean isNew() {
		return isNewEntry();
	}

	@Override
	public String getId() {
		return tableId;
	}

	public boolean isNewEntry() {
		return isNewEntry;
	}

	public void setNewEntry(boolean isNewEntry) {
		this.isNewEntry = isNewEntry;
	}

	@Override
	public String toString() {
		return "RestaurantTable [tableId=" + tableId + ", seatsNumber=" + seatsNumber + ", isTaken=" + isTaken
				+ ", isBooked=" + isBooked + ", userId=" + userId + ", isNewEntry=" + isNewEntry + "]";
	}
	
	
}
