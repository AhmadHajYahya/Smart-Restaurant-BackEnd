package com.smartRestaurant.boundaries;

import com.smartRestaurant.callWaiter.CallWaiter;

public class CallWaiterBoundary {

	private String callWaiterId;
	private String waiterId;
	private String tableId;

	public CallWaiterBoundary() {

	}

	public CallWaiterBoundary(CallWaiter waiter) {
		this.setCallWaiterId(waiter.getId());
		this.setWaiterId(waiter.getWaiterId());
		this.setTableId(waiter.getTableId());
	}

	public CallWaiterBoundary(String callWaiterId, String waiterId, String tableId) {
		super();
		this.callWaiterId = callWaiterId;
		this.waiterId = waiterId;
		this.tableId = tableId;
	}

	public String getCallWaiterId() {
		return callWaiterId;
	}

	public void setCallWaiterId(String callWaiterId) {
		this.callWaiterId = callWaiterId;
	}

	public String getWaiterId() {
		return waiterId;
	}

	public void setWaiterId(String waiterId) {
		this.waiterId = waiterId;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

}
