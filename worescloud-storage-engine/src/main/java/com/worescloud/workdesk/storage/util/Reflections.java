package com.worescloud.workdesk.storage.util;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static com.worescloud.workdesk.common.reflect.ObjectUtil.field;
import static com.worescloud.workdesk.storage.exception.StorageException.Error.BEAN_INVALID_KEY_ANNOTATIONS;
import static com.worescloud.workdesk.storage.exception.StorageException.Error.BEAN_NOT_SET_ANNOTATED;
import static java.lang.reflect.Modifier.*;
import static java.util.Arrays.stream;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.worescloud.workdesk.storage.annotation.Collection;
import com.worescloud.workdesk.storage.annotation.FieldAlias;
import com.worescloud.workdesk.storage.annotation.Key;
import com.worescloud.workdesk.storage.annotation.Transient;
import com.worescloud.workdesk.storage.exception.StorageException;

public class Reflections {

	private static final String NOT_ALLOWED_FIELD_NAME = "^[$_].+$";

	private static final Cache<Class, Field> KEY_FIELDS = newBuilder().build();

	@Nonnull
	public static  <E> BeanInfo analyse(E entity) throws StorageException {
		Class<?> aClass = entity.getClass();
		Field[] declaredFields = aClass.getDeclaredFields();
		Map<String, Object> attributes = attributes(entity, declaredFields);

		BeanInfo beanInfo = new BeanInfo();
		beanInfo.setSetName(beanSetName(aClass));
		beanInfo.setKey(beanKeyValue(entity, aClass));
		beanInfo.setAttributes(attributes);
		return beanInfo;
	}

	@Nonnull
	public static String beanSetName(Class<?> aClass) throws StorageException {
		if (!aClass.isAnnotationPresent(Collection.class)) {
			throw new StorageException(BEAN_NOT_SET_ANNOTATED);
		}

		return aClass.getAnnotation(Collection.class).value();
	}

	public static boolean isKeyAttribute(Class<?> aClass, String attrName) {
		try {
			Field field = beanKeyField(aClass);
			return field.getName().equals(attrName);
		} catch (StorageException e) {
			return false;
		}
	}

	public static String attributeName(Field field) {
		if (field.isAnnotationPresent(FieldAlias.class)) {
			FieldAlias binAlias = field.getAnnotation(FieldAlias.class);
			return binAlias.value();
		}

		return field.getName();
	}

	@Nonnull
	public static String[] storableAttributes(Class<?> aClass) {
		return storableFields(aClass.getDeclaredFields()).stream()
				.map(Field::getName)
				.toArray(String[]::new);
	}

	private static Object beanKeyValue(Object entity, Class<?> aClass) throws StorageException {
		try {
			return field(entity, beanKeyField(aClass));
		} catch (IllegalAccessException e) {
			throw new StorageException(e);
		}
	}

	private static Field beanKeyField(Class<?> aClass) throws StorageException {
		try {
			return KEY_FIELDS.get(aClass, () -> {
				Field[] keyFields = stream(aClass.getDeclaredFields())
						.filter(field -> field.isAnnotationPresent(Key.class))
						.toArray(Field[]::new);
				if (keyFields.length != 1) {
					throw new StorageException(BEAN_INVALID_KEY_ANNOTATIONS);
				}

				return keyFields[0];
			});
		} catch (ExecutionException e) {
			throw new StorageException(e);
		}
	}

	private static <E> Map<String, Object> attributes(E entity, Field[] declaredFields) throws StorageException {
		List<Field> legalFields = storableFields(declaredFields);
		Map<String, Object> attributes = new HashMap<>();
		for (Field field : legalFields) {
			attributes.put(attributeName(field), attributeValue(entity, field));
		}

		return attributes;
	}

	private static Object attributeValue(Object entity, Field field) throws StorageException {
		try {
			return field(entity, field);
		} catch (IllegalAccessException e) {
			throw new StorageException(e);
		}
	}

	private static List<Field> storableFields(Field[] declaredFields) {
		return stream(declaredFields)
				.filter(field -> !field.isAnnotationPresent(Key.class))
				.filter(field -> !field.isAnnotationPresent(Transient.class))
				.filter(field -> !isTransient(field.getModifiers()) && !isStatic(field.getModifiers()) && !isFinal(field.getModifiers()))
				.filter(field -> !field.getName().matches(NOT_ALLOWED_FIELD_NAME))
				.collect(Collectors.toList());
	}

	public static class BeanInfo {

		private String setName;
		private Object key;
		private Map<String, Object> attributes;

		public void setSetName(String setName) {
			this.setName = setName;
		}

		public String getSetName() {
			return setName;
		}

		public void setKey(Object key) {
			this.key = key;
		}

		public Object getKey() {
			return key;
		}

		public void setAttributes(Map<String,Object> attributes) {
			this.attributes = attributes;
		}

		public Map<String, Object> getAttributes() {
			return attributes;
		}

	}
}
