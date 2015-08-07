package com.worescloud.workdesk.common.jmx;

import com.worescloud.workdesk.common.jmx.inspect.AnnotationMBeanInspector;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
public abstract class CdiMBean extends MBean {

	@Inject
	private AnnotationMBeanInspector inspector;

	private ObjectName objectName;

	private MBeanInfo mBeanInfo;

	@PostConstruct
	public void start() {
		mBeanInfo = inspector.inspect(this.getClass());
		objectName = inspector.getObjectName(this.getClass());
		register(objectName, this);
	}

	@PreDestroy
	public void stop() {
		unregister(objectName);
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		return mBeanInfo;
	}
}
