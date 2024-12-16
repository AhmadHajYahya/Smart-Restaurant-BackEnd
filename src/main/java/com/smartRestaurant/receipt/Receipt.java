package com.smartRestaurant.receipt;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("receipt")
public class Receipt implements Persistable<String>{
	@Id
	private String receiptID;

	@Column("mealOrder_id")
	private String mealOrderId;

	@Column("total_price")
	private Double total_price;
	
	@Column("date")
	private LocalDate date;
	
	@Column("receipet_doc")
	private String receiptDoc;
	
	@Transient
	private boolean isNew = true;
	public Receipt() {

	}

	public Receipt(String mealOrderId, Double total_price) {
		super();
		this.mealOrderId = mealOrderId;
		this.total_price = total_price;
	}

	public String getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(String receiptID) {
		this.receiptID = receiptID;
	}

	public String getMealOrderId() {
		return mealOrderId;
	}

	public void setMealOrderId(String mealOrderId) {
		this.mealOrderId = mealOrderId;
	}

	public Double getTotal_price() {
		return total_price;
	}

	public void setTotal_price(Double total_price) {
		this.total_price = total_price;
	}

	

	@Override
	public String toString() {
		return "Receipt [receiptID=" + receiptID + ", mealOrderId=" + mealOrderId + ", total_price=" + total_price
				+ ", isNew=" + isNew + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(isNew, mealOrderId, receiptID, total_price);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Receipt)) {
			return false;
		}
		Receipt other = (Receipt) obj;
		return isNew == other.isNew && Objects.equals(mealOrderId, other.mealOrderId)
				&& Objects.equals(receiptID, other.receiptID) && Objects.equals(total_price, other.total_price);
	}

	@Override
	public String getId() {
		return receiptID;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public String getReceiptDoc() {
		return receiptDoc;
	}

	public void setReceiptDoc(String receiptDoc) {
		this.receiptDoc = receiptDoc;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

}
