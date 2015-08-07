package com.worescloud.workdesk.common.jmx;

import com.worescloud.workdesk.common.jmx.helper.MBeanAttribute;
import com.worescloud.workdesk.common.jmx.helper.MBeanHelper;
import com.worescloud.workdesk.common.jmx.helper.MBeanOperation;

import javax.management.*;
import java.lang.management.ManagementFactory;

// TODO not finished, should be continue
public abstract class MBean implements DynamicMBean {

	private MBeanHelper mBeanHelper;

	@Override
	public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
		MBeanAttribute mBeanAttribute = getMBeanHelper().getMBeanAttribute(attribute);
		return mBeanAttribute.getAttribute();
	}

	@Override
	public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
		MBeanAttribute mBeanAttribute = getMBeanHelper().getMBeanAttribute(attribute.getName());
		mBeanAttribute.setAttribute(attribute.getValue());
	}

	@Override
	public AttributeList getAttributes(String[] attributes) {

		AttributeList attributeList = new AttributeList();
		for (String attribute : attributes) {
			try {
				attributeList.add(new Attribute(attribute, getAttribute(attribute)));
			} catch (AttributeNotFoundException | MBeanException | ReflectionException ignored) {
			}
		}

		return attributeList;
	}

	@Override
	public AttributeList setAttributes(AttributeList attributes) {

		AttributeList attributeList = new AttributeList();
		for (Attribute attribute : attributes.asList()) {
			try {
				setAttribute(attribute);
				attributeList.add(attribute);
			} catch (AttributeNotFoundException | InvalidAttributeValueException | ReflectionException | MBeanException ignored) {
			}
		}

		return attributeList;
	}

	@Override
	public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
		MBeanOperation mBeanOperation = getMBeanHelper().getMBeanOperation(actionName, signature);
		return mBeanOperation.invoke(params);
	}

	protected void register(ObjectName objectName, Object objectInstance) {

		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			mBeanServer.registerMBean(objectInstance, objectName);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
			throw new IllegalStateException(e);
		}

	}

	protected void unregister(ObjectName objectName) {

		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			mBeanServer.unregisterMBean(objectName);
		} catch (MBeanRegistrationException | InstanceNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	protected MBeanHelper getMBeanHelper() {
		if (this.mBeanHelper == null) {
			this.mBeanHelper = new MBeanHelper(this);
		}

		return this.mBeanHelper;
	}

}
