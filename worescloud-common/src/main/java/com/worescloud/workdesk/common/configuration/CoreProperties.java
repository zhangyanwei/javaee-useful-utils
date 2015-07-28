package com.worescloud.workdesk.common.configuration;

import com.worescloud.workdesk.common.exception.CommonException;
import org.apache.commons.beanutils.ConvertUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.worescloud.workdesk.common.exception.CommonException.Error.PROPERTY_METHOD_MISSING_PARAMETER;

public final class CoreProperties {

	private static Map<String, NullableProperties> propertiesMap = new HashMap<String, NullableProperties>();
	
	private static Map<String, File> propertiesFileMap = new HashMap<String, File>();
	
	private static Map<String, Long> lastModifyTimeMap = new HashMap<String, Long>();

	@SuppressWarnings("unchecked")
	public static synchronized <T> T getProperties(Class<T> clazz) throws CommonException
	{
		// create instance for type T.
		T instance;
		try {
			Constructor<?> constructor = clazz.getConstructor();
			instance = (T) constructor.newInstance();
		} catch (Exception e) {
			throw new CommonException(e);
		}

		// check annotation
		if (!clazz.isAnnotationPresent(com.worescloud.workdesk.common.configuration.Properties.class)) {
			return instance;
		}

		// read properties file.
		com.worescloud.workdesk.common.configuration.Properties annotationProperties
				= clazz.getAnnotation(com.worescloud.workdesk.common.configuration.Properties.class);
		String propertiesName = annotationProperties.value();
		String dir = annotationProperties.dir();
		
		NullableProperties properties = propertiesMap.get(propertiesName);
		File propertiesFile = propertiesFileMap.get(propertiesName);
		Long lastModifyTime = lastModifyTimeMap.get(propertiesName);
		if (properties == null || propertiesFile == null || propertiesFile.lastModified() > lastModifyTime) {
			propertiesFile = FileFinder.findFile(propertiesName, dir);
			propertiesFileMap.put(propertiesName, propertiesFile);
			
			lastModifyTime = propertiesFile.lastModified();
            lastModifyTimeMap.put(propertiesName, lastModifyTime);
            
	        properties = (NullableProperties) readProperties(propertiesFile);
	        propertiesMap.put(propertiesName, properties);
		} else {
		    properties = propertiesMap.get(propertiesName);
		}
		
		// find set method which has Property annotation.
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			int mod = method.getModifiers();
			if (!Modifier.isPublic(mod) || !method.isAnnotationPresent(Property.class)) {
				continue;
			}
			
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length != 1) {
				throw new CommonException(PROPERTY_METHOD_MISSING_PARAMETER, method.getName(), 1);
			}
			
			Property annotationProperty = method.getAnnotation(Property.class);
			String key = annotationProperty.key();
			String defaultValue = annotationProperty.defaultValue();
			String propertyValue = properties.getProperty(key, defaultValue);
			try {
				method.invoke(instance, ConvertUtils.convert(propertyValue, parameterTypes[0]));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		if (instance instanceof Properties) {
			Properties props = (Properties) instance;
			props.putAll(properties);
		}
		
		return instance;
	}

	private static Properties readProperties(File file) throws CommonException {
		BufferedInputStream bis = null;
		Properties prop = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			prop = new NullableProperties();
			prop.load(bis);
		} catch (SecurityException | IOException ex1) {
			throw new CommonException(ex1);
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException ignored) {
			}
		}
		return prop;
	}
	
	public static class NullableProperties extends Properties {

		private static final long serialVersionUID = 8591137352668286302L;

		public String getProperty(String key, String defaultValue) {
			String origin = super.getProperty(key, defaultValue);
			if (origin != null && origin.trim().length() == 0) {
				// 空白
				return defaultValue;
			}
			return origin;
		}
	};
}
