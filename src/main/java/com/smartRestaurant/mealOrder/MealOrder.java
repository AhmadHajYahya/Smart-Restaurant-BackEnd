package com.smartRestaurant.mealOrder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.smartRestaurant.enums.Status;

@Table("meal_order")
public class MealOrder implements Persistable<String> {

	@Id
	private String mOrderID;

	@Column("user_id")
	private String userId;

	@Column("table_id")
	private String tableId;

	@Column("date")
	private LocalDate date;

	@Column("time")
	private LocalTime time;

	@Column("status")
	private Status status;

	@Transient
	private boolean isNew = true;

	public MealOrder() {

	}

	public MealOrder(String userId, String tableId) {
		this.userId = userId;
		this.tableId = tableId;
		this.date = LocalDate.now();
		this.time = LocalTime.now();
	}

	public String getmOrderID() {
		return mOrderID;
	}

	public void setmOrderID(String mOrderID) {
		this.mOrderID = mOrderID;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "MealOrder [mOrderID=" + mOrderID + ", userId=" + userId + ", tableId=" + tableId + ", date=" + date
				+ ", time=" + time + ", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, mOrderID, status, tableId, time, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof MealOrder)) {
			return false;
		}
		MealOrder other = (MealOrder) obj;
		return Objects.equals(date, other.date) && Objects.equals(mOrderID, other.mOrderID) && status == other.status
				&& Objects.equals(tableId, other.tableId) && Objects.equals(time, other.time)
				&& Objects.equals(userId, other.userId);
	}

	@Override
	public String getId() {
		return mOrderID;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

}
