package com.smartRestaurant.boundaries;

import com.smartRestaurant.receipt.Receipt;

public class ReceiptBoundary {
	private String receiptId;
	private String mealOrderId;
	private Double total_price;
	private String receiptDoc;

	public ReceiptBoundary() {

	}

	public ReceiptBoundary(Receipt receipt) {
		this.setMealOrderId(receipt.getMealOrderId());
		this.setRecieptId(receipt.getReceiptID());
		this.setTotal_price(receipt.getTotal_price());
		this.setReceiptDoc(receipt.getReceiptDoc());
	}

	public ReceiptBoundary(String receiptId, String mealOrderId, Double total_price, String receiptDoc) {
		super();
		this.receiptId = receiptId;
		this.mealOrderId = mealOrderId;
		this.total_price = total_price;
		this.receiptDoc = receiptDoc;
	}

	public String getRecieptId() {
		return receiptId;
	}

	public void setRecieptId(String recieptId) {
		this.receiptId = recieptId;
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

	public String getReceiptDoc() {
		return receiptDoc;
	}

	public void setReceiptDoc(String receiptDoc) {
		this.receiptDoc = receiptDoc;
	}

	public Receipt toEntity() {
		Receipt r = new Receipt();
		r.setMealOrderId(this.getMealOrderId());
		return r;
	}

}
