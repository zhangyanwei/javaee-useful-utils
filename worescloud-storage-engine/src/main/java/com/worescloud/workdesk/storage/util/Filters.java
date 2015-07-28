package com.worescloud.workdesk.storage.util;

import static com.worescloud.workdesk.storage.Filter.Action.*;

import com.worescloud.workdesk.storage.Filter;

public final class Filters {

	public static Filter eq(String fieldName, Object value) {
		return new Filter(EQ, fieldName, value);
	}

	public static Filter ne(String fieldName, Object value) {
		return new Filter(NE, fieldName, value);
	}

	public static Filter in(String fieldName, Object ... values) {
		return new Filter(IN, fieldName, values);
	}

	public static Filter in(String fieldName, Iterable values) {
		return new Filter(IN, fieldName, values);
	}

	public static Filter all(String fieldName, Object ... values) {
		return new Filter(ALL, fieldName, values);
	}

	public static Filter not(Filter filter) {
		return new Filter(NOT, null, filter);
	}

	public static Filter exists(String fieldName) {
		return new Filter(EXISTS, fieldName, true);
	}

	public static Filter exists(String fieldName, boolean exists) {
		return new Filter(EXISTS, fieldName, exists);
	}
}
