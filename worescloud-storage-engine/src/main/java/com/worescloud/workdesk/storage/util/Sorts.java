package com.worescloud.workdesk.storage.util;

import com.worescloud.workdesk.storage.Sort;

public final class Sorts {

	public static Sort asc(String fieldName) {
		return new Sort(fieldName, false);
	}

	public static Sort desc(String fieldName) {
		return new Sort(fieldName, true);
	}

}
