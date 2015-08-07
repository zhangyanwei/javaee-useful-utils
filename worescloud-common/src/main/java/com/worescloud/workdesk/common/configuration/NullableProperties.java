package com.worescloud.workdesk.common.configuration;

import static com.google.common.base.Strings.isNullOrEmpty;

public class NullableProperties extends java.util.Properties {

	private static final long serialVersionUID = 8591137352668286302L;

	@Override
	public String getProperty(String key, String defaultValue) {
		String origin = super.getProperty(key, defaultValue);
		if (isNullOrEmpty(origin)) {
			return defaultValue;
		}

		return origin;
	}
}
