package com.smartRestaurant.callWaiter;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("call_waiter")
public class CallWaiter implements Persistable<String>{

	@Id
	private String id;
	@Column("waiter_id")
	private String waiterId;
	@Column("table_id")
	private String tableId;

	public CallWaiter(String id, String waiterId, String tableId) {
		super();
		this.id = id;
		this.waiterId = waiterId;
		this.tableId = tableId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@Override
	public boolean isNew() {
		return true;
	}

}
