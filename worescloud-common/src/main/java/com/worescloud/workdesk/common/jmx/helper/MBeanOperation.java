package com.worescloud.workdesk.common.jmx.helper;

import com.google.common.base.Function;
import com.worescloud.workdesk.common.jmx.MBean;

import javax.annotation.Nullable;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;

public class MBeanOperation {

	public static final Map<String, Class> PRIMITIVE_TYPES = newHashMap();

	static {
		PRIMITIVE_TYPES.put(Boolean.TYPE.getName(), Boolean.TYPE);
		PRIMITIVE_TYPES.put(Character.TYPE.getName(), Character.TYPE);
		PRIMITIVE_TYPES.put(Byte.TYPE.getName(), Byte.TYPE);
		PRIMITIVE_TYPES.put(Short.TYPE.getName(), Short.TYPE);
		PRIMITIVE_TYPES.put(Integer.TYPE.getName(), Integer.TYPE);
		PRIMITIVE_TYPES.put(Long.TYPE.getName(), Long.TYPE);
		PRIMITIVE_TYPES.put(Float.TYPE.getName(), Float.TYPE);
		PRIMITIVE_TYPES.put(Double.TYPE.getName(), Double.TYPE);
		PRIMITIVE_TYPES.put(Void.TYPE.getName(), Void.TYPE);
	}

	private final MBean mBean;
	private final MBeanOperationInfo operationInfo;

	public MBeanOperation(MBean mBean, MBeanOperationInfo operationInfo) {
		this.mBean = mBean;
		this.operationInfo = operationInfo;
	}

	public Object invoke(Object[] params) throws ReflectionException, MBeanException {

		Method method = getMethod();
		try {
			return method.invoke(mBean, params);
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e);
		} catch (InvocationTargetException e) {
			throw new MBeanException(e);
		}
	}

	private Method getMethod() throws ReflectionException {
		MBeanParameterInfo[] signature = operationInfo.getSignature();
		List<Class> parameterTypes = transform(newArrayList(signature), new Function<MBeanParameterInfo, Class>() {
			@Nullable
			@Override
			public Class apply(MBeanParameterInfo parameterInfo) {
				String parameterType = parameterInfo.getType();
				return getClassFromString(parameterType);
			}
		});

		try {
			return mBean.getClass().getMethod(operationInfo.getName(), parameterTypes.toArray(new Class[parameterTypes.size()]));
		} catch (NoSuchMethodException e) {
			throw new ReflectionException(new NoSuchMethodException(format("No matching method found for operation [%s]", operationInfo.getName())));
		}
	}

	/**
	 * Takes the string representation of a class
	 * and returns the corresponding Class object.
	 */
	private static Class getClassFromString(String className) {

		Class type = PRIMITIVE_TYPES.get(className);

		if (type == null) {
			// Not a primitive type, just load the class based on the name
			try {
				type = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(format("Can not load class by name [%s]", className));
			}
		}

		return  type;
	}
}
