package com.worescloud.workdesk.common.configuration;

import com.worescloud.workdesk.common.configuration.impl.FileConfigurationReader;
import com.worescloud.workdesk.common.exception.CommonException;
import com.worescloud.workdesk.common.exception.WcRuntimeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import static com.worescloud.workdesk.common.exception.CommonException.Error.NOT_ANNOTATED_WITH_EXPECTED_ANNOTATION;
import static com.worescloud.workdesk.common.exception.CommonException.Error.PROPERTY_METHOD_MISSING_PARAMETER;
import static java.lang.reflect.Modifier.isPublic;
import static org.apache.commons.beanutils.ConvertUtils.convert;

public final class CoreProperties {

	private static ConfigurationReader reader;

	public static synchronized void registerConfigurationReader(ConfigurationReader reader) {
		if (CoreProperties.reader != null) {
			throw new WcRuntimeException("configuration reader already registered!");
		}

		CoreProperties.reader = reader;
	}

	public static synchronized <T> T getProperties(Class<T> clazz) throws CommonException {
		ensureReader();

		// read properties file.
		Properties properties = readProperties(clazz);

		// find set method which has Property annotation.
		return updateInstance(clazz, properties);
	}

	private static void ensureReader() {
		if (reader == null) {
			reader = new FileConfigurationReader();
		}
	}

	private static <T> Properties readProperties(Class<T> clazz) throws CommonException {

		if (!clazz.isAnnotationPresent(com.worescloud.workdesk.common.configuration.Properties.class)) {
			throw new CommonException(NOT_ANNOTATED_WITH_EXPECTED_ANNOTATION, com.worescloud.workdesk.common.configuration.Properties.class);
		}

		com.worescloud.workdesk.common.configuration.Properties annotationProperties
				= clazz.getAnnotation(com.worescloud.workdesk.common.configuration.Properties.class);

		try {
			return reader.readProperties(annotationProperties.value());
		} catch (java.io.IOException e) {
			throw new CommonException(e);
		}
	}

	private static <T> T updateInstance(Class<T> clazz, Properties properties) throws CommonException {

		// create instance for type T.
		T instance = createInstance(clazz);

		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			int mod = method.getModifiers();
			if (!isPublic(mod) || !method.isAnnotationPresent(Property.class)) {
				continue;
			}

			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length != 1) {
				throw new CommonException(PROPERTY_METHOD_MISSING_PARAMETER, method.getName(), 1);
			}

			Property annotationProperty = method.getAnnotation(Property.class);
			String propertyValue = properties.getProperty(annotationProperty.key(), annotationProperty.defaultValue());
			try {
				method.invoke(instance, convert(propertyValue, parameterTypes[0]));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				throw new CommonException(e);
			}
		}

		if (instance instanceof Properties) {
			Properties props = (Properties) instance;
			props.putAll(properties);
		}

		return instance;
	}

	@SuppressWarnings("unchecked")
	private static <T> T createInstance(Class<T> clazz) throws CommonException {

		try {
			Constructor<?> constructor = clazz.getConstructor();
			return (T) constructor.newInstance();
		} catch (Exception e) {
			throw new CommonException(e);
		}
	}

}
