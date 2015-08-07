package com.worescloud.workdesk.common.jmx.helper;

import com.worescloud.workdesk.common.jmx.MBean;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.ReflectionException;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.String.format;

public class MBeanAttribute {

	private MBean mBean;
	private MBeanAttributeInfo attributeInfo;

	public MBeanAttribute(MBean mBean, MBeanAttributeInfo attributeInfo) {
		this.mBean = mBean;
		this.attributeInfo = attributeInfo;
	}

	public Object getAttribute() throws AttributeNotFoundException, ReflectionException {
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor();
		Method readMethod = propertyDescriptor.getReadMethod();
		if (readMethod == null) {
			throw new AttributeNotFoundException(format("Attribute [%s] in [%s] not readable", this.attributeInfo.getName(), mBean.getClass().getName()));
		}

		return invoke(readMethod);
	}

	public void setAttribute(Object value) throws AttributeNotFoundException, ReflectionException {
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor();
		Method writeMethod = propertyDescriptor.getWriteMethod();
		if (writeMethod == null) {
			throw new AttributeNotFoundException(format("Attribute [%s] in [%s] not writable", this.attributeInfo.getName(), mBean.getClass().getName()));
		}

		invoke(writeMethod, value);
	}

	private PropertyDescriptor getPropertyDescriptor() throws AttributeNotFoundException {
		try {
			return new PropertyDescriptor(this.attributeInfo.getName(), mBean.getClass());
		} catch (IntrospectionException e) {
			throw new AttributeNotFoundException(format("Attribute [%s] in [%s] not found", this.attributeInfo.getName(), mBean.getClass().getName()));
		}
	}

	private Object invoke(Method method, Object ... args) throws ReflectionException {
		try {
			return method.invoke(mBean, args);
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new ReflectionException(e);
		}
	}
}
