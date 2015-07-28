package com.worescloud.workdesk.common.exception.code;

public enum ExceptionCategory {

	INNER(0),
	ILLEGAL(1),
	FORBIDDEN(2);

	private int category;

	private ExceptionCategory(int category) {
		this.category = category;
	}

	public int value() {
		return category;
	}
}
