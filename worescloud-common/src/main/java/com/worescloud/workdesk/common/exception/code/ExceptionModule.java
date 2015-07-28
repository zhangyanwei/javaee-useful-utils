package com.worescloud.workdesk.common.exception.code;

public enum ExceptionModule {

	COMMON(0),
	AUTH(1),
	MAIL(2);

	private int module;

	ExceptionModule(int module) {
		this.module = module;
	}

	public ExceptionCode createCode(ExceptionCategory category, int code) {
		return new ExceptionCode(this, category, code, null);
	}

	public ExceptionCode createCode(ExceptionCategory category, int code, String description) {
		return new ExceptionCode(this, category, code, description);
	}

	public int value() {
		return module;
	}
}
