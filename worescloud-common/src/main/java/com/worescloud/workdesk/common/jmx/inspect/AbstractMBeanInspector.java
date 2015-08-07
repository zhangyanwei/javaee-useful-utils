package com.worescloud.workdesk.common.jmx.inspect;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.worescloud.workdesk.common.jmx.MBean;

import javax.annotation.Nullable;
import javax.management.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static java.lang.String.format;

public abstract class AbstractMBeanInspector implements MBeanInspector {

	public static final String DOMAIN = "worescloud.com";

	private ObjectName objectName;

	@Nullable
	@Override
	public MBeanInfo inspect(Class<? extends MBean> mBeanClass) {

		if (isManaged(mBeanClass)) {
			return new MBeanInfo(
					getClassName(mBeanClass),
					getDescription(mBeanClass),
					analysisAttributes(mBeanClass),
					null,
					analysisOperations(mBeanClass),
					null
			);
		}

		return null;
	}

	@Nullable
	@Override
	public ObjectName getObjectName(Class<? extends MBean> mBeanClass) {

		if (objectName == null) {
			ObjectNameInfo objectNameInfo = analysisObjectName(mBeanClass);
			String domain = objectNameInfo.getDomain();
			String type = objectNameInfo.getType();
			String group = objectNameInfo.getGroup();
			String name = objectNameInfo.getName();

			String fullName;
			if (isNullOrEmpty(type)) {
				fullName = format("%s:name=%s", domain, name);
			} else if (isNullOrEmpty(group)) {
				fullName = format("%s:type=%s,name=%s", domain, type, name);
			} else {
				fullName = format("%s:type=%s,group=%s,name=%s", domain, type, group, name);
			}

			try {
				objectName = new ObjectName(fullName);
			} catch (MalformedObjectNameException e) {
				throw new IllegalStateException(e);
			}
		}

		return objectName;
	}

	protected abstract boolean isManaged(Class<? extends MBean> mBeanClass);

	protected abstract boolean isManaged(Field field);

	protected abstract boolean isManaged(Method method);

	protected abstract String getClassName(Class<? extends MBean> mBeanClass);

	protected abstract String getDescription(Class<? extends MBean> mBeanClass);

	protected abstract ObjectNameInfo analysisObjectName(Class<? extends MBean> mBeanClass);

	protected MBeanAttributeInfo[] analysisAttributes(final Class<? extends MBean> mBeanClass) {

		Field[] fields = mBeanClass.getDeclaredFields();

		List<MBeanAttributeInfo> attributes = transform(newArrayList(fields), new Function<Field, MBeanAttributeInfo>() {
			@Nullable
			@Override
			public MBeanAttributeInfo apply(Field field) {
				try {
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), mBeanClass);
					if (isManaged(field)) {
						return analysisAttribute(propertyDescriptor, field);
					}
				} catch (IntrospectionException e) {
					throw new IllegalStateException(e);
				}

				return null;
			}
		});

		return toArray(filter(attributes, new Predicate<MBeanAttributeInfo>() {
			@Override
			public boolean apply(@Nullable MBeanAttributeInfo mBeanAttributeInfo) {
				return mBeanAttributeInfo != null;
			}
		}), MBeanAttributeInfo.class);
	}

	protected MBeanOperationInfo[] analysisOperations(Class<? extends MBean> mBeanClass) {

		Method[] methods = mBeanClass.getMethods();

		List<MBeanOperationInfo> operations = transform(newArrayList(methods), new Function<Method, MBeanOperationInfo>() {
			@Nullable
			@Override
			public MBeanOperationInfo apply(final Method method) {
				if (isManaged(method)) {
					return analysisOperation(method);
				}

				return null;
			}
		});

		return toArray(filter(operations, new Predicate<MBeanOperationInfo>() {
			@Override
			public boolean apply(@Nullable MBeanOperationInfo mBeanOperationInfo) {
				return mBeanOperationInfo != null;
			}
		}), MBeanOperationInfo.class);
	}

	@Nullable
	protected abstract MBeanAttributeInfo analysisAttribute(PropertyDescriptor propertyDescriptor, Field field);

	@Nullable
	protected abstract MBeanOperationInfo analysisOperation(Method method);

	protected static class ObjectNameInfo {

		private String domain;
		private String type;
		private String group;
		private String name;

		public ObjectNameInfo(String domain, String type, String group, String name) {
			this.domain = domain;
			this.type = type;
			this.group = group;
			this.name = name;
		}

		public String getDomain() {
			return domain;
		}

		public String getType() {
			return type;
		}

		public String getGroup() {
			return group;
		}

		public String getName() {
			return name;
		}
	}

}
