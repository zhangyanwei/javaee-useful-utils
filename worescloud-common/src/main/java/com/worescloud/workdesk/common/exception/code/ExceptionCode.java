package com.worescloud.workdesk.common.exception.code;

import java.io.Serializable;
import java.text.DecimalFormat;

public class ExceptionCode implements Serializable {

	private static final String CODE_PREFIX = "WC_";

	private static final DecimalFormat MODULE_FORMAT = new DecimalFormat("00");
    private static final DecimalFormat CATEGORY_FORMAT = new DecimalFormat("00");
	private static final DecimalFormat CODE_FORMAT = new DecimalFormat("000");

	private final String description;
	private final String serializedCode;

	public ExceptionCode(ExceptionModule module, ExceptionCategory category, int code, String description) {
		this.description = description;
		this.serializedCode = CODE_PREFIX + MODULE_FORMAT.format(module.value()) + CATEGORY_FORMAT.format(category.value()) + CODE_FORMAT.format(code);
	}

	public String getDescription() {
		return description;
	}

	public String serializedCode() {
		return serializedCode;
	}
}
