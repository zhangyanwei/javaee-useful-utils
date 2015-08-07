package com.worescloud.workdesk.common.jmx.inspect;

import com.worescloud.workdesk.common.jmx.MBean;

import javax.annotation.Nullable;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

public interface MBeanInspector {

	@Nullable
	ObjectName getObjectName(Class<? extends MBean> mBeanClass);

	@Nullable
	MBeanInfo inspect(Class<? extends MBean> mBeanClass);

}
