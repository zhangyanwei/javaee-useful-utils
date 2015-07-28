package com.worescloud.workdesk.storage;

public class Sort {

	private String fieldName;
	private boolean desc;

	public Sort(String fieldName, boolean desc) {
		this.fieldName = fieldName;
		this.desc = desc;
	}

	public String getFieldName() {
		return fieldName;
	}

	public boolean isDesc() {
		return desc;
	}
}
