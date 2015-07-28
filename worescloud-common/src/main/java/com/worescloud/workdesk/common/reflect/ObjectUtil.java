package com.worescloud.workdesk.common.reflect;

import java.lang.reflect.Field;

public final class ObjectUtil {

	public static <T> T field(Object instance, Field field) throws IllegalAccessException {
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		try {
			//noinspection unchecked
			return (T) field.get(instance);
		} finally {
			field.setAccessible(accessible);
		}
	}

	public static void field(Object instance, Field field, Object value) throws IllegalAccessException {
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		try {
			//noinspection unchecked
			field.set(instance, value);
		} finally {
			field.setAccessible(accessible);
		}
	}

}
